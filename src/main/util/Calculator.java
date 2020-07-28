package main.util;

import main.building.BuildingLocation;

public class Calculator {
	//Sometimes calculations leave rounding errors
	private static final double MARGIN_OF_ERROR = 0.0001;
	
	private Calculator() {
		//Hidden constructor
	}
	
	public static double getDistanceBetweenLocations(Location location1, Location location2) {
		return Math.sqrt(Math.pow(location1.getX() - location2.getX(), 2) + Math.pow(location1.getY() - location2.getY(), 2));
	}
	
	public static double getAngleInRadians(Location location1, Location location2) {
		double theta = Math.atan2(Math.abs(location2.getY() - location1.getY()), Math.abs(location2.getX() - location1.getX()));
		if (location1.getX() > location2.getX()) {
			theta = Math.PI - theta;
		}
		if (location1.getY() > location2.getY()) {
			theta = 2 * Math.PI - theta;
		}
		return theta;
	}
	
	/**
	 * Return a BuildingLocation containing the x and y where the line passes closest to the point, and a direction pointing toward the point
	 */
	public static BuildingLocation getLocationWhereLineIsClosestToPoint(Location origin, double lineAngle, Location point) {
		double angle1 = Calculator.getAngleInRadians(origin, point) - lineAngle;
		double distanceFromOrigin = Calculator.getDistanceBetweenLocations(origin, point) * Math.cos(angle1);
		double x = origin.getX() + distanceFromOrigin * Math.cos(lineAngle);
		double y = origin.getY() + distanceFromOrigin * Math.sin(lineAngle);
		double direction = angle1 < 0 || angle1 > Math.PI ? lineAngle - Math.PI / 2 : lineAngle + Math.PI / 2;
		return new BuildingLocation(x, y, direction);
	}
	
	public static boolean pointLiesOnLineBetweenPoints(Location point, Location end1, Location end2) {
		return getDistanceBetweenLocations(point, end1) + getDistanceBetweenLocations(point, end2) == getDistanceBetweenLocations(end1, end2);
	}
	
	public static boolean distanceBetweenPointsLessThanLength(Location location1, Location location2, double length) {
		double distance = Calculator.getDistanceBetweenLocations(location1, location2);
		return distance + MARGIN_OF_ERROR < length;
	}
}
