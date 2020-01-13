package com.javarush.task.task37.task3714;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/* 
Древний Рим
*/
public class Solution {

    private static HashMap<Character, Integer> RtoA = new HashMap<>();
    private static HashMap<String, Integer> subtractionRule = new HashMap<>();

    static {
        RtoA.put('I', 1);
        RtoA.put('V', 5);
        RtoA.put('X', 10);
        RtoA.put('L', 50);
        RtoA.put('C', 100);
        RtoA.put('D', 500);
        RtoA.put('M', 1000);

        subtractionRule.put("IV", 4);
        subtractionRule.put("IX", 9);
        subtractionRule.put("XL", 40);
        subtractionRule.put("XC", 90);
        subtractionRule.put("CD", 400);
        subtractionRule.put("CM", 900);
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Input a roman number to be converted to decimal: ");
        String romanString = bufferedReader.readLine();

        System.out.println();
        System.out.println("Is entered roman number is valid? " + isValidRomanNumber(romanString));
        System.out.println("Conversion result equals " + romanToInteger(romanString));

    }

    public static int romanToInteger(String s) {
        char[] romanNumbers = s.toUpperCase().toCharArray();
        int arabicInt = 0;                                              //Integer

        System.out.print("romanNumbers ");
        System.out.println(romanNumbers);
        System.out.println("==================================================================================");

        for (int i = 0; i < romanNumbers.length; i++) {
            int firstIdx = i;
            int secondIdx = firstIdx + 1;
            int thirdIdx = secondIdx + 1;
            int fourthIdx = thirdIdx + 1;

            int subNumber = 0;
            int firstNumber = 0;
            int secondNumber = 0;
            int thirdNumber = 0;
            int fourthNumber = 0;

            System.out.println("Get first, second, third and fourth numbers ++++++++++++++++++++++++++++++++++++++");
            for (Map.Entry<Character, Integer> n : RtoA.entrySet()) {
                //get first, second, third and forth numbers
                if (firstIdx <= romanNumbers.length & romanNumbers[firstIdx] == n.getKey()) {
                    System.out.println("firstNumber " + romanNumbers[firstIdx] + " : " + n.getValue());
                    firstNumber = n.getValue();
                }
                if (secondIdx < romanNumbers.length & romanNumbers[secondIdx] == n.getKey()) {
                    System.out.println("secondNumber " + romanNumbers[secondIdx] + " : " + n.getValue());
                    secondNumber = n.getValue();
                }
                if (thirdIdx < romanNumbers.length & romanNumbers[thirdIdx] == n.getKey()) {
                    System.out.println("thirdNumber " + romanNumbers[thirdIdx] + " : " + n.getValue());
                    thirdNumber = n.getValue();
                }
                if (fourthIdx < romanNumbers.length & romanNumbers[fourthIdx] == n.getKey()) {
                    System.out.println("forthNumber " + romanNumbers[fourthIdx] + " : " + n.getValue());
                    fourthNumber = n.getValue();
                }
            }
            //last number summing to the result
            if (secondIdx >= romanNumbers.length) {
                arabicInt = arabicInt + firstNumber;
            }
            System.out.println();

            //checking subtraction rules
            if (secondIdx < romanNumbers.length) {
                if (firstNumber < secondNumber) {
                    System.out.println("rule #first < second# ============================================================");
                    String fs = Character.toString(romanNumbers[firstIdx]) + Character.toString(romanNumbers[secondIdx]);

                    for (Map.Entry<String, Integer> rule : subtractionRule.entrySet()) {
                        if (fs.equals(rule.getKey())) {
                            subNumber = rule.getValue();
                            arabicInt = arabicInt + subNumber;
                            System.out.println("Number for rule #second > first# " + subNumber);
                            break;
                        }
                    }
                    //switch index in the loop. So we skip checked numbers.
                    i = secondIdx;
                }

                if (firstNumber >= secondNumber) {
                    System.out.println("rule #first >= second# ===========================================================");
                    subNumber = firstNumber + secondNumber;
                    i = secondIdx;

                    //rule: XXX or XII
                    if (thirdIdx < romanNumbers.length & secondNumber == thirdNumber) {
                        System.out.println("rule #first >= second & second == third# =========================================");
                        subNumber = subNumber + thirdNumber;
                        i = thirdIdx;
                        System.out.println("Number for rule #first >= second & second == third# " + subNumber);
                    }

                    //rule: XIII
                    if (fourthIdx < romanNumbers.length & secondNumber == fourthNumber) {
                        System.out.println("rule #first >= second & second == fourth# ========================================");
                        subNumber = subNumber + fourthNumber;
                        i = fourthIdx;
                        System.out.println("Number for rule #first > second & second == fourth# " + subNumber);
                    }
                    System.out.println("Number for rules #first >= second & second == N# " + subNumber);
                    arabicInt = arabicInt + subNumber;
                }
            }
        }
        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
        return arabicInt;
    }

    private static boolean isValidRomanNumber(String romanNumber) {
        //TODO implement method for validate roman number (OPTIONAL)
        return true;
    }
}
