package com.waterwagen.study.java7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TestJava7NIOImprovements
{
	private static final String TEST_RESOURCES_DIR_PATHNAME = "src" + System.getProperty("file.separator") + 
																"test" + System.getProperty("file.separator") + 
																"resources";
	private Path mTestFile;

	@Before
	public void prepForTest() throws IOException
	{
		mTestFile = createTestFile();
	}
	
	@After
	public void cleanUpAfterTest() throws IOException
	{
		Files.delete(mTestFile);
//		
//		// assert that the test file was removed now that the test is over
//		assertTrue("The destination file was found but it should have been deleted.", !Files.exists(mTestFile));
	}

	@Test
	public void testFileFinding() throws IOException
	{
		final FileFoundStatusHolder result = new FileFoundStatusHolder();
		Path test_start_dir = Paths.get("./src");		
		Files.walkFileTree(test_start_dir, new SimpleFileVisitor<Path>()
		{
			@Override
			public FileVisitResult visitFile(Path visited_file, BasicFileAttributes arg1) throws IOException
			{
				if(Files.isSameFile(visited_file, mTestFile))
					result.markFileFound();
				
				return FileVisitResult.CONTINUE;
			}
		});
		
		assertTrue("Did not find the test file even though it is in a subdirectory of the directory where the test started.", result.isFileFound());
	}
	
	@Test(timeout = 45_000)
	public void testAsynchronousFileIOCallbackStyle() throws IOException, InterruptedException, ExecutionException
	{
		final int bytes_in_file = 10_000_000;
		int line_size = 1_000;
		try(AsynchronousFileChannel file_channel = AsynchronousFileChannel.open(mTestFile, StandardOpenOption.READ, StandardOpenOption.WRITE);)
		{
			ByteBuffer byte_buffer = ByteBuffer.allocate(bytes_in_file);
			StringBuffer str_buffer = new StringBuffer(bytes_in_file);
			int limit;
			while(byte_buffer.hasRemaining())
			{
				limit = Math.min(line_size, byte_buffer.remaining());
				for(int i = 0; i < limit - 2; i++)
					str_buffer.append("a");
				str_buffer.append(System.getProperty("line.separator"));
				byte_buffer.put(str_buffer.toString().getBytes("utf-8"));
				str_buffer.replace(0, str_buffer.length(), ""); // empty the string
			}
			
			// write from buffer
			CountDownLatch io_done_signal = new CountDownLatch(1);
			byte_buffer.flip();
			file_channel.write(byte_buffer, 0, io_done_signal, new CompletionHandler<Integer, CountDownLatch>()
			{
				@Override
				public void completed(Integer result, CountDownLatch io_is_done)
				{
					assertEquals("Unexpected number of bytes written to the test file.", bytes_in_file, result.intValue());
					io_is_done.countDown();
				}

				@Override
				public void failed(Throwable exc, CountDownLatch io_is_done)
				{
					fail("Failed to property complete the write operation." + exc.getMessage());
					io_is_done.countDown();
				}
			});
			io_done_signal.await();
			
			// read to buffer
			byte_buffer.clear();
			io_done_signal = new CountDownLatch(1);
			file_channel.read(byte_buffer, 0, io_done_signal, new CompletionHandler<Integer, CountDownLatch>()
			{
				@Override
				public void completed(Integer result, CountDownLatch io_is_done)
				{
					assertEquals("Unexpected number of bytes read from the test file.", bytes_in_file, result.intValue());
					io_is_done.countDown();
				}

				@Override
				public void failed(Throwable exc, CountDownLatch io_is_done)
				{
					fail("Failed to property complete the read operation." + exc.getMessage());
					io_is_done.countDown();
				}
			});
			io_done_signal.await();
		}
	}
	
	@Test(timeout = 45_000)
	public void testAsynchronousFileIOFutureStyle() throws IOException, InterruptedException, ExecutionException
	{
		int bytes_in_file = 10_000_000;
		int line_size = 1_000;
		try(AsynchronousFileChannel file_channel = AsynchronousFileChannel.open(mTestFile, StandardOpenOption.READ, StandardOpenOption.WRITE);)
		{
			ByteBuffer byte_buffer = ByteBuffer.allocate(bytes_in_file);
			StringBuffer str_buffer = new StringBuffer(bytes_in_file);
			int limit;
			while(byte_buffer.hasRemaining())
			{
				limit = Math.min(line_size, byte_buffer.remaining());
				for(int i = 0; i < limit - 2; i++)
					str_buffer.append("a");
				str_buffer.append(System.getProperty("line.separator"));
				byte_buffer.put(str_buffer.toString().getBytes("utf-8"));
				str_buffer.replace(0, str_buffer.length(), ""); // empty the string
			}
			
			// write from buffer
			byte_buffer.flip();
			Future<Integer> result = file_channel.write(byte_buffer, 0);
			while(!result.isDone())
			{
//				System.out.println("Waiting on file write!");
				Thread.sleep(500L);
			}
			assertEquals("Unexpected number of bytes written to the test file.", bytes_in_file, result.get().intValue());
			
			// read to buffer
			byte_buffer.clear();
			result = file_channel.read(byte_buffer, 0);
			while(!result.isDone())
			{
//				System.out.println("Waiting on file load!");
				Thread.sleep(500L);
			}
			assertEquals("Unexpected number of bytes read from the test file.", bytes_in_file, result.get().intValue());
		}
	}
	
	@Test
	public void testFileModifyWatching() throws IOException, InterruptedException
	{
		WatchService watcher = FileSystems.getDefault().newWatchService();
		WatchKey key = mTestFile.getParent().register(watcher, StandardWatchEventKinds.ENTRY_MODIFY);

		String test_msg = "Hello World, I am here...";
		try(BufferedWriter writer = Files.newBufferedWriter(mTestFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE))
		{
			writer.write(test_msg);
		}

		Thread.sleep(100L);
		
		List<WatchEvent<?>> events = key.pollEvents();
		assertEquals("Unexpected number of watch events sent.", 1, events.size());
		WatchEvent<?> event = events.get(0);
		assertEquals("Unexpected watch event kind.", StandardWatchEventKinds.ENTRY_MODIFY, event.kind());
		assertEquals("Unexpected path returned as the event context.", mTestFile.getFileName()/*mTestFile.getParent().relativize(mTestFile)*/, event.context());
	}
	
	@Test
	public void testFileWritingAndReading() throws IOException
	{
		String test_msg = "Hello World, I am here...";
		try(BufferedWriter writer = Files.newBufferedWriter(mTestFile, StandardCharsets.UTF_8, StandardOpenOption.WRITE))
		{
			writer.write(test_msg);
		}

		try(BufferedReader reader = Files.newBufferedReader(mTestFile, StandardCharsets.UTF_8))
		{
			String line;
			StringBuffer buffer = new StringBuffer();
			while((line = reader.readLine()) != null)
				buffer.append(line);
			assertEquals("Unexpected value read from the test file.", test_msg, buffer.toString());	
		}
	}
	
	@Test
	public void testFileCreationAndDeletion() throws IOException
	{
		Path test_file_path = Paths.get(TEST_RESOURCES_DIR_PATHNAME, "FileCreationAndDeletionTestFile.txt");
		Files.createFile(test_file_path);

		// test that the file was created as expected
		assertTrue("The destination file could not be found.", Files.exists(test_file_path));
	
		Files.delete(test_file_path);

		// assert that the test file was removed now that the test is over
		assertTrue("The destination file was found but it should have been deleted.", !Files.exists(test_file_path));
	
	}
	
	@Test
	public void testFileCopying() throws IOException
	{
		Path src_file = mTestFile;
		Path dest_file = Paths.get(mTestFile.toString() + "_copy");
		
		// assert that the expected files do or do not exist before the test
		assertTrue("The source file could not be found.", Files.exists(src_file));
		assertTrue("The destination file was found but it shouldn't exist yet.", !Files.exists(dest_file));
		
		Files.copy(src_file, dest_file, StandardCopyOption.REPLACE_EXISTING);

		// test that the file was copied as expected
		assertTrue("The destination file could not be found.", Files.exists(dest_file));
	
		Files.delete(dest_file);

		// assert that the test file was removed now that the test is over
		assertTrue("The destination file was found but it should have been deleted.", !Files.exists(dest_file));
	}

	///////////////////////
	/// Utility Methods ///
	///////////////////////
	
	private Path createTestFile() throws IOException
	{
		Path test_file = Paths.get(TEST_RESOURCES_DIR_PATHNAME, "TempTestFile"+(int)(Math.random()*1_000_000_000L)+".txt");
		Files.createFile(test_file);
		return test_file;
	}

	///////////////////////
	/// Utility Classes ///
	///////////////////////	
	
	private static class FileFoundStatusHolder
	{
		private boolean mFileFound = false;

		public void markFileFound()
		{
			mFileFound = true;
		}

		public boolean isFileFound()
		{
			return mFileFound;
		}
	}
}
