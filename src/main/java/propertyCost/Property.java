package propertyCost;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Property implements Serializable {

	float price;
	int bedrooms, bathrooms;
	String address, city, province, provinceId, zipcode, platform, uniqueID, description;
	TypeofHouse houseType;

	enum TypeofHouse {
		Apartment, House
	}

	// used to sort arraylist while printing
	public int getPrice() {
		return (int) this.price;
	}

	public final static String line ="------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	public static void printFilteredProperties(ArrayList<Property> allProperties, String city, int bedrooms,
			int bathroom, TypeofHouse houseType, float minPrice, float maxPrice, List<City> canadacities) {

		List<Property> subListFilteredProperties = new ArrayList<Property>();

		if (houseType == null) {
			subListFilteredProperties = allProperties.stream().filter(p -> p.city.toLowerCase().equals(city))
					.filter(p -> p.bathrooms == bathroom && p.bedrooms == bedrooms)
					.filter(p -> p.price > minPrice && p.price <= maxPrice)
					.sorted(Comparator.comparingInt(Property::getPrice)).collect(Collectors.toList());
		} else {
			subListFilteredProperties = allProperties.stream().filter(p -> p.houseType.equals(houseType))
					.filter(p -> p.city.toLowerCase().equals(city))
					.filter(p -> p.bathrooms == bathroom && p.bedrooms == bedrooms)
					.filter(p -> p.price >= minPrice && p.price <= maxPrice)
					.sorted(Comparator.comparingInt(Property::getPrice)).collect(Collectors.toList());
		}


		List<Property> subListBackUp = subListFilteredProperties; // remove first and last properties
		System.out.println(subListBackUp.size() + " Properties Found.\n");

		// remove first and last element
		subListFilteredProperties.remove(0);
		subListFilteredProperties.remove(subListFilteredProperties.size() - 1);

		float sum = 0;

		if (subListFilteredProperties.size() > 0) {

			try {
				// print all properties except for minimum and maximum properties
				for (Property property : subListFilteredProperties) {
					System.out.println(line);
					property.printProperty();
					sum += property.price;
				}
			} catch (Exception e) {

			}
		}
		
		System.out.println(line);

		// print minimum price property
		System.out.println("Property With Minimum Price:");
		subListBackUp.getFirst().printProperty();
		sum += subListBackUp.getFirst().price;

		System.out.println(line);

		// print maximum price property
		System.out.println("Property With Maximum Price:");
		subListBackUp.getLast().printProperty();
		sum += subListBackUp.getLast().price;

		System.out.println(subListBackUp.size() + " Properties Found.\n");
		
		sum /= subListBackUp.size();

		System.out.println(line);

		System.out.printf("Average Price for all properties found with given criteria CA$ %.2f \n", sum);

		printAverageOfEachCity(allProperties, city, bedrooms, bathroom, houseType, minPrice, maxPrice, canadacities);
	}

	private static void printAverageOfEachCity(ArrayList<Property> allProperties, String city, int bedrooms,
			int bathroom, TypeofHouse houseType, float minPrice, float maxPrice, List<City> canadaCities) {

		Set<String> allCities = City.giveAllCities(canadaCities);
		allCities.remove(city); // remove the city which was searched

		for (String currentCity : allCities) {
			List<Property> subListFilteredProperties = new ArrayList<Property>();

			if (houseType == null) {
				subListFilteredProperties = allProperties.stream().filter(p -> p.city.toLowerCase().equals(currentCity))
						.filter(p -> p.bathrooms == bathroom && p.bedrooms == bedrooms)
						.filter(p -> p.price > minPrice && p.price <= maxPrice)
						.sorted(Comparator.comparingInt(Property::getPrice)).collect(Collectors.toList());
			} else {
				subListFilteredProperties = allProperties.stream().filter(p -> p.houseType.equals(houseType))
						.filter(p -> p.city.toLowerCase().equals(currentCity))
						.filter(p -> p.bathrooms == bathroom && p.bedrooms == bedrooms)
						.filter(p -> p.price >= minPrice && p.price <= maxPrice)
						.sorted(Comparator.comparingInt(Property::getPrice)).collect(Collectors.toList());
			}

			if (!subListFilteredProperties.isEmpty()) {
				float sum = 0;
				for (Property prop : subListFilteredProperties) {
					sum += prop.price;
				}
				sum /= subListFilteredProperties.size();
				System.out.printf("\nAverage Price for all properties found with same filters in " + currentCity
						+ " is CA$ %.2f \n", sum);
			}
			subListFilteredProperties = null;
		}
		System.out.println(line);

	}

	public void printProperty() {
		System.out.println("MLS Number:" + this.uniqueID);
		System.out.println("Price: CA$" + this.price);
		System.out.println("House Type: " + this.houseType);
		System.out.print("Bedrooms: " + this.bedrooms);
		System.out.println(", Bathrooms: " + this.bathrooms);
		System.out.println("City: " + this.city);
		System.out.println("Address: " + this.address);
		if (this.zipcode != null) {
			System.out.println("ZipCode: " + this.zipcode);
		}
		System.out.println("Province: " + this.province);
		System.out.println("Description: " + this.description);
		System.out.println("Platform Scraped from: " + this.platform);
		System.out.println();
	}
}
