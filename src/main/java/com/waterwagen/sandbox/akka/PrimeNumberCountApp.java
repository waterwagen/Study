package com.waterwagen.sandbox.akka;

import akka.actor.*;
import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static akka.japi.pf.ReceiveBuilder.match;

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

    private final List<ActorRef> primeNumberCalculators;

    private int nextCalculatorCounter = 0;

    public PrimeNumberCalculatorSupervisor(String resultProcessorActorName) {
      primeNumberCalculators =
        Lists.newArrayList(createPrimeNumberCalculatorActor(context().system(), resultProcessorActorName, 1),
                           createPrimeNumberCalculatorActor(context().system(), resultProcessorActorName, 2),
                           createPrimeNumberCalculatorActor(context().system(), resultProcessorActorName, 3));
      receive(match(PrimeNumberCalculationRequestMsg.class, this::processMessage).build());
    }

    private static ActorRef createPrimeNumberCalculatorActor(ActorSystem actorSystem,
                                                             String resultProcessorActorName,
                                                             int id) {
      Props primeNumberCalculatorProps = Props.create(PrimeNumberCalculator.class, resultProcessorActorName);
      String primeNumberCalculatorName = BASE_PRIME_NUMBER_CALCULATOR_ACTOR_NAME + id;
      return actorSystem.actorOf(primeNumberCalculatorProps, primeNumberCalculatorName);
    }

    private void processMessage(PrimeNumberCalculationRequestMsg msg) {
      primeNumberCalculators.get(nextCalculatorCounter++ % primeNumberCalculators.size()).tell(msg, self());
    }

  }

  private final static class PrimeNumberCalculationRequestMsg {

    final long number;

    PrimeNumberCalculationRequestMsg(long number) {
      this.number = number;
    }

  }

  private static class PrimeNumberCalculator extends AbstractActor {

    private ActorSelection resultProcessorActor;

    public PrimeNumberCalculator(String resultProcessorActorName) {
      resultProcessorActor = context().system().actorSelection("/user/" + resultProcessorActorName);
      receive(match(PrimeNumberCalculationRequestMsg.class, this::processMessage).build());
    }

    private void processMessage(PrimeNumberCalculationRequestMsg msg) {
      int primeNumberCount = calculateCount(msg.number);
      resultProcessorActor.tell(new PrimeNumberCalculationResultMsg(msg.number, primeNumberCount), self());
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
