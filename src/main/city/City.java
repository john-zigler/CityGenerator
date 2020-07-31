package main.city;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
import main.city.street.StreetSegment;
import main.person.Person;
import main.person.profession.Profession;
import main.resource.ResourceAdjustment;
import main.util.Randomizer;
import main.util.WorldConfig;

public class City {
	private static final int SECTOR_SIZE = 40; //Math.sqrt(8) * WorldConfig.getLargestRadius();
//	private static final int LARGEST_MAP_SIDE_LENGTH = (int) (Math.sqrt(Integer.MAX_VALUE) / 4);
//	private static final int NUMBER_OF_SECTOR_INDEXES = LARGEST_MAP_SIDE_LENGTH / SECTOR_SIZE;
	
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
//	private Sector[][] sectorMap = new Sector[NUMBER_OF_SECTOR_INDEXES][NUMBER_OF_SECTOR_INDEXES];
	private Map<Integer, Map<Integer, Sector>> sectorMap = new HashMap<>();
	private static long timeSpentGettingSectors = 0;
	
	
	
	//TODO founder
	//TODO government

	public City(final String name) {
		this.name = name;
//		for (int i = 0; i < NUMBER_OF_SECTOR_INDEXES; i++) {
//			for (int j = 0; j < NUMBER_OF_SECTOR_INDEXES; j++) {
//				sectorMap[i][j] = new Sector();
//			}
//		}
		for (String resource : WorldConfig.resources) {
			nearbyResources.put(resource, 0);
			adjustedResources.put(resource, 0);
		}
		BuildingType graveyardBuildingType = WorldConfig.getBuildingTypeByName("Graveyard");
		BuildingLocation location = new BuildingLocation(new Point2D.Double(0, 0), graveyardBuildingType.getRadius(), Randomizer.generateRandomNumber(0.0, 2 * Math.PI));
		graveyard = new Building("Graveyard", graveyardBuildingType, null, location, null);
		addBuilding(graveyard, true);
	}
	
	public String getName() {
		return this.name;
	}
	public List<Building> getBuildings() {
		return this.buildings;
	}
	public void addBuilding(final Building building) {
		addBuilding(building, Randomizer.rollAgainstOdds(2 * building.getBuildingType().getRadius() / (double) WorldConfig.getLargestRadius()));
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
		for (Sector sector : getSectors(building.getLocation().getBounds2D())) {
			sector.addBuilding(building);
		}
		townCenterX += (building.getLocation().getCenter().getX() - townCenterX) / this.buildings.size();
		townCenterY += (building.getLocation().getCenter().getY() - townCenterY) / this.buildings.size();
		adjustMapEdges(building.getLocation().getBounds2D());
	}
	public void moveBuilding(final Building building, final BuildingLocation newLocation, final StreetSegment newStreetSegment) {
		for (Sector sector : getSectors(building.getLocation().getBounds2D())) {
			sector.getBuildings().remove(building);
		}
		for (Sector sector : getSectors(newLocation.getBounds2D())) {
			sector.addBuilding(building);
		}
		building.getStreetSegment().removeBuilding(building);
		for (BuildingLocation neighbor : building.getLocation().getNeighbors()) {
			neighbor.getNeighbors().remove(building.getLocation());
		}
		townCenterX += (newLocation.getCenter().getX() - building.getLocation().getCenter().getX()) / this.buildings.size();
		townCenterY += (newLocation.getCenter().getY() - building.getLocation().getCenter().getY()) / this.buildings.size();
		building.setLocation(newLocation);
		for (BuildingLocation neighbor : newLocation.getNeighbors()) {
			neighbor.addNeighbor(newLocation);
		}
		building.setStreetSegment(newStreetSegment);
		newStreetSegment.addBuilding(building);
		adjustMapEdges(newLocation.getBounds2D());
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
	}
	public void addStreetSegmentToSectors(StreetSegment segment) {
		for (Sector sector : getSectors(segment.getLine().getBounds2D())) {
			sector.addStreetSegment(segment);
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

	public Point2D getTownCenter() {
		return new Point2D.Double(this.townCenterX, this.townCenterY);
	}
	private void adjustMapEdges(Rectangle2D box) {
		if (box.getMaxX() > maxX) {
			maxX = box.getMaxX();
		}
		if (box.getMaxY() > maxY) {
			maxY = box.getMaxY();
		}
		if (box.getMinX() < minX) {
			minX = box.getMinX();
		}
		if (box.getMinY() < minY) {
			minY = box.getMinY();
		}
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

	public Set<Sector> getSectors(Rectangle2D box) {
		Set<Sector> sectorList = new HashSet<>();
		int minXIndex = getSectorIndex(box.getMinX());
		int minYIndex = getSectorIndex(box.getMinY());
		int maxXIndex = getSectorIndex(box.getMaxX());
		int maxYIndex = getSectorIndex(box.getMaxY());
		for (int i = minXIndex; i <= maxXIndex; i++) {
			for (int j = minYIndex; j <= maxYIndex; j++) {
				sectorList.add(getSector(i, j));
			}
		}
		return sectorList;
	}
	private int getSectorIndex(double length) {
		return (int) (length / SECTOR_SIZE) - (length < 0 ? 1 : 0);
	}
	public Sector getSector(int x, int y) {
		long start = new Date().getTime();
		if (!sectorMap.containsKey(x)) {
			sectorMap.put(x, new HashMap<>());
		}
		if (!sectorMap.get(x).containsKey(y)) {
			sectorMap.get(x).put(y, new Sector());
		}
		Sector sector = sectorMap.get(x).get(y);
		timeSpentGettingSectors += new Date().getTime() - start;
		return sector;
	}
	
	/**
	 * FOR UNIT TESTING PURPOSES ONLY!!!
	 */
	public void clearSectorMap() {
//		for (int i = 0; i < NUMBER_OF_SECTOR_INDEXES; i++) {
//			for (int j = 0; j < NUMBER_OF_SECTOR_INDEXES; j++) {
//				sectorMap[i][j] = new Sector();
//			}
//		}
		sectorMap.clear();
	}

	public static long getTimeSpentGettingSectors() {
		return timeSpentGettingSectors;
	}

	public void renderMap(String filename) {
		try {
			ImageIO.write(generateMap(), "png", new File(filename + ".png"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage generateMap() {
		int mapSizeX = (int) (maxX - minX);
		int mapSizeY = (int) (maxY - minY);
		BufferedImage mapImage = new BufferedImage(mapSizeX, mapSizeY, BufferedImage.TYPE_INT_ARGB);
		Graphics2D mapGraphics = mapImage.createGraphics();
		AffineTransform tx = AffineTransform.getTranslateInstance(-minX, -minY);
		for (Building building : buildings) {
			mapGraphics.setColor(Color.GRAY);
			mapGraphics.fill(tx.createTransformedShape(building.getLocation()));
		}
		for (Street street : streets) {
			for (StreetSegment segment : street.getSegments()) {
				mapGraphics.setColor(Color.YELLOW);
				mapGraphics.draw(tx.createTransformedShape(segment.getLine()));
			}
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
