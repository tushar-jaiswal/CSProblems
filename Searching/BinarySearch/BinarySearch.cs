//Author: Tushar Jaiswal
//Creation Date: 03/16/2018

using System;

class Solution
{
    static void Main(string[] args)
    {
        int[] arr = { 2, 5, 7, 8, 30 };
		Searching search = new Searching();
		int result = search.BinarySearch(arr, 7);
		Console.WriteLine("Result = " + result);
    }
}

public class Searching
	{
		/// <summary>
		/// Binary Search. Searches for a target in an array in O(log n) time complexity.
		/// </summary>
		/// <param name="nums">Array in which the search is to be performed</param>
		/// <param name="target">Target to search</param>
		/// <returns></returns>
		public int BinarySearch(int[] nums, int target)
		{
			int left = 0;
			int right = nums.Length - 1;

			while (left <= right)
			{
				int mid = left + (right - left) / 2;
				if (nums[mid] == target)
				{
					return mid;
				}
				if (target < nums[mid])
				{
					right = mid - 1;
				}
				else
				{
					left = mid + 1;
				}
			}
			return -1;
		}
	}
