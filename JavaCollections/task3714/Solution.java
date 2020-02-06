package com.javarush.task.task37.task3714;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/* 
Древний Рим
*/
public class Solution {

    private static HashMap<String, Integer> RtoA = new HashMap<>();

    static {
        RtoA.put("I", 1);
        RtoA.put("V", 5);
        RtoA.put("X", 10);
        RtoA.put("L", 50);
        RtoA.put("C", 100);
        RtoA.put("D", 500);
        RtoA.put("M", 1000);
        RtoA.put("IV", 4);
        RtoA.put("IX", 9);
        RtoA.put("XL", 40);
        RtoA.put("XC", 90);
        RtoA.put("CD", 400);
        RtoA.put("CM", 900);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input a roman number to be converted to decimal: ");
        String romanString = bufferedReader.readLine();
        String test = "LXXIII";//73
        String test2 = "CDXCIXIII";//502
        String test3 = "XVIII";//18
        System.out.println();
        System.out.println("Is entered roman number is valid? " + isValidRomanNumber(romanString));
        System.out.println("Conversion result equals " + romanToInteger(romanString));

    }

    public static int romanToInteger(String s) {
        String[] romanNumbers = s.toUpperCase().split("");
        int arabicInt = 0;

        System.out.print("romanNumbers ");
        System.out.println(Arrays.toString(romanNumbers));
        System.out.println("==================================================================================");

        for (int i = 0; i < romanNumbers.length; i++) {
            int firstIdx = i;
            int secondIdx = firstIdx + 1;

            int firstNumber = 0;
            int secondNumber = 0;

            if (firstIdx <= romanNumbers.length) {
                System.out.println("Get first, second numbers ++++++++++++++++++++++++++++++++++++++");
                for (Map.Entry<String, Integer> n : RtoA.entrySet()) {
                    //get first and second numbers
                    if (romanNumbers[firstIdx].equals(n.getKey())) {
                        System.out.println("firstNumber " + romanNumbers[firstIdx] + " : " + n.getValue());
                        firstNumber = n.getValue();
                    }
                    if (secondIdx < romanNumbers.length && romanNumbers[secondIdx].equals(n.getKey())) {
                        System.out.println("secondNumber " + romanNumbers[secondIdx] + " : " + n.getValue());
                        secondNumber = n.getValue();
                    }
                }
            }
            if (firstNumber < secondNumber) {
                String fs = romanNumbers[firstIdx] + romanNumbers[secondIdx];
                i = secondIdx;
                for (Map.Entry<String, Integer> rule : RtoA.entrySet()) {
                    if (fs.equals(rule.getKey())) {
                        System.out.println("rule #first < second# " + arabicInt + " + " + rule.getValue());
                        arabicInt = arabicInt + rule.getValue();
                        System.out.println("Number for rule #second > first# ");
                        break;
                    }
                }
            } else {
                System.out.println("rule #first >= second# " + arabicInt + " + " + firstNumber);
                arabicInt = arabicInt + firstNumber;
            }
        }
        return arabicInt;
    }

    private static boolean isValidRomanNumber(String romanNumber) {
        //TODO implement method for validate roman number
        return true;
    }
}
