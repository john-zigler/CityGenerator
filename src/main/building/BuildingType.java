package main.building;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import main.util.Randomizer;

@XmlRootElement
public class BuildingType {
	@XmlElement
	private String name;
	@XmlElement(name="position")
	private List<String> positions = new ArrayList<>();
	@XmlElement
	private NamingRules namingRules;
	@XmlElement
	private boolean houseIncluded = false;
	@XmlElement
	private int radius = Randomizer.generateRandomNumber(10, 30);
	
	public BuildingType(String name, List<String> positions, NamingRules namingRules, boolean houseIncluded, int radius) {
		this.name = name;
		this.positions = positions;
		this.namingRules = namingRules;
		this.houseIncluded = houseIncluded;
		this.radius = radius;
	}
	public BuildingType() {
		//Explicit default constructor
	}

	public String getName() {
		return name;
	}
	public List<String> getPositions() {
		return positions;
	}
	public NamingRules getNamingRules() {
		return namingRules;
	}
	public boolean isHouseIncluded() {
		return houseIncluded;
	}
	public boolean isHouse() {
		return name.equals("House");
	}
	public int getRadius() {
		return radius;
	}
}
