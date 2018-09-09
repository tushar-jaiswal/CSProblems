import java.util.*;

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
public class DistanceFromGuard {

	public static void main(String[] args) {
		char[][] arr = { { 'O', 'O', 'O', 'O', 'G' }, { 'O', 'W', 'W', 'O', 'O' }, { 'O', 'O', 'O', 'W', 'O' }, { 'G', 'W', 'W', 'W', 'O' }, { 'O', 'O', 'O', 'O', 'G' } };
		int[][] result = CalculateGuardDistance(arr);
		for(int i = 0; i < arr.length; i++)
		{
			System.out.println();
			for (int j = 0; j < arr[0].length; j++)
			{
				if(result[i][j] < 0)
				{
					System.out.print(" " + result[i][j]);
				}
				else
				{
					System.out.print("  " + result[i][j]);
				}
			}
		}
	}

	public static int[][] CalculateGuardDistance(char[][] arr) {
		int m = arr.length;
		int n = arr[0].length;
		int[][] result = new int[m][n];
		Queue<Map.Entry<Integer, Integer>> queue = new LinkedList<Map.Entry<Integer, Integer>>();
		for(int i = 0; i < m; i++)
		{
			for(int j = 0; j < n; j++)
			{
				if(arr[i][j] == 'G')
				{
					result[i][j] = 0;
					queue.offer(new AbstractMap.SimpleEntry<Integer, Integer>(i, j));
				}
				else
				{ result[i][j] = -2; }
			}
		}
		while(!queue.isEmpty())
		{
			Map.Entry<Integer, Integer> entry = queue.poll();
			int i = entry.getKey();
			int j = entry.getValue();
			if(i - 1 >= 0 && result[i - 1][j] == -2)
			{
				if (arr[i - 1][j] == 'O')
				{
					result[i - 1][j] = result[i][j] + 1;
					queue.offer(new AbstractMap.SimpleEntry<Integer, Integer>(i - 1, j));
				}
				else
				{ result[i - 1][j] = -1; }
			}
			if (i + 1 < m && result[i + 1][j] == -2)
			{
				if (arr[i + 1][j] == 'O')
				{
					result[i + 1][j] = result[i][j] + 1;
					queue.offer(new AbstractMap.SimpleEntry<Integer, Integer>(i + 1, j));
				}
				else
				{ result[i + 1][j] = -1; }
			}
			if (j - 1 >= 0 && result[i][j - 1] == -2)
			{
				if (arr[i][j - 1] == 'O')
				{
					result[i][j - 1] = result[i][j] + 1;
					queue.offer(new AbstractMap.SimpleEntry<Integer, Integer>(i, j - 1));
				}
				else
				{ result[i][j - 1] = -1; }
			}
			if (j + 1 < n && result[i][j + 1] == -2)
			{
				if (arr[i][j + 1] == 'O')
				{
					result[i][j + 1] = result[i][j] + 1;
					queue.offer(new AbstractMap.SimpleEntry<Integer, Integer>(i, j + 1));
				}
				else
				{ result[i][j + 1] = -1; }
			}
		}
		return result;
	}

}
