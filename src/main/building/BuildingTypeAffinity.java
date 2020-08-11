package main.building;

import javax.xml.bind.annotation.XmlAttribute;

public class BuildingTypeAffinity {
	@XmlAttribute
	private String buildingTypeName;
	@XmlAttribute
	private int value;
	
	public String getBuildingTypeName() {
		return buildingTypeName;
	}
	public int getValue() {
		return value;
	}
}
