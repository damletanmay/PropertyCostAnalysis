package propertyCost;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;



public class SpellChecker {
	private SplayTree<String, Integer> dictionary;

    public SpellChecker() {
        dictionary = new SplayTree<>();
        initializeDictionary();
    }

    // Method to initialize dictionary with city names from canadianCities
    private void initializeDictionary() {
        ArrayList<City> canadianCities = City.loadCityData();
        for (City city : canadianCities) {
            addWord(city.city.toLowerCase());
        }
    }

	// Spell Check with Similarity Metrics
	public boolean spellCheck(String word) {
		return dictionary.contains(word.toLowerCase());
	}

	// Add Word
	public void addWord(String word) {
		word = word.toLowerCase();
		Integer occurrences = dictionary.getValue(word);
		if (occurrences == null) {
			dictionary.insert(word, 1);
		} else {
			dictionary.insert(word, occurrences + 1);
		}
	}


	// Auto-correct with Similarity Metrics
	public String[] suggestCorrections(String word) {
		// Implement similarity metric logic using Levenshtein distance
		List<String> suggestions = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : dictionary.inOrderTraversal()) {
			String dictWord = entry.getKey();
			if (calculateLevenshteinDistance(word, dictWord) <= 3) { // Adjust threshold as needed
				suggestions.add(dictWord);
			}
		}
		return suggestions.toArray(new String[0]);
	}

	// Calculate Levenshtein Distance between two strings
	private int calculateLevenshteinDistance(String s1, String s2) {
		int[][] dp = new int[s1.length() + 1][s2.length() + 1];

		for (int i = 0; i <= s1.length(); i++) {
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0) {
					dp[i][j] = j;
				} else if (j == 0) {
					dp[i][j] = i;
				} else {
					dp[i][j] = min(dp[i - 1][j - 1] + costOfSubstitution(s1.charAt(i - 1), s2.charAt(j - 1)),
							dp[i - 1][j] + 1, dp[i][j - 1] + 1);
				}
			}
		}
		return dp[s1.length()][s2.length()];
	}

	private int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}

	private int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}
	
//	public static void main(String[] args) {
//	    Scanner scanner = new Scanner(System.in);
//	    SpellChecker spellChecker = new SpellChecker();
//
//	    while (true) {
//	        System.out.print("Enter word to spell check (or type 'exit' to quit): ");
//	        String word = scanner.nextLine().toLowerCase();
//
//	        if (word.equals("exit")) {
//	            System.out.println("Exiting spell checker. Goodbye!");
//	            break;
//	        }
//
//	        if (spellChecker.spellCheck(word)) {
//	            System.out.println(word + " is spelled correctly.");
//	        } else {
//	            System.out.println(word + " is misspelled.");
//	            System.out.println("Suggestions:  ");
//	            String[] suggestions = spellChecker.suggestCorrections(word);
//	            for (String suggestion : suggestions) {
//	                System.out.println(suggestion);
//	            }
//	        }
//	    }
//	    scanner.close();
//	}

}


