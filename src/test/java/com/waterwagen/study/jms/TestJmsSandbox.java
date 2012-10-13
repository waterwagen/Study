package com.waterwagen.study.jms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

public class TestJmsSandbox
{
	@Test
	public void testJmsFundamentals() throws Exception
	{
		// set up JMS objects
		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false");//"tcp://localhost:61616");
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue1 = session.createQueue("TestQueue1");
		MessageProducer producer = session.createProducer(queue1);
		MessageConsumer consumer = session.createConsumer(queue1);
		
		// send and receive message
		String msg_body = Double.toString(Math.random());
		producer.send(session.createTextMessage(msg_body));
		Thread.sleep(1000L);
		Message msg = consumer.receiveNoWait();
		
		// assert the expected happened
		assertTrue("No message received.", msg != null);
		assertTrue("Expected a text message.", msg instanceof TextMessage);
		assertEquals("Unexpected message body.", msg_body, ((TextMessage)msg).getText());

		conn.close();
		
//		// Obtain a JNDI connection using the jndi.properties file
//		InitialContext ctx = new InitialContext();
//		// Look up a JMS connection factory and create the connection
//		TopicConnectionFactory conFactory =
//		(TopicConnectionFactory)ctx.lookup(topicFactory);
//		TopicConnection connection = conFactory.createTopicConnection();
//		// Create two JMS session objects
//		TopicSession pubSession = connection.createTopicSession(
//		false, Session.AUTO_ACKNOWLEDGE);
//		TopicSession subSession = connection.createTopicSession(
//		false, Session.AUTO_ACKNOWLEDGE);
//		// Look up a JMS topic
//		Topic chatTopic = (Topic)ctx.lookup(topicName);
//		// Create a JMS publisher and subscriber. The additional parameters
//		// on the createSubscriber are a message selector (null) and a true
//		// value for the noLocal flag indicating that messages produced from
//		// this publisher should not be consumed by this publisher.
//		TopicPublisher publisher =
//		pubSession.createPublisher(chatTopic);
//		TopicSubscriber subscriber =
//		subSession.createSubscriber(chatTopic, null, true);
//		// Set a JMS message listener
//		subscriber.setMessageListener(this);
//		// Intialize the Chat application variables
//		this.connection = connection;
	}
}