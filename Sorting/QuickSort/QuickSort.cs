//Author: Tushar Jaiswal
//Creation Date: 07/26/2018

using System;

namespace ConsoleApp
{
	public class Solution
	{
		static void Main(string[] args)
		{
			int[] arr = { 2, 5, 7, 3, 7, 2, 1, 8, -3 };
			Sorting.QuickSort(arr, 0, arr.Length - 1);
			foreach (int i in arr)
			{
				Console.Write(i + " ");
			}
		}
	}

	public class Sorting
	{
		/// <summary>
		/// This quick sort function sorts an input array of integers in increasing order.
		/// </summary>
		/// <param name="arr">the input array of integers</param>
		public static void QuickSort(int[] arr, int low, int high)
		{
			if (low < high)
			{
				int pivot = Partition(arr, low, high);

				QuickSort(arr, low, pivot - 1);
				QuickSort(arr, pivot + 1, high);
			}
		}

		private static int Partition(int[] arr, int low, int high)
		{
			Random rnd = new Random();
			int pivot = rnd.Next(low, high + 1);
			Swap(ref arr[pivot], ref arr[high]);
			int i = low - 1;

			for(int j = low; j < high; j++)
			{
				if(arr[j] <= arr[high])
				{
					i++;
					Swap(ref arr[i], ref arr[j]);
				}
			}
			Swap(ref arr[i + 1], ref arr[high]);
			return (i + 1); 
		}

		/// <summary>
		/// This function swaps the values of its parameters which are passed in by reference. It is a generic method and it can operate on different types.
		/// </summary>
		/// <param name="a">First parameter</param>
		/// <param name="b">Second parameter</param>
		public static void Swap<T>(ref T a, ref T b)
		{
			T temp;
			temp = a;
			a = b;
			b = temp;
		}
	}
}