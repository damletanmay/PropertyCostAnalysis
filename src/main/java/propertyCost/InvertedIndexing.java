package propertyCost;

import java.util.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InvertedIndexing {

	public static Trie trieIndex = new Trie();
	public static final String[] platforms = { "Realtor", "Zolo", "RoyalLePage" }; // all the platforms
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path
	public static final Set<String> cityNames = Set.of("brampton", "calgary", "edmonton", "montreal", "ottawa",
			"quebec city", "toronto", "vancouver", "windsor", "winnipeg");

	// driver function
	public static Trie getTrieOfInvertedIndexing(List<City> citiesFromMain) {
		
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
							trieIndex.insert(term.toLowerCase(), documentId); // insert into trie	
						}
					}
				}
			}
		}
	}

	// parse data from html file
	// TODO: Improve what words to add in Trie
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
