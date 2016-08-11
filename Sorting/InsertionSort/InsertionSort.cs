//Author: Tushar Jaiswal
//Creation Date: 08/11/2016

using System;

class Solution
{
    static void Main(string[] args)
    {
        int[] arr = {2,5,7,3,7,2,1,8,-3};
        Sorting.InsertionSort(arr);
        foreach(int i in arr)
        {
            Console.Write(i + " ");
        }
    }
}

public class Sorting
{
    /// <summary>
    /// This insertion sort function sorts an input array of integers in increasing order.
    /// </summary>
    /// <param name="arr">the input array of integers</param>
    public static void InsertionSort(int[] arr)
    {
        for(int i = 1; i < arr.Length; i++)
        {
            int curr = arr[i];
            int j = i - 1;
            while(j >= 0 && arr[j] > curr)
            {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = curr;
        }
    }  
}
