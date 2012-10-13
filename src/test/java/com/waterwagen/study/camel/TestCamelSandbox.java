package com.waterwagen.study.camel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.sql.DataSource;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.sql.SqlComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Some of the tests in this class assume that an Oracle db is installed locally with two slightly different versions 
 * of a simple table and a sequence for the primary key of one of the tables. See the tests below for details.
 *  
 * @author waterwagen
 *
 */
public class TestCamelSandbox
{
	private static final String CAMEL_ERROR_LOG_CATEGORY = "com.waterwagen.CamelOnExceptionErrorHandling";
	private static final String CAMEL_MESSAGE_LOG_CATEGORY = TestCamelSandbox.class.getName();
	private static final String CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS = "?level=DEBUG&multiline=true&showAll=true";
//	private static final String CAMEL_LOG_EXCEPTION_ENDPOINT_OPTIONS = "?level=ERROR&multiline=true&showAll=true";
	private static final String SQLTYPE_VALUE_INSERT = "insert";
	private static final String SQLTYPE_VALUE_REQUEST = "request";
	private static final String CAMEL_MESSAGE_HEADER_SQLTYPE = "sqltype";
	
	private CamelContext mCamelContext;

	@Before
	public void setupTest() throws Exception
	{
		mCamelContext = setupCamelContext();		
		mCamelContext.start();
	}
	
	@After
	public void cleanupTest() throws Exception
	{
		mCamelContext.stop();
	}
	
	@Test
	public void testExceptionHandlingApproach() throws Exception
	{
		// Camel endpoint names
		final String insert_start_endpoint = "jms:endpointA";
        final String insert_last_endpoint = "sql:INSERT INTO CamelTest (IdNum, UserName, FirstName) VALUES (#, #, #)";
        final String maxid_last_endpoint = "sql:SELECT MAX(IdNum) as \"max_idnum\" FROM CamelTest";

		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
		    	onException(UnknownSqlTypeException.class).
				log(LoggingLevel.ERROR, CAMEL_ERROR_LOG_CATEGORY, "The expected error occurred. Exception: ${exception}").// Exception: ${in.caughtException}").
//				to("log:" + CAMEL_ERROR_LOG_CATEGORY + CAMEL_LOG_EXCEPTION_ENDPOINT_OPTIONS).
		    	handled(true).
		    	process(new Processor()
				{
					@Override
					public void process(Exchange exchange) throws Exception
					{
						Message in = exchange.getIn();
				        in.setBody("Unknown SQL type header value. Value is '" + in.getHeader(CAMEL_MESSAGE_HEADER_SQLTYPE) + "'");
					}
				});

		    	from(insert_start_endpoint).process(new Processor()
				{
					// Transform the input message body from a String into a List of the Strings delimited by spaces.
					@Override
					public void process(Exchange msg) throws Exception
					{
						msg.getIn().setBody(Arrays.asList(msg.getIn().getBody(String.class).split(" ")));
					}
				}).
				log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Sending SQL query message body: ${in.body}").
				to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS).
				choice().
	                when(header(CAMEL_MESSAGE_HEADER_SQLTYPE).isEqualTo(SQLTYPE_VALUE_REQUEST)).
	                    to(maxid_last_endpoint).
	                when(header(CAMEL_MESSAGE_HEADER_SQLTYPE).isEqualTo(SQLTYPE_VALUE_INSERT)).
	                    to(insert_last_endpoint).
	                otherwise().
	                	throwException(new UnknownSqlTypeException("Did not recognize the sql type of the input message."));
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();
		
		// send and receive test messages
		String dummy_sqltype_header = "dummyheadervalue";
		String dummy_body = "dummybodyvalue";
		String result = producer.requestBodyAndHeader(insert_start_endpoint, dummy_body, CAMEL_MESSAGE_HEADER_SQLTYPE, dummy_sqltype_header, String.class);

