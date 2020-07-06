package person.profession;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import building.BuildingType;
import resource.ResourceAdjustment;

@XmlRootElement
public class Profession {
	@XmlElement
	private String name;
	@XmlElement
	private int minTrainingRequired;
	@XmlElement
	private boolean buildingRequired;
	@XmlElement(name="resourceAdjustment")
	private List<ResourceAdjustment> resourceAdjustments = new ArrayList<>();
	//Set during BuildingType initialization; not in Profession xml
	private BuildingType proprietorOf;
	@XmlElement
	private String apprenticeName = "Apprentice";
	@XmlElement
	private int numberOfApprentices = 0;
	@XmlElement(name="professionTaught")
	private List<String> professionsTaught = new ArrayList<>();
	
	public String getName() {
		return name;
	}
	public int getMinTrainingRequired() {
		return minTrainingRequired;
	}
	public boolean isBuildingRequired() {
		return buildingRequired;
	}
	public List<ResourceAdjustment> getResourceAdjustments() {
		return resourceAdjustments;
	}
	public Integer getResourceAdjustment(String resourceName) {
		for (ResourceAdjustment resourceAdjustment : resourceAdjustments) {
			if (resourceAdjustment.getName().equals(resourceName)) {
				return resourceAdjustment.getValue();
			}
		}
		return 0;
	}
	
	public BuildingType getProprietorOf() {
		return proprietorOf;
	}
	public void setProprietorOf(BuildingType proprietorOf) {
		this.proprietorOf = proprietorOf;
	}
	public String getApprenticeName() {
		return apprenticeName;
	}
	public int getNumberOfApprentices() {
		return numberOfApprentices;
	}
	public List<String> getProfessionsTaught() {
		return professionsTaught;
	}
}
