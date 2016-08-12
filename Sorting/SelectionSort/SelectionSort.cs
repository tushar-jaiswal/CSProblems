//Author: Tushar Jaiswal
//Creation Date: 08/11/2016

using System;

public class Solution
{
    static void Main(string[] args)
    {
        int[] arr = {2,5,7,3,7,2,1,8,-3};
        Sorting.SelectionSort(arr);
        foreach(int i in arr)
        {
            Console.Write(i + " ");
        }
    }
}

public class Sorting
{
    /// <summary>
    /// This selection sort function sorts an input array of integers in increasing order.
    /// </summary>
    /// <param name="arr">the input array of integers</param>
    public static void SelectionSort(int[] arr)
    {
        for(int i = 0; i < arr.Length - 1; i++)
        {
            int min = i;
            for(int j = i + 1; j < arr.Length; j++)
            {
                if(arr[j] < arr[min])
                {
                    min = j;
                }
            }
            
            if(min != i)
            {
                Swap(ref arr[i], ref arr[min]);
            }
        }
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
