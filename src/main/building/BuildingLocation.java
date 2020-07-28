package main.building;

import main.util.Location;

public class BuildingLocation extends Location {
	private double rotationRadians;
	
	public BuildingLocation(double x, double y, double rotationRadians) {
		super(x, y);
		this.rotationRadians = rotationRadians;
	}
	
	public double getRotationRadians() {
		return rotationRadians;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof BuildingLocation) {
			BuildingLocation location = (BuildingLocation) object;
			return Math.rint(this.getX()) == Math.rint(location.getX()) && Math.rint(this.getY()) == Math.rint(location.getY()) 
					&& (this.getRotationRadians() + 2 * Math.PI) % (2 * Math.PI) == (location.getRotationRadians() + 2 * Math.PI) % (2 * Math.PI);
		}
		return false;
	}
}
