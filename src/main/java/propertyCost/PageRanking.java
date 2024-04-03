package propertyCost;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class PageRanking {

	// Store file paths of each search keyword i.e. city name
	// will contain, city,[files]
	public static HashMap<String, ArrayList<String>> filePaths = new HashMap<String, ArrayList<String>>();

	public static ArrayList<String> tempList = new ArrayList<String>(); // will contain files

	// splay tree to store page ranking will store <city ,(file, occurrences)>
	public static SplayTree<String, HashMap<String, Integer>> pageRankingST = new SplayTree<>();

	public static final String userDirectory = System.getProperty("user.dir"); // getting user path
	public static final String osName = System.getProperty("os.name"); // get OS name

	public static final String windowsPathScrapedData = userDirectory + "\\Scraped Data\\";
	public static final String linuxPathScrapedData = userDirectory + "/Scraped Data/";

	public static Set<String> cityNames = new HashSet<String>();

	public PageRanking(TrieClass loadedTrie, List<City> canadaCities) {
		populateSplayTree(loadedTrie, canadaCities);
	}

	// initialize pageRankingST variable by either reading or processing from files
	private static void populateSplayTree(TrieClass loadedTrie, List<City> canadaCities) {
		File savedObject = new File(userDirectory + "/Saved Objects/pageRankingSplayTree.dat");

		if (!savedObject.exists()) {

			cityNames = City.giveAllCities(canadaCities); // get all cities in lowercase

			getAllCitiesFilePath(loadedTrie, canadaCities); // get all file paths for trie

			// iterate over all cities and all files which hold that city word
			for (Map.Entry<String, ArrayList<String>> item : filePaths.entrySet()) {
				String city = item.getKey();
				ArrayList<String> allFiles = item.getValue();

				// temp map to store occurrences and file name
				HashMap<String, Integer> tempMap = new HashMap<String, Integer>();

				for (String file : allFiles) {
					// for each file get count of city
					int wordOccurrence = getKeywordOccurrences(new File(file), city);
					tempMap.put(file, wordOccurrence);
				}

				// sort temp map based on value
				tempMap = tempMap.entrySet().stream() // creating a stream API of
						.sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // sort by value to sort by city word frequency 
						.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
								(oldValue, newValue) -> oldValue, LinkedHashMap::new)); // collecting sorted and filtered data to a map

				pageRankingST.insert(city, tempMap); // add city and tempMap into Splay tree
			}
			saveDatFile(savedObject); // save pageRanking Splay tree
		} else {
			pageRankingST = readDatFile(savedObject);
		}
	}

	// populate filePaths with city name and list of files in which that word is
	// found
	private static void getAllCitiesFilePath(TrieClass loadedTrie, List<City> canadaCities) {

		// configure file path of scraped data directory according to system os
		String filePath = null;

		if (osName.toLowerCase().contains("windows")) {
			filePath = windowsPathScrapedData;
		} else {
			filePath = linuxPathScrapedData;
		}

		// for each city -> get all files -> iterate over files -> find path for each
		// file and store it into templist,
		// after each file save them to filePaths hash map

		for (City city : canadaCities) {

			// reset the temp list for each city
			tempList = new ArrayList<String>();

			// for each file, find path of it in our file system
			for (String fileName : loadedTrie.searchNode(city.city)) {
				getFilePaths(filePath, fileName);
			}

			if (tempList.size() == loadedTrie.searchNode(city.city).size()) {
				filePaths.put(city.city.toLowerCase(), tempList);
			}
		}
	}

	// get full file paths for input file name and directory it will go to the
	// lowest sub directory level and get file path for each file
	private static void getFilePaths(String filePath, String fileNameToSearch) {

		File file = new File(filePath);
		String[] names = file.list();

		// traverse platform directories
		for (String name : names) {
			String subDirectory = filePath + name;

			if (new File(subDirectory).isDirectory()) {
				// traverse city directories
				for (String f : new File(subDirectory).list()) {
					String[] htmlFiles = new File(subDirectory + "/" + f).list();

					// traverse all files in a folder
					for (String htmlFile : htmlFiles) {
						if (htmlFile.equals(fileNameToSearch)) {
							// if file is found store it's file path into
							// hash map for later use when searching for count of words
							// break if file found
							if (osName.toLowerCase().contains("windows")) {
								tempList.add(subDirectory + "\\" + f + "\\" + fileNameToSearch);
								break;
							} else {
								tempList.add(subDirectory + "/" + f + "/" + fileNameToSearch);
								break;
							}

						}
					}
				}
			}
		}
	}

	// Count Occurrences of Web Pages
	private static int countKeywordOccurrences(String[] terms, String keyword) {

		int count = 0;

		// Use file name as document ID
		for (String term : terms) {
			if (keyword.equals(term)) {
				count += 1;
			}
		}
		return count;
	}

	// parse data from html file
	private static String parseHTMLFile(File file) {
		String plainText = new String();
		try {
			Document doc = Jsoup.parse(file, "UTF-8");
			plainText = doc.text();
			// Replace special characters with spaces
			String text = plainText.toString().replaceAll("[^a-zA-Z0-9\\s]", " ") // Replace non-alphanumeric characters
																					// with spaces
					.replaceAll("\\s+", " "); // Replace multiple spaces with single space
			return text.trim(); // Trim leading and trailing spaces
		} catch (IOException e) {
			e.printStackTrace();
			return ""; // Return empty string in case of an error
		}
	}

	private static int getKeywordOccurrences(File file, String keyword) {

		int occurrences = 0;

		if (file.isFile() && file.getName().endsWith(".html")) {

			String text = parseHTMLFile(file);

			String[] terms = text.toLowerCase().split("\\s+");

			occurrences = countKeywordOccurrences(terms, keyword);

		}
		return occurrences;
	}

	
	public void printNFiles(String cityName,int n) {
		
		HashMap<String, Integer> pageRankingOfOneCity = pageRankingST.getValue(cityName);
		int i = 0;

		System.out.println("PAGE RANKING");
		for(Map.Entry<String, Integer> item : pageRankingOfOneCity.entrySet()) {
			

	        // create object of Path 
	        Path path = Paths.get(item.getKey()); 
	  
	        // call getFileName() and get FileName path object 
	        String fileName = path.getFileName().toString(); 
			
			if (i<n) {
				System.out.println( "File:" + fileName + " has " + (item.getValue()+1) + " Occurrences.");
			}
			i++;
		}	
	}
	
	// read saved dat files which should have pageRankingST splay tree saved as a
	// dat file at savedObject path
	private static SplayTree<String, HashMap<String, Integer>> readDatFile(File savedObject) {
		try {
			FileInputStream inputStreamDat = new FileInputStream(savedObject);
			ObjectInputStream objectReader = new ObjectInputStream(inputStreamDat);
			Object object = objectReader.readObject();
			SplayTree<String, HashMap<String, Integer>> savedData = (SplayTree<String, HashMap<String, Integer>>) object;

			objectReader.close();
			inputStreamDat.close();
			return savedData;
		} catch (Exception e) {
			System.out.println("Problem in reading dat files");
			return null;
		}
	}
	

	
	// save pageRankingST splay tree to savedObject path
	private static void saveDatFile(File savedObject) {

		try {
			FileOutputStream outputStream = new FileOutputStream(savedObject);
			ObjectOutputStream fileWriterDat = new ObjectOutputStream(outputStream);

			// write all objects to file
			fileWriterDat.writeObject(pageRankingST);
			fileWriterDat.close();
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Object Saving Error");
		}
	}

}