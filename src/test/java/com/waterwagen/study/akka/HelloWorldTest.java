package com.waterwagen.study.akka;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Inbox;
import akka.actor.Props;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import scala.concurrent.duration.FiniteDuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.waterwagen.study.akka.HelloWorldTestCompanion.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HelloWorldTest {

  private ActorSystem system;

  private BlockingQueue<String> sharedStateQueue;

  private String msgContents;

  @Before
  public void setup() {
    system = ActorSystem.create(TEST_ACTOR_SYSTEM_NAME);
    sharedStateQueue = new LinkedBlockingQueue<>();
    msgContents = "this is a random message for testing purposes. The time is " + System.currentTimeMillis();
  }

  @After
  public void tearDown() {
    system.shutdown();
  }

  @Test
  public void testSingleActorReceivesMessage() throws InterruptedException {
    testSentMessageEndsUpInSharedState(() ->
      system.actorOf(Props.create(MessageProcessor.class, PREFIX_TO_ADD, sharedStateQueue)));
  }

  private void testSentMessageEndsUpInSharedState(Supplier<ActorRef> testActorCreator) throws InterruptedException {
    // given
    ActorRef testActor = testActorCreator.get();

    // when
    testActor.tell(createMsg(msgContents), ActorRef.noSender());

    // then
    assertEquals("The shared state retrieved did not have the expected value. Perhaps the actor did not set any value on the shared state?",
      PREFIX_TO_ADD + msgContents, sharedStateQueue.poll(1, TimeUnit.SECONDS));
  }

  @Test
  public void testMessageForwardedFromActorIsReceivedBySecondActor() throws InterruptedException {
    testSentMessageEndsUpInSharedState(() ->
      system.actorOf(Props.create(MessageDispatcher.class, PREFIX_TO_ADD, sharedStateQueue)));
  }

  @Test
  public void testUsingInboxToSend() throws InterruptedException {
    // given
    Inbox inbox = Inbox.create(system);
    ActorRef messageProcessor = system.actorOf(Props.create(MessageProcessor.class, PREFIX_TO_ADD, sharedStateQueue));
    Msg msg = createMsg("blah blah blah " + System.currentTimeMillis());

    // when
    inbox.send(messageProcessor, msg);

    // then
    assertEquals("Expected the message sent via the inbox to make it into the shared state queue.",
        PREFIX_TO_ADD + msg.getContents(), sharedStateQueue.poll(1, TimeUnit.SECONDS));
  }

  @Test
  public void testUsingInboxToReceive() {
    // given
    Inbox inbox = Inbox.create(system);
    ActorRef messageResponder = system.actorOf(Props.create(MessageResponder.class, PREFIX_TO_ADD));
    Msg msg = createMsg("story line page " + System.currentTimeMillis());
    inbox.send(messageResponder, msg);

    // when
    Msg responseMsg = (Msg) inbox.receive(FiniteDuration.create(1, TimeUnit.SECONDS));

    // then
    assertNotNull("There was no response message!", responseMsg);
    assertEquals("Unexpected response message contents", PREFIX_TO_ADD + msg.getContents(), responseMsg.getContents());
  }

}
