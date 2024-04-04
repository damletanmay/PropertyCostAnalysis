package propertyCost;

import java.util.*;


public class AutocompleteTrieMap {
	// Declaring the trie data structure
	public static Triewordcompletion trie;

	// Constructor to initialize the trie with city data
	public AutocompleteTrieMap(List<City> canadaCities) {
		// Initialize trie by loading city data
		loadDictionary(trie, canadaCities);
	}

	// Static nested class representing a trie node
	public static class Trie_Node {
		char data;
		HashMap<Character, Trie_Node> children = new HashMap<>();
		boolean isEnd = false;

		// Constructor for trie node
		Trie_Node(char c) {
			this.data = c;
		}
	}

	// Class for trie-based autocomplete functionality
	public static class Triewordcompletion {

		// Root node of the trie
		Trie_Node root = new Trie_Node(' ');

		// Method for adding a word to the trie data structure
		void insert(String word_city) {
			Trie_Node node = root;
			for (char ch : word_city.toCharArray()) {
				if (!node.children.containsKey(ch))
					node.children.put(ch, new Trie_Node(ch));
				node = node.children.get(ch);
			}
			node.isEnd = true; // Marking the end of the word
		}

		// Method to find all words with a given prefix
		List<String> autocomplete(String prefix) {

			prefix = prefix.toLowerCase();

			List<String> res = new LinkedList<String>(); // List to store autocomplete results
			Trie_Node node = root;
			for (char ch : prefix.toCharArray()) {
				if (node.children.containsKey(ch))
					node = node.children.get(ch);
				else
					return res; // No words found for the given prefix
			}
			// Call helper function to retrieve words recursively
			helper(node, res, prefix.substring(0, prefix.length() - 1));
			return res; // Return the autocomplete results
		}

		// Helper function called recursively for autocomplete
		void helper(Trie_Node node, List<String> res, String prefix) {
			if (node.isEnd)
				res.add(prefix + node.data); // Adding word to the result list
			for (Character ch : node.children.keySet())
				helper(node.children.get(ch), res, prefix + node.data); // Recursively call helper function
		}
	}

	// Open a dictionary file and load words into the trie
	static void loadDictionary(Triewordcompletion trie, List<City> canadianCities) {
		for (City city : canadianCities) {
			trie.insert(city.city.toLowerCase()); // Insert each city into the trie
		}
	}

}
