package main.city.street;

import java.util.ArrayList;
import java.util.List;

import main.building.BuildingLocation;
import main.building.BuildingType;
import main.city.City;

public class Street {
	private String name;
	private List<StreetSegment> segments = new ArrayList<>();
	
	public Street(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public List<StreetSegment> getSegments() {
		return segments;
	}
	public void addSegment(StreetSegment segment) {
		this.segments .add(segment);
	}
	
	public List<BuildingLocation> getAllLocations(BuildingType buildingType, City city) {
		List<BuildingLocation> locations = new ArrayList<>();
		for (StreetSegment segment : segments) {
			locations.addAll(segment.getAllLocations(buildingType, city));
		}
		return locations;
	}
}
