package propertyCost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import propertyCost.Property.TypeofHouse;

public class ScrapperCrawler {

	public static String userDirectory = System.getProperty("user.dir"); // getting user path
	public static String osName = System.getProperty("os.name"); // get OS name

	public static ArrayList<City> canadaCities = new ArrayList<City>(); // cities are loaded into canadaCities object
	public static ArrayList<Property> allProperties = new ArrayList<Property>();

	public static String[] platforms = { "Realtor", "Zillow", "Zolo" }; // all the platforms

	public static final String[][] platformsLink = { {
			"https://www.realtor.ca/map#ZoomLevel=4&Center=64.942702%2C-108.739845&LatitudeMax=73.67762&LongitudeMax=-56.00547&LatitudeMin=51.99920&LongitudeMin=-161.47422&Sort=6-D&GeoName=Vancouver%2C%20BC&PropertyTypeGroupID=1&TransactionTypeId=2&PropertySearchTypeId=0&Currency=CAD",
			"Realtor", "txtMapSearchInput", "mapSearchIcon" }, }; // links of each platform with details

	public static List<String> fileDeleteFail = new ArrayList<String>();
	public static int faultyPages = 0;

	public static File savedObject = new File(userDirectory + "/Saved Objects/allProperties.dat");

	private static void makeFolders() {

		// make scraped data folder
		if (!new File(userDirectory + "/Scraped Data").exists()) {
			// if scraped data doesn't exist create new folder
			new File(userDirectory + "/Scraped Data").mkdirs();
		}

		// make cities data folder
		if (!new File(userDirectory + "/Cities Data").exists()) {
			// if scraped data doesn't exist create new folder
			new File(userDirectory + "/Cities Data").mkdirs();
		}

		// make saved objects data folder
		if (!new File(userDirectory + "/Saved Objects").exists()) {
			// if scraped data doesn't exist create new folder
			new File(userDirectory + "/Saved Objects").mkdirs();
		}

		// make folders for each platform
		for (String platform : platforms) {

			if (!new File(userDirectory + String.format("/Scraped Data/%s", platform)).exists()) {
				// if platform doesn't exist create new folder
				new File(userDirectory + String.format("/Scraped Data/%s", platform)).mkdirs();
			}
			// make folders for each city inside each platform
			for (City city : canadaCities) {

				if (!new File(userDirectory
						+ String.format("/Scraped Data/%s/%s", platform, city.city + ", " + city.provinceId))
						.exists()) {
					// if platform doesn't exist create new folder
					new File(userDirectory
							+ String.format("/Scraped Data/%s/%s", platform, city.city + ", " + city.provinceId))
							.mkdirs();
				}
			}

		}
	}

	// a web crawler that uses edge browser to scrape data.
	public static void webCrawler(List<City> list) {

		// new edge driver
		WebDriver driver = new EdgeDriver();

		// looping over all platforms link
		for (int j = 0; j < platformsLink.length; j++) {

			// unpacking data from platformsLink
			String link = platformsLink[j][0];
			String platform = platformsLink[j][1];
			String identifierSearch = platformsLink[j][2];
			String identifierButton = platformsLink[j][3];
			driver.get(link);

			timeOut(16); // so that I can by pass bot verification

			// get data for all the cities for a platform
			for (City city : list) {

				String searchQuery = city.city + ", " + city.provinceId;
				driver.manage().window().maximize();

				WebElement searchElement, button;

				String windowsPath = String.format("%s\\Scraped Data\\%s\\%s\\", userDirectory, platform, searchQuery);

				String linuxPath = String.format("%s/Scraped Data/%s/%s", userDirectory, platform, searchQuery);

				if (platform.equals("Realtor")) {
					scrapeRealtorData(driver, identifierSearch, identifierButton, searchQuery, windowsPath, linuxPath);
				}
			}
			driver.close();
		}
	}

