//Author: Tushar Jaiswal
//Creation Date: 02/10/2019

/*Given an array nums of integers, an integer target, are there n elements (n >= 2) in nums such that their sum equals target? Find all unique sets of n numbers in the array which gives the sum of target.
Note: The solution set must not contain duplicate sets.

Example:
Given array nums = [1, 0, -1, 0, -2, 2], target = 0, and n = 4.
A solution set is:
[
  [-1,  0, 0, 1],
  [-2, -1, 1, 2],
  [-2,  0, 0, 2]
]*/

public class Solution {
    public IList<IList<int>> NSum(int[] nums, int target, int n) {
        Array.Sort(nums);
        return Helper(nums, target, 0, new List<int>(), n);
    }

    public IList<IList<int>> Helper(int[] nums, int target, int start, List<int> items, int n)
    {
        IList<IList<int>> result = new List<IList<int>>();
        if(n > 2)
        {
            for(int i = start; i < nums.Length - (n - 1); i++)
            {
                if(i > start && nums[i - 1] == nums[i])
                { continue; }
                items.Add(nums[i]);
                ((List<IList<int>>)result).AddRange(Helper(nums, target - nums[i], i + 1, items, n - 1));
                items.Remove(nums[i]);
            }
        }
        else
        {
            int left = start;
            int right = nums.Length - 1;
            while(left < right)
            {
                int sum = nums[left] + nums[right];
                if(sum == target)
                {
                    List<int> list = new List<int>(items);
                    list.Add(nums[left]);
                    list.Add(nums[right]);
                    result.Add(list);
                    do
                    {
                        left++;
                    }while(left < right && nums[left - 1] == nums[left]);
                    do
                    {
                        right--;
                    }while(left < right && nums[right] == nums[right + 1]);
                }
                else if(sum < target)
                {
                    left++;
                }
                else
                {
                    right--;
                }
            }
        }
        return result;
    }
}
