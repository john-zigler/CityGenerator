package test.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import static org.junit.jupiter.api.Assertions.assertFalse;

import javax.xml.bind.JAXBException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import main.building.BuildingLocation;
import main.exceptions.InitializationException;
import main.util.CollisionDetector;

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
		Line2D street = new Line2D.Double(0, 0, 0, 200);
		double buildingRadius = 10;
		double halfStreetWidth = 2;

		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(0, 0), buildingRadius, 0), street, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(0, 200), buildingRadius, 0), street, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(0, 100), buildingRadius, 0), street, halfStreetWidth));
		assertTrue(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(10, 100), buildingRadius, 0), street, halfStreetWidth));

		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(0, -12), buildingRadius, 0), street, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(0, 212), buildingRadius, 0), street, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(12, 0), buildingRadius, 0), street, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(12, 100), buildingRadius, 0), street, halfStreetWidth));
		assertFalse(CollisionDetector.buildingCollidesWithStreet(new BuildingLocation(new Point2D.Double(12, 200), buildingRadius, 0), street, halfStreetWidth));
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
