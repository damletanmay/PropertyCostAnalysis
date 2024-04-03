package propertyCost;

import java.util.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InvertedIndexing {

	public static TrieClass trieIndex = new TrieClass();
	public static final String[] platforms = { "Realtor", "Zolo", "RoyalLePage" }; // all the platforms
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path
	public static final String osName = System.getProperty("os.name"); // get OS name
	public static Set<String> cityNames = new HashSet<String>();
	
	
	// driver function to return to mainClass
	public static TrieClass getLoadedTrie(List<City> citiesFromMain) {
		
		cityNames = City.giveAllCities(citiesFromMain);
		
		String windowsPath = String.format("%s\\Saved Objects\\trie.dat", userDirectory);

		String linuxPath = String.format("%s/Saved Objects/trie.dat", userDirectory);
		
		String filePath = userDirectory;
		
		if (osName.toLowerCase().contains("windows")) {
			filePath = windowsPath;
		} else {
			filePath = linuxPath;
		}
		
		TrieClass loadedTrie;
		
		File datFile = new File(filePath);
		
		if (datFile.exists()) {
		    // Load trie from the existing .dat file
		    loadedTrie = TrieSerialization.loadTrieFromDatFile(filePath);
		} else {
		    // Build the trie since the .dat file doesn't exist
		    TrieClass trie = getTrieOfInvertedIndexing(citiesFromMain);
		    TrieSerialization.saveTrieToDatFile(trie, filePath);
		    loadedTrie = TrieSerialization.loadTrieFromDatFile(filePath);
		}
		
		return loadedTrie;
	}
	
	// get inverted indexing trie
	private static TrieClass getTrieOfInvertedIndexing(List<City> citiesFromMain) {
		
		// for each platform, for each city make trie of each file in each folder
		for (String platform: platforms) {
			for (City city : citiesFromMain) {
				buildTrieforAFolder(new File(userDirectory+"/Scraped Data/" + platform + "/"  + city.city + ", " + city.provinceId ));
			} 
		}
		return trieIndex;
	}

	// add to public trie for each term in each file
	private static void buildTrieforAFolder(File folder) {
		
		File[] files = folder.listFiles(); // list files

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					// Parse HTML file to extract text
					String text = parseHTMLFile(file);
					// Tokenizes and pre-process the text
					String[] terms = text.toLowerCase().split("\\s+");

					// Index the terms
					String documentId = file.getName(); // Use file name as document ID
					for (String term : terms) {
						if (cityNames.contains(term)) {
							trieIndex.insertNode(term.toLowerCase(), documentId); // insert into trie	
						}
					}
				}
			}
		}
	}
	
	
	// parse data from html file
	private static String parseHTMLFile(File file) {
		StringBuilder textBuilder = new StringBuilder();
		try {
			Document doc = Jsoup.parse(file, "UTF-8");

			// Extract text from all elements in the HTML file
			Elements elements = doc.getAllElements();
			for (Element element : elements) {
				textBuilder.append(element.text()).append(" ");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return textBuilder.toString();
	}
}
