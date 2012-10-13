package com.waterwagen.study.jmock;

import java.util.HashSet;
import java.util.Set;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class TestJMockThroughExample
{
	private final Mockery context = new JUnit4Mockery();

	/////////////
	/// Tests ///
	/////////////
	
	@Test
	public void oneSubscriberUnsubscribedDoesNotReceiveAMessage()
	{
		final Subscriber subscriber = context.mock(Subscriber.class);
		final String message = "message";

		Publisher publisher = new Publisher();
		publisher.add(subscriber);
		publisher.remove(subscriber);
		
		context.checking(new Expectations() {{
		    never(subscriber).receive(message);
		}});
		
		publisher.publish(message);
	}
	
	@Test
	public void oneSubscriberReceivesAMessage()
	{
		final Subscriber subscriber = context.mock(Subscriber.class);
		final String message = "message";

		Publisher publisher = new Publisher();
		publisher.add(subscriber);
		
		context.checking(new Expectations() {{
		    oneOf(subscriber).receive(message);
		}});
		
		publisher.publish(message);
	}
	
	@Test
	public void multipleSubscribersReceivesAMessage()
	{
		final Subscriber subscriber1 = context.mock(Subscriber.class, "sub1");
		final Subscriber subscriber2 = context.mock(Subscriber.class, "sub2");
		final Subscriber subscriber3 = context.mock(Subscriber.class, "sub3");
		final String message = "message";

		Publisher publisher = new Publisher();
		publisher.add(subscriber1);
		publisher.add(subscriber2);
		publisher.add(subscriber3);
		
		context.checking(new Expectations() {{
		    oneOf(subscriber1).receive(message);
		    oneOf(subscriber2).receive(message);
		    oneOf(subscriber3).receive(message);
		}});
		
		publisher.publish(message);
	}
	
	/////////////////////
	/// Utility Types ///
	/////////////////////
	
	private static class Publisher
	{
		private Set<Subscriber> mSubscribers = new HashSet<>();

		public void add(Subscriber subscriber)
		{
			mSubscribers.add(subscriber);
		}

		public void remove(Subscriber subscriber)
		{
			mSubscribers.remove(subscriber);
		}

		public void publish(String message)
		{
			for (Subscriber subscriber : mSubscribers)
				subscriber.receive(message);
		}

	}

	public interface Subscriber 
	{
	    public void receive(String message);
	}
}
