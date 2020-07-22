package city;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import exceptions.InitializationException;
import person.Person;
import person.PersonGenerator;
import util.HtmlOutputter;
import util.Randomizer;
import util.WorldConfig;

public class CityGenerator {
	private static final double ODDS_OF_NEW_FAMILY = 0.1;
	private static final int numberOfChildren = 3;
	private static final int numberOfStartingFamilies = 5;
	private static final int endingYear = 1759;
	private static final int targetPopulation = 5000;
	private static final String cityName = "Alvis";

	public static void main(String[] args) throws JAXBException, InitializationException, IOException {
		WorldConfig.initialize();
		System.out.println();
		System.out.println();
		Date start = new Date();
		City city = generateCity();
		Date finish = new Date();
		long totalTime = (finish.getTime() - start.getTime());

		System.out.println("Total time spent: " + totalTime);
		System.out.println("Time spent handling death: " + Person.TIME_SPENT_HANDLING_DEATH);
		System.out.println("Time spent making babies: " + Person.TIME_SPENT_MAKING_BABIES);
		System.out.println("Time spent choosing apprenticeships: " + Person.TIME_SPENT_CHOOSING_APPRENTICESHIPS);
		System.out.println("Time spent looking for mentors: " + Person.TIME_SPENT_LOOKING_FOR_MENTORS);
		System.out.println("Time spent considering professions for apprenticeship: " + Person.TIME_SPENT_CONSIDERING_PROFESSIONS);
		System.out.println("Time spent choosing professions: " + Person.TIME_SPENT_CHOOSING_PROFESSIONS);
		System.out.println("Time spent moving out: " + Person.TIME_SPENT_MOVING_OUT);
		System.out.println("Time spent finding spouses: " + Person.TIME_SPENT_FINDING_SPOUSES);
		System.out.println("Time spent handling job knowledge: " + Person.TIME_SPENT_HANDLING_JOB_KNOWLEDGE);

		HtmlOutputter outputter = new HtmlOutputter("C:/CityGenerator/" + city.getName() + "_" + new Date().getTime() + "/");
		outputter.outputCity(city);

		System.out.println();
		List<String> resources = new ArrayList<>(WorldConfig.resources);
		Collections.sort(resources);
		for (String resource : resources) {
			System.out.println("Final amount of " + resource + ": " + city.getAvailableResources().get(resource));
		}
		System.out.println();
		System.out.println("Final amount of living citizens: " + city.getLivingCitizens().size());
		System.out.println("Final amount of dead citizens: " + city.getDeadCitizens().size());
	}
	
	public static City generateCity() {
		City city = new City(cityName);
		for (int i = 0; i < numberOfStartingFamilies; i++) {
			PersonGenerator.generateFamily(city, numberOfChildren);
		}
		while (city.getLivingCitizens().size() > 0 && city.getLivingCitizens().size() < targetPopulation) {
			WorldConfig.incrementYear();
			long start = new Date().getTime();
			Set<Person> livingCitizens = new HashSet<>(city.getLivingCitizens());
			for (Person citizen : livingCitizens) {
				citizen.addYear();
			}
			if (Randomizer.rollAgainstOdds(ODDS_OF_NEW_FAMILY)) {
				PersonGenerator.generateFamily(city, numberOfChildren);
			}
			long finish = new Date().getTime();
			System.out.println("Year " + WorldConfig.getYear() + " processed in " + (finish - start) + " ms. Current population: " + city.getLivingCitizens().size());
		}
		int startingYear = endingYear - WorldConfig.getYear();
		for (Person citizen : city.getLivingCitizens()) {
			citizen.adjustYears(startingYear);
		}
		for (Person citizen : city.getDeadCitizens()) {
			citizen.adjustYears(startingYear);
		}
		WorldConfig.adjustYear(startingYear);
		return city;
	}

}
