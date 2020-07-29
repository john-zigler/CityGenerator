package test.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


import org.junit.jupiter.api.Test;

import main.util.Calculator;

public class CalculatorTest {
	@Test
	public void getAngleInRadiansTest() {
		Point2D point0 = new Point2D.Double(0, 0);
		Point2D point1 = new Point2D.Double(1, 0);
		Point2D point2 = new Point2D.Double(1, 1);
		Point2D point3 = new Point2D.Double(0, 1);
		Point2D point4 = new Point2D.Double(-1, 1);
		Point2D point5 = new Point2D.Double(-1, 0);
		Point2D point6 = new Point2D.Double(-1, -1);
		Point2D point7 = new Point2D.Double(0, -1);
		Point2D point8 = new Point2D.Double(1, -1);
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
		Line2D line = new Line2D.Double(-1, 0, 1, 0);
		Point2D point1 = new Point2D.Double(0, 1);
		Point2D expectedLocation1 = new Point2D.Double(0, 0);
		assertTrue(Calculator.closeEnoughForGovernmentWork(Calculator.getLocationWhereLineIsClosestToPoint(line, point1), expectedLocation1));

		Point2D point2 = new Point2D.Double(0, -1);
		Point2D expectedLocation2 = new Point2D.Double(0, 0);
		assertTrue(Calculator.closeEnoughForGovernmentWork(Calculator.getLocationWhereLineIsClosestToPoint(line, point2), expectedLocation2));
	}
	
	@Test
	public void getPointOfIntersectionBetweenTwoLinesTest() {
		Point2D point1 = new Point2D.Double(1, 0);
		double angle1 = 0;
		
		Point2D point2 = new Point2D.Double(1, 1);
		double angle2 = Math.PI / 4;
		
		assertTrue(Calculator.closeEnoughForGovernmentWork(Calculator.getPointOfIntersectionBetweenTwoLines(angle1, point1, angle2, point2), new Point2D.Double(0, 0)));
	}
	
	@Test
	public void getPointByOriginAngleAndDistanceTest() {
		assertTrue(Calculator.closeEnoughForGovernmentWork(Calculator.getPointByOriginAngleAndDistance(new Point2D.Double(0, 0), 0, 10), new Point2D.Double(10, 0)));
		assertTrue(Calculator.closeEnoughForGovernmentWork(Calculator.getPointByOriginAngleAndDistance(new Point2D.Double(-25.0, 12.5), Math.PI / 2, 12.5), new Point2D.Double(-25.0, 25.0)));
	}
}
