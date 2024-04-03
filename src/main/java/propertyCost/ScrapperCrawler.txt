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
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import propertyCost.Property.TypeofHouse;

public class ScrapperCrawler {

	public static final String userDirectory = System.getProperty("user.dir"); // getting user path
	public static final String osName = System.getProperty("os.name"); // get OS name

	public static List<City> canadaCities = new ArrayList<City>(); // cities are loaded into canadaCities object
	public static ArrayList<Property> allProperties = new ArrayList<Property>();

	public static final String[] platforms = { "Realtor", "Zolo", "RoyalLePage" }; // all the platforms

	public static final String[][] platformsLink = { {
			"https://www.realtor.ca/map#ZoomLevel=4&Center=64.942702%2C-108.739845&LatitudeMax=73.67762&LongitudeMax=-56.00547&LatitudeMin=51.99920&LongitudeMin=-161.47422&Sort=6-D&GeoName=Vancouver%2C%20BC&PropertyTypeGroupID=1&TransactionTypeId=2&PropertySearchTypeId=0&Currency=CAD",
			platforms[0], "txtMapSearchInput", "mapSearchIcon" },
			{ "https://www.zolo.ca/", platforms[1], ".text-input", ".submit-search.button" },
			{ "https://www.royallepage.ca/en/searchgeo/homes/on/windsor?property_type=&house_type=&features=&listing_type=&lat=42.317438&lng=-83.035225&bypass=&address=Windsor&address_type=city&city_name=Windsor&prov_code=ON&display_type=gallery-view&da_id=&travel_time=&school_id=&boundary=true&search_str=Windsor%2C+ON%2C+CAN&id_search_str=Windsor%2C+ON%2C+CAN&school_search_str=&travel_time_min=30&travel_time_mode=drive&travel_time_congestion=&min_price=0&max_price=5000000%2B&min_leaseprice=0&max_leaseprice=5000%2B&beds=0&baths=0&transactionType=SALE&keyword=",
					platforms[2], "id_search_str", "div.button.button--square" } }; // links of each

	// platform with details

	public static List<String> fileDeleteFail = new ArrayList<String>();
	public static int faultyPages = 0;
	public static int faultyProperties = 0;
	public static List<String> uniqueIDs = new ArrayList<String>(); // to get unique ids

	public static final File savedObject = new File(userDirectory + "/Saved Objects/allProperties.dat");

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
		driver.manage().window().maximize();
		// looping over all platforms link
		for (int j = 0; j < platformsLink.length; j++) {

			// unpacking data from platformsLink
			String link = platformsLink[j][0];
			String platform = platformsLink[j][1];
			String identifierSearch = platformsLink[j][2];
			String identifierButton = platformsLink[j][3];

			driver.get(link);

			timeOut(16); // so that I can by pass bot verification always keep it at 16 seconds
			// get data for all the cities for a platform
			for (City city : list) {

				String searchQuery = city.city + ", " + city.provinceId;

				WebElement searchElement, button;

				String windowsPath = String.format("%s\\Scraped Data\\%s\\%s\\", userDirectory, platform, searchQuery);

				String linuxPath = String.format("%s/Scraped Data/%s/%s/", userDirectory, platform, searchQuery);

				timeOut(2);

				// to scrape all the listings in a city below methods are called
				if (platform.equals(platforms[0])) {
					scrapeRealtorData(driver, identifierSearch, identifierButton, searchQuery, windowsPath, linuxPath);
				} else if (platform.equals(platforms[1])) {
					scrapeZoloData(driver, identifierSearch, identifierButton, searchQuery, windowsPath, linuxPath);
				} else if (platform.equals(platforms[2])) {
					scrapeRoyalLePageData(driver, identifierSearch, identifierButton, searchQuery, windowsPath,
							linuxPath);
				}
			}
		}

