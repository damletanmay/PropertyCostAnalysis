package propertyCost;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.opencsv.CSVReader;

public class City {

	public static String osName = System.getProperty("os.name");
	final static int numberOfCities = 10;

	String city, provinceId, province;
	String zipCode[];

	public static Set<String> giveAllCities(List<City> citiesFromMain){
		
		Set<String> allCities = new HashSet<String>();
		
		for (City city:citiesFromMain) {
			allCities.add(city.city.toLowerCase());
		}
		return allCities;
	}
	
	public static ArrayList<City> loadCityData() {

		String userDirectory = System.getProperty("user.dir"); // getting user path
		String windowsPath = "\\Cities Data\\cities.csv";
		String linuxPath = "/Cities Data/cities.csv";
		String filePath;
		
		if (osName.toLowerCase().contains("windows")) {
			filePath = userDirectory + windowsPath;
		}
		else {			
			filePath = userDirectory + linuxPath;
		}
		
		ArrayList<City> canadaCities = new ArrayList<City>(); // currently 10 cities in the csv

//		long end, start = System.currentTimeMillis();
		
		try {
			
			FileReader cityData = new FileReader(filePath);
			CSVReader scanner =  new CSVReader(cityData);

			scanner.readNext(); // skipping fields (row 0 in csv)
		
			String [] nextLine;  
			while ((nextLine = scanner.readNext())!= null) {
				
				
				City city = new City(); // initializing object
				city.city = nextLine[0]; // ascii city
				city.provinceId = nextLine[1]; // province id
				city.province = nextLine[2]; // province 

				StringTokenizer st = new StringTokenizer(nextLine[3]); // postal code
				
				city.zipCode = new String[st.countTokens()]; // initializing zipcode array

				int j = 0; // index for zipcode

				// to store all zip codes
				while (st.hasMoreTokens()) {
					city.zipCode[j] = st.nextToken();
					j++; // zipcode index
				}
				canadaCities.add(city);
			}

//			end = System.currentTimeMillis();

//			System.out.println(end - start + " milliseconds to load city data");

		} catch (Exception e) {
			System.out.println("Error in loading data");
			e.printStackTrace();
		}

		return canadaCities;
	}
}
