package propertyCost;

import java.io.*;
import java.util.*;

public class SearchFrequency {
	
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path

	public static final File filePath = new File(userDirectory + "/Saved Objects/allProperties.dat");

	
    public static HashMap<City, Integer> createSearchFrequencyMap() {
        // Load city data
        ArrayList<City> canadaCities = City.loadCityData();
        
        
        // Initialize the HashMap with cities mapped to 0
        HashMap<City, Integer> searchFrequencyMap = new HashMap<>();
        for (City city : canadaCities) {
            searchFrequencyMap.put(city, 0);
        }
        return searchFrequencyMap;
    }
    
    
    public static void incrementCityValue(HashMap<City, Integer> searchFrequencyMap, City cityToUpdate) {
        // Increment the value of the city in the search frequency map
        if (searchFrequencyMap.containsKey(cityToUpdate)) {
            int currentValue = searchFrequencyMap.get(cityToUpdate);
            searchFrequencyMap.put(cityToUpdate, currentValue + 1);
            System.out.println("Seacrh: " + cityToUpdate.city + ", Search Frequency: " + searchFrequencyMap.get(cityToUpdate));
            
        } 
    }
    
    

    
    

    public static void main(String[] args) {
        // Declare
    	HashMap<City, Integer> searchFrequencyMap = createSearchFrequencyMap();

        Scanner scanner = new Scanner(System.in);
        boolean continueLoop = true;

        while (continueLoop) {
            System.out.print("Enter the city name :");
            String cityName = scanner.nextLine().trim();

            if (cityName.equalsIgnoreCase("exit")) {
                continueLoop = false;
                break;
            }

            City cityToUpdate = null;
            for (City city : searchFrequencyMap.keySet()) {
                if (city.city.equalsIgnoreCase(cityName)) {
                    cityToUpdate = city;
                    break;
                }
            }

            if (cityToUpdate != null) {
                incrementCityValue(searchFrequencyMap, cityToUpdate);
                System.out.println();
            } else {
                System.out.println("City not found.");
            }
        }
        scanner.close();
    }
}
