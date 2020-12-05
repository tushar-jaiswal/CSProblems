//Author: Tushar Jaiswal
//Creation Date: 12/05/2020

/*Given a binary tree, find the maximum sum of a subtree.

Example 1:
Input: root = [1,2,3]
Output: 6

Example 2:
Input: root = [-10,9,20,null,null,15,7]
Output: 42

Example 3:
Input: root = [5,4,8,11,null,13,4,7,2,null,null,null,1]
Output: 55
*/

/*Runtime Complexity: O(number of nodes in tree)
Space Complexity: O(1)*/

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */

class Solution {
    public int maxSum(TreeNode root) {
        if (root == null) {
            return Integer.MIN_VALUE;
        }
        Sum sum = helper(root);
        return Math.max(sum.sumWithRoot, sum.sumWithoutRoot);
    }

    private Sum helper(TreeNode root) {
        Sum left = root.left == null ? null : helper(root.left);
        Sum right = root.right == null ? null : helper(root.right);
        int sumWithRoot = 0;
        int sumWithoutRoot = 0;

        if (left == null && right == null) {
            sumWithRoot = root.val;
            sumWithoutRoot = root.val;
        } else if (left == null) {
            sumWithRoot = Math.max(root.val, root.val + right.sumWithRoot);
            sumWithoutRoot = Math.max(right.sumWithRoot, right.sumWithoutRoot);
        } else if (right == null) {
            sumWithRoot = Math.max(root.val, root.val + left.sumWithRoot);
            sumWithoutRoot = Math.max(left.sumWithRoot, left.sumWithoutRoot);
        } else {
            int leftSumWithRoot = Math.max(root.val, root.val + left.sumWithRoot);
            int rightSumWithRoot = Math.max(root.val, root.val + right.sumWithRoot);
            int bothSumWithRoot = Math.max(root.val, root.val + left.sumWithRoot + right.sumWithRoot);
            sumWithRoot = Math.max(Math.max(leftSumWithRoot, rightSumWithRoot), bothSumWithRoot);

            int leftSumWithoutRoot = Math.max(left.sumWithRoot, left.sumWithoutRoot);
            int rightSumWithoutRoot = Math.max(right.sumWithRoot, right.sumWithoutRoot);
            sumWithoutRoot = Math.max(leftSumWithoutRoot, rightSumWithoutRoot);
        }
        return new Sum(sumWithRoot, sumWithoutRoot);
    }
}

class Sum {
    int sumWithRoot;
    int sumWithoutRoot;

    public Sum(int sumWithRoot, int sumWithoutRoot) {
        this.sumWithRoot = sumWithRoot;
        this.sumWithoutRoot = sumWithoutRoot;
    }
}
