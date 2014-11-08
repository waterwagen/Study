package com.waterwagen.study.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Inbox;
import akka.actor.Props;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.FiniteDuration;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static akka.japi.pf.ReceiveBuilder.match;

public class HelloWorldTestCompanion {

  public static final FiniteDuration NO_TIME = Duration.Zero();
  static final String TEST_ACTOR_SYSTEM_NAME = "testActorSystem";

  static final String PREFIX_TO_ADD = "RANDOM_PREFIX: ";
  static final FiniteDuration TEN_MILLISECONDS = Duration.create(10, TimeUnit.MILLISECONDS);

  static Msg createMsg(final String msgContents) {
    return () -> msgContents;
  }

  static Queue<Msg> receiveMsgs(Inbox inbox) {
    Queue<Msg> msgsReceived = new LinkedList<>();

    Msg nextMsgReceived = receiveMsg(inbox);
    while(nextMsgReceived != null) {
      msgsReceived.add(nextMsgReceived);
      nextMsgReceived = receiveMsg(inbox);
    }

    return msgsReceived;
  }

  static Msg receiveMsg(Inbox inbox) {
    Msg result = null;
    try {
      result = (Msg) inbox.receive(immediately());
    }
    finally {
      return result;
    }
  }

  static FiniteDuration immediately() {
    return NO_TIME;
  }

  static interface Msg {
    String getContents();
  }

  static class MessageProcessor extends AbstractActor {

    public MessageProcessor(String prefixToAdd, BlockingQueue<String> resultQueue) {
      receive(match(Msg.class, m -> resultQueue.add(prefixToAdd + m.getContents())).build());
    }

  }

  static class MessageDispatcher extends AbstractActor {

    private ActorRef messageProcessor;

    public MessageDispatcher(String prefixToAdd, BlockingQueue<String> resultQueue) {
      messageProcessor = context().system().actorOf(Props.create(MessageProcessor.class, prefixToAdd, resultQueue));
      receive(match(Msg.class, m -> messageProcessor.forward(m, context())).build());
    }

  }

  static class MessageResponder extends AbstractActor {

    public MessageResponder(String prefixToAdd) {
      receive(match(Msg.class, m -> sender().tell(createMsg(prefixToAdd + m.getContents()), self())).build());
    }

  }

}
