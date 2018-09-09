//Author: Tushar Jaiswal
//Creation Date: 09/09/2018

public class Searching {

	public static void main(String[] args) {
		int[] arr = { 2, 5, 7, 8, 30 };
		Searching search = new Searching();
		int result = search.BinarySearch(arr, 7);
		System.out.println("Result = " + result);
	}
	
	/**
	 * Binary Search. Searches for a target in an array in O(log n) time complexity.
	 * @param nums Array in which the search is to be performed
	 * @param target Target to search
	 * @return The target element's index if found, -1 otherwise
	 */
	public int BinarySearch(int[] nums, int target)
	{
		int left = 0;
		int right = nums.length - 1;

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