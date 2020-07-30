package main.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import main.building.Building;
import main.building.BuildingLocation;
import main.city.City;
import main.city.Sector;
import main.city.street.StreetSegment;
import main.exceptions.BuildingCollisionException;
import main.exceptions.StreetCollisionException;

public class CollisionDetector {
	private static final double MARGIN_OF_ERROR = 0.0001;
	private static long timeSpentDetectingBuildingCollisions = 0;
	private static long timeSpentDetectingStreetCollisions = 0;
	private static long timeSpentDetectingPointCollisions = 0;
	
	private CollisionDetector() {
		//Hidden constructor
	}
	
	public static boolean isBuildingLocationValid(BuildingLocation location, City city) {
		try {
			checkBuildingLocationValid(location, city);
			return true;
		} catch (BuildingCollisionException | StreetCollisionException e) {
			return false;
		}
	}
	
	public static void checkBuildingLocationValid(BuildingLocation location, City city) throws BuildingCollisionException, StreetCollisionException {
		Set<Building> buildings = new HashSet<>();
		Set<StreetSegment> segments = new HashSet<>();
		for (Sector sector : city.getSectors(location.getBounds2D())) {
			buildings.addAll(sector.getBuildings());
			segments.addAll(sector.getStreetSegments());
		}
		checkBuildingLocationValid(location, buildings, segments);
	}
	
	public static void checkBuildingLocationValid(BuildingLocation location, Set<Building> buildings, Set<StreetSegment> segments) throws BuildingCollisionException, StreetCollisionException {
		long start = new Date().getTime();
		for (Building building : buildings) {
			if (building.getLocation().overlaps(location)) {
				timeSpentDetectingBuildingCollisions = getTimeSpentDetectingBuildingCollisions() + new Date().getTime() - start;
				throw new BuildingCollisionException();
			}
		}
		timeSpentDetectingBuildingCollisions = getTimeSpentDetectingBuildingCollisions() + new Date().getTime() - start;
		long start2 = new Date().getTime();
		for (StreetSegment segment : segments) {
			if (buildingCollidesWithStreet(location, segment.getLine(), segment.getWidth() / 2.0)) {
				timeSpentDetectingStreetCollisions = getTimeSpentDetectingStreetCollisions() + new Date().getTime() - start2;
				throw new StreetCollisionException();
			}
		}
		timeSpentDetectingStreetCollisions = getTimeSpentDetectingStreetCollisions() + new Date().getTime() - start2;
	}
	
	public static boolean isStreetLocationValid(Line2D line, double halfStreetWidth, City city) {
		try {
			checkStreetLocationValid(line, halfStreetWidth, city);
			return true;
		} catch (StreetCollisionException e) {
			return false;
		}
	}
	
	public static void checkStreetLocationValid(Line2D line, double halfStreetWidth, City city) throws StreetCollisionException {
		Set<Building> buildings = new HashSet<>();
		for (Sector sector : city.getSectors(line.getBounds2D())) {
			buildings.addAll(sector.getBuildings());
		}
		checkStreetLocationValid(line, halfStreetWidth, buildings);
	}
	
	public static void checkStreetLocationValid(Line2D line, double halfStreetWidth, Set<Building> buildings) throws StreetCollisionException {
		long start = new Date().getTime();
		for (Building building : buildings) {
			if (buildingCollidesWithStreet(building.getLocation(), line, halfStreetWidth)) {
				timeSpentDetectingStreetCollisions = getTimeSpentDetectingStreetCollisions() + new Date().getTime() - start;
				throw new StreetCollisionException();
			}
		}
		timeSpentDetectingStreetCollisions = getTimeSpentDetectingStreetCollisions() + new Date().getTime() - start;
	}
	
	public static boolean pointCollidesWithBuilding(Point2D point, City city) {
		long start = new Date().getTime();
		for (Building building : city.getBuildings()) {
			if (building.getLocation().contains(point)) {
				timeSpentDetectingPointCollisions = getTimeSpentDetectingPointCollisions() + new Date().getTime() - start;
				return true;
			}
		}
		timeSpentDetectingPointCollisions = getTimeSpentDetectingPointCollisions() + new Date().getTime() - start;
		return false;
	}
	
	public static boolean buildingCollidesWithStreet(BuildingLocation buildingLocation, Line2D line, double halfStreetWidth) {
		return buildingLocation.intersects(line) || buildingLocation.comesWithinDistanceOfLine(halfStreetWidth - MARGIN_OF_ERROR, line) || buildingLocation.contains(line.getP1());
	}

	public static long getTimeSpentDetectingBuildingCollisions() {
		return timeSpentDetectingBuildingCollisions;
	}

	public static long getTimeSpentDetectingStreetCollisions() {
		return timeSpentDetectingStreetCollisions;
	}

	public static long getTimeSpentDetectingPointCollisions() {
		return timeSpentDetectingPointCollisions;
	}
}
