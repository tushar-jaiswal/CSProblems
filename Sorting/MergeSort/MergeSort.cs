//Author: Tushar Jaiswal
//Creation Date: 09/30/2018

using System;

namespace ConsoleApp
{
	public class Solution
	{
		static void Main(string[] args)
		{
			int[] arr = { 2, 5, 7, 3, 7, 2, 1, 8, -3 };
			Sorting.MergeSort(arr, 0, arr.Length - 1);
			foreach (int i in arr)
			{
				Console.Write(i + " ");
			}
		}
	}

	public class Sorting
	{
		/// <summary>
		/// This merge sort function sorts an input array of integers in increasing order.
		/// </summary>
		/// <param name="arr">the input array of integers</param>
		/// <param name="low">lower bound of the array to operate on</param>
		/// <param name="high">higher bound of the array to operate on</param>
		public static void MergeSort(int[] arr, int low, int high)
		{
			if (low < high)
			{
				int mid = low + (high - low) / 2;
				MergeSort(arr, low, mid);
				MergeSort(arr, mid + 1, high);
				Merge(arr, low, mid, mid + 1, high);
			}
		}

		/// <summary>
		/// This functions merges two sorted arrays in increasing order
		/// </summary>
		/// <param name="arr">the input array of integers</param>
		/// <param name="low1">the lower bound of the first array</param>
		/// <param name="high1">the higher bound of the first array</param>
		/// <param name="low2">the lower bound of the second array</param>
		/// <param name="high2">the higher bound of the second array</param>
		private static void Merge(int[] arr, int low1, int high1, int low2, int high2)
		{
			int i = 0, p = low1, q = low2;
			int size = high1 - low1 + high2 - low2 + 2;
			int[] newArr = new int[size];
			while(p <= high1 && q <= high2)
			{
				if(arr[p] < arr[q])
				{
					newArr[i++] = arr[p++];
				}
				else
				{
					newArr[i++] = arr[q++];
				}
			}
			while (p <= high1)
			{
				newArr[i++] = arr[p++];
			}
			while (q <= high2)
			{
				newArr[i++] = arr[q++];
			}
			for(i = 0; i < size; i++, low1++)
			{
				arr[low1] = newArr[i];
			}
		}
	}
}