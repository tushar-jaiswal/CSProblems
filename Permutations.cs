//Author: Tushar Jaiswal
//Creation Date: 07/05/2016
//Solution for creating all possible permutations of a string.

using System;
using System.Collections.Generic;

class Permutations
{
    static void Main(string[] args)
    {
        string str = "abcd";
        Permute("", new List<char>(str.ToCharArray()));
    }
    
    /// <summary>Prints all possible permutations of the characters in the List after adding them to the passed in string. If the passed in string is empty, this creates all possible permutations of the characters in the passed in List of characters.</summary>
    /// <param name="str">The string to add all the permutations to</param>
    /// <param name="chars">The list of characters whose permutations have to be found</param>
    private static void Permute(string str, List<char> chars)
    {
        if(chars.Count == 0)
        {
            Console.WriteLine(str.ToString());
            return;
        }
        
        for(int i=0; i < chars.Count; i++)
        {
            String cpy = str + chars[i];
            List<char> copy = new List<char>(chars);
            copy.RemoveAt(i);
            Permute(cpy, copy);
        }
    }
}
