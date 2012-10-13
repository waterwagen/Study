package com.waterwagen.study.groovy

import java.util.regex.Matcher;
import java.util.regex.Pattern

import org.junit.Ignore;
import org.junit.Test;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.xml.internal.ws.api.streaming.XMLStreamReaderFactory.Default;
import com.waterwagen.study.guice.TestGuiceBindings.TestAnno;

import static org.junit.Assert.*;

import groovy.transform.Immutable;
import groovy.xml.MarkupBuilder 

class TestGroovyBasics
{
	@Test
	public void testClosures()
	{
		Closure multiplyNums = 
		{
			num1,num2 ->
			
			num1 * num2
		}
		
		assertEquals("Unexpected closure result value.", 12, multiplyNums(3,4))
		assertEquals("Unexpected closure result value.", 63, multiplyNums(9,7))
	}
	
	@Test
	public void testRegexSupport()
	{
		// ~ creates a Pattern
		Pattern pattern = ~/\d+/
		String str = 'adfsaf98765asdfaf'
		Matcher matcher = pattern.matcher(str)
		assertTrue(matcher.find())
		assertEquals('98765', str.substring(matcher.start(), matcher.end()))
		assertTrue(!matcher.find())
		
		boolean matches = (~/\d+/).matcher('3463463').matches()
		assertTrue(matches)
		
		// =~ creates a Matcher
		str = '90809808thisisasentence09809'
		matcher = str =~ /[a-zA-Z]+/
		assertTrue(matcher.find())
		assertEquals('thisisasentence', str.substring(matcher.start(), matcher.end()))
		assertTrue(!matcher.find())
		
		matches = ('yesthesearewords' =~ /\w+/).matches()
		assertTrue(matches)

		// ==~ evaluates the whole string based on Pattern.matches()
		assertTrue(!('90809808thisisasentence09809' ==~ /[a-zA-Z]+/))
		assertTrue('thisisasentence' ==~ /[a-zA-Z]+/)
	}

	@Test
	public void testGStrings()
	{
		String name = 'Harry'
		int trip_count = 4
		
		String gstring = "${name} went to the store ${trip_count} times"
		assertEquals("Harry went to the store 4 times", gstring)
	}
	
	@Test
	public void testXmlParsing()
	{
		def xml = """
			<animals count='3'>
				<cheetah name='Billy' color='yellow'>
					<type>carnivore</type>
				</cheetah>
				<elephant name='Dumbo' color='gray'>
					<type>herbivore</type>
				</elephant>
				<wolf name='Randy' color='white'>
					<type>carnivore</type>
				</wolf>
			</animals>
		"""
		def animals = new XmlParser().parseText(xml)
		assertEquals("3", animals.@count)
		List animal_list = animals.children()
		assertEquals(3, animal_list.size())
		assertEquals('Billy', animals.cheetah.@name[0])
		assertEquals('yellow', animals.cheetah.@color[0])
		assertEquals('carnivore', animals.cheetah.type.text())
		assertEquals('Dumbo', animals.elephant.@name[0])
		assertEquals('gray', animals.elephant.@color[0])
		assertEquals('herbivore', animals.elephant.type.text())
		assertEquals('Randy', animals.wolf.@name[0])
		assertEquals('white', animals.wolf.@color[0])
		assertEquals('carnivore', animals.wolf.type.text())
	}
			
	@Test
	public void testXmlBuilder()
	{
		def writer = new StringWriter()
		def xml = new MarkupBuilder(writer)
		xml.person(id:2)
		{
			name 'Gweneth'
			age 1
			beefaloBill(thisIsAnAttribute:'yes')
			{
				firstName 'Beefalo'
				lastName 'Bill'
				occupation 'Beefaloing'
			}
		}
		String built_xml = writer.toString()
		assertTrue("Expected to find 'person' tags.", built_xml.contains("<person"))
		Matcher matcher = built_xml =~ /<beefaloBill[\w\s='"]*thisIsAnAttribute=['"]yes['"]/
		assertTrue("Expected an attribute named 'thisIsAnAttribute.", matcher.find())
		assertTrue("Expected to find 'occupation' tags.", built_xml.contains("<occupation>"))
		
		def expected_xml = /\s*<person\s*id='2'\s*>\s*<name>Gweneth<\/name>\s*<age>1<\/age>\s*<beefaloBill\s*thisIsAnAttribute='yes'\s*>\s*<firstName>Beefalo<\/firstName>\s*<lastName>Bill<\/lastName>\s*<occupation>Beefaloing<\/occupation>\s*<\/beefaloBill>\s*<\/person>/
		assertTrue("The builder produced XML doesn't match the expected Pattern.", built_xml ==~ expected_xml)
	}
	
