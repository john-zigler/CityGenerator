package main.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import main.building.BuildingType;
import main.exceptions.InitializationException;
import main.person.profession.Profession;
import main.person.race.Race;
import main.resource.ResourceAdjustment;

public class WorldConfig {
	public static final Set<String> resources = new HashSet<>(Arrays.asList("Clothing", "Fish", "Leadership",
			"Luxury Item", "Magic", "Medicine", "Pottery", "Religion", "Security", "Water", "Wood"));

	private static Map<String, BuildingType> buildingTypeMap = new HashMap<>();
	private static Map<String, Profession> professionMap = new HashMap<>();
	private static Map<String, Race> raceMap = new HashMap<>();
	private static List<Race> racesWithLikelihood = new ArrayList<>();
	private static int id = Integer.MIN_VALUE;
	private static int year = 0;

	private static final String buildingTypesFilepath = "config/buildingTypes";
	private static final String professionsFilepath = "config/professions";
	private static final String racesFilepath = "config/races";
	
	private WorldConfig() {
		//Hidden constructor
	}
	
	public static void initialize() throws JAXBException, InitializationException {
		loadProfessions();
		loadBuildingTypes();
		loadRaces();
	}
	
	public static List<BuildingType> getBuildingTypes() {
		return new ArrayList<>(buildingTypeMap.values());
	}
	public static BuildingType getBuildingTypeByName(String name) {
		return buildingTypeMap.get(name);
	}
	
	public static List<Profession> getProfessions() {
		return new ArrayList<>(professionMap.values());
	}
	public static Profession getProfessionByName(String name) {
		return professionMap.get(name);
	}

	public static List<Race> getRaces() {
		return new ArrayList<>(raceMap.values());
	}
	public static List<Race> getRacesWithLikelihood() {
		return racesWithLikelihood;
	}
	public static Race getRaceByName(String name) {
		return raceMap.get(name);
	}
	public static int getNextId() {
		//If this fails, we have generated over 4 billion objects.
		return id++;
	}
	public static int getYear() {
		return year;
	}
	public static void adjustYear(int adjustment) {
		year += adjustment;
	}
	public static void incrementYear() {
		year++;
	}
	
	private static void loadBuildingTypes() throws JAXBException, InitializationException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Profession.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		File folder = new File(buildingTypesFilepath);
		for (File file : folder.listFiles()) {
			BuildingType buildingType = (BuildingType) jaxbUnmarshaller.unmarshal(file);
			buildingTypeMap.put(buildingType.getName(), buildingType);
			if (!buildingType.getPositions().isEmpty()) {
				for (String position : buildingType.getPositions()) {
					if (getProfessionByName(position) == null) {
						throw new InitializationException("Error while loading " + buildingType.getName() + ": Unable to find profession named '" + position + "'");
					}
				}
				Profession profession = getProfessionByName(buildingType.getPositions().get(0));
				profession.setProprietorOf(buildingType);
			}
			System.out.println("Loaded building type: " + buildingType.getName());
		}
	}
	
	private static void loadProfessions() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Profession.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		File folder = new File(professionsFilepath);
		for (File file : folder.listFiles()) {
			Profession profession = (Profession) jaxbUnmarshaller.unmarshal(file);
			professionMap.put(profession.getName(), profession);
			for (ResourceAdjustment resourceAdjustment : profession.getResourceAdjustments()) {
				resources.add(resourceAdjustment.getName());
			}
			System.out.println("Loaded profession: " + profession.getName());
		}
	}
	
	private static void loadRaces() throws JAXBException {
		JAXBContext jaxbContext = JAXBContext.newInstance(Race.class);
		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		File folder = new File(racesFilepath);
		for (File file : folder.listFiles()) {
			Race race = (Race) jaxbUnmarshaller.unmarshal(file);
			raceMap.put(race.getName(), race);
			for (int i = 0; i < race.getLikelihood(); i++) {
				racesWithLikelihood.add(race);
			}
			System.out.println("Loaded race: " + race.getName());
		}
	}
}
