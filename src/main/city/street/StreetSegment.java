package main.city.street;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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

public class StreetSegment {
	private static long timeSpentGettingCornerLots = 0;
	private static long timeSpentGettingCenterLots = 0;
	private static long timeSpentGettingNeighborLots = 0;
	private static final double INCREMENT_DISTANCE = 2.0;
	private static final BuildingLocation END_OF_STREET = new BuildingLocation(new Point2D.Double(0, 0), 0, 0);
	
	private Street street;
	private int width;
	private Line2D line;
	private double angle;
	private double length;
	private Point2D centerPoint;
	private Map<Building, Point2D> buildings = new HashMap<>();

	public StreetSegment(Street street, Point2D end1, Point2D end2, int width) {
		this.street = street;
		this.width = width;
		this.line = new Line2D.Double(end1, end2);
		this.angle = Calculator.getAngleInRadians(end1, end2);
		this.length = end1.distance(end2);
		this.centerPoint = Calculator.getPointByOriginAngleAndDistance(end1, angle, length / 2);
	}
	
	public Street getStreet() {
		return street;
	}
	public int getWidth() {
		return width;
	}
	public Line2D getLine() {
		return line;
	}
	public double getAngle() {
		return angle;
	}
	
	public void addBuilding(Building building) {
		buildings.put(building, Calculator.getLocationWhereLineIsClosestToPoint(line, building.getLocation().getCenter()));
	}
	
	public List<BuildingLocation> getAllLocations(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		long time1 = new Date().getTime();
		locations.addAll(getCornerLots(buildingType, city));
		long time2 = new Date().getTime();
		locations.addAll(getCenterLots(buildingType, city));
		long time3 = new Date().getTime();
		for (Entry<Building, Point2D> buildingEntry : buildings.entrySet()) {
			locations.addAll(getAllLocationsNextTo(buildingEntry, buildingType, city));
		}
		long time4 = new Date().getTime();
		timeSpentGettingCornerLots += time2 - time1;
		timeSpentGettingCenterLots += time3 - time2;
		timeSpentGettingNeighborLots += time4 - time3;
		return locations;
	}
	
	private List<BuildingLocation> getCornerLots(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		if (buildingType.getRadius() > length) {
			return locations;
		}
		Point2D point1 = Calculator.getPointByOriginAngleAndDistance(line.getP1(), angle, buildingType.getRadius());
		Point2D point2 = Calculator.getPointByOriginAngleAndDistance(line.getP2(), angle + Math.PI, buildingType.getRadius());
		boolean consumesWholeStreet = (point1.distance(line.getP2()) <= buildingType.getRadius());
		for (Point2D point : Arrays.asList(point1, point2)) {
			for (double ang : Arrays.asList(angle + Math.PI / 2, angle + 3 * Math.PI / 2)) {
				BuildingLocation potentialLocation = new BuildingLocation(
						Calculator.getPointByOriginAngleAndDistance(point, ang, buildingType.getRadius() + this.width / 2.0), buildingType.getRadius(), ang + Math.PI);
				if (CollisionDetector.isBuildingLocationValid(potentialLocation, city)) {
					potentialLocation.addNeighbor(END_OF_STREET);
					if (consumesWholeStreet) {
						potentialLocation.addNeighbor(END_OF_STREET);
					}
					locations.add(potentialLocation);
				}
			}
		}
		return locations;
	}
	
	private List<BuildingLocation> getCenterLots(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		for (double ang : Arrays.asList(angle + Math.PI / 2, angle + 3 * Math.PI / 2)) {
			BuildingLocation potentialLocation = new BuildingLocation(
					Calculator.getPointByOriginAngleAndDistance(centerPoint, ang, buildingType.getRadius() + this.width / 2.0), buildingType.getRadius(), ang + Math.PI);
			if (CollisionDetector.isBuildingLocationValid(potentialLocation, city)) {
				if (buildingType.getRadius() > this.length / 2) {
					potentialLocation.addNeighbor(END_OF_STREET);
					potentialLocation.addNeighbor(END_OF_STREET);
				}
				locations.add(potentialLocation);
			}
		}
		return locations;
	}

