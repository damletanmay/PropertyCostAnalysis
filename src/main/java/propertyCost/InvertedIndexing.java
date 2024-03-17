package propertyCost;

import java.util.*;
import java.io.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class InvertedIndexing {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Building Trie...");
		
		Trie trieIndex = new Trie();
		
		// Assuming you have a folder named "Toronto_ON" containing HTML files for house
		// listings
		File folder = new File("D:\\UWINDSOR\\ACC\\Final Project\\PropertyCostAnalysis-main\\Scraped Data\\Realtor\\Toronto, ON");
		File[] files = folder.listFiles();

		if (files != null) {
			for (File file : files) {
				if (file.isFile()) {
					// Parse HTML file to extract text
					String text = parseHTMLFile(file);

					// Tokenize and preprocess the text
					String[] terms = text.toLowerCase().split("\\s+");

					// Index the terms
					String documentId = file.getName(); // Use file name as document ID
					for (String term : terms) {
						trieIndex.insert(term, documentId);
					}
				}
			}
		}
		
		System.out.println("Trie Built Succesfully");
		
		// Example: User input for search query
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter search query: ");
		String query = scanner.nextLine();

		// Search for house listings containing the search query
		Set<String> result = trieIndex.search(query.toLowerCase());
		if (!result.isEmpty()) {
			System.out.println("House listings found for query '" + query + "': ");
			for (String documentId : result) {
				System.out.println(documentId);
				// Additional processing to display information from the HTML file
			}
		} else {
			System.out.println("No house listings found for query '" + query + "'");
		}
		scanner.close();
	}
	
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
