//Author: Tushar Jaiswal
//Creation Date: 09/24/2018

/*Given a matrix of n*n. Each cell contain 0, 1, -1. 
 * 0 denotes there is no diamond but there is a path. 
 * 1 denotes there is diamond at that location with a path 
 * -1 denotes that the path is blocked. 
 * Now you have start from 0,0 and reach to last cell & then return back to 0,0 collecting maximum no of diamonds. 
 * While going to last cell you can move only right and down. While returning back you can move only left and up.*/

using System;

namespace ConsoleApp
{
	class MaxDiamonds
	{
		int count;
		static void Main(string[] args)
		{
			int[,] arr = { { 1, 1, 1 }, 
						   { 1, 1, -1 }, 
						   { 0, 1, 1 } };
			MaxDiamonds findDiamonds = new MaxDiamonds();
			PrintArray(arr);
			Console.WriteLine("Max Diamonds: " + findDiamonds.CalcMaxDiamonds(arr));
		}

		private static void PrintArray(int[,] arr)
		{
			for (int i = 0; i < arr.GetLength(0); i++)
			{
				Console.WriteLine();
				for (int j = 0; j < arr.GetLength(1); j++)
				{
					if (arr[i, j] < 0)
					{
						Console.Write(" " + arr[i, j]);
					}
					else
					{
						Console.Write("  " + arr[i, j]);
					}
				}
			}
			Console.WriteLine();
		}

		private int CalcMaxDiamonds(int[,] arr)
		{
			count = 0;
			DoesPathExist(arr, 0, 0, true);
			return count;
		}

		private bool DoesPathExist(int[,] arr, int x, int y, bool toLastCell)
		{
			if(x == 0 && y == 0 && !toLastCell)
			{ return true; }
			if (!TraverseCell(arr, x, y))
			{
				return false;
			}
			int cellVal = arr[x, y];
			arr[x, y] = 0;
			int numRows = arr.GetLength(0);
			int numCols = arr.GetLength(1);
			bool validPath = false;
			if (x == numRows - 1 && y == numCols - 1)
			{
				toLastCell = false;
			}
			if(toLastCell)
			{
				if (y + 1 < numCols && arr[x, y + 1] == 1)
				{
					validPath = DoesPathExist(arr, x, y + 1, toLastCell);
				}
				if (!validPath && x + 1 < numRows && arr[x + 1, y] == 1)
				{
					validPath = DoesPathExist(arr, x + 1, y, toLastCell);
				}
				if (!validPath && y + 1 < numCols && arr[x, y + 1] == 0)
				{
					validPath = DoesPathExist(arr, x, y + 1, toLastCell);
				}
				if (!validPath && x + 1 < numRows && arr[x + 1, y] == 0)
				{
					validPath = DoesPathExist(arr, x + 1, y, toLastCell);
				}
				if(!validPath)
				{
					if (cellVal == 1)
					{
						count--;
					}
					return false;
				}
			}
			else
			{
				if (y - 1 >= 0 && arr[x, y - 1] == 1)
				{
					validPath = DoesPathExist(arr, x, y - 1, toLastCell);
				}
				if (!validPath && x - 1 >= 0 && arr[x - 1, y] == 1)
				{
					validPath = DoesPathExist(arr, x - 1, y, toLastCell);
				}
				if (!validPath && y - 1 >= 0 && arr[x, y - 1] == 0)
				{
					validPath = DoesPathExist(arr, x, y - 1, toLastCell);
				}
				if (!validPath && x - 1 >= 0 && arr[x - 1, y] == 0)
				{
					validPath = DoesPathExist(arr, x - 1, y, toLastCell);
				}
				if (!validPath)
				{
					if (arr[x, y] == 1)
					{
						count--;
					}
					return false;
				}
			}
			return true;
		}

		private bool TraverseCell(int[,] arr, int x, int y)
		{
			switch (arr[x, y])
			{
				case -1:
					return false;
				case 0:
					return true;
				case 1:
					count++;
					break;
				default:
					return false;
			}
			return true;
		}
	}
}
