package city;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import building.Building;
import building.BuildingType;
import person.Person;
import person.profession.Profession;
import resource.ResourceAdjustment;
import util.WorldConfig;

public class City {
	private String name;
	private List<Building> buildings = new ArrayList<>();
	private Building graveyard = new Building("Graveyard", WorldConfig.getBuildingTypeByName("Graveyard"), null);
	private Set<Person> livingCitizens = new HashSet<>();
	private Set<Person> deadCitizens = new HashSet<>();
	private Set<Person> orphans = new HashSet<>();
	private Map<Profession, List<Person>> registeredProfessionals = new HashMap<>();
	private Map<Profession, List<Person>> registeredApprentices = new HashMap<>();
	private Map<String, Integer> nearbyResources = new HashMap<>(); //Profession output not included;
	private Map<String, Integer> adjustedResources = new HashMap<>(); //Profession output included
	
	//TODO founder
	//TODO government

	public City(final String name) {
		this.name = name;
		buildings.add(graveyard);
		for (String resource : WorldConfig.resources) {
			nearbyResources.put(resource, 0);
			adjustedResources.put(resource, 0);
		}
	}
	
	public String getName() {
		return this.name;
	}
	public List<Building> getBuildings() {
		return this.buildings;
	}
	public void addBuilding(final Building building) {
		this.buildings.add(building);
	}
	public boolean containsBuildingWithName(String name) {
		for (Building building : buildings) {
			if (building.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	public Building getGraveyard() {
		return this.graveyard;
	}
	public Set<Person> getLivingCitizens() {
		return livingCitizens;
	}
	public void addLivingCitizen(Person person) {
		livingCitizens.add(person);
		adjustResources(person.getResourceAdjustments(), false, false);
	}
	public Set<Person> getDeadCitizens() {
		return deadCitizens;
	}
	public Set<Person> getOrphans() {
		return orphans;
	}
	public void addOrphan(Person orphan) {
		orphans.add(orphan);
	}
	public void removeOrphan(Person orphan) {
		orphans.remove(orphan);
	}
	public void processCitizenDeath(Person person) {
		orphans.remove(person);
		livingCitizens.remove(person);
		deadCitizens.add(person);
		adjustResources(person.getResourceAdjustments(), false, true);
	}
	
	public void registerApprentice(Person person) {
		if (person.getApprenticeship() != null) {
			getRegisteredApprentices(person.getApprenticeship()).add(person);
			adjustResources(person.getApprenticeship().getResourceAdjustments(), true, true);
		}
	}
	public void unregisterApprentice(Person person) {
		if (person.getApprenticeship() != null) {
			getRegisteredApprentices(person.getApprenticeship()).remove(person);
			adjustResources(person.getApprenticeship().getResourceAdjustments(), true, false);
		}
	}
	public List<Person> getRegisteredApprentices(Profession apprenticeship) {
		return registeredApprentices.computeIfAbsent(apprenticeship, p -> new ArrayList<>());
	}
	
	public void registerProfessional(Person person) {
		if (person.getProfession() != null) {
			getRegisteredProfessionals(person.getProfession()).add(person);
			adjustResources(person.getProfession().getResourceAdjustments(), false, true);
		}
	}
	public void unregisterProfessional(Person person) {
		if (person.getProfession() != null && getRegisteredProfessionals(person.getProfession()).remove(person)) {
			adjustResources(person.getProfession().getResourceAdjustments(), false, false);
		}
	}
	public List<Person> getRegisteredProfessionals(Profession profession) {
		return registeredProfessionals.computeIfAbsent(profession, p -> new ArrayList<>());
	}

	private void adjustResources(List<ResourceAdjustment> adjustments, boolean apprentice, boolean positive) {
		for (ResourceAdjustment adjustment : adjustments) {
			int value = (apprentice ? adjustment.getValue() / 2 : adjustment.getValue());
			adjustResource(adjustment.getName(), positive ? value : 0 - value);
		}
	}
	private void adjustResource(String name, Integer adjustment) {
		adjustedResources.put(name, (adjustedResources.get(name) + adjustment));
	}

	public Map<String, Integer> getAvailableResources() {
		return adjustedResources;
	}
	
	public Map<Profession, List<Building>> getEmploymentOpportunities() {
		Map<Profession, List<Building>> employmentOpportunities = new HashMap<>();
		for (Building building : buildings) {
			for (Profession profession : building.getAvailablePostions()) {
				employmentOpportunities.computeIfAbsent(profession, p -> new ArrayList<>()).add(building);
			}
		}
		return employmentOpportunities;
	}
	
	public List<Person> getApprenticeshipOpportunities(Profession profession) {
		List<Person> apprenticeshipOpportunities = new ArrayList<>();
		for (Person person : getRegisteredProfessionals(profession)) {
			if (person.getAvailableApprenticeships() > 0) {
				apprenticeshipOpportunities.add(person);
			}
		}
		return apprenticeshipOpportunities;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder(this.name);
		sb.append("\n");
//		for (Building building : buildings) {
//			if (building.getBuildingType().isHouse()) {
//				sb.append("-");
//				sb.append(building.toString());
//				sb.append("\n");
//			}
//		}
		
		Map<BuildingType, Integer> buildingMap = new HashMap<>();
		
		for (Building building : buildings) {
			if (!building.getBuildingType().isHouse()) {
				sb.append("-");
				sb.append(building.toString());
				sb.append("\n");
			}
			buildingMap.put(building.getBuildingType(), buildingMap.getOrDefault(building.getBuildingType(), 0) + 1);
		}

		sb.append("\nNumber of buildings by type:\n");
		for (Entry<BuildingType, Integer> entry : buildingMap.entrySet()) {
			sb.append("-");
			sb.append(entry.getKey().getName() + ": " + entry.getValue());
			sb.append("\n");
		}
		
		sb.append("\nProfessionals without places of employment:\n");
		for (Person citizen : livingCitizens) {
			if (citizen.getPlaceOfEmployment() == null && citizen.getProfession() != null) {
				sb.append("-");
				sb.append(citizen.toString());
				sb.append("\n");
			}
		}
		return sb.toString();
	}
}
