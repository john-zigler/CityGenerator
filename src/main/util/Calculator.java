package main.util;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Calculator {
	private Calculator() {
		//Hidden constructor
	}
	
	public static double getAngleInRadians(Point2D point1, Point2D point2) {
		double theta = Math.atan2(Math.abs(point2.getY() - point1.getY()), Math.abs(point2.getX() - point1.getX()));
		if (point1.getX() > point2.getX()) {
			theta = Math.PI - theta;
		}
		if (point1.getY() > point2.getY()) {
			theta = 2 * Math.PI - theta;
		}
		return theta;
	}
	
	public static Point2D getLocationWhereLineIsClosestToPoint(Line2D line, Point2D point) {
		double lineAngle = getAngleInRadians(line.getP1(), line.getP2());
		double angle1 = Calculator.getAngleInRadians(line.getP1(), point) - lineAngle;
		double distanceFromOrigin = line.getP1().distance(point) * Math.cos(angle1);
		Point2D location = getPointByOriginAngleAndDistance(line.getP1(), lineAngle, distanceFromOrigin);
		return location;
	}
	
	public static Point2D getPointOfIntersectionBetweenTwoLines(double angle1, Point2D point1, double angle2, Point2D point2) {
		if (angle1 == angle2) {
			return null;
		}

		double cos1 = Math.cos(angle1);
		double sin1 = Math.sin(angle1);
		double cos2 = Math.cos(angle2);
		double sin2 = Math.sin(angle2);
		
		double u = (cos1 * (point2.getY() - point1.getY()) - sin1 * (point2.getX() - point1.getX())) / (cos2 * sin1 - sin2 * cos1);
		double x = point2.getX() + u * cos2;
		double y = point2.getY() + u * sin2;
		
		return new Point2D.Double(x, y);
	}
	
	public static double getAreaOfTriangle(Point2D point1, Point2D point2, Point2D point3) {
		return Math.abs((point1.getX() * (point2.getY() - point3.getY()) + point2.getX() * (point3.getY() - point1.getY()) + point3.getX() * (point1.getY() - point2.getY())) / 2.0);
	}
	
	public static Point2D getPointByOriginAngleAndDistance(Point2D origin, double angle, double distance) {
		return new Point2D.Double(origin.getX() + distance * Math.cos(angle), origin.getY() + distance * Math.sin(angle));
	}
	public static  boolean closeEnoughForGovernmentWork(Point2D expectedPoint, Point2D actualPoint) {
		return Math.rint(expectedPoint.getX() * 100) == Math.rint(actualPoint.getX() * 100)
				&& Math.rint(expectedPoint.getY() * 100) == Math.rint(actualPoint.getY() * 100);
	}
}
