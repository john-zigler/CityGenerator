package main.building;

import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import main.util.Calculator;

public class BuildingLocation extends Polygon {
	private static final long serialVersionUID = -9114099732743354205L;
	private Point2D center;
	private List<Point2D> points = new ArrayList<>();
	private List<Line2D> perimeter = new ArrayList<>();
	private double rotationRadians;
	private double angularRadius;
	private List<BuildingLocation> neighbors = new ArrayList<>();
	
	public BuildingLocation(Point2D center, double radius, double rotationRadians) {
		//For now, treat them all as squares
		super();
		this.center = center;
		this.angularRadius = Math.sqrt(2) * radius;
		Point2D p1 = Calculator.getPointByOriginAngleAndDistance(center, rotationRadians + 1 * Math.PI / 4, angularRadius);
		Point2D p2 = Calculator.getPointByOriginAngleAndDistance(center, rotationRadians + 3 * Math.PI / 4, angularRadius);
		Point2D p3 = Calculator.getPointByOriginAngleAndDistance(center, rotationRadians + 5 * Math.PI / 4, angularRadius);
		Point2D p4 = Calculator.getPointByOriginAngleAndDistance(center, rotationRadians + 7 * Math.PI / 4, angularRadius);
		addPoints(p1, p2, p3, p4);
		perimeter.add(new Line2D.Double(p1, p2));
		perimeter.add(new Line2D.Double(p2, p3));
		perimeter.add(new Line2D.Double(p3, p4));
		perimeter.add(new Line2D.Double(p4, p1));
		this.rotationRadians = rotationRadians;
	}
	
	public void addPoints(Point2D... pointArray) {
		for (Point2D point : pointArray) {
			addPoint((int) point.getX(), (int) point.getY());
			points.add(point);
		}
	}
	public double getRotationRadians() {
		return rotationRadians;
	}
	public Point2D getCenter() {
		return center;
	}
	public void addNeighbor(BuildingLocation location) {
		this.neighbors.add(location);
	}
	public List<BuildingLocation> getNeighbors() {
		return neighbors;
	}
	public boolean overlaps(BuildingLocation location) {
		if (this.center.distance(location.center) > this.angularRadius + location.angularRadius) {
			//Quick and dirty check
			return false;
		}
		for (Point2D point : points) {
			if (location.contains(point)) {
				return true;
			}
		}
		for (Point2D point : location.points) {
			if (this.contains(point)) {
				return true;
			}
		}
		return location.contains(center) || this.contains(location.center);
	}
	public boolean intersects(Line2D otherLine) {
		for (Line2D line : perimeter) {
			if (line.intersectsLine(otherLine)) {
				return true;
			}
		}
		return false;
	}
	public boolean comesWithinDistanceOfLine(double distance, Line2D line) {
		for (Point2D point : points) {
			if (line.ptSegDist(point) < distance) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof BuildingLocation) {
			BuildingLocation location = (BuildingLocation) object;
			if (this.npoints != location.npoints) {
				return false;
			}
			for (int i = 0; i < npoints; i++) {
				if (!Calculator.closeEnoughForGovernmentWork(this.points.get(i), location.points.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
