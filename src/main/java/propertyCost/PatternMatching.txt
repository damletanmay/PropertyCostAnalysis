package propertyCost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternMatching {

	// save data for each city in count by city
	public static HashMap<String,HashMap<String, Integer>> countByCity = new HashMap<>();
	public static HashMap<String, Integer> tempHashMap = new HashMap<>();
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path

	// constructor
	public PatternMatching(PageRanking pageRanking, List<City> canadaCities) {
		
		File savedObject = new File(userDirectory+"/Saved Objects/patterns.dat");
		
		// if file doesn't exist then save dat file or else read dat file
		if(!savedObject.exists()) {
			initalizeCountByCity(canadaCities);
			saveDatFile(savedObject);
		}
		else {
			countByCity = readDatFile(savedObject);
		}	
	}
	
	// print email and phone number
	public void printEmailPhoneNumber(String userInput) {
		
		tempHashMap = countByCity.get(userInput.toLowerCase());
		
		if (tempHashMap != null) {
			System.out.println(tempHashMap.get("Email") + " - Emails Found in files");
			System.out.println(tempHashMap.get("Phone Number") +  " - Phone Number Found in files");			
		}
		
	}
	
	// populate count by city
	private void initalizeCountByCity(List<City> canadaCities) {
		Set<String> allCities = City.giveAllCities(canadaCities);
		
		// for each city, for each file, find phone numbers and email
		for (String city : allCities) {
			HashMap<String, Integer> allFiles = PageRanking.pageRankingST.getValue(city);
			
			tempHashMap = new HashMap<>(); // reset hashmap for new city
			
			// initialize 
			tempHashMap.put("Email", 0);
			tempHashMap.put("Phone Number", 0);
			
			for (Entry<String, Integer> file : allFiles.entrySet()) {

				// for each increment email and phone numbers found 
				File fileName = new File(file.getKey());
				searchPhoneNumbers(fileName);
				searchEmail(fileName);
				
			}
			countByCity.put(city, tempHashMap); // add city and it's patterns found
		}
	}

	// Validate email address
	public static void extractEmail(String email) {
		String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);

		while (matcher.find()) {
			tempHashMap.put("Email", tempHashMap.getOrDefault("Email", 0) + 1); // add email count
		}
	}

	// search for phone numbers
	private static void searchPhoneNumbers(File file) {

		if (file.getName().endsWith(".html")) {
			try {
				String htmlContent = readFile(file);
				extractPhoneNumbers(htmlContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// search for email
	private static void searchEmail(File file) {

		if (file.getName().endsWith(".html")) {
			try {
				String htmlContent = readFile(file);
				extractEmail(htmlContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// read html file
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

	// extract phone numbers
	private static void extractPhoneNumbers(String text) {
		Pattern pattern = Pattern.compile("\\b\\d{3}-\\d{3}-\\d{4}\\b"); // mobile pattern
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			tempHashMap.put("Phone Number", tempHashMap.getOrDefault("Phone Number", 0) + 1); // add Phone Number count
		}
	}
	
	// read saved dat files which should have countByCity hash map saved as a
	// dat file at savedObject path
	private static HashMap<String,HashMap<String, Integer>> readDatFile(File savedObject) {
		try {
			FileInputStream inputStreamDat = new FileInputStream(savedObject);
			ObjectInputStream objectReader = new ObjectInputStream(inputStreamDat);
			Object object = objectReader.readObject();
			HashMap<String,HashMap<String, Integer>> savedData = (HashMap<String,HashMap<String, Integer>>) object;

			objectReader.close();
			inputStreamDat.close();
			return savedData;
		} catch (Exception e) {
			System.out.println("Problem in reading dat files");
			return null;
		}
	}
	
	// save countByCity hash map to savedObject path
	private static void saveDatFile(File savedObject) {

		try {
			FileOutputStream outputStream = new FileOutputStream(savedObject);
			ObjectOutputStream fileWriterDat = new ObjectOutputStream(outputStream);

			// write all objects to file
			fileWriterDat.writeObject(countByCity);
			fileWriterDat.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Object Saving Error");
		}
	}
	
}
