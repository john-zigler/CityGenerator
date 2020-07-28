package test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import main.building.BuildingLocation;
import main.util.Calculator;
import main.util.Location;

public class CalculatorTest {
	@Test
	public void getDistanceBetweenLocationsTest() {
		Location point0 = new Location(0, 0);
		Location point1 = new Location(1, 0);
		Location point2 = new Location(1, 1);
		Location point3 = new Location(0, 1);
		assertEquals(Calculator.getDistanceBetweenLocations(point0, point1), 1.0);
		assertEquals(Calculator.getDistanceBetweenLocations(point0, point2), Math.sqrt(2));
		assertEquals(Calculator.getDistanceBetweenLocations(point0, point3), 1.0);
	}
	
	@Test
	public void getAngleInRadiansTest() {
		Location point0 = new Location(0, 0);
		Location point1 = new Location(1, 0);
		Location point2 = new Location(1, 1);
		Location point3 = new Location(0, 1);
		Location point4 = new Location(-1, 1);
		Location point5 = new Location(-1, 0);
		Location point6 = new Location(-1, -1);
		Location point7 = new Location(0, -1);
		Location point8 = new Location(1, -1);
		assertEquals(Calculator.getAngleInRadians(point0, point1), 0.0);
		assertEquals(Calculator.getAngleInRadians(point0, point2), 1 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point3), 2 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point4), 3 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point5), 4 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point6), 5 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point7), 6 * Math.PI / 4);
		assertEquals(Calculator.getAngleInRadians(point0, point8), 7 * Math.PI / 4);
	}
	
	@Test
	public void getLocationWhereLineIsClosestToPointTest() {
		Location origin = new Location(-1, 0);
		double lineAngle = 0;
		
		Location point1 = new Location(0, 1);
		BuildingLocation expectedLocation1 = new BuildingLocation(0, 0, Math.PI / 2);
		assertEquals(Calculator.getLocationWhereLineIsClosestToPoint(origin, lineAngle, point1), expectedLocation1);

		Location point2 = new Location(0, -1);
		BuildingLocation expectedLocation2 = new BuildingLocation(0, 0, 3 * Math.PI / 2);
		assertEquals(Calculator.getLocationWhereLineIsClosestToPoint(origin, lineAngle, point2), expectedLocation2);
	}
	
	@Test
	public void pointLiesOnLineBetweenPointsTest() {
		Location end1 = new Location(-1, 0);
		Location end2 = new Location(1, 0);
		assertTrue(Calculator.pointLiesOnLineBetweenPoints(new Location(0, 0), end1, end2));
		assertFalse(Calculator.pointLiesOnLineBetweenPoints(new Location(0, 1), end1, end2));
	}
	
	@Test
	public void distanceBetweenPointsLessThanLengthTest() {
		Location point1 = new Location(0, 0);
		Location point2 = new Location(1, 1);
		assertTrue(Calculator.distanceBetweenPointsLessThanLength(point1, point2, 2));
		assertFalse(Calculator.distanceBetweenPointsLessThanLength(point1, point2, 1));
	}
}
