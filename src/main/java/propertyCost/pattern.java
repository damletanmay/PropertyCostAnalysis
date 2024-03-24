package propertyCost;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class pattern {
    public static void main(String[] args) {

    	File directory = new File("Scraped Data/Realtor");

        if (directory.isDirectory()) {
            searchPhoneNumbers(directory);
        } else {
            System.out.println("Specified path is not a directory.");
        }
    }
    
    // Validate email address
    public static String extractEmail(String email) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.group();
    }

    private static void searchPhoneNumbers(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    searchPhoneNumbers(file); // Recursively search sub directories
                } else if (file.getName().endsWith(".html")) {
                    try {
                        String htmlContent = readFile(file);
                        extractPhoneNumbers(htmlContent, file);
                        break; // TOD0: remove later
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static String readFile(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    private static void extractPhoneNumbers(String text, File file) {
    	
    	Pattern pattern = Pattern.compile("\\b\\d{3}-\\d{3}-\\d{4}\\b"); // mobile pattern
   
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            System.out.println("Phone number found in file " + file.getName() + ": " + matcher.group(0));
        }
    }
}