	private List<BuildingLocation> getAllLocationsNextTo(Entry<Building, Point2D> buildingEntry, BuildingType newBuildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		Building existingBuilding = buildingEntry.getKey();
		Point2D existingBuildingLocationOnStreet = buildingEntry.getValue();
		double existingBuildingAngle = Calculator.getAngleInRadians(existingBuildingLocationOnStreet, existingBuilding.getLocation().getCenter());
		
		//Across the street
		Point2D pointAcrossTheStreet = new Point2D.Double(
				existingBuildingLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingAngle + Math.PI),
				existingBuildingLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingAngle + Math.PI));
		BuildingLocation acrossTheStreet = new BuildingLocation(pointAcrossTheStreet, newBuildingType.getRadius(), existingBuildingAngle);
		if (CollisionDetector.isBuildingLocationValid(acrossTheStreet, city)) {
			if (existingBuildingLocationOnStreet.distance(line.getP1()) <= newBuildingType.getRadius()) {
				acrossTheStreet.addNeighbor(END_OF_STREET);
			}
			if (existingBuildingLocationOnStreet.distance(line.getP2()) <= newBuildingType.getRadius()) {
				acrossTheStreet.addNeighbor(END_OF_STREET);
			}
			locations.add(acrossTheStreet);
		}
		
		if (existingBuilding.getLocation().getNeighbors().size() < 2) {
			double radiusSum = existingBuilding.getBuildingType().getRadius() + newBuildingType.getRadius() + 2; // 2 for buffer
			double xDiff = radiusSum * Math.cos(angle);
			double yDiff = radiusSum * Math.sin(angle);
	
			double xIncrement = INCREMENT_DISTANCE * Math.cos(angle);
			double yIncrement = INCREMENT_DISTANCE * Math.sin(angle);
			
			//Closer to end of street
			BuildingLocation closerToEnd1;
			Point2D closerLocationOnStreet;
			int i = 0;
			boolean buildingCollidesWithStreet = false;
			boolean validLocation = false;
			do {
				closerLocationOnStreet = new Point2D.Double(existingBuildingLocationOnStreet.getX() - xDiff - (i * xIncrement), existingBuildingLocationOnStreet.getY() - yDiff - (i * yIncrement));
				i++;
				Point2D actualPointCloserToEnd1 = new Point2D.Double(
						closerLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingAngle),
						closerLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingAngle));
				closerToEnd1 = new BuildingLocation(actualPointCloserToEnd1, newBuildingType.getRadius(), (existingBuildingAngle + Math.PI) % (2 * Math.PI));
				try {
					CollisionDetector.checkBuildingLocationValid(closerToEnd1, city);
					validLocation = true;
					buildingCollidesWithStreet = false;
				} catch (BuildingCollisionException e) {
					buildingCollidesWithStreet = false;
				} catch (StreetCollisionException e) {
					buildingCollidesWithStreet = true;
				}
			} while (line.ptSegDist(closerLocationOnStreet) == 0 && buildingCollidesWithStreet);
			if (validLocation && line.ptSegDist(closerLocationOnStreet) == 0) {
				closerToEnd1.addNeighbor(existingBuilding.getLocation());
				if (closerLocationOnStreet.distance(line.getP1()) <= newBuildingType.getRadius()) {
					closerToEnd1.addNeighbor(END_OF_STREET);
				}
				locations.add(closerToEnd1);
			}
			
			//Further from end of street
			BuildingLocation closerToEnd2;
			Point2D furtherLocationOnStreet;
			i = 0;
			validLocation = false;
			do {
				furtherLocationOnStreet = new Point2D.Double(existingBuildingLocationOnStreet.getX() + xDiff + (i * xIncrement), existingBuildingLocationOnStreet.getY() + yDiff + (i * yIncrement));
				i++;
				Point2D actualPointCloserToEnd2 = new Point2D.Double(
						furtherLocationOnStreet.getX() + (newBuildingType.getRadius() + this.width / 2.0) * Math.cos(existingBuildingAngle),
						furtherLocationOnStreet.getY() + (newBuildingType.getRadius() + this.width / 2.0) * Math.sin(existingBuildingAngle));
				closerToEnd2 = new BuildingLocation(actualPointCloserToEnd2, newBuildingType.getRadius(), (existingBuildingAngle + Math.PI) % (2 * Math.PI));
				try {
					CollisionDetector.checkBuildingLocationValid(closerToEnd2, city);
					validLocation = true;
					buildingCollidesWithStreet = false;
				} catch (BuildingCollisionException e) {
					buildingCollidesWithStreet = false;
				} catch (StreetCollisionException e) {
					buildingCollidesWithStreet = true;
				}
			} while (line.ptSegDist(furtherLocationOnStreet) == 0 && buildingCollidesWithStreet);
			if (validLocation && line.ptSegDist(furtherLocationOnStreet) == 0) {
				closerToEnd2.addNeighbor(existingBuilding.getLocation());
				if (furtherLocationOnStreet.distance(line.getP2()) <= newBuildingType.getRadius()) {
					closerToEnd2.addNeighbor(END_OF_STREET);
				}
				locations.add(closerToEnd2);
			}
		}
		
		return locations;
	}

	public static long getTimeSpentGettingCornerLots() {
		return timeSpentGettingCornerLots;
	}
	public static long getTimeSpentGettingCenterLots() {
		return timeSpentGettingCenterLots;
	}
	public static long getTimeSpentGettingNeighborLots() {
		return timeSpentGettingNeighborLots;
	}
}
