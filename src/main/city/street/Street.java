package main.city.street;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import main.building.Building;
import main.building.BuildingLocation;
import main.building.BuildingType;
import main.city.City;
import main.exceptions.BuildingCollisionException;
import main.exceptions.StreetCollisionException;
import main.util.Calculator;
import main.util.CollisionDetector;
import main.util.Location;

public class Street {
	private static final double INCREMENT_DISTANCE = 2.0;
	
	private String name;
	private int width;
	private double length;
	private Location end1;
	private Location end2;
	private double angle;
	private Map<Building, BuildingLocation> buildings = new HashMap<>();
	
	public Street(String name, Location end1, Location end2, int width) {
		this.name = name;
		this.width = width;
		this.end1 = end1;
		this.end2 = end2;
		this.length = Calculator.getDistanceBetweenLocations(end1, end2);
		this.angle = Calculator.getAngleInRadians(end1, end2);
	}
	
	public String getName() {
		return name;
	}
	public int getWidth() {
		return width;
	}
	public Location getEnd1() {
		return end1;
	}
	public Location getEnd2() {
		return end2;
	}
	public double getLength() {
		return length;
	}
	
	public void addBuilding(Building building) {
		buildings.put(building, Calculator.getLocationWhereLineIsClosestToPoint(end1, angle, building.getLocation()));
	}
	
	public List<BuildingLocation> getAllLocations(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		for (Entry<Building, BuildingLocation> buildingEntry : buildings.entrySet()) {
			locations.addAll(getAllLocationsNextTo(buildingEntry, buildingType, city));
		}
		return locations;
	}

	private List<BuildingLocation> getAllLocationsNextTo(Entry<Building, BuildingLocation> buildingEntry, BuildingType newBuildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		Building existingBuilding = buildingEntry.getKey();
		BuildingLocation existingBuildingLocationOnStreet = buildingEntry.getValue();
		
		//Across the street
		BuildingLocation acrossTheStreet = new BuildingLocation(
				existingBuildingLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingLocationOnStreet.getRotationRadians() + Math.PI),
				existingBuildingLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingLocationOnStreet.getRotationRadians() + Math.PI),
				existingBuildingLocationOnStreet.getRotationRadians());
		locations.add(acrossTheStreet);
		
		double radiusSum = existingBuilding.getBuildingType().getRadius() + newBuildingType.getRadius() + 2; // 2 for buffer
		double xDiff = radiusSum * Math.cos(this.angle);
		double yDiff = radiusSum * Math.sin(this.angle);

		double xIncrement = INCREMENT_DISTANCE * Math.cos(this.angle);
		double yIncrement = INCREMENT_DISTANCE * Math.sin(this.angle);
		
		//Closer to end of street
		BuildingLocation closerToEnd1;
		Location closerLocationOnStreet;
		int i = 0;
		boolean buildingCollidesWithStreet = false;
		do {
			closerLocationOnStreet = new Location(existingBuildingLocationOnStreet.getX() - xDiff - (i * xIncrement), existingBuildingLocationOnStreet.getY() - yDiff - (i * yIncrement));
			i++;
			closerToEnd1 = new BuildingLocation(
					closerLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingLocationOnStreet.getRotationRadians()),
					closerLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingLocationOnStreet.getRotationRadians()),
					(existingBuildingLocationOnStreet.getRotationRadians() + Math.PI) % (2 * Math.PI));
			try {
				CollisionDetector.checkBuildingLocationValid(closerToEnd1, newBuildingType.getRadius(), city);
				buildingCollidesWithStreet = false;
			} catch (BuildingCollisionException e) {
				buildingCollidesWithStreet = false;
			} catch (StreetCollisionException e) {
				buildingCollidesWithStreet = true;
			}
		} while (Calculator.pointLiesOnLineBetweenPoints(closerLocationOnStreet, end1, end2) && buildingCollidesWithStreet);
		locations.add(closerToEnd1);
		
		//Further from end of street
		BuildingLocation closerToEnd2;
		Location furtherLocationOnStreet;
		i = 0;
		do {
			furtherLocationOnStreet = new Location(existingBuildingLocationOnStreet.getX() + xDiff + (i * xIncrement), existingBuildingLocationOnStreet.getY() + yDiff + (i * yIncrement));
			i++;
			closerToEnd2 = new BuildingLocation(
					furtherLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingLocationOnStreet.getRotationRadians()),
					furtherLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingLocationOnStreet.getRotationRadians()),
					(existingBuildingLocationOnStreet.getRotationRadians() + Math.PI) % (2 * Math.PI));
			try {
				CollisionDetector.checkBuildingLocationValid(closerToEnd2, newBuildingType.getRadius(), city);
				buildingCollidesWithStreet = false;
			} catch (BuildingCollisionException e) {
				buildingCollidesWithStreet = false;
			} catch (StreetCollisionException e) {
				buildingCollidesWithStreet = true;
			}
		} while (Calculator.pointLiesOnLineBetweenPoints(furtherLocationOnStreet, end1, end2) && buildingCollidesWithStreet);
		locations.add(closerToEnd2);
		
		return locations;
	}
}
