/**
* Execution class that illustrates the use of the sorting methods.
* @author  Tushar Jaiswal
*/

class Solution 
{
    public static void main(String[] args) 
    {
        int[] arr = {2,5,7,3,7,2,1,8,-3};
        Sorting.insertionSort(arr);
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
    * @param  The input array of integers.
    * @return Nothing.
    */
    public static void insertionSort(int[] arr) 
    {
        for(int i = 1; i < arr.length; i++) 
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
