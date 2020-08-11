package main.building.floor;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import main.building.BuildingType;
import main.util.Randomizer;

public class FloorGenerator {
	/**
	 * This produces a list of floors that describe a building with a jettied architecture
	 * https://www.youtube.com/watch?v=zBVPcr7VjyQ
	 * 
	 * @param radius the building radius, as stored in the xml
	 * @param includeCellar true if this building has a cellar
	 * @param includeLoft true if this building has a loft
	 * @param numberOfFloors the number of floors to generate, not including the cellar or loft
	 * @return the list of floors
	 */
	public static List<Floor> generateFloors(BuildingType buildingType, boolean includeCellar, boolean includeLoft, int numberOfFloors) {
		int radius = buildingType.getRadius();
		List<Floor> floors = new ArrayList<>();
		List<Rectangle> originalRectangles = generateRectangles(radius, buildingType.getFloorplanComplexity());
		if (includeCellar) {
			floors.add(new Floor(radius, decreaseRectangles(originalRectangles)));
		}
		floors.add(new Floor(radius, originalRectangles));
		List<Rectangle> rectangles = originalRectangles;
		for (int i = 1; i < numberOfFloors; i++) {
			rectangles = increaseRectangles(rectangles, radius);
			floors.add(new Floor(radius, rectangles));
		}
		if (includeLoft) {
			floors.add(new Floor(radius, rectangles));
		}
		return floors;
	}
	
	private static List<Rectangle> generateRectangles(int radius, int numberOfRectangles) {
		List<Rectangle> rectangles = new ArrayList<>();
		for (int i = 0; i < numberOfRectangles; i++) {
			Rectangle rectangle = new Rectangle();
			int minSize = radius / (i + 1);
			rectangle.width = Randomizer.generateRandomNumber(minSize, minSize * 2);
			rectangle.height = Randomizer.generateRandomNumber(minSize, minSize * 2);
			rectangle.x = Randomizer.generateRandomNumber(0, radius * 2 - rectangle.width);
			rectangle.y = Randomizer.generateRandomNumber(0, radius * 2 - rectangle.height);
			rectangles.add(rectangle);
		}
		return rectangles;
	}
	
	private static List<Rectangle> increaseRectangles(List<Rectangle> rectangles, int radius) {
		List<Rectangle> newRectangles = new ArrayList<>();
		for (Rectangle rectangle : rectangles) {
			Rectangle newRectangle = new Rectangle();
			newRectangle.x = rectangle.x == 0 ? 0 : rectangle.x - 1;
			newRectangle.y = rectangle.y == 0 ? 0 : rectangle.y - 1;
			if (newRectangle.x + rectangle.width + 2 >= 2 * radius) {
				newRectangle.width = 2 * radius - newRectangle.x - 1;
			} else {
				newRectangle.width = rectangle.width + 2;
			}
			if (newRectangle.y + rectangle.height + 2 >= 2 * radius) {
				newRectangle.height = 2 * radius - newRectangle.y - 1;
			} else {
				newRectangle.height = rectangle.height + 2;
			}
			newRectangles.add(rectangle);
		}
		return newRectangles;
	}
	
	private static List<Rectangle> decreaseRectangles(List<Rectangle> rectangles) {
		List<Rectangle> newRectangles = new ArrayList<>();
		for (Rectangle rectangle : rectangles) {
			Rectangle newRectangle = new Rectangle();
			newRectangle.x = rectangle.x + 1;
			newRectangle.y = rectangle.y + 1;
			newRectangle.width = rectangle.width - 2;
			newRectangle.height = rectangle.height - 2;
			newRectangles.add(rectangle);
		}
		return newRectangles;
	}
}
