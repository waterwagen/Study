package com.waterwagen.study.hibernate.jpa

import java.sql.SQLException;

import groovy.sql.Sql
import groovy.transform.TypeChecked

import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

import org.junit.After;
import org.junit.Before;
import org.junit.Test

@TypeChecked
class TestHibernateBasics
{
	private EntityManager mEntityManager
//	String url = 'jdbc:oracle:thin:waterwagen/superduper79@localhost:1521:XE'
//	String username = null
//	String password = null
//	String driver_name = 'oracle.jdbc.OracleDriver'
	String url = 'jdbc:h2:mem:db1;DB_CLOSE_DELAY=-1;MVCC=TRUE'
	String username = 'sa'
	String password = null
	String driver_name = 'org.h2.Driver'
	Sql db = Sql.newInstance(url, username, password, driver_name)
		
	@Before
	void setUp()
	{
		try { db.execute 'DROP TABLE HIBERNATE_TEST_EVENTS' } catch(SQLException exc) { /*PROBABLY because table doesn't exist*/ println exc.message }

		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory('com.waterwagen.study.hibernate.jpa')
		mEntityManager = entityManagerFactory.createEntityManager()		
	}
	
	@After
	void tearDown()
	{
		mEntityManager.close()
	}
	
	@Test
	void show_basic_insert_via_JPA()
	{	
		mEntityManager.getTransaction().begin()

		String test_message_1 = 'First event!' + System.currentTimeMillis()
		String test_message_2 = 'Second event. Blah.' + System.currentTimeMillis()
		Date timestamp_1 = new Date()
		Date timestamp_2 = new Date()
		mEntityManager.persist(new JpaHibernateTestEvent(testMessage:test_message_1, testTimestamp:timestamp_1))
		mEntityManager.persist(new JpaHibernateTestEvent(testMessage:test_message_2, testTimestamp:timestamp_2))
		
		mEntityManager.getTransaction().commit()

		// verify result
		def result = db.rows('select * from HIBERNATE_TEST_EVENTS')
		assert result.size == 2
		// row 1
		assert result[0]['TEST_MESSAGE'] == test_message_1
		assert result[1]['TEST_MESSAGE'] == test_message_2
		// row 2
//		assert result[0]['TEST_TIMESTAMP'] == timestamp_1
//		assert result[1]['TEST_TIMESTAMP'] == timestamp_2	
	}
}