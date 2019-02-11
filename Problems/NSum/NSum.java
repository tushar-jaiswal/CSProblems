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

class Solution {
    public List<List<Integer>> fourSum(int[] nums, int target, int n) {
        Arrays.sort(nums);
        return NSum(nums, target, 0, new ArrayList<Integer>(), n);
    }

    public List<List<Integer>> NSum(int[] nums, int target, int start, List<Integer> items, int n)
    {
        List<List<Integer>> result = new ArrayList<List<Integer>>();
        if(n > 2)
        {
            for(int i = start; i < nums.length - (n - 1); i++)
            {
                if(i > start && nums[i - 1] == nums[i])
                { continue; }
                items.add(nums[i]);
                result.addAll(NSum(nums, target - nums[i], i + 1, items, n - 1));
                items.remove((Integer)nums[i]);
            }
        }
        else
        {
            int left = start;
            int right = nums.length - 1;
            while(left < right)
            {
                int sum = nums[left] + nums[right];
                if(sum == target)
                {
                    List list = new ArrayList<Integer>(items);
                    list.add(nums[left]);
                    list.add(nums[right]);
                    result.add(list);
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
