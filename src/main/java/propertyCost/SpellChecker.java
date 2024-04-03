package propertyCost;


import java.io.*;
import java.util.*;


public class SpellChecker {
	public static SplayTree<String, Integer> dictionary;
	
	
	// Saving 
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path
	public static final String osName = System.getProperty("os.name"); // get OS name
	public static File savedObject = new File(userDirectory+"/Saved Objects/searchFrequency.dat");
	
	
	// Spell checker fetches 
    public SpellChecker(List<City> canadianCities) {
    	
    	if (!savedObject.exists()) {
            dictionary = new SplayTree<>();
            // importing city names from cities.csv and to splaytree(string,int)
            // cities are to string and are intialized by 0
            initializeDictionary(canadianCities);
            saveDatFile(dictionary);
    	}
    	else {
    		readDatFile();
    	}
    }

    public Integer searchFrequency(String searchCity) {
    	return dictionary.getValue(searchCity.toLowerCase());
    }
    
    // Method to initialize dictionary with city names from canadianCities
    private void initializeDictionary(List<City> canadianCities) {
        for (City city : canadianCities) {
            addWord(city.city.toLowerCase());
        }
    }

	// Spell Check with Similarity Metrics
	public boolean spellCheck(String word) {
		return dictionary.contains(word.toLowerCase());
	}

	// Add Word
	public SplayTree<String, Integer> addWord(String word) {
		word = word.toLowerCase();
		Integer occurrences = dictionary.getValue(word);
		if (occurrences == null) {
			dictionary.insert(word, 0);
		} else {
			dictionary.insert(word, occurrences + 1);
		}
		return dictionary;
	}


	// Auto-correct with Similarity Metrics
	public String[] suggestCorrections(String word) {
		  word = word.toLowerCase();
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

	// Distance between word 1 & word 2 
	// we can count how many operations will BE required to reach from one string to another
	private int calculateLevenshteinDistance(String wrd1, String wrd2) {
		int[][] dp = new int[wrd1.length() + 1][wrd2.length() + 1];

		for (int x = 0; x <= wrd1.length(); x++) {
			for (int y = 0; y <= wrd2.length(); y++) {
				if (x == 0) {
					dp[x][y] = y;
				} else if (y == 0) {
					dp[x][y] = x;
				} else {
					dp[x][y] = min(dp[x - 1][y - 1] + costOfSubstitution(wrd1.charAt(x - 1), wrd2.charAt(y - 1)),
							dp[x - 1][y] + 1, dp[x][y - 1] + 1);
				}
			}
		}
		
		// Returning the difference
		return dp[wrd1.length()][wrd2.length()];
	}

	
	// costOfSubstitution used by levisatn distance
	private int costOfSubstitution(char a, char b) {
		return a == b ? 0 : 1;
	}


	// read saved dat files which should have dictionary variable at the savedObject path
	public static SplayTree<String, Integer> readDatFile() {
		try {
			FileInputStream inputStreamDat = new FileInputStream(savedObject);
			ObjectInputStream objectReader = new ObjectInputStream(inputStreamDat);
			Object object = objectReader.readObject();
			dictionary = (SplayTree<String, Integer>) object;
			// reader close
			objectReader.close();
			inputStreamDat.close();
			return dictionary;
			
		} 
		// Exception Handling: - file
		catch (Exception expn) {
			expn.printStackTrace();
			// Printing errors
			System.out.println("Error: Problem occured while reading file");
			System.out.println("Check file directory & paths given to file");
			return null;
		}
	}
	

	// save dictionary array list to savedObject path
	public static void saveDatFile(SplayTree<String, Integer> dictionary) {

		try {
			FileOutputStream outputStream = new FileOutputStream(savedObject);
			ObjectOutputStream fileWriterDat = new ObjectOutputStream(outputStream);
			// reader close
			// write all objects to file
			fileWriterDat.writeObject(dictionary);
			fileWriterDat.close();
			outputStream.close();
		} 
		// Exception Handling: - file
		catch (Exception expn) {
			expn.printStackTrace();
			// Printing errors
			System.out.println("Error :- object is not getting saved in saved objects");
			System.out.println("Check file directory");
		}
	}
	
	private int min(int... numbers) {
		return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
	}
}