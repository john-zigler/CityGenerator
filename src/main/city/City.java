package main.city;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import java.util.Set;

import main.building.Building;
import main.building.BuildingLocation;
import main.building.BuildingType;
import main.city.street.Street;
import main.city.street.StreetGenerator;
import main.person.Person;
import main.person.profession.Profession;
import main.resource.ResourceAdjustment;
import main.util.Location;
import main.util.Randomizer;
import main.util.WorldConfig;

public class City {
	private String name;
	private List<Building> buildings = new ArrayList<>();
	private List<Street> streets = new ArrayList<>();
	private Building graveyard;
	private Set<Person> livingCitizens = new HashSet<>();
	private Set<Person> deadCitizens = new HashSet<>();
	private Set<Person> orphans = new HashSet<>();
	private Map<Profession, List<Person>> registeredProfessionals = new HashMap<>();
	private Map<Profession, List<Person>> registeredApprentices = new HashMap<>();
	private Map<String, Integer> nearbyResources = new HashMap<>(); //Profession output not included;
	private Map<String, Integer> adjustedResources = new HashMap<>(); //Profession output included
	private double townCenterX = 0;
	private double townCenterY = 0;
	private double maxX = 0;
	private double maxY = 0;
	private double minX = 0;
	private double minY = 0;
	
	
	//TODO founder
	//TODO government

	public City(final String name) {
		this.name = name;
		for (String resource : WorldConfig.resources) {
			nearbyResources.put(resource, 0);
			adjustedResources.put(resource, 0);
		}
		graveyard = new Building("Graveyard", WorldConfig.getBuildingTypeByName("Graveyard"), null, new BuildingLocation (0, 0, Randomizer.generateRandomNumber(0.0, 2 * Math.PI)), null);
		addBuilding(graveyard, true);
	}
	
	public String getName() {
		return this.name;
	}
	public List<Building> getBuildings() {
		return this.buildings;
	}
	public void addBuilding(final Building building) {
		addBuilding(building, Randomizer.rollAgainstOdds(building.getBuildingType().getRadius() / 100.0));
	}
	public void addBuilding(final Building building, boolean generateStreet) {
		this.buildings.add(building);
		if (generateStreet) {
			Street newStreet = StreetGenerator.generateStreet(this, building);
			//In case of error when creating street, don't fail completely
			if (newStreet != null) {
				addStreet(newStreet);
			}
		}
		townCenterX += (building.getLocation().getX() - townCenterX) / this.buildings.size();
		townCenterY += (building.getLocation().getY() - townCenterY) / this.buildings.size();
		if (building.getLocation().getX() + building.getBuildingType().getRadius() > maxX) {
			maxX = building.getLocation().getX() + building.getBuildingType().getRadius();
		}
		if (building.getLocation().getY() + building.getBuildingType().getRadius() > maxY) {
			maxY = building.getLocation().getY() + building.getBuildingType().getRadius();
		}
		if (building.getLocation().getX() - building.getBuildingType().getRadius() < minX) {
			minX = building.getLocation().getX() - building.getBuildingType().getRadius();
		}
		if (building.getLocation().getY() - building.getBuildingType().getRadius() < minY) {
			minY = building.getLocation().getY() - building.getBuildingType().getRadius();
		}
	}
	public boolean containsBuildingWithName(String name) {
		for (Building building : buildings) {
			if (building.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	public List<Street> getStreets() {
		return this.streets;
	}
	public void addStreet(final Street street) {
		this.streets.add(street);
		if (street.getEnd1().getX() > maxX) {
			maxX = street.getEnd1().getX();
		}
		if (street.getEnd2().getX() > maxX) {
			maxX = street.getEnd2().getX();
		}
		if (street.getEnd1().getY() > maxY) {
			maxY = street.getEnd1().getY();
		}
		if (street.getEnd2().getY() > maxY) {
			maxY = street.getEnd2().getY();
		}
		if (street.getEnd1().getX() < minX) {
			minX = street.getEnd1().getX();
		}
		if (street.getEnd2().getX() < minX) {
			minX = street.getEnd2().getX();
		}
		if (street.getEnd1().getY() < minY) {
			minY = street.getEnd1().getY();
		}
		if (street.getEnd2().getY() < minY) {
			minY = street.getEnd2().getY();
		}
	}
	public boolean containsStreetWithName(String name) {
		for (Street street : streets) {
			if (street.getName().equals(name)) {
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

	public Location getTownCenter() {
		return new Location(this.townCenterX, this.townCenterY);
	}
	public double getMinX() {
		return this.minX;
	}
	public double getMinY() {
		return this.minY;
	}
	public double getMaxX() {
		return this.maxX;
	}
	public double getMaxY() {
		return this.maxY;
	}
	
	public void renderMap(String folder) {
		try {
			ImageIO.write(generateMap(), "png", new File(folder + name + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage generateMap() {
		//double unadjustedSize = (maxX - minX) * (maxY - minY);
		double scale = 1; //unadjustedSize > Integer.MAX_VALUE ? Integer.MAX_VALUE / unadjustedSize : 1.0;
		int mapSizeX = (int) (scale * (maxX - minX));
		int mapSizeY = (int) (scale * (maxY - minY));
		BufferedImage mapImage = new BufferedImage(mapSizeX, mapSizeY, BufferedImage.TYPE_INT_ARGB);
		Graphics2D mapGraphics = mapImage.createGraphics();
		for (Building building : buildings) {
			int radius = (int) (scale * building.getBuildingType().getRadius());
			BufferedImage buildingImage = new BufferedImage(radius * 4, radius * 4, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D buildingGraphics = buildingImage.createGraphics();
			buildingGraphics.setColor(Color.GRAY);
//			buildingGraphics.fillRect(radius, radius, radius * 2, radius * 2);
			buildingGraphics.fillOval(radius, radius, radius * 2, radius * 2);
			buildingGraphics.setColor(Color.BLACK);
			buildingGraphics.drawLine(radius * 2, radius * 2, radius * 3, radius * 2);
			
			BuildingLocation location = building.getLocation();
			AffineTransform tx = AffineTransform.getRotateInstance(location.getRotationRadians(), radius * 2, radius * 2);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
	
			// Drawing the rotated image at the required drawing locations
			mapGraphics.drawImage(op.filter(buildingImage, null), (int) ((scale * location.getX()) - (radius * 2) - minX), (int) ((scale * location.getY()) - (radius * 2) - minY), null);
		}
		for (Street street : streets) {
			mapGraphics.setColor(Color.YELLOW);
			mapGraphics.drawLine((int) (scale * street.getEnd1().getX() - minX), (int) (scale * street.getEnd1().getY() - minY), (int) (scale * street.getEnd2().getX() - minX), (int) (scale * street.getEnd2().getY() - minY));
		}
		return mapImage;
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
