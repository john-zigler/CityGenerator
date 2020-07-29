package main.city;

import java.util.ArrayList;
import java.util.List;

import main.building.Building;
import main.city.street.StreetSegment;

public class Sector {
	private List<Building> buildings = new ArrayList<>();
	private List<StreetSegment> streetSegments = new ArrayList<>();
	
	public List<Building> getBuildings() {
		return buildings;
	}
	public void addBuilding(Building building) {
		this.buildings.add(building);
	}
	public List<StreetSegment> getStreetSegments() {
		return streetSegments;
	}
	public void addStreetSegment(StreetSegment streetSegment) {
		this.streetSegments.add(streetSegment);
	}
}