	private static void scrapeRealtorData(WebDriver driver, String identifierSearch, String identifierButton,
			String searchQuery, String windowsPath, String linuxPath) {

		try {
			// get search element and button
			WebElement searchElement, button;
			// search element and put keys
			searchElement = driver.findElement(By.id(identifierSearch));

			// enter our query i.e. city name + province id into the search bar
			searchElement.clear();
			searchElement.click();
			searchElement.sendKeys(searchQuery);

			// click button
			button = driver.findElement(By.id(identifierButton));
			button.click();
		} catch (Exception e) {
			System.out.println("Main Page not found");
			return;
		}

		// disable auto complete
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(
				"document.getElementById('AutoCompleteCon-txtMapSearchInput').setAttribute('class', 'hidden')");

		timeOut(4);

		// get all listings in a particular city
		List<WebElement> listings = driver.findElements(By.className("cardCon"));

		for (WebElement listing : listings) {

			timeOut(2);
			try {
				listing.click(); // click a listing
			} catch (Exception e) {
				System.out.println("Listing not clickable");
			}

			// get main window's window handle
			String base = driver.getWindowHandle();

			// make a set of all window handles
			Set<String> set = driver.getWindowHandles();

			// remove the main window handle from a base
			set.remove(base);
			assert set.size() == 1; // make set size 1

			for (String otherWindow : set) {
				driver.switchTo().window(otherWindow);
			}

			timeOut(6);

			// get html source code of a particular listing
			String html = driver.getPageSource();

			// get a unique id to save by file name
			String uniqueID;
			try {
				uniqueID = driver.findElement(By.id("MLNumberVal")).getText();
				// make a file path
				String filePath;

				if (osName.contains("Windows")) {
					filePath = windowsPath + uniqueID + ".html";
				} else {
					filePath = linuxPath + uniqueID + ".html";
				}

				// save a file
				saveHTMLFile(html, filePath);

			} catch (Exception e) {
				System.out.println("Listing Not Found");
				continue;
			}
			try {
				driver.close(); // Close a tab
				driver.switchTo().window(base); // switch to main base tab
				js.executeScript("mapSidebarBodyCon.scrollBy(0,100)"); // scroll by 100 pixels
			} catch (Exception e) {
				System.out.println("Listing Not Found");
				continue;
			}

		}
	}

	private static void saveHTMLFile(String html, String filePath) {
		try {
			System.out.println(filePath); // TODO : COMMENT LATER
			File file = new File(filePath);
			file.createNewFile();
			Writer fileWriter = new FileWriter(file, false); // overwrites file

			fileWriter.write(html);
			fileWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
		}
	}

