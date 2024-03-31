package propertyCost;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class Test {

	public static ArrayList<Property> allProperties = new ArrayList<Property>();
	public static String userDirectory = System.getProperty("user.dir"); // getting user path

	public static void main(String[] args) {
		
		ArrayList<City> x = City.loadCityData();
		
		for( String w: City.giveAllCities(x)) {
			System.out.println(w);
		}
	
	}

}
