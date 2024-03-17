package propertyCost;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FrequencyCounter {

	public static void main(String[] args) {
		// Directory where .dat files are stored
		
		String dataDirectory = "D:\\UWINDSOR\\ACC\\Final Project\\PropertyCostAnalysis-main\\Saved Objects";

		// Get a list of all .dat files in the directory
		File[] datFiles = new File(dataDirectory).listFiles((dir, name) -> name.endsWith(".dat"));

		Scanner sc = new Scanner(System.in);
		
		System.out.println("Enter the keyword:");
		String city = sc.nextLine();

		// Perform frequency count of cities
		Map<String, Integer> cityFrequency = countCitiesFrequency(city, datFiles);

		// Display the frequency count
		for (Map.Entry<String, Integer> entry : cityFrequency.entrySet()) {
			System.out.println("Occurences of the keyword '" + entry.getKey() + "': " + entry.getValue());
		}
		sc.close();
	}

	// Function to perform frequency count of cities
	public static Map<String, Integer> countCitiesFrequency(String city, File[] datFiles) {
		Map<String, Integer> cityFrequency = new HashMap<>();

		for (File datFile : datFiles) {
			try (BufferedReader reader = new BufferedReader(new FileReader(datFile))) {
				while ((reader.readLine()) != null) {
					// Assuming each line contains city information
					cityFrequency.put(city, cityFrequency.getOrDefault(city, 0) + 1);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return cityFrequency;
	}
}