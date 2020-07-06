package util;

import java.util.List;
import java.util.Random;

public class Randomizer {
	private static final double DEVIATION = .15;
	private static Random random = new Random();
	
	private Randomizer() {
		//Hidden constructor
	}

	//Generates a number between a given min and max, inclusive
	public static int generateRandomNumber(int min, int max) {
		return random.nextInt(1+max-min) + min;
	}
	//Generates a number between 0 and 1, using the given points as guidelines.
	public static double generateRandomDouble(double point1, double point2) {
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
		//Deviate up to .1 in either direction
		double lowerBound = (chosenPoint - DEVIATION) < 0 ? 0 : (chosenPoint - DEVIATION);
		double upperBound = (chosenPoint + DEVIATION) > 1 ? 1 : (chosenPoint + DEVIATION);
		return random.nextDouble() * (upperBound - lowerBound) + lowerBound;
	}
	public static <T> T pickElement(final List<T> list) {
		return list.get(random.nextInt(list.size()));
	}
	public static <T> T pickElement(final T[] array) {
		return array[random.nextInt(array.length)];
	}
	public static boolean rollAgainstOdds(double odds) {
		double rgn = random.nextDouble();
		return rgn < odds;
	}
}
