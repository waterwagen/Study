package com.waterwagen.study.akka;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.concurrent.BlockingQueue;

import static akka.japi.pf.ReceiveBuilder.match;

public class HelloWorldTestCompanion {

  static final String TEST_ACTOR_SYSTEM_NAME = "testActorSystem";

  static final String PREFIX_TO_ADD = "RANDOM_PREFIX: ";

  static Msg createMsg(final String msgContents) {
    return () -> msgContents;
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
