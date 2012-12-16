package com.waterwagen.study.algorithms.brainteasers;

import java.util.Stack;

public class TowerOfHanoiSolver
{
	public void solveSimple(Stack<Integer> t1, Stack<Integer> t2, Stack<Integer> t3)
	{
		while(!t1.isEmpty())
			moveSimple(t1, t3, t2);
	}

	private void moveSimple(Stack<Integer> source, Stack<Integer> target, Stack<Integer> other)
	{
		while(!target.isEmpty() && target.peek() < source.peek())
			moveSimple(target, other, source);
		Integer item = source.peek();
		target.push(source.pop());
		while(!other.isEmpty() && other.peek() < item)
			moveSimple(other, target, source);
	}

//////////////////////////////////////////////////////////////
	
	public void solveSpeedOptimized(Stack<Integer> t1, Stack<Integer> t2, Stack<Integer> t3)
	{
		moveSpeedOptimized(t1,t3,t2);
	}

	private void moveSpeedOptimized(Stack<Integer> source, Stack<Integer> target, Stack<Integer> other)
	{
		if(source.isEmpty()) return;
		while(source.size() > 1)
			moveTop(source, other, target);
		target.push(source.pop());
		moveSpeedOptimized(other, target, source);
	}

	private void moveTop(Stack<Integer> source, Stack<Integer> target, Stack<Integer> other)
	{
		int move_count = 0;
		while(!target.isEmpty() && target.peek() < source.peek())
		{
			moveTop(target, other, source);
			move_count++;
		}
		target.push(source.pop());
		for(int i = 1; i <= move_count; i++)
			moveTop(other, target, source);
	}
}
