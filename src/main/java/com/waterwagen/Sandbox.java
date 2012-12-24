package com.waterwagen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;

public class Sandbox
{
	public static void main(String[] args) throws IOException
	{
		int num_ints = 10_000_000;		
		Path output_file = createFile("src/test/resources/FileWith"+num_ints+"RandomInts.txt");
		try(BufferedWriter writer = Files.newBufferedWriter(output_file, StandardCharsets.UTF_8, StandardOpenOption.WRITE))
		{
			writer.write(stringOfInts(num_ints));
		}

	}

	private static Path createFile(String filepath) throws IOException
	{
		Path output_file = Paths.get(filepath);
		return Files.createFile(output_file);
	}

	private static String stringOfInts(int num_ints)
	{
		StringBuilder result = new StringBuilder();
		
		Collection<Integer> ints = Utilities.randomCollectionOfInts(num_ints);
		for(Integer next_int : ints)
			result.append(next_int).append("\n");
		
		return result.toString();
	}
}
