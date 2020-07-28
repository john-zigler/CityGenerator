package main.util;

import main.building.Building;
import main.building.BuildingLocation;
import main.city.City;
import main.city.street.Street;
import main.exceptions.BuildingCollisionException;
import main.exceptions.StreetCollisionException;

public class CollisionDetector {
	private CollisionDetector() {
		//Hidden constructor
	}
	
	public static boolean isBuildingLocationValid(BuildingLocation location, double radius, City city) {
		try {
			checkBuildingLocationValid(location, radius, city);
			return true;
		} catch (BuildingCollisionException | StreetCollisionException e) {
			return false;
		}
	}
	
	public static void checkBuildingLocationValid(BuildingLocation location, double radius, City city) throws BuildingCollisionException, StreetCollisionException {
		for (Building building : city.getBuildings()) {
			if (Calculator.distanceBetweenPointsLessThanLength(location, building.getLocation(), building.getBuildingType().getRadius() + radius)) {
				throw new BuildingCollisionException();
			}
		}
		for (Street street : city.getStreets()) {
			if (buildingCollidesWithStreet(location, radius, street.getEnd1(), street.getEnd2(), street.getWidth() / 2.0)) {
				throw new StreetCollisionException();
			}
		}
	}
	
	public static boolean isStreetLocationValid(Location location1, Location location2, double halfStreetWidth, City city) {
		try {
			checkStreetLocationValid(location1, location2, halfStreetWidth, city);
			return true;
		} catch (StreetCollisionException e) {
			return false;
		}
	}
	
	public static void checkStreetLocationValid(Location location1, Location location2, double halfStreetWidth, City city) throws StreetCollisionException {
		for (Building building : city.getBuildings()) {
			if (buildingCollidesWithStreet(building.getLocation(), building.getBuildingType().getRadius(), location1, location2, halfStreetWidth)) {
				throw new StreetCollisionException();
			}
		}
	}
	
	public static boolean pointCollidesWithBuilding(Location location, City city) {
		for (Building building : city.getBuildings()) {
			if (Calculator.distanceBetweenPointsLessThanLength(location, building.getLocation(), building.getBuildingType().getRadius())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean buildingCollidesWithStreet(BuildingLocation buildingLocation, double buildingRadius,
			Location streetEnd1, Location streetEnd2, double halfStreetWidth) {
		Location closestPointOnStreetToBuilding = Calculator.getLocationWhereLineIsClosestToPoint(streetEnd1, Calculator.getAngleInRadians(streetEnd1, streetEnd2), buildingLocation);
		if (Calculator.pointLiesOnLineBetweenPoints(closestPointOnStreetToBuilding, streetEnd1, streetEnd2)) {
			return Calculator.distanceBetweenPointsLessThanLength(closestPointOnStreetToBuilding, buildingLocation, buildingRadius + halfStreetWidth);
		} else {
			//check endpoints for collision
			return Calculator.distanceBetweenPointsLessThanLength(streetEnd1, buildingLocation, buildingRadius + halfStreetWidth)
					|| Calculator.distanceBetweenPointsLessThanLength(streetEnd2, buildingLocation, buildingRadius + halfStreetWidth);
		}
	}
}
