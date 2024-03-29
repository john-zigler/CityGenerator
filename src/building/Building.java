package building;

import java.util.ArrayList;
import java.util.List;

import person.Person;
import person.profession.Profession;
import util.WorldConfig;

public class Building {
	private int id;
	private String name;
	//People who live there
	private List<Person> occupants = new ArrayList<>();
	//People who work there
	private List<Person> employees = new ArrayList<>();
	private BuildingType buildingType;
	private Person founder;
	
	public Building(String name, BuildingType buildingType, Person founder) {
		this.id = WorldConfig.getNextId();
		this.name = name;
		this.buildingType = buildingType;
		this.founder = founder;
		if (founder != null) {
			founder.addPlaceFounded(this);
		}
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Person> getOccupants() {
		return occupants;
	}
	public void addOccupant(final Person occupant) {
		if (!this.occupants.contains(occupant)) {
			this.occupants.add(occupant);
		}
	}
	public List<Person> getEmployees() {
		return employees;
	}
	public void addEmployee(Person employee) {
		if (!this.employees.contains(employee)) {
			this.employees.add(employee);
		}
	}
	public BuildingType getBuildingType() {
		return buildingType;
	}
	public Person getFounder() {
		return founder;
	}

	public List<Profession> getAvailablePostions() {
		List<Profession> availablePostions = new ArrayList<>();
		List<String> positions = new ArrayList<>(buildingType.getPositions());
		if (positions == null || positions.isEmpty()) {
			//Don't bother with the rest
			return availablePostions;
		}
		boolean hasProprietor = false;
		for (Person employee : employees) {
			positions.remove(employee.getProfession().getName());
			if (buildingType.equals(employee.getProfession().getProprietorOf())) {
				hasProprietor = true;
			}
		}
		if (hasProprietor) {
			for (String position : positions) {
				availablePostions.add(WorldConfig.getProfessionByName(position));
			}
		} else {
			availablePostions.add(WorldConfig.getProfessionByName(positions.get(0)));
		}
		return availablePostions;
	}

	//This is where we'll calculate stuff like family relationships, racism, sexism, and
	//other factors aside from job knowledge that would impact a character being hired.
	//Range is from 0 (Not a chance in hell) to something > 1 (family business). 1 = no preference.
	public double considerEmploymentFit(Person character) {
		double totalScore = 0;
		for (Person employee : employees) {
			totalScore += character.getImpressionOf(employee) * employee.getImpressionOf(character);
		}
		return employees.isEmpty() ? 10.0 : (totalScore / employees.size());
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(this.name);
		sb.append("\n");
		for (Person occupant : occupants) {
			sb.append("-- Occupant: ");
			sb.append(occupant.toString());
			sb.append("\n");
		}
		for (Person employee : employees) {
			sb.append("-- Employee: ");
			sb.append(employee.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		return object != null && object.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}

}
