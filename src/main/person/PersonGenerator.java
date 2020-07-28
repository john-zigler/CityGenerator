package main.person;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.city.City;
import main.person.gender.Gender;
import main.person.profession.Profession;
import main.person.race.Race;
import main.util.Randomizer;
import main.util.WorldConfig;

public class PersonGenerator {
	public static final double MAX_AGE_RATIO = 2.0;
	
	private PersonGenerator() {
		//Hidden constructor
	}
	
	public static Person generateAdultperson(City homeCity) {
		Race race = Randomizer.pickElement(WorldConfig.getRacesWithLikelihood());
		Gender gender = Randomizer.pickElement(Gender.values());
		String name = generatePersonName(race, gender, new ArrayList<>());
		int age = race.getAgeOfAdulthood();
		while (!Randomizer.rollAgainstOdds(race.getMoveOutRate()) && age < race.getMaxAge()) {
			age++;
		}
		Person person = new Person(name, Randomizer.pickElement(race.getFamilyNames()), race, gender, homeCity, age);
		for (Profession profession : WorldConfig.getProfessions()) {
			person.setEmploymentPreference(profession, Randomizer.generateRandomPreference(0, 1));
			//Make sure the new person is qualified to do any job, to bring new knowledge sets to the town
			person.setJobKnowledge(profession, profession.getMinTrainingRequired());
		}
		person.chooseProfession();
		person.findHouse(false);
		return person;
	}
	
	public static Person generateChild(Person mother, Person father) {
		Race race = mother.getRace().getOffspringRace(father.getRace());
		Gender gender = Randomizer.pickElement(Gender.values());
		String name = generatePersonName(race, gender, Arrays.asList(mother, father));
		Person person = new Person(name, father.getFamilyName(), race, gender, mother.getHomeCity(), 0);
		person.setParents(Arrays.asList(mother, father));
		mother.getOffspring().add(person);
		father.getOffspring().add(person);
		person.setHome(mother.getHome(), true);
		for (Profession profession : WorldConfig.getProfessions()) {
			person.setEmploymentPreference(profession, Randomizer.generateRandomPreference(
					mother.getEmploymentPreference(profession), father.getEmploymentPreference(profession)));
		}
		if (mother.getProfession() != null) {
			person.setEmploymentPreference(mother.getProfession(), Randomizer.generateRandomPreference(
					person.getEmploymentPreference(mother.getProfession()), 1.0));
		}
		if (father.getProfession() != null) {
			person.setEmploymentPreference(father.getProfession(), Randomizer.generateRandomPreference(
					person.getEmploymentPreference(father.getProfession()), 1.0));
		}
		for (Race otherRace : WorldConfig.getRaces()) {
			person.setRacialPreference(otherRace, Randomizer.generateRandomPreference(
					mother.getRacialPreference(otherRace), father.getRacialPreference(otherRace)));
		}
		return person;
	}
	
	public static String generatePersonName(Race race, Gender gender, List<Person> parents) {
		List<String> possibleNames = new ArrayList<>();
		possibleNames.addAll(race.getUnisexNames());
		possibleNames.addAll(gender.equals(Gender.Male) ? race.getMaleNames() : race.getFemaleNames());
		String name = Randomizer.pickElement(possibleNames);
		while (hasSiblingWithSameName(name, parents)) {
			name = Randomizer.pickElement(possibleNames);
		}
		return name;
	}
	
	private static boolean hasSiblingWithSameName(String name, List<Person> parents) {
		for (Person parent : parents) {
			for (Person sibling : parent.getOffspring()) {
				if (sibling.getFirstName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public static Person generateSpouse(Person person) {
		//randomly choose from among all races that will produce offspring
		Race race = Randomizer.pickElement(person.getRace().getMatingRaces());
		Gender gender = person.getGender().equals(Gender.Male) ? Gender.Female : Gender.Male;
		String name = generatePersonName(race, gender, new ArrayList<>());
		
		//Generate min and max ages based on how far along in adulthood they are; e.g. a middle-aged human can marry a middle-aged elf
		double adulthoodRatio = new Double(person.getAge() - person.getRace().getAgeOfAdulthood())
				/ new Double(person.getRace().getMaxAge() - person.getRace().getAgeOfAdulthood());
		int spouseAdulthoodLength = race.getMaxAge() - race.getAgeOfAdulthood();
		int minAge = (int) (adulthoodRatio * spouseAdulthoodLength * (1/MAX_AGE_RATIO)) + race.getAgeOfAdulthood();
		int maxAge = (int) (adulthoodRatio * spouseAdulthoodLength * MAX_AGE_RATIO) + race.getAgeOfAdulthood();
		maxAge = maxAge < race.getMaxAge() ? maxAge : race.getMaxAge();
		int age = Randomizer.generateRandomNumber(minAge, maxAge);
		
		Person spouse = new Person(name, Randomizer.pickElement(race.getFamilyNames()), race, gender, person.getHomeCity(), age);
		for (Profession profession : WorldConfig.getProfessions()) {
			spouse.setEmploymentPreference(profession, Randomizer.generateRandomPreference(0, 1));
			//Make sure the new person is qualified to do any job, to bring new knowledge sets to the town
			spouse.setJobKnowledge(profession, profession.getMinTrainingRequired());
		}
		
		spouse.chooseProfession();
		person.marry(spouse);
		return spouse;
	}
	
	public static List<Person> generateFamily(City city, int maxNumberOfKids) {
		List<Person> family = new ArrayList<>();
		System.out.println("A new family has moved in!");
		Person person = generateAdultperson(city);
		family.add(person);
		Person spouse = generateSpouse(person);
		family.add(spouse);
		
		for (Race otherRace : WorldConfig.getRaces()) {
			person.setRacialPreference(otherRace, Randomizer.generateRandomPreference(0, 1));
			spouse.setRacialPreference(otherRace, Randomizer.generateRandomPreference(0, 1));
		}
		//They should at least like each other
		spouse.setRacialPreference(person.getRace(), .9);
		person.setRacialPreference(spouse.getRace(), .9);
		//Go through again and re-calculate for halfbreeds
		for (Race otherRace : WorldConfig.getRaces()) {
			Race personOffSpringRace = person.getRace().getOffspringRace(otherRace);
			if (personOffSpringRace != null && !personOffSpringRace.equals(person.getRace()) && !personOffSpringRace.equals(otherRace)) {
				person.setRacialPreference(personOffSpringRace, Randomizer.generateRandomPreference(person.getRacialPreference(otherRace), 1.0));
			}
			Race spouseOffSpringRace = spouse.getRace().getOffspringRace(otherRace);
			if (spouseOffSpringRace != null && !spouseOffSpringRace.equals(spouse.getRace()) && !spouseOffSpringRace.equals(otherRace)) {
				spouse.setRacialPreference(spouseOffSpringRace, Randomizer.generateRandomPreference(spouse.getRacialPreference(otherRace), 1.0));
			}
		}
		
		while (person.isAlive() && spouse.isAlive() && person.getOffspring().size() < maxNumberOfKids) {
			person.addYear();
			spouse.addYear();
			for (Person child : person.getOffspring()) {
				if (child.isAlive()) {
					child.addYear();
				}
			}
		}
		family.addAll(person.getOffspring());
		return family;
	}
}
