package main.util;

import java.util.List;
import java.util.Random;

public class Randomizer {
	private static final double PREFERENCE_DEVIATION = .15;
	private static final double STREET_ANGLE_DEVIATION = .3;
	private static Random random = new Random();
	
	private Randomizer() {
		//Hidden constructor
	}

	//Generates a number between a given min and max, inclusive
	public static int generateRandomNumber(int min, int max) {
		return random.nextInt(1+max-min) + min;
	}

	//Generates a number between a given min (inclusive) and max (exclusive) 
	public static double generateRandomNumber(double min, double max) {
		return random.nextDouble() * (max-min) + min;
	}
	
	//Generates a number between 0 and 1, using the given points as guidelines.
	public static double generateRandomPreference(double point1, double point2) {
		double chosenPoint;
		switch (random.nextInt(3)) {
		case 0:
			chosenPoint = point1;
			break;
		case 1:
			chosenPoint = point2;
			break;
		default:
			chosenPoint = (point1 + point2) / 2;
			break;
		}
		//Deviate in either direction
		double lowerBound = (chosenPoint - PREFERENCE_DEVIATION) < 0 ? 0 : (chosenPoint - PREFERENCE_DEVIATION);
		double upperBound = (chosenPoint + PREFERENCE_DEVIATION) > 1 ? 1 : (chosenPoint + PREFERENCE_DEVIATION);
		return random.nextDouble() * (upperBound - lowerBound) + lowerBound;
	}
	public static <T> T pickElement(final List<T> list) {
		if (list.isEmpty()) {
			System.err.println("Can't pick element from empty list!");
		}
		return list.get(random.nextInt(list.size()));
	}
	public static <T> T pickElement(final T[] array) {
		return array[random.nextInt(array.length)];
	}
	public static boolean rollAgainstOdds(double odds) {
		double rgn = random.nextDouble();
		return rgn < odds;
	}
	public static double generateRandomStreetAngle(double startingAngle) {
		double deviation = (random.nextDouble() * 2 * STREET_ANGLE_DEVIATION) -  STREET_ANGLE_DEVIATION;
		return startingAngle + deviation;
	}
}
