package main.city.street;

import java.util.ArrayList;
import java.util.List;

import main.city.City;

public class Street {
	private String name;
	private List<StreetSegment> segments = new ArrayList<>();
	private List<StreetSegment> ends = new ArrayList<>();
	private City city;
	
	public Street(String name, City city) {
		this.name = name;
		this.city = city;
	}
	
	public String getName() {
		return name;
	}
	public List<StreetSegment> getSegments() {
		return segments;
	}
	public void addSegment(StreetSegment segment) {
		this.segments .add(segment);
		if (segment.getParent() != null) {
			this.ends.remove(segment.getParent());
		}
		this.ends.add(segment);
		city.addStreetSegmentToSectors(segment);
	}
	public List<StreetSegment> getEnds() {
		return ends;
	}
	public City getCity() {
		return city;
	}
}
