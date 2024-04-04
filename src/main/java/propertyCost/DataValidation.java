package propertyCost;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {

    // Method to check & validat Canadian postal_code 
    public static boolean isValidPostalCode(String postal_Code) {
        // Reg_expre for validating postal code format
        String regexPostalCode = "^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]\\d[A-Za-z] ?\\d[A-Za-z]\\d$";
        Pattern patternPostalCode = Pattern.compile(regexPostalCode, Pattern.CASE_INSENSITIVE);
        Matcher match = patternPostalCode.matcher(postal_Code);
        return match.matches(); // Return true if the postal code matches the pattern
    }

    // Method to validate if a string represents a city
    public static boolean isCity(String cities) {
        // Reg-ex for validatng city name format
        String regexCity = "[A-Za-z ]+";
        Pattern patternCity = Pattern.compile(regexCity);
        Matcher match = patternCity.matcher(cities);
        return match.matches(); // Return true if the string represents a valid city name
    }
}
