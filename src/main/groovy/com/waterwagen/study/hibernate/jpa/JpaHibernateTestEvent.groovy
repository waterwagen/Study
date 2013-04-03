package com.waterwagen.study.hibernate.jpa

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import groovy.transform.TypeChecked

@Entity
@Table(name='HIBERNATE_TEST_EVENTS')
@TypeChecked
class JpaHibernateTestEvent
{
	@Id
	@GeneratedValue
	private Long id
	
	@Column(name='TEST_MESSAGE')
	String testMessage
	
	@Column(name='TEST_TIMESTAMP')
	@Temporal(TemporalType.TIMESTAMP)
	Date testTimestamp
}