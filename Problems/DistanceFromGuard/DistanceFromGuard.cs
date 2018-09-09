//Author: Tushar Jaiswal
//Creation Date: 09/09/2018

/*Find Shortest distance from a guard in a Bank
Given a matrix that is filled with ‘O’, ‘G’, and ‘W’ where ‘O’ represents open space, ‘G’ represents guards and ‘W’ represents walls in a Bank. Replace all of the O’s in the matrix with their shortest distance from a guard, without being able to go through any walls. Also, replace the guards with 0 and walls with -1 in output matrix.

Expected Time complexity is O(MN) for a M x N matrix.

Examples:

O ==> Open Space
G ==> Guard
W ==> Wall

Input: 
  O  O  O  O  G
  O  W  W  O  O
  O  O  O  W  O
  G  W  W  W  O
  O  O  O  O  G

Output:  
  3  3  2  1  0
  2 -1 -1  2  1
  1  2  3 -1  2
  0 -1 -1 -1  1
  1  2  2  1  0
 */

using System;
using System.Collections.Generic;

namespace ConsoleApp
{
	class DistanceFromGuard
	{
		static void Main(string[] args)
		{
			char[,] arr = { { 'O', 'O', 'O', 'O', 'G' }, { 'O', 'W', 'W', 'O', 'O' }, { 'O', 'O', 'O', 'W', 'O' }, { 'G', 'W', 'W', 'W', 'O' }, { 'O', 'O', 'O', 'O', 'G' } };
			int[,] result = CalculateGuardDistance(arr);
			for(int i = 0; i < arr.GetLength(0); i++)
			{
				Console.WriteLine();
				for (int j = 0; j < arr.GetLength(1); j++)
				{
					if(result[i, j] < 0)
					{
						Console.Write(" " + result[i, j]);
					}
					else
					{
						Console.Write("  " + result[i, j]);
					}
				}
			}
			Console.WriteLine();
		}

		public static int[,] CalculateGuardDistance(char[,] arr)
		{
			int m = arr.GetLength(0);
			int n = arr.GetLength(1);
			int[,] result = new int[m, n];
			Queue<Tuple<int, int>> queue = new Queue<Tuple<int, int>>();
			for(int i = 0; i < m; i++)
			{
				for(int j = 0; j < n; j++)
				{
					if(arr[i, j] == 'G')
					{
						result[i, j] = 0;
						queue.Enqueue(new Tuple<int, int>(i, j));
					}
					else
					{ result[i, j] = -2; }
				}
			}
			while(queue.Count != 0)
			{
				Tuple<int, int> tpl = queue.Dequeue();
				int i = tpl.Item1;
				int j = tpl.Item2;
				if(i - 1 >= 0 && result[i - 1, j] == -2)
				{
					if (arr[i - 1, j] == 'O')
					{
						result[i - 1, j] = result[i, j] + 1;
						queue.Enqueue(new Tuple<int, int>(i - 1, j));
					}
					else
					{ result[i - 1, j] = -1; }
				}
				if (i + 1 < m && result[i + 1, j] == -2)
				{
					if (arr[i + 1, j] == 'O')
					{
						result[i + 1, j] = result[i, j] + 1;
						queue.Enqueue(new Tuple<int, int>(i + 1, j));
					}
					else
					{ result[i + 1, j] = -1; }
				}
				if (j - 1 >= 0 && result[i, j - 1] == -2)
				{
					if (arr[i, j - 1] == 'O')
					{
						result[i, j - 1] = result[i, j] + 1;
						queue.Enqueue(new Tuple<int, int>(i, j - 1));
					}
					else
					{ result[i, j - 1] = -1; }
				}
				if (j + 1 < n && result[i, j + 1] == -2)
				{
					if (arr[i, j + 1] == 'O')
					{
						result[i, j + 1] = result[i, j] + 1;
						queue.Enqueue(new Tuple<int, int>(i, j + 1));
					}
					else
					{ result[i, j + 1] = -1; }
				}
			}
			return result;
		}
	}
}
