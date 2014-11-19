package com.waterwagen.sandbox.akka;

import akka.actor.*;
import akka.routing.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import scala.collection.JavaConversions;
import scala.collection.immutable.IndexedSeq;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.japi.pf.ReceiveBuilder.match;

// Improve this app by:
// - add caching of previous calculated values in the Actor. Use the Guava cache.
// - add support in the supervisor actor for handling errors in a child actor?
// - Also, look into adding a database and saving events to it (event sourcing).
// - Add tests.
// - Add logging.
public class PrimeNumberCountApp {

  private static final String USER_PROMPT = "Enter a result to calculate: ";

  private static final String PRIME_NUMBER_COUNTER_ACTOR_SYSTEM_NAME = "prime-doubleValue-counter-actor-system";

  private static final String RESULT_PROCESSOR_ACTOR_NAME = "result-processor-actor";

  private static final String PRIME_NUMBER_CALCULATOR_SUPERVISOR_ACTOR_NAME = "prime-result-calculator-actor-supervisor";

  public static void main(String[] args) {
    // create Akka objects
    ActorSystem actorSystem = ActorSystem.create(PRIME_NUMBER_COUNTER_ACTOR_SYSTEM_NAME);
    ActorRef primeNumberCalculatorSupervisor = createPrimeNumberCalculatorSupervisorActor(actorSystem);
    createResultProcessorActor(actorSystem);

    //  Read input from the user and calculate the prime number count
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    try {
      String input = readInput(br);
      while(!input.equalsIgnoreCase("exit")) {
        Long number = Long.valueOf(input);
        primeNumberCalculatorSupervisor.tell(new PrimeNumberCalculationRequestMsg(number), ActorRef.noSender());
        input = readInput(br);
      }

    }
    catch (IOException ioe) {
       System.out.println("IO error trying to read your input!");
       System.exit(1);
    }
    finally {
      actorSystem.shutdown();
    }

  }

  private static String readInput(BufferedReader br) throws IOException {
    System.out.print(USER_PROMPT);
    return br.readLine().trim();
  }

  private static ActorRef createPrimeNumberCalculatorSupervisorActor(ActorSystem actorSystem) {
    Props primeNumberCalculatorSupervisorProps = Props.create(PrimeNumberCalculatorSupervisor.class, RESULT_PROCESSOR_ACTOR_NAME);
    String primeNumberCalculatorSupervisorName = PRIME_NUMBER_CALCULATOR_SUPERVISOR_ACTOR_NAME;
    return actorSystem.actorOf(primeNumberCalculatorSupervisorProps, primeNumberCalculatorSupervisorName);
  }

  private static ActorRef createResultProcessorActor(ActorSystem actorSystem) {
    Props resultProcessorProps = Props.create(ResultProcessor.class);
    String resultProcessorName = RESULT_PROCESSOR_ACTOR_NAME;
    return actorSystem.actorOf(resultProcessorProps, resultProcessorName);
  }

  private static class PrimeNumberCalculatorSupervisor extends AbstractActor {

    private static final String BASE_PRIME_NUMBER_CALCULATOR_ACTOR_NAME = "prime-result-calculator-actor-";

    private final Router workerActorRouter;

    private final ActorSelection resultProcessorActor;

    public PrimeNumberCalculatorSupervisor(String resultProcessorActorName) {
      resultProcessorActor = createActorWhichProcessesResult(resultProcessorActorName);
      workerActorRouter = createRouterWithWorkerActorsToRunCalculations();
      configureMessageHandling();
    }

    private ActorSelection createActorWhichProcessesResult(String resultProcessorActorName) {
      return context().system().actorSelection("/user/" + resultProcessorActorName);
    }

    private Router createRouterWithWorkerActorsToRunCalculations() {
      Set<Routee> quickResponseRoutees = createQuickResponseActorsAsRoutees();
      Set<Routee> longRunningRoutees = createLongRunningActorsAsRoutees();
      List<Routee> supervisedPrimeNumberCalculators = createListFromAll(quickResponseRoutees, longRunningRoutees);
      PrimeNumberCalculationRoutingLogic primeNumberCalculatorRoutingLogic =
        new PrimeNumberCalculationRoutingLogic(quickResponseRoutees, longRunningRoutees);

      return new Router(primeNumberCalculatorRoutingLogic, supervisedPrimeNumberCalculators);
    }

    private ImmutableSet<Routee> createQuickResponseActorsAsRoutees() {
      return ImmutableSet.of(wrapActorAsRoutee(createPrimeNumberCalculatorActor("quickresponse")));
    }

    private ImmutableSet<Routee> createLongRunningActorsAsRoutees() {
      return ImmutableSet.of(wrapActorAsRoutee(createPrimeNumberCalculatorActor("longrunning-1")),
          wrapActorAsRoutee(createPrimeNumberCalculatorActor("longrunning-2")));
    }

    private static ImmutableList<Routee> createListFromAll(Collection<Routee>... routeeCollections) {
      ImmutableList.Builder<Routee> listBuilder = ImmutableList.<Routee>builder();
      for(Collection<Routee> routees : routeeCollections) {
        listBuilder.addAll(routees);
      }
      return listBuilder.build();
    }

    private ActorRef createPrimeNumberCalculatorActor(String id) {
      Props primeNumberCalculatorProps = Props.create(PrimeNumberCalculator.class);
      String primeNumberCalculatorName = BASE_PRIME_NUMBER_CALCULATOR_ACTOR_NAME + id;
      return context().system().actorOf(primeNumberCalculatorProps, primeNumberCalculatorName);
    }

