package main.building;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import main.city.City;
import main.city.street.Street;
import main.city.street.StreetGenerator;
import main.city.street.StreetSegment;
import main.person.Person;
import main.util.Constants;
import main.util.Pair;
import main.util.Randomizer;

public class BuildingGenerator {
	private static final int MAX_ATTEMPTS = 20;
	private static long timeSpentGeneratingBuildings = 0;
	private static long timeSpentCollectingLocations = 0;
	private static long timeSpentComparingLocations = 0;
	
	private BuildingGenerator() {
		//Hidden constructor
	}
	
	public static Building generateBuilding(BuildingType buildingType, Person proprieter, City city) {
		long start = new Date().getTime();
		Pair<BuildingLocation, StreetSegment> bestLocation = getBestLocation(buildingType, city);
		while (bestLocation == null) {
			bestLocation = getBestLocation(buildingType, StreetGenerator.generateStreetSegmentsForWholeCity(city), city);
		}
		timeSpentGeneratingBuildings += new Date().getTime() - start;
		return new Building(generateBuildingName(buildingType, proprieter), buildingType, proprieter, bestLocation.getKey(), bestLocation.getValue());
	}
	
	public static Pair<BuildingLocation, StreetSegment> getBestLocation(BuildingType buildingType, City city) {
		List<StreetSegment> streetSegments = new ArrayList<>();
		for (Street street : city.getStreets()) {
			streetSegments.addAll(street.getSegments());
		}
		return getBestLocation(buildingType, streetSegments, city);
	}
	
	public static Pair<BuildingLocation, StreetSegment> getBestLocation(BuildingType buildingType, List<StreetSegment> streetSegments, City city) {
		Point2D townCenter = city.getTownCenter();
		double bestDistance = -1;
		BuildingLocation bestLocation = null;
		Map<BuildingLocation, StreetSegment> locations = new HashMap<>();

		long time1 = new Date().getTime();
		for (StreetSegment segment : streetSegments) {
			for (BuildingLocation location : segment.getAllLocations(buildingType)) {
				locations.put(location, segment);
			}
		}
		long time2 = new Date().getTime();
		for (BuildingLocation location : locations.keySet()) {
			double distance = location.getCenter().distance(townCenter);
			if (bestLocation == null || distance < bestDistance) {
				bestLocation = location;
				bestDistance = distance;
			}
		}
		long time3 = new Date().getTime();

		timeSpentCollectingLocations += time2 - time1;
		timeSpentComparingLocations += time3 - time2;
		if (bestLocation != null) {
			return new Pair<>(bestLocation, locations.get(bestLocation));
		} else {
			return null;
		}
	}
	
	public static String generateBuildingName(BuildingType buildingType, Person proprieter) {
		try {
			int attempts = 0;
			String name;
			do {
				name = Randomizer.pickElement(buildingType.getNamingRules().getNamingConventions());
				name = name.replaceAll(Constants.FIRST_NAME_PLACEHOLDER, proprieter.getFirstName())
						.replaceAll(Constants.FAMILY_NAME_PLACEHOLDER, proprieter.getFamilyName())
						.replaceAll(Constants.CITY_NAME_PLACEHOLDER, proprieter.getHomeCity().getName());
				name = replacePlaceholdersWithUniqueWords(name, Constants.ADJECTIVE_PLACEHOLDER, buildingType.getNamingRules().getAdjectives());
				name = replacePlaceholdersWithUniqueWords(name, Constants.NOUN_PLACEHOLDER, buildingType.getNamingRules().getNouns());
				name = replacePlaceholdersWithUniqueWords(name, Constants.PRODUCT_PLACEHOLDER, buildingType.getNamingRules().getProducts());
				name = replacePlaceholdersWithUniqueWords(name, Constants.BUILDING_TYPE_PLACEHOLDER, buildingType.getNamingRules().getBuildingTypes());
				attempts++;
			} while (!buildingType.isHouse() && proprieter.getHomeCity().containsBuildingWithName(name) && attempts < MAX_ATTEMPTS);
			return name;
		} catch (Exception e) {
			return proprieter.getFirstName() + "'s " + buildingType.getName();
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

	public static long getTimeSpentGeneratingBuildings() {
		return timeSpentGeneratingBuildings;
	}
	public static long getTimeSpentCollectingLocations() {
		return timeSpentCollectingLocations;
	}
	public static long getTimeSpentComparingLocations() {
		return timeSpentComparingLocations;
	}
}