		driver.close();
	}

	private static void scrapeRoyalLePageData(WebDriver driver, String identifierSearch, String identifierButton,
			String searchQuery, String windowsPath, String linuxPath) {

		try {
			WebElement searchElement = driver.findElement(By.id(identifierSearch));

			// enter our search query and click search
			searchElement.clear();
//			searchElement.sendKeys(Keys.chord(Keys.LEFT_CONTROL, Keys.BACK_SPACE, Keys.BACK_SPACE));
			searchElement.click();
			timeOut(1);
			searchElement.sendKeys(searchQuery + ", CAN");

			timeOut(1);
			searchElement.click();
			searchElement.sendKeys(Keys.chord(Keys.ENTER));

			timeOut(10); // wait 10 seconds
		} catch (Exception e) {
			System.out.println("Main Page not found");
//			e.printStackTrace();
			return;
		}

		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript("scrollBy(0,450)"); // scroll by 450 pixels

		WebElement gallery = driver.findElement(By.id("gallery-view2"));
		WebElement ul = gallery.findElements(By.tagName("ul")).get(0);

		List<WebElement> listings = ul.findElements(By.tagName("li"));

		for (WebElement listing : listings) {
			// get main window's window handle
			String base = driver.getWindowHandle();
			Set<String> set;

			try {
				// remove ads
				if (listing.getAttribute("class").equalsIgnoreCase("advertisment")) {
					continue;
				}

				String clicklnk = Keys.chord(Keys.CONTROL, Keys.ENTER);
				// open the link in new tab,by pressing ctrl + enter
				listing.findElement(By.tagName("a")).sendKeys(clicklnk); // click a listing by ctrl + enter so that we
																			// can open into new tab

				// make a set of all window handles
				set = driver.getWindowHandles();

				if (set.size() != 2) {
					continue;
				}

				// remove the main window handle from a base i.e. just keep second tab in the
				// set
				set.remove(base);
				assert set.size() == 1; // make set size 1

				// switch to second tab
				for (String otherWindow : set) {
					driver.switchTo().window(otherWindow);
				}

				timeOut(6);

				// get html source code of a particular listing
				String html = driver.getPageSource();

				// get a unique id to save by file name

				String uniqueID;

				try {
					uniqueID = driver.findElement(By.cssSelector("span.article.body-15")).getText()
							.replaceAll("[^0-9A-Za-z]", "");

					String[] strArray = uniqueID.split(" ");

					uniqueID = strArray[strArray.length - 1].substring(3);

					// make a file path
					String filePath;

					if (osName.toLowerCase().contains("windows")) {
						filePath = windowsPath + uniqueID + ".html";
					} else {
						filePath = linuxPath + uniqueID + ".html";
					}
					// save a file
					saveHTMLFile(html, filePath);
				} catch (Exception e) {
					System.out.println("Listing Not Found and not saved");
				}

				try {
					driver.close(); // Close a tab
					driver.switchTo().window(base); // switch to main base tab
					js.executeScript("scrollBy(0,65)"); // scroll by 65 pixels

				} catch (Exception e) {
					System.out.println("Unable to close new tab");
				}

			} catch (Exception e) {
				System.out.println("Listing Not Clickable");
			}
		}

	}

	private static void scrapeZoloData(WebDriver driver, String identifierSearch, String identifierButton,
			String searchQuery, String windowsPath, String linuxPath) {

		// code to search and click for a city
		try {
			WebElement searchElement = driver.findElement(By.cssSelector(identifierSearch));
			WebElement button = driver.findElement(By.cssSelector(identifierButton));

			// enter our search query and click search
			searchElement.clear();
			searchElement.click();
			searchElement.sendKeys(searchQuery);

			button.click();

			timeOut(4); // wait 4 seconds

		} catch (Exception e) {
			System.out.println("Main Page not found");
//			e.printStackTrace();
			return;
		}

		// check if listings are found
		try {
			WebElement strongError = driver.findElement(By.tagName("Strong"));
			if (strongError.getText().toLowerCase().contains("oops")) {
				return; // no properties are found
			}
		} catch (Exception e) {
			System.out.println("No Strong Tag Found that means listings are found for a city");
		}

		// getting all listings' id
		WebElement gallery = driver.findElement(By.cssSelector("#gallery div"));

		// getting articles
		List<WebElement> articles = gallery.findElements(By.cssSelector("article div div a"));

		for (WebElement article : articles) {

			// get main window's window handle
			String base = driver.getWindowHandle();
			Set<String> set;
			try {
				String clicklnk = Keys.chord(Keys.CONTROL, Keys.ENTER);
				// open the link in new tab,by pressing ctrl + enter
				article.sendKeys(clicklnk); // click a listing by ctrl + enter so that we can open into new tab

				// make a set of all window handles
				set = driver.getWindowHandles();

				if (set.size() != 2) {
					continue;
				}

				// remove the main window handle from a base i.e. just keep second tab in the
				// set
				set.remove(base);
				assert set.size() == 1; // make set size 1

				// switch to second tab
				for (String otherWindow : set) {
					driver.switchTo().window(otherWindow);
				}

				timeOut(6);

				// get html source code of a particular listing
				String html = driver.getPageSource();

				// get a unique id to save by file name

				String uniqueID;
				try {
					uniqueID = driver.findElement(By.cssSelector(".key-fact-mls .priv")).getText();
					// make a file path
					String filePath;

					if (osName.toLowerCase().contains("windows")) {
						filePath = windowsPath + uniqueID + ".html";
					} else {
						filePath = linuxPath + uniqueID + ".html";
					}

					// save a file
					saveHTMLFile(html, filePath);
				} catch (Exception e) {
					System.out.println("Listing Not Found and not saved");
				}

				try {
					driver.close(); // Close a tab
					driver.switchTo().window(base); // switch to main base tab
					JavascriptExecutor js = (JavascriptExecutor) driver;
					js.executeScript("gallery.scrollBy(0,34)"); // scroll by 34 pixels
				} catch (Exception e) {
					System.out.println("Unable to close tab");
					return;
				}

			} catch (Exception e) {
				System.out.println("Listing not clickable");
			}
		}
	}

	// scrapes realtor website data and uses saveHTMLFile() to save to file to
	// appropriate path
	private static void scrapeRealtorData(WebDriver driver, String identifierSearch, String identifierButton,
			String searchQuery, String windowsPath, String linuxPath) {
		// code to search and click for a city
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

			timeOut(4);

			driver.findElement(By.id("polygonOptInBtn")).click(); // search within boundary toggle
			timeOut(2);

		} catch (Exception e) {
			System.out.println("Main Page not found");
//			e.printStackTrace();
			return;
		}

		// disable auto complete
		JavascriptExecutor js = (JavascriptExecutor) driver;
		js.executeScript(
				"document.getElementById('AutoCompleteCon-txtMapSearchInput').setAttribute('class', 'hidden')");

		timeOut(2);

		// get all listings in a particular city
		List<WebElement> listings = driver.findElements(By.className("cardCon"));

		// get main window's window handle

		for (WebElement listing : listings) {

			String base = driver.getWindowHandle();
			try {
				timeOut(2);
				listing.click(); // click a listing

				// make a set of all window handles
				Set<String> set = driver.getWindowHandles();

				if (set.size() != 2) {
					continue;
				}

				// remove the main window handle from a base i.e. just keep the second tab in
				// the set
				set.remove(base);
				assert set.size() == 1; // make set size 1

				// switch to second tab
				for (String otherWindow : set) {
					driver.switchTo().window(otherWindow);
				}

				timeOut(10);

				// get html source code of a particular listing
				String html = driver.getPageSource();

				// get a unique id to save by file name
				String uniqueID;
				try {
					uniqueID = driver.findElement(By.id("MLNumberVal")).getText();
					// make a file path
					String filePath;

					if (osName.toLowerCase().contains("windows")) {
						filePath = windowsPath + uniqueID + ".html";
					} else {
						filePath = linuxPath + uniqueID + ".html";
					}

					// save a file
					saveHTMLFile(html, filePath);

					try {
						driver.close(); // Close a tab
						driver.switchTo().window(base); // switch to main base tab
						js.executeScript("mapSidebarBodyCon.scrollBy(0,100)"); // scroll by 100 pixels

					} catch (Exception e) {
						System.out.println("Unable to close new tab");
					}

				} catch (Exception e) {
					System.out.println("Listing Not Found and not saved");
					driver.close();
					driver.switchTo().window(base); // switch to main base tab
					js.executeScript("mapSidebarBodyCon.scrollBy(0,100)"); // scroll by 100 pixels
				}

			} catch (Exception e) {
				// probably means we're blocked again and have 10 seconds to solve captcha
				System.out.println("Listing not clickable");
				driver.switchTo().window(base);
			}
		}

		try {
			driver.findElement(By.id("PolygonClearingBtn")).click(); // search within boundary toggle
		} catch (Exception e) {
			return; // if button becomes hidden due to some reason then np, just move ahead
		}

	}

	// below function saves HTML file to given path
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
//			e.printStackTrace();
		}
	}

	// a timeout function for the thread to wait while the website is loading /
	// bypassing captcha
	private static void timeOut(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds); // sleep 7 seconds
		} catch (InterruptedException e) {
			System.out.println("Timeout error");
		}
	}

	// a function that returns a List of strings which has file names of all the
	// files in a folder
	private static List<String> listFiles(String dir) {
		// making a stream, filtering files to see if they are directory or not, mapping
		// and then collecting to list
		return Stream.of(new File(dir).listFiles()).filter(file -> !file.isDirectory()).map(File::getName)
				.collect(Collectors.toList());
	}

	// this function loads previously scraped data into class property i.e. make
	// objects
	private static void loadScrapedDataIntoClass(List<City> cities) {

		// for each platform, for each city, for each file inside a city save data of
		// all the files in them
		for (String platform : platforms) {

			// for each cities in a platform folder
			for (City city : cities) {

				String windowsPath = String.format("%s\\Scraped Data\\%s\\%s\\", userDirectory, platform,
						city.city + ", " + city.provinceId);
				String linuxPath = String.format("%s/Scraped Data/%s/%s/", userDirectory, platform,
						city.city + ", " + city.provinceId);

				String filePath;

				// configure file path based on operating system.
				if (osName.toLowerCase().contains("windows")) {
					filePath = windowsPath;
				} else {
					filePath = linuxPath;
				}

				// traversing each file in a city folder

				List<String> allFiles = listFiles(filePath);

				if (allFiles.size() != 0) {

					for (String uniqueFileIdentifier : listFiles(filePath)) {
						String propertyListingHTMLfilePath = filePath + uniqueFileIdentifier;

						if (platform.equals(platforms[0])) {
							// save data into array list of properties
							saveRealtorScrappedData(propertyListingHTMLfilePath, city, uniqueFileIdentifier);
						} else if (platform.equals(platforms[1])) {
							// save data into array list of properties
							saveZoloScrappedData(propertyListingHTMLfilePath, city, uniqueFileIdentifier);
						} else if (platform.equals(platforms[2])) {
							// save data into array list of properties
							saveRoyalLePageScrappedData(propertyListingHTMLfilePath, city, uniqueFileIdentifier);
						}
					}
				}

			}
		}

	}

	// this function parses data saved in html files in RoyalLePage folder and makes
	// objects of property
	private static void saveRoyalLePageScrappedData(String filePath, City city, String uniqueID) {

		try {
			File htmlFile = new File(filePath); // file

			Document doc = Jsoup.parse(htmlFile, "UTF-8"); // file parsing

			Element price = doc.select("span.title.title--h1.price").first();

			Element bedRooms = doc.select("div.bed-bath-box__item.beds").first();

			Element bathRooms = doc.select("div.bed-bath-box__item.baths").first();

			Elements description = doc.select("p.body-15.body-15--light ");

			Elements address = doc.select("div.address-bar > h1.title--h2.u-no-margins");

			Elements typeOfHouse = doc.select("ul.property-features-list");

			if (price != null && bedRooms != null && bathRooms != null && description != null && address != null
					&& typeOfHouse != null) {

				Property property = new Property();

				property.price = Float.parseFloat(price.text().replaceAll("[^0-9.]", ""));
				property.address = address.text();
				property.city = city.city;
				property.province = city.province;
				property.provinceId = city.provinceId;
				property.zipcode = property.address.substring(property.address.length() - 7).replaceAll("\\s", ""); // removing
																													// space
																													// from
																													// last
																													// 7
																													// characters
				property.description = description.text();
				property.platform = platforms[2];
				property.uniqueID = uniqueID.substring(0, uniqueID.length() - 5);

				// solving house type
				if (typeOfHouse.text().toLowerCase().contains("apartment")) {
					property.houseType = TypeofHouse.Apartment;
				} else {
					property.houseType = TypeofHouse.House;
				}

				int above = 0;
				int below = 0;
				String bedrooms = bedRooms.text();
				// solving bedrooms
				if (bedrooms.contains("+")) {
					above = Integer.parseInt(bedrooms.substring(0, 1));
					below = Integer.parseInt(bedrooms.substring(2, 3));

				} else {
					above = Integer.parseInt(bedrooms.substring(0, 1));
				}

				property.bathrooms = Integer.parseInt(bathRooms.text().replaceAll("[^0-9]", "")); // remove everything,
																									// except numbers
				property.bedrooms = above + below;

				// to avoid repetition of same data.
				if (!uniqueIDs.contains(uniqueID)) {
					// if a unique id is not present then add it and add the property as well
					uniqueIDs.add(uniqueID);
					allProperties.add(property); // add to all properties
				} else {
					deleteUselessFiles(uniqueID, htmlFile);
				}

				property = null;
			} else {
				// delete files which are plots or doesn't have full info
				deleteUselessFiles(uniqueID, htmlFile);
			}

		} catch (Exception e) {
			System.out.println("Error in loading data from file");
			deleteUselessFiles(uniqueID, new File(filePath));
		}

	}

	// this function parses data saved in html files in zolo folder and makes
	// objects of property
	private static void saveZoloScrappedData(String filePath, City city, String uniqueID) {

		try {
			File htmlFile = new File(filePath); // file

			Document doc = Jsoup.parse(htmlFile, "UTF-8"); // file parsing

			Elements price = doc.getElementsByClass("listing-price");

			Elements bedBath = doc.getElementsByClass("priv heavy");

			Elements description = doc.select("div.section-listing-content > div > span.priv ");

			Elements address = doc.getElementsByClass("listing-location");

			Elements typeOfHouse = doc.select("section.section-listing-content > div");

			if (price != null && bedBath != null && description != null && address != null && typeOfHouse != null) {

				Property property = new Property();

				StringTokenizer st = new StringTokenizer(price.text());

				while (st.hasMoreTokens()) {
					property.price = Float.parseFloat(st.nextToken().replaceAll("[^0-9.]", ""));
					// break after first element because first has price
					break;
				}

				property.address = address.text();
				property.city = city.city;
				property.province = city.province;
				property.provinceId = city.provinceId;
				property.zipcode = null; // zip code not available for any listings
				property.description = description.text();
				property.platform = platforms[1];
				property.uniqueID = uniqueID.substring(0, uniqueID.length() - 5);

				// solving house type
				if (typeOfHouse.text().toLowerCase().contains("apartment")) {
					property.houseType = TypeofHouse.Apartment;
				} else {
					property.houseType = TypeofHouse.House;
				}

				st = new StringTokenizer(bedBath.text());

				int i = 1; // first element is bedrooms
				String bedRooms = null;
				int bathRooms = 0;

				// separting bathrooms and bedrooms
				while (st.hasMoreTokens()) {
					if (i == 1) {
						bedRooms = st.nextToken();
						i++;
					} else if (i == 2) {
						bathRooms = Integer.parseInt(st.nextToken().replaceAll("[^0-9]", "").strip());
						break;
					}
				}

				int above = 0;
				int below = 0;

				// solving bedrooms
				if (bedRooms.contains("+")) {
					above = Integer.parseInt(bedRooms.substring(0, 1));
					below = Integer.parseInt(bedRooms.substring(2, 3));

				} else {
					above = Integer.parseInt(bedRooms.substring(0, 1));
				}

				property.bedrooms = above + below;
				property.bathrooms = bathRooms;

				// to avoid repetition of same data.
				if (!uniqueIDs.contains(uniqueID)) {
					// if a unique id is not present then add it and add the property as well
					uniqueIDs.add(uniqueID);
					allProperties.add(property); // add to all properties
				} else {
					deleteUselessFiles(uniqueID, htmlFile);
				}

				property = null;
			} else {
				// delete files which are plots or doesn't have full info
				deleteUselessFiles(uniqueID, htmlFile);
			}

		} catch (Exception e) {
			System.out.println("Error in loading data from file");
			deleteUselessFiles(uniqueID, new File(filePath));
		}

	}

	// this function parses data saved in html files in realtor folder and makes
	// objects of property
	private static void saveRealtorScrappedData(String filePath, City city, String uniqueID) {
		// filename passed becomes unique id for a property
		try {

			File htmlFile = new File(filePath); // file

			Document doc = Jsoup.parse(htmlFile, "UTF-8"); // file parsing

			// getting required elements for getting text inside them
			Element price = doc.getElementById("listingPriceValue");
			Element address = doc.getElementById("listingAddress");

			Element bedRooms = doc.getElementById("BedroomIcon"); // bedrooms

			Element bathRooms = doc.getElementById("BathroomIcon"); // bathrooms

			Element description = doc.getElementById("propertyDescriptionCon"); // description

			Elements typeOfHouse = doc.getElementsByClass("propertyDetailsSectionContentValue");

			// check all the elements if they are found
			if (bedRooms != null && typeOfHouse != null && bathRooms != null && price != null && address != null) {

				Property property = new Property();

				property.price = Float.parseFloat(price.text().replaceAll("[^0-9.]", ""));
				property.address = address.text();
				property.city = city.city;
				property.province = city.province;
				property.provinceId = city.provinceId;
				// saving last 6 characters as zipcode
				property.zipcode = property.address.substring(property.address.length() - 6);
				property.description = description.text();
				property.platform = platforms[0];
				property.uniqueID = uniqueID.substring(0, uniqueID.length() - 5);

				String bedrooms = bedRooms.text().replaceAll("[^0-9+]", "").strip(); // replace all except + and numbers

				int above = 0;
				int below = 0;
				// solving bedrooms
				if (bedrooms.contains("+")) {
					above = Integer.parseInt(bedrooms.substring(0, 1));
					below = Integer.parseInt(bedrooms.substring(2, 3));

				} else {
					above = Integer.parseInt(bedrooms.substring(0, 1));
				}

				property.bathrooms = Integer.parseInt(bathRooms.text().replaceAll("[^0-9]", "")); // remove everything,
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

				// to avoid repetition of same data.
				if (!uniqueIDs.contains(uniqueID)) {
					// if a unique id is not present then add it and add the property as well
					uniqueIDs.add(uniqueID);
					allProperties.add(property); // add to all properties
				} else {
					deleteUselessFiles(uniqueID, htmlFile);
				}

				property = null; // freeing memory even though java has a garbage collector
			} else {
				deleteUselessFiles(uniqueID, htmlFile);
			}
		} catch (Exception e) {
			System.out.println("File Not Found");
			deleteUselessFiles(uniqueID, new File(filePath));
		}

	}

	private static void deleteFaultyProperties() {
		ArrayList<Property> deleteObjects = new ArrayList<Property>();
		for (Property p : allProperties) {
			if (p.bedrooms == 0 || p.bathrooms == 0 || p.description == null || p.address == null
					|| p.houseType == null) {
				String searchQuery = p.city + ", " + p.provinceId;

				String windowsPath = String.format("%s\\Scraped Data\\%s\\%s\\", userDirectory, p.platform,
						searchQuery);

				String linuxPath = String.format("%s/Scraped Data/%s/%s/", userDirectory, p.platform, searchQuery);

				String filePath = "";

				if (osName.toLowerCase().contains("windows")) {
					filePath = windowsPath + p.uniqueID + ".html";
				} else {
					filePath = linuxPath + p.uniqueID + ".html";
				}

				System.out.println(filePath);
				deleteUselessFiles(p.uniqueID, new File(filePath));
				deleteObjects.add(p);
				faultyProperties++;
			}
		}
		allProperties.removeAll(deleteObjects);
	}

	private static void deleteUselessFiles(String uniqueID, File htmlFile) {

		// if above grade is null that means that this listing is not a
		// apartment or a house or not all information is there in the files
		// so delete such files

		faultyPages += 1;

		if (htmlFile.delete()) {
			System.out.println("File deleted successfully");
		} else {
			System.out.println("Error in deleting file " + uniqueID);
			fileDeleteFail.add(uniqueID);
		}
	}

	// read saved dat files which should have allProperties array list saved as a
	// dat file at savedObject path
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

	// save allProperties array list to savedObject path
	private static void saveDatFile(ArrayList <Property> allProperties) {

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

	// driver function
	public static ArrayList <Property> getProperties(List<City> canadaCitiesFromMain) {

		canadaCities = canadaCitiesFromMain; // 
		
		// making all the required folders for different purposes
		makeFolders();
		
		// open saved object files if exist

		if (!savedObject.exists()) {
			// if allProperties.dat is not found, start scraping
			
			try {
				webCrawler(canadaCities);
				// webCrawler(canadaCities.subList(6, 10));
			} catch (Exception e) {
				System.out.println("Connection Reset Error");
//				e.printStackTrace();
			}

			// load scraped data into allProperties array list
			loadScrapedDataIntoClass(canadaCities);

			// print stuff
//			System.out.println(allProperties.size());
//			System.out.println(faultyPages);
//			System.out.println(fileDeleteFail);
			
			// delete faulty properties 
			deleteFaultyProperties();
			
			System.out.println("Deleted " + faultyProperties + " faulty properties");

			// save allProperties array list to dat file
			saveDatFile(allProperties);
		} else {
			// if dat file available just load the dat file 
			
			// uncomment and run below lines if error in loading file
//			allProperties = readDatFile();
//			deleteFaultyProperties();
//			saveDatFile(allProperties);
			allProperties = readDatFile();
		}
		return allProperties;
	}

}
