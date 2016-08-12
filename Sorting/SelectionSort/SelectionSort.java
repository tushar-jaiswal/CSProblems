/**
* Execution class that illustrates the use of the sorting methods.
* @author  Tushar Jaiswal
*/

class Solution 
{
    public static void main(String[] args) 
    {
        int[] arr = {2,5,7,3,7,2,1,8,-3};
        Sorting.selectionSort(arr);
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
    * This selection sort function sorts an input array of integers in increasing order.
    * @param  The input array of integers.
    * @return Nothing.
    */
    public static void selectionSort(int[] arr) 
    {
        for(int i = 0; i < arr.length - 1; i++)
        {
            int min = i;
            for(int j = i + 1; j < arr.length; j++)
            {
                if(arr[j] < arr[min])
                {
                    min = j;
                }
            }
            
            if(min != i)
            {
                int temp = arr[i];
                arr[i] = arr[min];
                arr[min] = temp;
            }
        }
    }
}