		// assert that the expected error message was returned
		assertEquals("Value of result item is unexpected.", "Unknown SQL type header value. Value is '" + dummy_sqltype_header + "'", result);
	}
	
	@Test
	public void testJmsToDbInsertDataRouteSeqPKIncrement() throws Exception
	{
		// Camel endpoint names
		final String sql_start_endpoint = "jms:endpointA";
        final String insert_last_endpoint = "sql:INSERT INTO CamelTest2 (UserName, FirstName) VALUES (#, #)";
 
		// configure and start up Camel
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
		    	from(sql_start_endpoint).process(new Processor()
				{
					// Transform the input message body from a String into a List of the Strings delimited by spaces.
					@Override
					public void process(Exchange msg) throws Exception
					{
						msg.getIn().setBody(Arrays.asList(msg.getIn().getBody(String.class).split(" ")));
					}
				}).
					log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Sending SQL query message body: ${in.body}").
					to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS).
	            to(insert_last_endpoint).
		   			log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Received the result message.").
		   			to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS);
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();

		// send and receive test messages
		int rand_num = (int)(Math.random() * 1_000_000_000L);
		String username = "username" + rand_num;
		String firstname = "firstname" + rand_num;
		@SuppressWarnings("unchecked")
		List<String> insert_result = producer.requestBody(sql_start_endpoint, username + " " + firstname, List.class);

		// assert that the expected data was returned for the requested rows
		assertEquals("Unexpected number of items in the result message body.", 2, insert_result.size());
		assertEquals("Value of result item is unexpected.", username, insert_result.get(0));
		assertEquals("Value of result item is unexpected.", firstname, insert_result.get(1));
	}
	
	@Test
	public void testJmsToDbInsertDataRouteManualPKIncrement() throws Exception
	{
		// utility variables
		String maxidnum_column = "max_idnum";
		// Camel endpoint names
		final String sql_start_endpoint = "jms:endpointA";
        final String insert_last_endpoint = "sql:INSERT INTO CamelTest (IdNum, UserName, FirstName) VALUES (#, #, #)";
        final String maxid_last_endpoint = "sql:SELECT MAX(IdNum) as \"" + maxidnum_column + "\" FROM CamelTest";

		// configure and start up Camel
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
		    	from(sql_start_endpoint).process(new Processor()
				{
					// Transform the input message body from a String into a List of the Strings delimited by spaces.
					@Override
					public void process(Exchange msg) throws Exception
					{
						msg.getIn().setBody(Arrays.asList(msg.getIn().getBody(String.class).split(" ")));
					}
				}).
				log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Sending SQL query message body: ${in.body}").
				to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS).
				choice().
	                when(header(CAMEL_MESSAGE_HEADER_SQLTYPE).isEqualTo(SQLTYPE_VALUE_REQUEST)).
	                    to(maxid_last_endpoint).
		   				log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Received the result message.").
						to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS).
	                when(header(CAMEL_MESSAGE_HEADER_SQLTYPE).isEqualTo(SQLTYPE_VALUE_INSERT)).
	                    to(insert_last_endpoint).
		   				log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Received the result message.").
		   				to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS);
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();

		// send and receive test messages
		@SuppressWarnings("unchecked")
		List<Map<String, BigDecimal>> max_idnum_result = producer.requestBodyAndHeader(sql_start_endpoint, "", CAMEL_MESSAGE_HEADER_SQLTYPE, SQLTYPE_VALUE_REQUEST, List.class);
		int max_idnum = max_idnum_result.get(0).get(maxidnum_column).intValue();
		int rand_num = (int)(Math.random() * 1_000_000_000L);
		String idnum = (max_idnum + 1) + "";
		String username = "username" + rand_num;
		String firstname = "firstname" + rand_num;
		@SuppressWarnings("unchecked")
		List<String> insert_result = producer.requestBodyAndHeader(sql_start_endpoint, idnum + " " + username + " " + firstname, CAMEL_MESSAGE_HEADER_SQLTYPE, SQLTYPE_VALUE_INSERT, List.class);

		// assert that the expected data was returned for the requested rows
		assertEquals("Unexpected number of items in the result message body.", 3, insert_result.size());
		assertEquals("Value of result item is unexpected.", idnum, insert_result.get(0));
		assertEquals("Value of result item is unexpected.", username, insert_result.get(1));
		assertEquals("Value of result item is unexpected.", firstname, insert_result.get(2));
	}
	
	@Test
	public void testJmsToDbRequestDataRoute() throws Exception
	{
		// some utility variables
		final String first_endpoint = "jms:endpointA";
        final String last_endpoint = "sql:SELECT * FROM CamelTest WHERE IdNum IN (#)";//"sql:INSERT INTO CamelTest (IdNum, UserName, FirstName) VALUES (#, #, #)";
		
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
				from(first_endpoint).process(new Processor()
				{
					@Override
					public void process(Exchange msg) throws Exception
					{
						String msg_body = msg.getIn().getBody(String.class);
						String[] tokens = msg_body.split(" ");
						List<String> token_list = Arrays.asList(tokens);
						msg.getIn().setBody(token_list);
					}
				}).
				log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Sending SQL query message body: ${in.body}").
				to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS).
				to(last_endpoint).log(LoggingLevel.DEBUG, CAMEL_MESSAGE_LOG_CATEGORY, "Logging the result message from the SQL query...").
				to("log:" + CAMEL_MESSAGE_LOG_CATEGORY + CAMEL_LOG_STANDARD_ENDPOINT_OPTIONS);//.to("mock:result");
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();
		
		// send and receive test messages
		@SuppressWarnings("unchecked")
		List<Map<String,Object>> rows = producer.requestBody(first_endpoint, "23", List.class);

		// assert that the expected data was returned for the requested rows
		assertEquals("Value of result item is unexpected.", BigDecimal.valueOf(23), rows.get(0).get("IdNum"));
		assertEquals("Value of result item is unexpected.", "blah", rows.get(0).get("UserName"));
		assertEquals("Value of result item is unexpected.", "Jimmy", rows.get(0).get("FirstName"));
	}

	@Test
	@Ignore
	public void testReceiveExternalJms() throws Exception
	{
		final String EXT_BROKER_URL = "tcp://localhost:61616";
		final String EXT_QUEUE_NAME = "ExtMsgSend1";
		final String EXT_FIRST_ENDPOINT = "jms:" + EXT_QUEUE_NAME;
		final String EXT_MSG_BODY = "External message send body";

		// configure and start up JMS producer
		String queue_name = EXT_QUEUE_NAME;
		String broker_url = EXT_BROKER_URL;
		String msg_body = EXT_MSG_BODY;	
		final String last_endpoint = "seda:endpointB";

		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(broker_url);
		Connection conn = factory.createConnection();
		conn.start();
		Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue1 = session.createQueue(queue_name);
		MessageProducer producer = session.createProducer(queue1);

		// configure and start up Camel
		mCamelContext.addComponent("jms", JmsComponent.jmsComponent(new ActiveMQConnectionFactory(EXT_BROKER_URL)));		
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
				from(EXT_FIRST_ENDPOINT).to(last_endpoint);
		    }
		});
		ConsumerTemplate consumer = mCamelContext.createConsumerTemplate();
				
		// send and receive message
		producer.send(session.createTextMessage(msg_body));
		String rcvd_msg = (String)consumer.receiveBody(last_endpoint, 2000L);

		// assert expected events
		String msg = rcvd_msg;
		assertTrue("Null message received.", msg != null); 
		assertTrue("Unexpected message body. Received " + msg, msg.startsWith(EXT_MSG_BODY));
		
		producer.close();
		session.close();
		conn.close();
	}
	
	@Test
	public void testCreatingJmsEndpoint() throws Exception
	{
		// some utility variables
		final String first_endpoint = "jms:endpointA";
        final String last_endpoint = "seda:endpointB";
		
		// configure and start up Camel
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
				from(first_endpoint).to(last_endpoint);
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();
		ConsumerTemplate consumer = mCamelContext.createConsumerTemplate();
		
		// send and receive test messages
		String msg_body_boiler = "Test Message: ";
		int msg_count = 10;
		for (int i = 0; i < msg_count; i++)
			producer.sendBody(first_endpoint, msg_body_boiler + i);

		List<String> rcvd_msgs = new ArrayList<String>();
		for (int i = 0; i < msg_count; i++) 
			rcvd_msgs.add((String)consumer.receiveBody(last_endpoint, 1000L));

		// assert expected events
		for (int i = 0; i < msg_count; i++) 
		{
			String msg = rcvd_msgs.get(i);
			assertTrue("Null message received. Complete rcvd msg list: " + rcvd_msgs, msg != null); 
			assertEquals("Unexpected message body. Complete rcvd msg list: " + rcvd_msgs, msg_body_boiler + i, msg);
		}
	}
	
	@Test
	public void testCamelFundamentals() throws Exception
	{
		// some utility variables
		final String first_endpoint = "seda:endpointA";
        final String last_endpoint = "seda:endpointB";
		
		// configure and start up Camel
		mCamelContext.addRoutes(new RouteBuilder() 
		{
		    public void configure() 
		    {
				from(first_endpoint).to(last_endpoint);
		    }
		});
		ProducerTemplate producer = mCamelContext.createProducerTemplate();
		ConsumerTemplate consumer = mCamelContext.createConsumerTemplate();
		
		// send and receive test messages
		String msg_body_boiler = "Test Message: ";
		int msg_count = 10;
		for (int i = 0; i < msg_count; i++)
			producer.sendBody(first_endpoint, msg_body_boiler + i);

		List<String> rcvd_msgs = new ArrayList<String>();
		for (int i = 0; i < msg_count; i++) 
			rcvd_msgs.add((String)consumer.receiveBody(last_endpoint, 1000L));

		// assert expected events
		for (int i = 0; i < msg_count; i++) 
		{
			String msg = rcvd_msgs.get(i);
			assertTrue("Null message received. Complete rcvd msg list: " + rcvd_msgs, msg != null); 
			assertEquals("Unexpected message body. Complete rcvd msg list: " + rcvd_msgs, msg_body_boiler + i, msg);
		}
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private DataSource buildDataSource()
	{
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.OracleDriver");
        dataSource.setUrl("jdbc:oracle:thin:waterwagen/superduper79nomelissa@localhost:1521:XE");
//        dataSource.setUsername("waterwagen");
//        dataSource.setPassword("superduper79nomelissa");
        return dataSource;	
    }

	private CamelContext setupCamelContext()
	{
		CamelContext context = new DefaultCamelContext();	
		context.addComponent("jms", JmsComponent.jmsComponent(new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")));	
		SqlComponent sql_comp = new SqlComponent(context);
		DataSource my_ds = buildDataSource();
		sql_comp.setDataSource(my_ds);
		context.addComponent("sql", sql_comp);
		return context;
	}

	///////////////////////
	/// Utility Classes ///
	///////////////////////
	
	private static class UnknownSqlTypeException extends Exception
	{
		private static final long serialVersionUID = 1L;

		public UnknownSqlTypeException(String string)
		{
			super(string);
		}
	}
}