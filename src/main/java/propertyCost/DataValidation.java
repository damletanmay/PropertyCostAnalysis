import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataValidation {

    // Validate email address
    public static boolean isValidEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Validate phone number
    public static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^(?:\\+\\d{1,3}[- ]?)?\\d{10}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

 // Validate Canadian postal code with restricted letters
    public static boolean isValidCanadianPostalCode(String postalCode) {
        String regex = "^[ABCEGHJKLMNPRSTVXYabceghjklmnprstvxy]\\d[A-Za-z] ?\\d[A-Za-z]\\d$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(postalCode);
        return matcher.matches();
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Validate email
        String email;
        do {
            System.out.print("Enter email address: ");
            email = scanner.nextLine();
            if (!isValidEmail(email)) {
                System.out.println("Invalid email address. Please enter a valid email.");
            }
        } while (!isValidEmail(email));
        System.out.println("Valid email address.");

        // Validate phone number
        String phoneNumber;
        do {
            System.out.print("Enter phone number: ");
            phoneNumber = scanner.nextLine();
            if (!isValidPhoneNumber(phoneNumber)) {
                System.out.println("Invalid phone number. Please enter a valid phone number.");
            }
        } while (!isValidPhoneNumber(phoneNumber));
        System.out.println("Valid phone number.");

        // Validate Canadian postal code
        String postalCode;
        do {
            System.out.print("Enter Canadian postal code: ");
            postalCode = scanner.nextLine();
            if (!isValidCanadianPostalCode(postalCode)) {
                System.out.println("Invalid Canadian postal code. Please enter a valid postal code.");
            }
        } while (!isValidCanadianPostalCode(postalCode));
        System.out.println("Valid Canadian postal code.");

        scanner.close();
    }
}
