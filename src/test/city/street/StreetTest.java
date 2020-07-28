package test.city.street;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


import main.building.Building;
import main.building.BuildingLocation;
import main.building.BuildingType;
import main.city.City;
import main.city.street.Street;
import main.exceptions.InitializationException;
import main.util.Location;
import main.util.WorldConfig;

public class StreetTest {
	private static City city;
	private static BuildingType buildingType;
	private static Building originatingBuilding;
	
	@BeforeAll
	public static void init() throws JAXBException, InitializationException {
		WorldConfig.initialize();
		city = new City("TestCity");
		city.getBuildings().clear();
		buildingType = new BuildingType("TestBuildingType", new ArrayList<>(), null, true, 10);
		BuildingLocation buildingLocation = new BuildingLocation(0, 0, 0);
		originatingBuilding = new Building("TestBuilding", buildingType, null, buildingLocation, null);
		city.getBuildings().add(originatingBuilding);
	}
	@BeforeEach
	public void beforeMethod() {
		city.getStreets().clear();
	}
	@Test
	public void getAllLocationsHorizontalTest() {
		Street street = new Street("TestStreet", new Location(-35, 12.5), new Location(35, 12.5), 5);
		street.addBuilding(originatingBuilding);
		city.addStreet(street);
		List<BuildingLocation> buildingLocations = street.getAllLocations(buildingType, city);
		BuildingLocation expectedLocation1 = new BuildingLocation(0, 25, 1.5 * Math.PI);
		assertTrue(buildingLocations.contains(expectedLocation1));
		
		BuildingLocation expectedLocation2 = new BuildingLocation(-22, 0, 0.5 * Math.PI);
		assertTrue(buildingLocations.contains(expectedLocation2));
		
		BuildingLocation expectedLocation3 = new BuildingLocation(22, 0, 0.5 * Math.PI);
		assertTrue(buildingLocations.contains(expectedLocation3));
	}
	
	@Test
	public void getPossibleLocationsVerticalTest() {
		Street street = new Street("TestStreet", new Location(12.5, -35), new Location(12.5, 35), 5);
		street.addBuilding(originatingBuilding);
		city.addStreet(street);
		List<BuildingLocation> buildingLocations = street.getAllLocations(buildingType, city);
		BuildingLocation expectedLocation1 = new BuildingLocation(25, 0, Math.PI);
		assertTrue(buildingLocations.contains(expectedLocation1));
		
		BuildingLocation expectedLocation2 = new BuildingLocation(0, -22, 0);
		assertTrue(buildingLocations.contains(expectedLocation2));
		
		BuildingLocation expectedLocation3 = new BuildingLocation(0, 22, 0);
		assertTrue(buildingLocations.contains(expectedLocation3));
	}
}
