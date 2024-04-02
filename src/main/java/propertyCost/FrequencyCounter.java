package propertyCost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class FrequencyCounter {
	
	public static Map<String, Integer> cityFrequency = new HashMap<>();
	
	public FrequencyCounter(List<Property> allProperties) {
		loadDataIntoFrequency(allProperties);
	}
	
	// driver function loadDataIntoFrequency
	private void loadDataIntoFrequency(List<Property> allProperties) {
		// add all cities into frequency counter
		if (allProperties != null) {
			for(Property p : allProperties) {
				String city = p.city.toLowerCase(); // extract city
				cityFrequency.put(city, cityFrequency.getOrDefault(city, 0) + 1); // add city
			}
		}
		else{
			System.out.println("Error in Loading All Properties into Frequency Counter");
		}
	}

	// get Frequency Count for a City 
	public Integer getFrequency(String city){
		return cityFrequency.get(city.toLowerCase());
	}
	
}