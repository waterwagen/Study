package com.waterwagen.study.algorithms.searching;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class HashVsBinarySearchComparison
{
	private static List<Integer> MASTER_LIST_OF_RANDOM_INTS = null;

	public static void main(String[] args)
	{
		int limit = 1_000_000;
		HashSet<Integer> powers_of_ten = powersOfTenUpTo(limit);
		for(int i = 1; i <= limit; i++)
		{
			if(!(powers_of_ten.contains(Integer.valueOf(i)))) continue;
			
			int check_accounts_total = i;
			int accounts_db_total = i * 10;
			
			Collection<Integer> accounts_to_check = randomCollectionOfIntsUpTo(check_accounts_total);
			Collection<Integer> accounts_db = randomCollectionOfIntsUpTo(accounts_db_total);
			Set<Integer> accountsdb_hash = new HashSet<>(accounts_db);
			Set<Integer> accountsdb_tree = new TreeSet<>(accounts_db);
			
			int hash_found = 0;
			int tree_found = 0;
			
			// check time with hash
			long hash_start_time = System.nanoTime();
			for(Integer account : accounts_to_check)
				if(accountsdb_hash.contains(account)) hash_found += 1;
			long hash_end_time = System.nanoTime();
			
			// check time with binary search
			long binarysearch_start_time = System.nanoTime();
			for(Integer account : accounts_to_check)
				if(accountsdb_tree.contains(account)) tree_found += 1;
			long binarysearch_end_time = System.nanoTime();
			
			assertThat(hash_found, is(equalTo(tree_found))); // sanity check
			
			System.out.printf("Accounts to check total of %d, accounts db total of %d : hash found %d in total time=%f seconds | binarysearch found %d in total time=%f seconds %n", 
				check_accounts_total,
				accounts_db_total,
				hash_found,
				((double)hash_end_time - (double)hash_start_time)/1000000000, 
				tree_found,
				((double)binarysearch_end_time - (double)binarysearch_start_time)/1000000000);
		}
	}

	private static Collection<Integer> randomCollectionOfIntsUpTo(int total)
	{
		if(MASTER_LIST_OF_RANDOM_INTS == null) 
			MASTER_LIST_OF_RANDOM_INTS = provideRandomInts();
		
		Collection<Integer> result = new ArrayList<>();
		
		int add_count = 0;
		while(add_count < total)
		{
			int index = 0;
			int step = 1 + new Random(System.currentTimeMillis()).nextInt(19);
			while(index + step < MASTER_LIST_OF_RANDOM_INTS.size() && add_count < total)
			{
				index += step;
				result.add(MASTER_LIST_OF_RANDOM_INTS.get(index));
				add_count++;
			}
		} 
		
		assertThat(result.size(), is(equalTo(total)));
		
		return result;
	}

	private static List<Integer> provideRandomInts()
	{
		List<Integer> result = new ArrayList<>();

		String path_str = "src/test/resources/FileWith10000000RandomInts.txt";
		Path int_file = Paths.get(path_str);
		try(BufferedReader reader = Files.newBufferedReader(int_file, StandardCharsets.UTF_8))
		{
			String line;
			while((line = reader.readLine()) != null)
				result.add(Integer.valueOf(line));
		}
		catch (IOException exc)
		{
			throw new RuntimeException(String.format("Failed to load ints from file at %s", path_str), exc);
		}

		return result;
	}

	private static HashSet<Integer> powersOfTenUpTo(int limit)
	{
		HashSet<Integer> result = new HashSet<>();

		int num = 1;
		while(num < limit)
		{
			num *= 10;
			result.add(num);
		}
			
		return result;
	}
}
