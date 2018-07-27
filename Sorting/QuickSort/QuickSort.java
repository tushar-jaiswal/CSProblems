/**
* Execution class that illustrates the use of the sorting methods.
* @author  Tushar Jaiswal
*/

import java.util.concurrent.ThreadLocalRandom;

class Solution 
{
    public static void main(String[] args) 
    {
        int[] arr = {2,5,7,3,7,2,1,8,-3};
        Sorting.quickSort(arr, 0, arr.length - 1);
        for (int i : arr) 
        {
            System.out.print(i + " ");
        }
    }
}

/**
* Class with different sorting methods.
* @author  Tushar Jaiswal
*/
class Sorting 
{
    /**
    * This insertion sort function sorts an input array of integers in increasing order.
    * @param arr The input array of integers.
    * @param low Lower bound of the array to operate on
    * @param high Higher bound of the array to operate on
    * @return Nothing.
    */
    public static void quickSort(int[] arr, int low, int high) 
    {
        if (low < high)
        {
            int pivot = partition(arr, low, high);

            quickSort(arr, low, pivot - 1);
            quickSort(arr, pivot + 1, high);
        }
    }
    
    /**
    * This function generates a random pivot and arranges all elements lesser than or equal to itself before its position and all elements greater than itself after its position.
    * @param arr The input array of integers.
    * @param low Lower bound of the array to operate on.
    * @param high Higher bound of the array to operate on.
    * @return The index of the pivot element.
    */
    private static int partition(int[] arr, int low, int high)
    {
        int pivot = ThreadLocalRandom.current().nextInt(low, high + 1);
        
        int temp = arr[pivot];
        arr[pivot] = arr[high];
        arr[high] = temp;
        
        int i = low - 1;

        for(int j = low; j < high; j++)
        {
            if(arr[j] <= arr[high])
            {
                i++;
                temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return (i + 1); 
    }
}