    private static Routee wrapActorAsRoutee(ActorRef actor) {
      return new ActorRefRoutee(actor);
    }

    private void configureMessageHandling() {
      receive(match(PrimeNumberCalculationRequestMsg.class, this::processRequestMessage).
              match(PrimeNumberCalculationResultMsg.class, this::processResultMessage).build());
    }

    private void processRequestMessage(PrimeNumberCalculationRequestMsg msg) {
      workerActorRouter.route(msg, self());
    }

    private void processResultMessage(PrimeNumberCalculationResultMsg msg) {
      resultProcessorActor.tell(msg, self());
    }

    private static class PrimeNumberCalculationRoutingLogic implements RoutingLogic {

      private final SmallestMailboxRoutingLogic smallestMailboxRoutingLogic;

      private final Map<RouteeType, IndexedSeq<Routee>> routeeTypeToRouteesMap;

      private PrimeNumberCalculationRoutingLogic(Set<Routee> quickResponseWorkers, Set<Routee> longRunningWorkers) {
        smallestMailboxRoutingLogic = new SmallestMailboxRoutingLogic();
        routeeTypeToRouteesMap = Maps.newHashMap();
        routeeTypeToRouteesMap.put(RouteeType.QUICK_RESPONSE, toIndexedSeq(quickResponseWorkers));
        routeeTypeToRouteesMap.put(RouteeType.LONG_RUNNING, toIndexedSeq(longRunningWorkers));
      }

      private static <T> IndexedSeq<T> toIndexedSeq(Collection<T> routees) {
        return JavaConversions.asScalaIterator(routees.iterator()).toIndexedSeq();
      }

      @Override
      public Routee select(Object message, IndexedSeq<Routee> routees) {
        Preconditions.checkArgument(message instanceof PrimeNumberCalculationRequestMsg,
                                    unexpectedRouterMessageTypeMessage(message));

        PrimeNumberCalculationRequestMsg messageToRoute = (PrimeNumberCalculationRequestMsg) message;

        // choose routee based on the size of the prime number in the request. Requests for number <= 10^5 should go to
        // the quick response routee(s)/actor(s). The other requests should route between the long-running request
        // routee(s)/actor(s).
        RouteeType messageRouteeType = RouteeType.forPrimeNumberCalculationOf(messageToRoute.number);
        IndexedSeq<Routee> applicableRoutees = routeeTypeToRouteesMap.get(messageRouteeType);
        return smallestMailboxRoutingLogic.select(message, applicableRoutees);
      }

      private static enum RouteeType {
        QUICK_RESPONSE, LONG_RUNNING;

        private static RouteeType forPrimeNumberCalculationOf(long number) {
          return number <= 10*10*10*10*10 ? QUICK_RESPONSE : LONG_RUNNING;
        }
      }

      private static String unexpectedRouterMessageTypeMessage(Object message) {
        String messageTemplate =
          "Unexpected message type to be routed. Expected PrimeNumberCalculationRequestMsg but was %s";
        return String.format(messageTemplate, message.getClass().getName());
      }

    }

  }

  private final static class PrimeNumberCalculationRequestMsg {

    final long number;

    PrimeNumberCalculationRequestMsg(long number) {
      this.number = number;
    }

  }

  private static class PrimeNumberCalculator extends AbstractActor {

    public PrimeNumberCalculator() {
      receive(match(PrimeNumberCalculationRequestMsg.class, this::processMessage).build());
    }

    private void processMessage(PrimeNumberCalculationRequestMsg msg) {
      int primeNumberCount = calculateCount(msg.number);
      sender().tell(new PrimeNumberCalculationResultMsg(msg.number, primeNumberCount), self());
    }

    private static int calculateCount(long number) {
      AtomicInteger count = new AtomicInteger(0);
      incrementForAllPrimesLessThanOrEqualTo(number, count);
      return count.get();
    }

    private static void incrementForAllPrimesLessThanOrEqualTo(long number, AtomicInteger count) {
      for(long potentialPrime = 1; potentialPrime <= number; potentialPrime++) {
        if(isPrime(new PrimeCounterLong(potentialPrime))) {
          count.incrementAndGet();
        }
      }
    }

    private static boolean isPrime(PrimeCounterLong number) {
      if(number.longValue < 2) {
        return false;
      }

      long maxPotentialFactorWorthChecking = number.longValue / 2;
      for(long potentialFactor = 2; potentialFactor <= maxPotentialFactorWorthChecking; potentialFactor++) {
        if(number.hasFactor(potentialFactor)) {
          return false;
        }
      }
      return true;
    }

    private static class PrimeCounterLong {

      private final double doubleValue;

      private final long longValue;

      private PrimeCounterLong(long number) {
        this.doubleValue = number;
        this.longValue = number;
      }

      private boolean hasFactor(long potentialFactor) {
        double resultOfDividingByPotentialFactor = doubleValue / potentialFactor;
        return isWholeNumber(resultOfDividingByPotentialFactor);
      }

      private static boolean isWholeNumber(double resultOfDividingByFactor) {
        return resultOfDividingByFactor % 1 == 0.0;
      }

    }

  }

  private final static class PrimeNumberCalculationResultMsg {

    final long number;

    final long result;

    PrimeNumberCalculationResultMsg(long number, long result) {
      this.number = number;
      this.result = result;
    }

  }
  private static class ResultProcessor extends AbstractActor {

    public ResultProcessor() {
      receive(match(PrimeNumberCalculationResultMsg.class,
                    m -> System.out.printf("\nResult for doubleValue %d: %d\n", m.number, m.result)).build());
    }

  }

}
