package main.building;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import main.building.floor.Floor;
import main.building.floor.FloorGenerator;
import main.city.street.StreetSegment;
import main.exceptions.BuildingCollisionException;
import main.exceptions.StreetCollisionException;
import main.person.Person;
import main.person.profession.Profession;
import main.util.CollisionDetector;
import main.util.Randomizer;
import main.util.WorldConfig;

public class Building {
	private int id;
	private String name;
	//People who live there
	private List<Person> occupants = new ArrayList<>();
	//People who work there
	private List<Person> employees = new ArrayList<>();
	private BuildingType buildingType;
	private Person founder;
	private BuildingLocation location;
	private StreetSegment streetSegment;
	private Person owner;
	private List<Floor> floors;
	private List<Building> includedBuildings = new ArrayList<>();
	
	public Building(String name, BuildingType buildingType, Person founder, BuildingLocation location, StreetSegment streetSegment) {
		this.id = WorldConfig.getNextId();
		this.name = name;
		this.buildingType = buildingType;
		this.founder = founder;
		this.owner = founder;
		if (founder != null) {
			founder.addPlaceFounded(this);
		}
		this.location = location;
		if (location != null) {
			for (BuildingLocation neighbor : location.getNeighbors()) {
				neighbor.addNeighbor(location);
			}
			this.streetSegment = streetSegment;
			if (streetSegment != null) {
				//Street should only be null for the first building in town
				streetSegment.addBuilding(this);
				streetSegment.getStreet().addSegment(streetSegment);
			}
		}
		floors = FloorGenerator.generateFloors(buildingType, true, true, 3);
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
		if (this.owner == null && buildingType.equals(employee.getProfession().getProprietorOf())) {
			this.owner = employee;
		}
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
	public BuildingLocation getLocation() {
		return location;
	}
	public void setLocation(BuildingLocation location) {
		this.location = location;
	}
	public StreetSegment getStreetSegment() {
		return streetSegment;
	}
	public void setStreetSegment(StreetSegment streetSegment) {
		this.streetSegment = streetSegment;
	}
	public Person getOwner() {
		return owner;
	}
	public void setOwner(Person owner) {
		this.owner = owner;
	}

	public List<Building> getIncludedBuildings() {
		return includedBuildings;
	}

	public void addIncludedBuilding(Building includedBuilding) {
		int thisRadius = this.buildingType.getRadius();
		int includedBuildingRadius = includedBuilding.buildingType.getRadius();
		if (this.includedBuildings.isEmpty()) {
			//First building should be at the edge of the property, facing the street.
			Point2D center = new Point2D.Double(thisRadius * 2 - includedBuildingRadius - 2, thisRadius);
			includedBuilding.location = new BuildingLocation(center, includedBuildingRadius, 0);
		} else {
			Exception caughtException;
			do {
				caughtException = null;
				double x = Randomizer.generateRandomNumber(includedBuildingRadius + 2, thisRadius * 2 - includedBuildingRadius - 2);
				double y = Randomizer.generateRandomNumber(includedBuildingRadius + 2, thisRadius * 2 - includedBuildingRadius - 2);
				Point2D center = new Point2D.Double(x, y);
				includedBuilding.location = new BuildingLocation(center, includedBuildingRadius, Randomizer.generateRandomNumber(0, 2 * Math.PI));
				try {
					CollisionDetector.checkBuildingLocationValid(includedBuilding.location, this.includedBuildings, null);
				} catch (BuildingCollisionException | StreetCollisionException e) {
					caughtException = e;
				}
			} while (caughtException != null);
		}
		this.includedBuildings.add(includedBuilding);
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
	
	public BufferedImage generateImageForMap() {
		int radius = (int) (buildingType.getRadius());
		BufferedImage image = new BufferedImage(radius * 4, radius * 4, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D graphics = image.createGraphics();
		if (includedBuildings != null && !includedBuildings.isEmpty()) {
			graphics.setColor(Color.GRAY);
			graphics.drawRect(radius, radius, radius * 2, radius * 2);
			for (Building includedBuilding : includedBuildings) {
				int x = radius + (int) includedBuilding.location.getCenter().getX() - 2 * includedBuilding.buildingType.getRadius();
				int y = radius + (int) includedBuilding.location.getCenter().getY() - 2 * includedBuilding.buildingType.getRadius();
				graphics.drawImage(includedBuilding.generateImageForMap(), x, y, null);
			}
		}
		for (Floor floor : floors) {
//			graphics.drawImage(floor.generateImageForMap(buildingType.getName().equals("House") ? Color.RED : Color.GRAY), radius, radius, null);
			graphics.drawImage(floor.generateImageForMap(Color.GRAY), radius, radius, null);
		}
//		graphics.setColor(Color.BLACK);
//		graphics.drawLine(radius * 2, radius * 2, radius * 3, radius * 2);
		
		AffineTransform tx = AffineTransform.getRotateInstance(location.getRotationRadians(), image.getWidth() / 2, image.getHeight() / 2);
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		
		return op.filter(image, null);
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
