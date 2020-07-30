package main.city.street;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import main.building.Building;
import main.building.BuildingLocation;
import main.city.City;
import main.util.Calculator;
import main.util.CollisionDetector;
import main.util.Randomizer;

public class StreetGenerator {
	private static long timeSpentGeneratingStreets = 0;
	private static final int MAX_ATTEMPTS = 20;
	private static final int MIN_STREET_LENGTH = 20;
	private static final int MAX_STREET_LENGTH = 90;
	private static final int MIN_STREET_WIDTH = 4;
	private static final int MAX_STREET_WIDTH = 10;
	
	private StreetGenerator() {
		//Hidden Constructor
	}
	
	public static Street generateStreet(City city, Building buildingAtIntersection) {
		long startTime = new Date().getTime();
		Street newStreet = new Street(generateStreetName(city), city);
		int width = Randomizer.generateRandomNumber(MIN_STREET_WIDTH, MAX_STREET_WIDTH);
		BuildingLocation loc = buildingAtIntersection.getLocation();
		double angle = Randomizer.generateRandomStreetAngle(loc.getRotationRadians());
		Point2D start = getValidStartingLocation(city, buildingAtIntersection, width, angle);
		if (start == null) {
			timeSpentGeneratingStreets += new Date().getTime() - startTime;
			return null;
		}
		double length = buildingAtIntersection.getBuildingType().getRadius() * 2;
		Point2D end = Calculator.getPointByOriginAngleAndDistance(start, angle, length);
		Line2D line = new Line2D.Double(start, end);
		while (length > 1 && !CollisionDetector.isStreetLocationValid(line, width / 2, city)) {
			length -= 1;
			end = Calculator.getPointByOriginAngleAndDistance(start, angle, length);
		}
		Point2D middle = Calculator.getPointBetween(start, end);
		newStreet.addSegment(new StreetSegment(newStreet, middle, end, width));
		newStreet.addSegment(new StreetSegment(newStreet, middle, start, width));
//		city.renderMap("C:/CityGenerator/newStreet" + new Date().getTime());
		return newStreet;
	}
	
	private static Point2D getValidStartingLocation(City city, Building buildingAtIntersection, int width, double angle) {
		BuildingLocation loc = buildingAtIntersection.getLocation();
		double perpendicularAngle = (angle + Math.PI / 2) % (2 * Math.PI);
		double radiusMultiple = Math.abs(Math.cos(angle - loc.getRotationRadians())) + Math.abs(Math.sin(angle - loc.getRotationRadians()));

		int radius = buildingAtIntersection.getBuildingType().getRadius();
		Point2D start;
		Point2D start1 = Calculator.getPointByOriginAngleAndDistance(loc.getCenter(), perpendicularAngle, (radius * radiusMultiple + width + 1));
		Point2D start2 = Calculator.getPointByOriginAngleAndDistance(loc.getCenter(), perpendicularAngle + Math.PI, (radius * radiusMultiple + width + 1));
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
		if (buildingAtIntersection.getStreetSegment() == null) {
			return start;
		}
		return Calculator.getPointOfIntersectionBetweenTwoLines(angle, start, buildingAtIntersection.getStreetSegment().getAngle(), buildingAtIntersection.getStreetSegment().getLine().getP1());
	}
	
	public static List<StreetSegment> generateStreetSegmentsForWholeCity(City city) {
		List<StreetSegment> newSegments = new ArrayList<>();
		for (Street street : city.getStreets()) {
			newSegments.addAll(generateStreetSegmentsForStreet(street));
		}
//		city.renderMap("C:/CityGenerator/longerStreets" + new Date().getTime());
		return newSegments;
	}
	
	public static List<StreetSegment> generateStreetSegmentsForStreet(Street street) {
		List<StreetSegment> newSegments = new ArrayList<>();
		for (StreetSegment end : street.getEnds()) {
			StreetSegment newSegment = generateStreetSegment(end);
			if (newSegment != null) {
				newSegments.add(newSegment);
			}
		}
		for (StreetSegment segment : newSegments) {
			segment.getStreet().addSegment(segment);
		}
		return newSegments;
	}
	
	private static StreetSegment generateStreetSegment(StreetSegment parent) {
		if (parent.isBlocked()) {
			return null;
		}
		Point2D start = parent.getLine().getP2();
		Point2D end;
		int attempts = 0;
		int width;
		double angle;
		do {
			width = parent.getWidth() + Randomizer.generateRandomNumber(-1, 1);
			if (width < MIN_STREET_WIDTH) {
				width = MIN_STREET_WIDTH;
			} else if (width > MAX_STREET_WIDTH) {
				width = MAX_STREET_WIDTH;
			}
			int length = Randomizer.generateRandomNumber(MIN_STREET_LENGTH, MAX_STREET_LENGTH);
			angle = Randomizer.generateRandomStreetAngle(parent.getAngle());
			end = new Point2D.Double(start.getX() + length * Math.cos(angle), start.getY() + length * Math.sin(angle));
			attempts++;
			if (attempts > MAX_ATTEMPTS) {
				parent.setBlocked(true);
				return null;
			}
		} while (!CollisionDetector.isStreetLocationValid(new Line2D.Double(start, end), width / 2, parent.getStreet().getCity()));
		return new StreetSegment(parent, end, width);
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

	public static long getTimeSpentGeneratingStreets() {
		return timeSpentGeneratingStreets;
	}
}
