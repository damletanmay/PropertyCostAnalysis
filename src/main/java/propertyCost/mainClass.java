package propertyCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import propertyCost.AutocompleteTrieMap.TrieAutoComplete;

public class mainClass {

	public static final List<City> canadaCities = City.loadCityData(); // cities are loaded into canadaCities object

	// driver function for the whole program
	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);

		System.out.println(
				"-- -- -- ---- -- -- -- WELCOME TO PROPERTICO - A PROPERTY ANALYSIS APP -- -- -- ---- -- -- --");
		System.out.println("Available Cities Data:");
		int i = 1;
		for (City city : canadaCities) {
			System.out.println(i + ". " + city.city);
			i++;
		}
		System.out.println("Press exit to exit the program");

		// TANMAY'S WEB CRAWLER AND HTML PARSER 
		ArrayList<Property> allProperties = ScrapperCrawler.getProperties(canadaCities); 

		// DHRUV'S Spell Checker And Search Frequency implemented by splay tree
		SpellChecker spellCheckAndSearchFrequency = new SpellChecker(canadaCities); // initialize spell checker
		
		// VATSAL'S Auto Completed Implemented with Trie 
		TrieAutoComplete autocomplete = new TrieAutoComplete();
		AutocompleteTrieMap.loadDictionary(autocomplete, canadaCities);// initialize autocomplete

		// SHAURYAN'S Inverted Indexing Implemented with Trie  
//		Trie invertedIndexing = InvertedIndexing.getTrieOfInvertedIndexing(canadaCities);
		
		// SHAURYAN'S Frequency Counter Implemented with HashMap
		FrequencyCounter frequencyCounter = new FrequencyCounter(allProperties); // loads all property's cities data
		
		// ARYA'S Pattern Matching
		
		
		String userInput = "";
		
		// infinite loop 
		do {

			System.out.println("Enter a city or a Zipcode:");

			userInput = scan.nextLine();
			
			// if user input is null then prompt again because if this is removed the enter character might go into the next question
			if (userInput.length() == 0) {
				System.out.println("City or Postal Code can't be blank, Try Again!");
				continue;
			}
			
			boolean isCity = DataValidation.isCity(userInput); // VATSAL'S DATA VALIDATION FEATURE
			boolean isPostalCode = DataValidation.isValidPostalCode(userInput.replaceAll(" ", "")); // VATSAL'S DATA VALIDATION FEATURE

			// check if user input is a city or a postal code
			if (isCity || isPostalCode) {

				if (isCity) {

					// search by city

					boolean isSpelledCorrectly = spellCheckAndSearchFrequency.spellCheck(userInput);

					List<String> autoCompleteSuggestions = autocomplete.autocomplete(userInput);

					if (!isSpelledCorrectly) {

						// if word is not spelled correctly there are 2 things that can happen
						// first we would be able to auto complete that word if the starting is correct
						// or
						// we can show users new suggestions
						// the first priority is to auto complete the user words
						// else we can go for spell checking suggestions

						if (!autoCompleteSuggestions.isEmpty()) {

							System.out.println("Did you mean, " + autoCompleteSuggestions.get(0) + " ? (Y/n)"); // we need the first suggestion only

							char cont = scan.next().charAt(0);
							scan.nextLine(); // to take the enter space and whatever garbage user enter

							if (cont == 'Y' || cont == 'y') {
								// make user input as the correct word to display word below
								userInput = autoCompleteSuggestions.get(0); 
							} else {
								continue;
							}

						} else {

							System.out.println("The word " + userInput + " is not spelled correctly");

							String[] suggestions = spellCheckAndSearchFrequency.suggestCorrections(userInput);
							if (suggestions.length != 0) {
								System.out.println("Try entering below Suggestions:");
								for (String suggestion : suggestions) {
									System.out.println(suggestion);
								}
								continue; // start loop again
							} else {
								System.out.println("No Suggestions for this city found in the database");
								System.out.println("Try again");
								continue; // start loop again
							}
						}
					}

					// word is entered correctly or made correct then add that word to search frequency 
					// and display search frequency
					spellCheckAndSearchFrequency.addWord(userInput); // add word to search database
					Integer frequency = spellCheckAndSearchFrequency.searchFrequency(userInput);
					System.out.println("The city " + userInput + " has been searched for " + frequency + " times.");
					
					// Inverted Indexing
//					Set<String> userInputInFiles = invertedIndexing.search(userInput);				
//					StringBuilder str = new StringBuilder();
//					for (String document: userInputInFiles) {
//						str.append(document);
//						str.append(",");
//					}
//					System.out.println("The city " + userInput + " is found " + userInputInFiles.size() + " files:");
//					System.out.println(str.toString());
					
					// Frequency Counter 
					System.out.println(frequencyCounter.getFrequency(userInput)+" Listings Found for " + userInput);
					
					// Pattern Matching
					
					
				} else {
					// search by postal code
					System.out.println("Postal Code");
				}

			} else {
				System.out.println("Enter a valid city or postal code!");
			}

			// prompt user to continue
			System.out.println("Do you want to continue ? (Y/n):");
			char cont = scan.next().charAt(0);
			scan.nextLine(); // to take the enter space

			if (cont == 'Y' || cont == 'y') {
				System.out.println(); // for space
				continue;
			}

			break; // exit the never ending loop

		} while (true);
	}

}
