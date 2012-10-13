package com.waterwagen.study.java7;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.WrongMethodTypeException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class TestJava7ProjectCoinImprovements
{
	static class Bean
	{
		String getSpecialString(int prefix, Object obj)
		{
			return prefix + obj.toString();
		}
	}
	
	@Test
	public void testMethodHandles() throws Throwable
	{
		Bean bean = new Bean();
		MethodType mtype = MethodType.methodType(String.class, int.class, Object.class);
		MethodHandle mhandle = MethodHandles.lookup().findVirtual(Bean.class, "getSpecialString", mtype);
		try
		{
			assertThat((String)mhandle.invokeExact(bean, 35, "string1"), is(equalTo((Object)"35string1"))); // this fails because I substituted a String for an Object argument and the invocation is "exact"
		}
		catch(WrongMethodTypeException exc)
		{
			assertThat((String)mhandle.invoke(bean, 35, "string1"), is(equalTo("35string1"))); // does necessary conversion to invoke the method (i.e. casts the String argument to an Object)
			return;
		}
		fail("Should not have reached this point because an exception should have been thrown on the 'exact' invocation.");
	}
	
	@Test
	public void testDiamondSyntaxForGenerics()
	{
		Map<Integer,Map<String,String>> data_map = new HashMap<>();
		for(int i = 1; i <= 10; i++)
		{
			Map <String,String> value_map = new HashMap<>();
			value_map.put("Key1", "Value1" + i);
			value_map.put("Key2", "Value2" + i);
			data_map.put(i, value_map);
		}
		assertTrue("Unexpected value type.", data_map.get(1) instanceof Map);
		assertEquals("Unexpected value in map.", "Value210", data_map.get(10).get("Key2"));
	}
	
	@Test
	public void testTryWithResourceBlocks() throws IOException
	{
		// Confirming the old manual close behavior in an exception-less situation
		ClosedResourceTracker tracker = new ClosedResourceTracker();
		try /*(FileSystem file_system = getMockFileSystem(tracker);
			 BufferedReader reader = getMockBufferedReader(file_system, tracker))*/ 
		{
			FileSystem file_system = getMockFileSystem(tracker);
			BufferedReader reader = getMockBufferedReader(tracker, file_system, "src/test/resources", "TryWithResourcesTestFile.txt");

		    while (reader.readLine() != null){} 

		} finally{}
		assertTrue("The mock FileSystem was closed unexpectedly.", !tracker.isFileSystemClosed());
		assertTrue("The mock BufferedReader was closed unexpectedly.", !tracker.isBufferedReaderClosed());

		// Confirming the new auto-close behavior in an exception-less situation
		tracker = new ClosedResourceTracker();
		try (FileSystem file_system = getMockFileSystem(tracker);
			 BufferedReader reader = getMockBufferedReader(tracker, file_system, "src/test/resources", "TryWithResourcesTestFile.txt"))
		{
		    while (reader.readLine() != null){} 
		}
		assertTrue("The mock FileSystem was not closed when it should have been.", tracker.isFileSystemClosed());
		assertTrue("The mock BufferedReader not closed when it should have been.", tracker.isBufferedReaderClosed());

		// Purposely reference a non-existent file and test if the FileSystem is closed. Of course, no point in testing
		// the BufferedReader's state since it was never created in the first place as the exceptionw as thrown during
		// its construction
		tracker = new ClosedResourceTracker();
		try
		{
			try (FileSystem file_system = getMockFileSystem(tracker);
				 BufferedReader reader = getMockBufferedReader(tracker, file_system, "src/test/resources", "blah.txt"))
			{
			    while (reader.readLine() != null){} 
			}
		}catch (IOException exc){/*exc.printStackTrace();*/}
		assertTrue("The mock FileSystem was not closed when it should have been.", tracker.isFileSystemClosed());
		
		// Purposely throw an exception inside the try block, after the resources have been fully created,
		// and assert that all resources were auto closed
		tracker = new ClosedResourceTracker();
		try (FileSystem file_system = getMockFileSystem(tracker);
			 MockBufferedReader reader = getMockBufferedReader(tracker, file_system, "src/test/resources", "TryWithResourcesTestFile.txt"))
		{
			reader.throwIOException();
		}catch(IOException exc){System.out.println("Caught mock IOException, thrown for testing purposes.");};
		assertTrue("The mock FileSystem was not closed when it should have been.", tracker.isFileSystemClosed());
		assertTrue("The mock BufferedReader not closed when it should have been.", tracker.isBufferedReaderClosed());
	}

	@Test
	@Ignore // not really testable, just leaving here as an example
	public void testExceptionSpecificTypeRetained() throws SQLException, IllegalArgumentException
	{
		try
		{
			throwIllegalArgumentException();
			throwSQLException();
		}
		catch (Exception exc)
		{
			throw exc;
		}
	}

	@Test
	public void testMultipleExceptionCaughtAtOnce()
	{
		try
		{
			try
			{
				throw new IllegalArgumentException("dummy");
			}
			catch(IllegalArgumentException | NullPointerException exc)
			{
				// nothing
			}
		}
		catch(Exception exc)
		{
			fail("No exception should have made it outside the core, multiple exception catch block.");
		}

		try
		{
			try
			{
				throw new NullPointerException("dummy");
			}
			catch(IllegalArgumentException | NullPointerException exc)
			{
				// nothing
			}
		}
		catch(Exception exc)
		{
			fail("No exception should have made it outside the core, multiple exception catch block.");
		}

		try
		{
			try
			{
				throw new SQLException("dummy");
			}
			catch(IllegalArgumentException | NullPointerException exc)
			{
				fail("No exception should have been caught here.");
			}
		}
		catch(SQLException exc)
		{
			// nothing
		}

	}
	
	@Test
	public void testUnderscoreInNumericLiterals()
	{
		long num = 100_000_000;		
		assertEquals("Unexpected numeric value.", 100000000, num);

		num = 45_678_589_650L;	
		assertEquals("Unexpected numeric value.", 45678589650L, num);
	}

	@Test
	public void testBinaryNumbericLiterals()
	{
		int bin = 0b1011;		
		assertEquals("Unexpected numeric value.", 11, bin);

		bin = 0b10001101;		
		assertEquals("Unexpected numeric value.", 141, bin);
	}
	
	@Test
	public void testSwitchOnString()
	{
		String switch_var = "Tuesday";
		boolean found = false;
		switch(switch_var)
		{
			case "Sunday" : found = true; break;
			case "Monday" : found = true; break;
			case "Tuesday" : found = true; break;
			case "Wednesday" : found = true; break;
			case "Thursday" : found = true; break;
			case "Friday" : found = true; break;
			case "Saturday" : found = true; break;
		}
		assertEquals("Did not find the matching case for the day being switched on.", true, found);
	
		switch_var = "Noday";
		found = false;
		switch(switch_var)
		{
			case "Sunday" : found = true; break;
			case "Monday" : found = true; break;
			case "Tuesday" : found = true; break;
			case "Wednesday" : found = true; break;
			case "Thursday" : found = true; break;
			case "Friday" : found = true; break;
			case "Saturday" : found = true; break;
		}
		assertEquals("Did not expect to find a matching case for the fake day being switched on.", false, found);
	
		Exception exc_thrown = null;
		try
		{
			switch_var = "Noday";
			switch(switch_var)
			{
				case "Sunday" : found = true; break;
				case "Monday" : found = true; break;
				case "Tuesday" : found = true; break;
				case "Wednesday" : found = true; break;
				case "Thursday" : found = true; break;
				case "Friday" : found = true; break;
				case "Saturday" : found = true; break;
				default : throw new RuntimeException("Failed to recognize the specified day: " + switch_var + "");
			}
		}
		catch(RuntimeException exc)
		{
			exc_thrown = exc;
		}
		assertTrue("Expected an exception to be thrown but none was.", exc_thrown != null);
		assertEquals("Unexpected message in the thrown exception.", "Failed to recognize the specified day: " + switch_var, exc_thrown.getMessage());
	
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private void throwIllegalArgumentException() throws IllegalArgumentException
	{
		throw new IllegalArgumentException("dummy");
		
	}

	private void throwSQLException() throws SQLException
	{
		throw new SQLException("dummy");
	}

	private MockBufferedReader getMockBufferedReader(ClosedResourceTracker tracker, FileSystem file_system, String dir, String... path) throws IOException
	{
		return new MockBufferedReader(tracker, Files.newBufferedReader(file_system.getPath(dir, path), StandardCharsets.UTF_8));
	}

	private MockFileSystem getMockFileSystem(ClosedResourceTracker tracker)
	{
		return new MockFileSystem(tracker, FileSystems.getDefault());
	}

	private static class MockBufferedReader extends BufferedReader
	{
		private ClosedResourceTracker mResourceTracker;

		public MockBufferedReader(ClosedResourceTracker tracker, BufferedReader newBufferedReader)
		{
			super(newBufferedReader);
			mResourceTracker = tracker;
		}

		@Override
		public void close() throws IOException
		{
			super.close();
			mResourceTracker.setBufferedReaderClosed();
		}

		public void throwIOException() throws IOException
		{
			throw new IOException("Mock exception for testing purposes.");
		}
	}

	private static class MockFileSystem extends FileSystem
	{
		private ClosedResourceTracker mResourceTracker;
		private FileSystem mOrigFileSystem;

		public MockFileSystem(ClosedResourceTracker tracker, FileSystem orig_file_system)
		{
			mResourceTracker = tracker;
			mOrigFileSystem = orig_file_system;
		}

		public void close() throws IOException
		{
			try{this.mOrigFileSystem.close();}catch(Exception exc){/*exc.printStackTrace();*/}
			mResourceTracker.setFileSystemClosed();
		}

		public boolean equals(Object obj)
		{
			return this.mOrigFileSystem.equals(obj);
		}

		public Iterable<FileStore> getFileStores()
		{
			return this.mOrigFileSystem.getFileStores();
		}

		public Path getPath(String arg0, String... arg1)
		{
			return this.mOrigFileSystem.getPath(arg0, arg1);
		}

		public PathMatcher getPathMatcher(String arg0)
		{
			return this.mOrigFileSystem.getPathMatcher(arg0);
		}

		public Iterable<Path> getRootDirectories()
		{
			return this.mOrigFileSystem.getRootDirectories();
		}

		public String getSeparator()
		{
			return this.mOrigFileSystem.getSeparator();
		}

		public UserPrincipalLookupService getUserPrincipalLookupService()
		{
			return this.mOrigFileSystem.getUserPrincipalLookupService();
		}

		public int hashCode()
		{
			return this.mOrigFileSystem.hashCode();
		}

		public boolean isOpen()
		{
			return this.mOrigFileSystem.isOpen();
		}

		public boolean isReadOnly()
		{
			return this.mOrigFileSystem.isReadOnly();
		}

		public WatchService newWatchService() throws IOException
		{
			return this.mOrigFileSystem.newWatchService();
		}

		public FileSystemProvider provider()
		{
			return this.mOrigFileSystem.provider();
		}

		public Set<String> supportedFileAttributeViews()
		{
			return this.mOrigFileSystem.supportedFileAttributeViews();
		}

		public String toString()
		{
			return this.mOrigFileSystem.toString();
		}
	}

	private static class ClosedResourceTracker
	{
		private boolean mBufferedReaderClosed = false;
		private boolean mFileSystemClosed = false;

		public boolean isBufferedReaderClosed()
		{
			return mBufferedReaderClosed;
		}

		public boolean isFileSystemClosed()
		{
			return mFileSystemClosed;
		}

		public void setBufferedReaderClosed()
		{
			mBufferedReaderClosed  = true;
		}

		public void setFileSystemClosed()
		{
			mFileSystemClosed = true;
		}
	}
}