	private static void timeOut(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds); // sleep 7 seconds
		} catch (InterruptedException e) {
			System.out.println("Timeout error");
		}
	}

	// a function that returns a set of strings which has information about all the
	// files in a folder
	public static List<String> listFiles(String dir) {
		return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
				.collect(Collectors.toList());
	}

	private static void loadScrapedDataIntoClass(ArrayList<City> cities) {

		ArrayList<Property> properties = new ArrayList<Property>();

		// for each platform, for each city, for each file inside a city save data of
		// all those files
		for (String platform : platforms) {

			// for each cities
			for (City city : cities) {

				String windowsPath = String.format("%s\\Scraped Data\\%s\\%s\\", userDirectory, platform,
						city.city + ", " + city.provinceId);
				String linuxPath = String.format("%s/Scraped Data/%s/%s/", userDirectory, platform,
						city.city + ", " + city.provinceId);

				String filePath;

				if (osName.contains("Windows")) {
					filePath = windowsPath;
				} else {
					filePath = linuxPath;
				}
//				System.out.println(city.city);

				// for each file in each city folder
				for (String uniqueFileIdentifier : listFiles(filePath)) {
					String propertyListingHTMLfilePath = filePath + uniqueFileIdentifier;

					if (platform.equals(platforms[0])) {
						// save data into arraylist of properties
						saveRealtorScrappedData(propertyListingHTMLfilePath, city, uniqueFileIdentifier);
					}
				}
			}
		}

	}

	// this function parses data saved in html files and saves objects
	private static void saveRealtorScrappedData(String filePath, City city, String uniqueID) {
		// filename passed becomes unique id for a property
		try {
			File htmlFile = new File(filePath);

			Document doc = Jsoup.parse(htmlFile, "UTF-8");

			Element price = doc.getElementById("listingPriceValue");
			Element address = doc.getElementById("listingAddress");

			Element bedRooms = doc.getElementById("BedroomIcon"); // bedrooms

			Element bathRooms = doc.getElementById("BathroomIcon"); // bathrooms

			Element description = doc.getElementById("propertyDescriptionCon"); // bathrooms

			Elements typeOfHouse = doc.getElementsByClass("propertyDetailsSectionContentValue");

			// check all the elements
			if (bedRooms != null && typeOfHouse != null && bathRooms != null && price != null && address != null) {

				Property property = new Property();

				property.price = Float.parseFloat(price.text().replaceAll("[^0-9.]", ""));
				property.address = address.text();
				property.city = city.city;
				property.province = city.province;
				property.provinceId = city.provinceId;
				property.zipcode = property.address.substring(property.address.length() - 6); // saving last 6
																								// characters
																								// as zipcode
				property.platform = platforms[0];
				property.uniqueID = uniqueID;

				String bedrooms = bedRooms.text().replaceAll("[^0-9+]", "").strip(); // replace all except + and numbers

				int above = 0;
				int below = 0;

				if (bedrooms.contains("+")) {
					above = Integer.parseInt(bedrooms.substring(0, 1));
					below = Integer.parseInt(bedrooms.substring(2, 3));

				} else {
					above = Integer.parseInt(bedrooms.substring(0, 1));
				}

				property.bathrooms = Integer.parseInt(bathRooms.text().replaceAll("[^0-9]", "")); // remove everything
																									// except numbers
				property.bedrooms = above + below;

				// to see
				if (typeOfHouse.text().toLowerCase().contains("house")) {
					property.houseType = TypeofHouse.House;
				} else if (typeOfHouse.text().toLowerCase().contains("apartment")
						|| typeOfHouse.text().toLowerCase().contains("condo")) {
					property.houseType = TypeofHouse.Apartment;
				}

//				System.out.println(filePath.substring(103));
//				property.printProperty();
				allProperties.add(property); // add to all properties
				property = null; // freeing memory
			} else {

				faultyPages += 1;

				// if above grade is null that means that this listing is not a
				// apartment or a house or not all information is there in the files
				// so delete such files

				if (htmlFile.delete()) {
					System.out.println("File deleted successfully");
				} else {
					System.out.println("Error in deleting file " + uniqueID);
					fileDeleteFail.add(uniqueID);

				}
			}
		} catch (IOException e) {
			System.out.println("File Not Found");
		}
	}

	// read saved objects
	private static ArrayList<Property> readDatFile() {
		try {
			FileInputStream inputStreamDat = new FileInputStream(savedObject);
			ObjectInputStream objectReader = new ObjectInputStream(inputStreamDat);
			Object object = objectReader.readObject();
			ArrayList<Property> savedData = (ArrayList<Property>) object;

			objectReader.close();
			inputStreamDat.close();
			return savedData;
		} catch (Exception e) {
			System.out.println("Problem in reading dat files");
			return null;
		}
	}

	// save all objects 
	private static void saveDatFile() {
		
		try {
			FileOutputStream outputStream = new FileOutputStream(savedObject);
			ObjectOutputStream fileWriterDat = new ObjectOutputStream(outputStream);

			// write all objects to file
			fileWriterDat.writeObject(allProperties);
			fileWriterDat.close();
			outputStream.close();
		} catch (Exception e) {
			System.out.println("Object Saving Error");
		}
	}

	public static void main(String[] args) {

		// making all the required folders for different purposes
		makeFolders();
		// open saved object files
		
		File savedObjects = new File(userDirectory + "/Saved Objects/allProperties.dat");
		
		if (!savedObjects.exists()) {
			// if allProperties.dat is not found, start scraping
			canadaCities = City.loadCityData(); // load city data 
			
			// save data into files
			try {			
				webCrawler(canadaCities);
				// webCrawler(canadaCities.subList(6, 7));
			}
			catch (Exception e) {
				System.out.println("Connection Reset Error");
			}
			
			// load scraped data into allProperties array list
			loadScrapedDataIntoClass(canadaCities);
			
			// print stuff
			System.out.println(allProperties.size());
			System.out.println(faultyPages);
			System.out.println(fileDeleteFail);

			// save allProperties array list to dat file
			saveDatFile();
		}
		else {
		allProperties = readDatFile();
		System.out.println("here");
		}
		
	}
}
