package propertyCost;

import java.io.Serializable;

public class Property implements Serializable {
	
	float price; 
	int bedrooms, bathrooms;
	String address, city, province, provinceId, zipcode, platform, uniqueID, description;
	TypeofHouse houseType;
	enum TypeofHouse {
		Apartment, House
	}	
	
	public void printProperty() {
		System.out.println(this.uniqueID);
		System.out.println(this.price);
		System.out.println(this.city);
		System.out.println(this.address);
		System.out.println(this.bedrooms);
		System.out.println(this.bathrooms);
		System.out.println(this.province);
		System.out.println(this.provinceId);
		System.out.println(this.platform);
		System.out.println(this.zipcode);
		System.out.println(this.houseType);
		System.out.println();
	}
	
}
