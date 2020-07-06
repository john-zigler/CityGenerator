package building;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import person.Person;
import util.Constants;
import util.Randomizer;

public class BuildingGenerator {
	private static final int MAX_ATTEMPTS = 20;
	
	private BuildingGenerator() {
		//Hidden constructor
	}
	
	public static Building generateBuilding(BuildingType type, Person proprieter) {
		return new Building(generateBuildingName(type, proprieter), type, proprieter);
	}
	
	public static String generateBuildingName(BuildingType type, Person proprieter) {
		try {
			int attempts = 0;
			String name;
			do {
				name = Randomizer.pickElement(type.getNamingRules().getNamingConventions());
				name = name.replaceAll(Constants.FIRST_NAME_PLACEHOLDER, proprieter.getFirstName())
						.replaceAll(Constants.FAMILY_NAME_PLACEHOLDER, proprieter.getFamilyName())
						.replaceAll(Constants.CITY_NAME_PLACEHOLDER, proprieter.getHomeCity().getName());
				name = replacePlaceholdersWithUniqueWords(name, Constants.ADJECTIVE_PLACEHOLDER, type.getNamingRules().getAdjectives());
				name = replacePlaceholdersWithUniqueWords(name, Constants.NOUN_PLACEHOLDER, type.getNamingRules().getNouns());
				name = replacePlaceholdersWithUniqueWords(name, Constants.PRODUCT_PLACEHOLDER, type.getNamingRules().getProducts());
				name = replacePlaceholdersWithUniqueWords(name, Constants.BUILDING_TYPE_PLACEHOLDER, type.getNamingRules().getBuildingTypes());
				attempts++;
			} while (!type.isHouse() && proprieter.getHomeCity().containsBuildingWithName(name) && attempts < MAX_ATTEMPTS);
			return name;
		} catch (Exception e) {
//			System.err.println("Encountered an error naming a(n) " + type.getName() + ". Using default convention.");
			return proprieter.getFirstName() + "'s " + type.getName();
		}
	}
	
	private static String replacePlaceholdersWithUniqueWords(String originalString, String placeholder, List<String> options) {
		Set<String> usedWords = new HashSet<>();
		String name = originalString;
		while (name.contains(placeholder)) {
			String nextWord = Randomizer.pickElement(options);
			int attempts = 0;
			while (usedWords.contains(nextWord) && attempts < MAX_ATTEMPTS) {
				nextWord = Randomizer.pickElement(options);
				attempts++;
			}
			name = name.replaceFirst(placeholder, nextWord);
			usedWords.add(nextWord);
		}
		return name;
	}
}
