package propertyCost;

import java.util.*;
import java.io.*;

public class AutocompleteTrieMap {
	public static TrieAutoComplete trie;

	// constructor which loads city data
	public AutocompleteTrieMap(List<City> canadaCities) {
		// pass cities list to initialize trie
		loadDictionary(trie, canadaCities);
	}

	// node static class
	public static class TrieNode {
		char data;
		HashMap<Character, TrieNode> children = new HashMap<>();
		boolean isEnd = false;

		TrieNode(char c) {
			this.data = c;
		}
	}

	// trie autocomplete class
	public static class TrieAutoComplete {

		TrieNode root = new TrieNode(' ');

		// insert a node
		void insert(String word) {
			TrieNode node = root;
			for (char ch : word.toCharArray()) {
				if (!node.children.containsKey(ch))
					node.children.put(ch, new TrieNode(ch));
				node = node.children.get(ch);
			}
			node.isEnd = true;
		}

		// find all word with given prefix
		List<String> autocomplete(String prefix) {

			prefix = prefix.toLowerCase();

			List<String> res = new LinkedList<String>();
			TrieNode node = root;
			for (char ch : prefix.toCharArray()) {
				if (node.children.containsKey(ch))
					node = node.children.get(ch);
				else
					return res;
			}
			helper(node, res, prefix.substring(0, prefix.length() - 1));
			return res;
		}

		// recursive function called by autocomplete
		void helper(TrieNode node, List<String> res, String prefix) {
			if (node.isEnd)
				res.add(prefix + node.data);
			for (Character ch : node.children.keySet())
				helper(node.children.get(ch), res, prefix + node.data);
		}
	}

	// Load words from a dictionary file into the trie
	static void loadDictionary(TrieAutoComplete trie, List<City> canadianCities) {
		for (City city : canadianCities) {
			trie.insert(city.city.toLowerCase());
		}
	}

}