	@Test
	public void testEquality()
	{
		def str1 = "blah"
		def str2 = new String("blah")
		assertTrue("Expected the two strings to return true from the == operator because equals() is really being called behind the scenes.", 
					str1 == str2)
		assertTrue("Expected the 'is' check to return false because two different instances of String were created.", 
					!str1.is(str2))
	}
	
	@Test
	public void testListAndMapSyntax()
	{
		// lists as first class objects
		def jvmlanguage_list = ["Java", "Groovy", "Scala", "Clojure"]		
		assertEquals("Java", jvmlanguage_list[0])
		assertEquals("Clojure", jvmlanguage_list[-1])
		assertEquals("Scala", jvmlanguage_list[-2])
		assertEquals(["Groovy", "Scala", "Clojure"], jvmlanguage_list[1..3])
		assertEquals(4, jvmlanguage_list.size)
		
		// maps as first class objects
		def jvmlanguage_rating_map = ["Java":90, "Groovy":89, "Scala":-1, "Clojure":-1]
		assertEquals(90, jvmlanguage_rating_map["Java"])
		assertEquals(90, jvmlanguage_rating_map.Java)
		
		jvmlanguage_rating_map.Java = 95
		assertEquals(95, jvmlanguage_rating_map["Java"])
		assertEquals(4, jvmlanguage_rating_map.size()) // have to use the full method call signature otherwise 'size' is interpreted as a key value
		
		jvmlanguage_rating_map = [:]
		assertEquals(0, jvmlanguage_rating_map.size()) // have to use the full method call signature otherwise 'size' is interpreted as a key value

		// ranges
		def range = 5..<8
		assertEquals(3, range.size())
		assertEquals(6, range[1])		
		assertTrue(!range.contains(8))
		assertEquals([5,6,7], range)
		def list = [5,6,7]
		assertEquals(list, range)
		
		def str_builder = new StringBuilder()
		('a'..'d').each
		{
			character ->
			
			str_builder.append(character)
		}
		assertEquals('abcd', str_builder.toString())
		
		str_builder = new StringBuilder()
		for(i in 5..1)
			str_builder.append(i)
		assertEquals('54321', str_builder.toString())
	}
	
	@Test
	public void testGroovyBeans() 
	{
		def groovy_bean = new SimpleBeanMutable(id:10, name:'grah')
		assertEquals("Unexpected bean id property value.", 10, groovy_bean.id);
		assertEquals("Unexpected bean name property value.", 'grah', groovy_bean.name);
		groovy_bean.id = 21
		groovy_bean.name = 'grah2'
		assertEquals("Unexpected bean id property value.", 21, groovy_bean.id);
		assertEquals("Unexpected bean name property value.", 'grah2', groovy_bean.name);

		groovy_bean = new SimpleBeanImmutable(id:11, name:'blah')
		assertEquals("Unexpected bean id property value.", 11, groovy_bean.id);
		assertEquals("Unexpected bean name property value.", 'blah', groovy_bean.name);
		try
		{
			groovy_bean.id = 87
		}
		catch(ReadOnlyPropertyException exc)
		{
			return;
		}
		fail("Expected a ReadOnlyPropertyException to be thrown when attempting to set a property value on the Immutable annotated class.")
	}
	
	private final static class SimpleBeanMutable{int id; String name}	
	@Immutable  
	private final static class SimpleBeanImmutable {int id; String name}
}
