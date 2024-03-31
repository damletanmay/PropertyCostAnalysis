package propertyCost;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {

    // Validate Canadian postal code with restricted letters
    public static boolean isValidPostalCode(String postalCode) {
        String regex = "^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]\\d[A-Za-z] ?\\d[A-Za-z]\\d$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(postalCode);
        return matcher.matches();
    }

    // Validate if it is a city 
    public static boolean isCity(String city) {
        String regex = "[A-Za-z ]+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(city);
        return matcher.matches();
    }
}
