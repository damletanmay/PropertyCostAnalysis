package propertyCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import propertyCost.AutocompleteTrieMap.TrieAutoComplete;
import propertyCost.Property.TypeofHouse;

public class mainClass {

	public static final List<City> canadaCities = City.loadCityData(); // cities are loaded into canadaCities object
	public static final String userDirectory = System.getProperty("user.dir"); // getting user path

	// driver function for the whole program
	public static void main(String[] args) {

		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);

		System.out.println(
				  "            .------------------------------------------------------------------------------. \n"
				+ "            |██████╗ ██████╗  ██████╗ ██████╗ ███████╗██████╗ ████████╗██╗ ██████╗ ██████╗ | \n"
				+ "            |██╔══██╗██╔══██╗██╔═══██╗██╔══██╗██╔════╝██╔══██╗╚══██╔══╝██║██╔════╝██╔═══██╗| \n"
				+ "            |██████╔╝██████╔╝██║   ██║██████╔╝█████╗  ██████╔╝   ██║   ██║██║     ██║   ██║| \n"
				+ "            |██╔═══╝ ██╔══██╗██║   ██║██╔═══╝ ██╔══╝  ██╔══██╗   ██║   ██║██║     ██║   ██║| \n"
				+ "            |██║     ██║  ██║╚██████╔╝██║     ███████╗██║  ██║   ██║   ██║╚██████╗╚██████╔╝| \n"
				+ "            |╚═╝     ╚═╝  ╚═╝ ╚═════╝ ╚═╝     ╚══════╝╚═╝  ╚═╝   ╚═╝   ╚═╝ ╚═════╝ ╚═════╝ | \n"
				+ "            '------------------------------------------------------------------------------' \n"
				+ "-- -- -- ---- -- -- -- WELCOME TO PROPERTICO - A PROPERTY ANALYSIS APP -- -- -- ---- -- -- --\n");

		System.out.println("Available Cities Data:");
		int i = 1;
		for (City city : canadaCities) {
			System.out.println(i + ". " + city.city);
			i++;
		}
		System.out.println("Press exit at any time to exit the program");

		// TANMAY'S WEB CRAWLER AND HTML PARSER
		ArrayList<Property> allProperties = ScrapperCrawler.getProperties(canadaCities);
		
		// DHRUV'S Spell Checker And Search Frequency implemented with splay tree
		SpellChecker spellCheckAndSearchFrequency = new SpellChecker(canadaCities); // initialize spell checker

		// VATSAL'S Auto Completed Implemented with Trie
		TrieAutoComplete autocomplete = new TrieAutoComplete();
		AutocompleteTrieMap.loadDictionary(autocomplete, canadaCities);// initialize autocomplete

		// SHAURYAN'S Inverted Indexing Implemented with Trie
		TrieClass loadedTrie = InvertedIndexing.getLoadedTrie(canadaCities);

		// SHAURYAN'S Frequency Counter Implemented with HashMap
		FrequencyCounter frequencyCounter = new FrequencyCounter(allProperties); // loads all property's cities data

		// ARYA'S Page Ranking implemented with splay tree
		PageRanking pageRanking = new PageRanking(loadedTrie, canadaCities);

		// ARYA'S Pattern Matching Implemented with HashMap
		PatternMatching patternMatching = new PatternMatching(pageRanking, canadaCities);

		String userInput = "";

