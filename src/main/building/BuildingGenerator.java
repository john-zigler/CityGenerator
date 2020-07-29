package main.building;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import main.city.City;
import main.city.street.Street;
import main.city.street.StreetSegment;
import main.exceptions.BuildingCollisionException;
import main.exceptions.StreetCollisionException;
import main.person.Person;
import main.util.CollisionDetector;
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
		if (bestLocation == null) {
			timeSpentGeneratingBuildings += new Date().getTime() - start;
			outputInvalidLocationsConsidered(buildingType, city);
			throw new RuntimeException("Unable to place new " + buildingType.getName());
		}
		timeSpentGeneratingBuildings += new Date().getTime() - start;
		return new Building(generateBuildingName(buildingType, proprieter), buildingType, proprieter, bestLocation.getKey(), bestLocation.getValue());
	}
	
	private static Pair<BuildingLocation, StreetSegment> getBestLocation(BuildingType buildingType, City city) {
		Point2D townCenter = city.getTownCenter();
		double bestDistance = -1;
		BuildingLocation bestLocation = null;
		Map<BuildingLocation, StreetSegment> locations = new HashMap<>();

		long time1 = new Date().getTime();
		for (Street street : city.getStreets()) {
			for (StreetSegment segment : street.getSegments()) {
				for (BuildingLocation location : street.getAllLocations(buildingType, city)) {
					locations.put(location, segment);
				}
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
	
	private static void outputInvalidLocationsConsidered(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		for (Street street : city.getStreets()) {
			locations.addAll(street.getAllLocations(buildingType, city));
		}
		
		BufferedImage mapImage = city.generateMap();
		Graphics2D mapGraphics = mapImage.createGraphics();
		
		for (BuildingLocation location : locations) {
			try {
				CollisionDetector.checkBuildingLocationValid(location, city);
				mapGraphics.setColor(Color.GREEN);
				mapGraphics.drawOval((int) (location.getCenter().getX() - buildingType.getRadius() - city.getMinX()),
						(int) (location.getCenter().getY() - buildingType.getRadius() - city.getMinY()),
						buildingType.getRadius() * 2, buildingType.getRadius() * 2);
			} catch (BuildingCollisionException e) {
				mapGraphics.setColor(Color.RED);
				mapGraphics.drawOval((int) (location.getCenter().getX() - buildingType.getRadius() - city.getMinX()),
						(int) (location.getCenter().getY() - buildingType.getRadius() - city.getMinY()),
						buildingType.getRadius() * 2, buildingType.getRadius() * 2);
			} catch (StreetCollisionException e) {
				mapGraphics.setColor(Color.ORANGE);
				mapGraphics.drawOval((int) (location.getCenter().getX() - buildingType.getRadius() - city.getMinX()),
						(int) (location.getCenter().getY() - buildingType.getRadius() - city.getMinY()),
						buildingType.getRadius() * 2, buildingType.getRadius() * 2);
			}
		}
		
		try {
			ImageIO.write(mapImage, "png", new File("C:/CityGenerator/" + buildingType.getName() + (new Date().getTime()) + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
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
//			System.err.println("Encountered an error naming a(n) " + type.getName() + ". Using default convention.");
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
