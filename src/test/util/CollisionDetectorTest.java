package test.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.building.BuildingLocation;
import main.exceptions.InitializationException;
import main.util.CollisionDetector;
import main.util.Location;

public class CollisionDetectorTest {
//	private static City city;
	
	@BeforeAll
	public static void init() throws JAXBException, InitializationException {
//		WorldConfig.initialize();
//		city = new City("TestCity");
//		city.getBuildings().clear();
//		city.getStreets().clear();
//		BuildingType buildingType = new BuildingType("TestBuildingType", new ArrayList<>(), null, true, 10);
//		BuildingLocation buildingLocation = new BuildingLocation(0, 0, 0);
//		Building originatingBuilding = new Building("TestBuilding", buildingType, null, buildingLocation, null);
//		city.getBuildings().add(originatingBuilding);
//		Street street = new Street("TestStreet", new Location(-35, 12.5), new Location(35, 12.5), 5);
//		street.addBuilding(originatingBuilding);
//		city.addStreet(street);
	}
	@Test
	public void isBuildingLocationValidTest() {
		//TODO
	}
	@Test
	public void isStreetLocationValidTest() {
		//TODO
	}
	@Test
	public void pointCollidesWithBuildingTest() {
		//TODO
	}
	@Test
	public void buildingCollidesWithStreetTest() {
		Location streetEnd1 = new Location(0, 0);
		Location streetEnd2 = new Location(0, 200);
		double buildingRadius = 10;
		double halfStreetWidth = 2;

		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(0, 0, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(0, 200, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(0, 100, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(10, 100, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));

		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(0, -12, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(0, 212, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(12, 0, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(12, 100, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(12, 200, 0), buildingRadius, streetEnd1, streetEnd2, halfStreetWidth));
		//TODO
	}
	@Test
	public void buildingCollidesWithBuildingTest() {
		//TODO
	}
	@Test
	public void distanceBetweenPointsLessThanLengthTest() {
		//TODO
	}
}