		// infinite loop
		do {

			System.out.println("\nEnter a city or a Zipcode:");

			userInput = scan.nextLine().trim().strip().replaceAll("\t","").replaceAll(" ","");

			if (userInput.toLowerCase().contains("exit")) {
				System.out.println("Program Terminated ! ");
				System.exit(0); // exit system
			}

			// if user input is null then prompt again because if this is removed the enter
			// character might go into the next question
			if (userInput.length() == 0) {
				System.out.println("\nCity or Postal Code can't be blank, Try Again!");
				continue;
			}

			// VATSAL'S DATA VALIDATION FEATURE
			boolean isCity = DataValidation.isCity(userInput);
			boolean isPostalCode = DataValidation.isValidPostalCode(userInput.replaceAll(" ", ""));
			
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

							// we need the first suggestion only
							System.out.println("Did you mean, " + autoCompleteSuggestions.get(0) + " ? (Y/n)");

							String input = scan.next().trim().strip().replaceAll("\t","").replaceAll(" ","");
							
							scan.nextLine();
							
							if (input.toLowerCase().contains("exit")) {
								System.out.println("Program Terminated ! ");
								System.exit(0); // exit system
							}

							char cont = input.charAt(0);
							if (cont == 'Y' || cont == 'y') {
								// make user input as the correct word to display word below
								userInput = autoCompleteSuggestions.get(0);
							} else {
								continue;
							}

						} else {

							System.out.println("\nThe word " + userInput + " is not spelled correctly");

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

					// for displaying features
					displayFeatures(userInput, spellCheckAndSearchFrequency, loadedTrie, pageRanking, frequencyCounter,
							patternMatching);

					// ask and print listings
					printListings(allProperties, userInput, scan);

				} else {

					// search by postal code
					// extract first letters of a string
					String fullZip = userInput;
					String zip = userInput.substring(0, 3).toLowerCase();
					boolean zipFound = false;

					for (City city : canadaCities) {
						for (String zipCode : city.zipCode) {
							if (zipCode.toLowerCase().equals(zip.toLowerCase())) {
								// zipcode found for a city then user input
								zipFound = true;
								userInput = city.city.toLowerCase(); // change user input
								break;
							}
						}
					}

					// if zipcode found then display features for that userinput
					if (zipFound == true) {
						System.out.println("The Zipcode " + fullZip + " maps to city " + userInput);
						// for displaying features
						displayFeatures(userInput, spellCheckAndSearchFrequency, loadedTrie, pageRanking,
								frequencyCounter, patternMatching);
						printListings(allProperties, userInput, scan);
					} else {
						System.out.println("The Postal Code is not found in the database");
					}
				}

			} else {
				System.out.println("Enter a valid city or postal code!");
			}

			// prompt user to continue
			
			System.out.println("Do you want to continue ? (Y/n):");
			String input = scan.next().trim().strip().replaceAll("\t","").replaceAll(" ","");
			scan.nextLine();
			char cont = input.charAt(0);
			
			if (cont == 'Y' || cont == 'y') {
				System.out.println(); // for space
				continue;
			}
			System.out.println("Program Terminated ! ");
			break; // exit the never ending loop

		} while (true);
	}

	private static void displayFeatures(String userInput, SpellChecker spellCheckAndSearchFrequency, TrieClass loadedTrie,
			PageRanking pageRanking, FrequencyCounter frequencyCounter, PatternMatching patternMatching) {
		// word is entered correctly or made correct then add that word to search
		// frequency
		// and display search frequency

		spellCheckAndSearchFrequency.addWord(userInput); // add word to search database
		// save search frequency after each search
		SpellChecker.saveDatFile(spellCheckAndSearchFrequency.dictionary);

		Integer frequency = spellCheckAndSearchFrequency.searchFrequency(userInput);
		System.out.println("\nThe city " + userInput + " has been searched for " + frequency + " times.");

		System.out.println();

		// Inverted Indexing
		Set<String> userInputInFiles = loadedTrie.searchNode(userInput);
		StringBuilder str = new StringBuilder();
		for (String document : userInputInFiles) {
			str.append(document);
			str.append(",");
		}
		System.out.println("The word " + userInput + " is found " + userInputInFiles.size() + " files (INVERTED INDEXING)");
//		System.out.println(str.toString()); // print all files 
		System.out.println();

		// Page Ranking
		pageRanking.printNFiles(userInput, 10);

		System.out.println();
		// Frequency Counter
		System.out.println(
				frequencyCounter.getFrequency(userInput) + " Listings Found for " + userInput + " (FREQUENCY COUNT)");
		System.out.println();

		// Pattern Matching
		patternMatching.printEmailPhoneNumber(userInput);
	}

	// ask for filters and print analysis of entered criteria
	private static void printListings(ArrayList<Property> allProperties, String userInput, Scanner scan) {

		while (true) {

			System.out.println("\nAre you looking for a House or an Apartment or both ?");
			String houseOrApartment = scan.nextLine().trim().strip().replaceAll("\t","").replaceAll(" ","");

			if (houseOrApartment.toLowerCase().contains("exit")) {
				System.out.println("Program Terminated ! ");
				System.exit(0); // exit system
			}

			TypeofHouse houseType;

			if (houseOrApartment.toLowerCase().contains("both") || (houseOrApartment.toLowerCase().contains("house")
					&& houseOrApartment.toLowerCase().contains("apartment"))) {
				houseType = null;
			} else {
				if (houseOrApartment.toLowerCase().contains("house")) {
					houseType = TypeofHouse.House;
				} else if (houseOrApartment.toLowerCase().contains("apartment")) {
					houseType = TypeofHouse.Apartment;
				} else {
					System.out.println("Enter only House, Apartment or both!");
					continue;
				}
			}

			try {
				System.out.println("Enter The Number of Bedrooms you want:");
				String bed = scan.next().trim().strip().replaceAll("\t","");
				scan.nextLine();
				if (bed.toLowerCase().contains("exit")) {
					System.out.println("Program Terminated ! ");
					System.exit(0); // exit system
				}
				
				int bedrooms = Integer.parseInt(bed);

				System.out.println("Enter The Number of Bathrooms you want:");
				String bath = scan.next().trim().strip().replaceAll("\t","");
				scan.nextLine();
				if (bath.toLowerCase().contains("exit")) {
					System.out.println("Program Terminated ! ");
					System.exit(0); // exit system
				}
				int bathrooms = Integer.parseInt(bath);

				System.out.println("Enter Minimum Price in CAD:");
				String minP = scan.next().trim().strip().replaceAll("\t","");
				scan.nextLine();
				
				if (minP.toLowerCase().contains("exit")) {
					System.exit(0); // exit system
				}
				float minPrice = Float.parseFloat(minP);
				

				System.out.println("Enter Maximum Price in CAD:");
				String maxP = scan.next().trim().strip().replaceAll("\t","");
				scan.nextLine();

				if (maxP.toLowerCase().contains("exit")) {
					System.out.println("Program Terminated ! ");
					System.exit(0); // exit system
				}
				float maxPrice = Float.parseFloat(maxP);
				
				if (minPrice < maxPrice) {

					Property.printFilteredProperties(allProperties, userInput.toLowerCase(), bedrooms, bathrooms, houseType,
							minPrice, maxPrice, canadaCities);	
				}
				else {
					System.out.println("Minimum Price Should be lesser than maximum price");
					System.out.println("Please Try Again!");
					continue;
				}
				
				break; // break loop if everything okay

			} catch (Exception e) {
				System.out.println("Enter ONLY Numbers for Bedrooms, Bathrooms, Minimum and Maximum Price !");
			}

		}
	}

}
