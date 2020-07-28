package main.city.street;

import main.building.Building;
import main.building.BuildingLocation;
import main.city.City;
import main.util.CollisionDetector;
import main.util.Location;
import main.util.Randomizer;

public class StreetGenerator {
	private static final int MAX_ATTEMPTS = 20;
	private static final int MIN_STREET_LENGTH = 50;
	private static final int MAX_STREET_LENGTH = 900; //Max distance from origin intersection
	private static final int MIN_STREET_WIDTH = 4;
	private static final int MAX_STREET_WIDTH = 10;
	
	private StreetGenerator() {
		//Hidden Constructor
	}
	
	public static Street generateStreet(City city, Building buildingAtIntersection) {
		Location end1, end2;
		int width;
		do {
			BuildingLocation loc = buildingAtIntersection.getLocation();
			double radians = Randomizer.generateRandomStreetAngleInRadians(loc.getRotationRadians());
			double perpendicularAngle = (radians + Math.PI / 2) % (2 * Math.PI);
			double radiusMultiple = Math.abs(Math.cos(radians - loc.getRotationRadians())) + Math.abs(Math.sin(radians - loc.getRotationRadians()));
			width = Randomizer.generateRandomNumber(MIN_STREET_WIDTH, MAX_STREET_WIDTH);

			int radius = buildingAtIntersection.getBuildingType().getRadius();
			Location start;
			Location start1 = new Location(loc.getX() + (radius * radiusMultiple + width + 1) * Math.cos(perpendicularAngle),
					loc.getY() + (radius * radiusMultiple + width + 1) * Math.sin(perpendicularAngle));
			Location start2 = new Location(loc.getX() - (radius * radiusMultiple + width + 1) * Math.cos(perpendicularAngle),
					loc.getY() - (radius * radiusMultiple + width + 1) * Math.sin(perpendicularAngle));
			if (CollisionDetector.pointCollidesWithBuilding(start1, city)) {
				if (CollisionDetector.pointCollidesWithBuilding(start2, city)) {
					//both points invalid
					System.err.println("Unable to place street");
					return null;
				} else {
					start = start2;
				}
			} else {
				if (CollisionDetector.pointCollidesWithBuilding(start2, city)) {
					start = start1;
				} else {
					if (Randomizer.rollAgainstOdds(0.5)) {
						start = start2;
					} else {
						start = start1;
					}
				}
			}
			
			int minIntersectionLength = (int) (radius * radiusMultiple);
			boolean isTJunction = Randomizer.rollAgainstOdds(0.5);
			int length1 = Randomizer.generateRandomNumber(MIN_STREET_LENGTH, MAX_STREET_LENGTH);
			int length2 = isTJunction ? minIntersectionLength : Randomizer.generateRandomNumber(MIN_STREET_LENGTH, MAX_STREET_LENGTH);
			end1 = new Location(start.getX() + length1 * Math.cos(radians), start.getY() + length1 * Math.sin(radians));
			end2 = new Location(start.getX() - length2 * Math.cos(radians), start.getY() - length2 * Math.sin(radians));
		} while (!CollisionDetector.isStreetLocationValid(end1, end2, width / 2, city));
		
		Street newStreet = new Street(generateStreetName(city), end1, end2, width);
		System.out.println("Adding " + newStreet.getName() + " from (" + end1.getX() + ", " + end1.getY() + ") to (" + end2.getX() + ", " + end2.getY() + ")");
		newStreet.addBuilding(buildingAtIntersection);
		return newStreet;
	}
	
	public static String generateStreetName(City city) {
		try {
			int attempts = 0;
			String name;
			do {
				attempts++;
				//TODO Choose from list of street names and naming conventions
				name = "Main Street";
			} while (city.containsStreetWithName(name) && attempts < MAX_ATTEMPTS);
			return name;
		} catch (Exception e) {
//			System.err.println("Encountered an error naming a(n) " + type.getName() + ". Using default convention.");
			return (city.getStreets().size() + 1) + " Avenue";
		}
	}
}
