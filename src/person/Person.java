package person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import building.Building;
import building.BuildingGenerator;
import city.City;
import event.Event;
import person.gender.Gender;
import person.profession.Profession;
import person.race.Race;
import resource.ResourceAdjustment;
import util.Constants;
import util.Pair;
import util.Randomizer;
import util.WorldConfig;

public class Person {
	public static long TIME_SPENT_HANDLING_DEATH = 0l;
	public static long TIME_SPENT_MAKING_BABIES = 0l;
	public static long TIME_SPENT_CHOOSING_APPRENTICESHIPS = 0l;
	public static long TIME_SPENT_CHOOSING_PROFESSIONS = 0l;
	public static long TIME_SPENT_MOVING_OUT = 0l;
	public static long TIME_SPENT_FINDING_SPOUSES = 0l;
	public static long TIME_SPENT_HANDLING_JOB_KNOWLEDGE = 0l;
	public static long TIME_SPENT_LOOKING_FOR_MENTORS = 0l;
	public static long TIME_SPENT_CONSIDERING_PROFESSIONS = 0l;
	
	private final int id = WorldConfig.getNextId();
	private String firstName;
	private String familyName;
	private Race race;
	private Gender gender;
	private Person spouse;
	private List<Person> parents = new ArrayList<>();
	private List<Person> offspring = new ArrayList<>();
	private boolean alive = true;
	//Probably 0-1 scale, with 0 being "I ain't doin' that" and 1 being "I was BORN for this!"
	private Map<Profession, Double> employmentPreferences = new HashMap<>();
	private Map<Race, Double> racialPreferences = new HashMap<>();
	private Map<Profession, Integer> jobKnowledges = new HashMap<>();
	private Profession apprenticeship;
	private Person mentor;
	private List<Person> oldMentors = new ArrayList<>();
	private Profession profession;
	private List<Person> apprentices = new ArrayList<>();
	private List<Person> oldApprentices = new ArrayList<>();
	private Building placeOfEmployment;
	private List<Building> placesFounded = new ArrayList<>();
	private City homeCity;
	private Building home;
	private List<ResourceAdjustment> resourceAdjustments = new ArrayList<>();
	private Integer birthYear;
	private Integer deathYear;
	private List<Event> events = new ArrayList<>();
	
	public Person(String firstName, String familyName, Race race, Gender gender, City homeCity, int age) {
		this.gender = gender;
		this.race = race;
		this.firstName = firstName;
		this.familyName = familyName;
		for (Profession profession : WorldConfig.getProfessions()) {
			employmentPreferences.put(profession, 0.1);
			jobKnowledges.put(profession, 0);
		}
		for (Race otherRace : WorldConfig.getRaces()) {
			racialPreferences.put(otherRace, 1.0);
		}
		for (String resource : WorldConfig.resources) {
			resourceAdjustments.add(new ResourceAdjustment(resource, getResourceAdjustment(resource)));
		}
		this.setHomeCity(homeCity);
		this.birthYear = WorldConfig.getYear() - age;
		events.add(new Event(this.birthYear, "Born"));
	}

	public String getFirstName() {
		return firstName;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public String getFullName() {
		return race.getNamingConvention()
				.replaceAll(Constants.FIRST_NAME_PLACEHOLDER, firstName)
				.replaceAll(Constants.FAMILY_NAME_PLACEHOLDER, familyName);
	}
	public Race getRace() {
		return race;
	}
	public Gender getGender() {
		return gender;
	}
	public int getAge() {
		return (deathYear == null ? WorldConfig.getYear() - birthYear : deathYear - birthYear);
	}
	public Person getSpouse() {
		return spouse;
	}
	public void setSpouse(Person spouse) {
		this.spouse = spouse;
	}
	public List<Person> getParents() {
		return parents;
	}
	public void setParents(List<Person> parents) {
		this.parents = parents;
	}
	public List<Person> getOffspring() {
		return offspring;
	}
	public void setOffspring(List<Person> offspring) {
		this.offspring = offspring;
	}
	public Set<Person> getSiblings() {
		Set<Person> siblings = new HashSet<>();
		for (Person parent : parents) {
			siblings.addAll(parent.getOffspring());
		}
		siblings.remove(this);
		return siblings;
	}
	public Set<Person> getImmediateFamily() {
		Set<Person> family = new HashSet<>();
		family.addAll(getParents());
		family.addAll(getSiblings());
		family.addAll(getOffspring());
		return family;
	}
	//Grandparents, aunts, uncles, cousins, nieces, nephews, grandchildren
	public Set<Person> getExtendedFamily() {
		Set<Person> family = new HashSet<>();
		if (parents.isEmpty()) {
			family.addAll(getImmediateFamily());
		} else {
			for (Person parent : parents) {
				family.addAll(parent.getImmediateFamily());
			}
		}
		Set<Person> includingGrandchildren = new HashSet<>(family);
		for (Person familyMember : family) {
			includingGrandchildren.addAll(familyMember.offspring);
		}
		Set<Person> includingSpouses = new HashSet<>(includingGrandchildren);
		for (Person familyMember : includingGrandchildren) {
			if (familyMember.spouse != null) {
				includingSpouses.add(familyMember.spouse);
			}
		}
		return includingSpouses;
	}
	public boolean isAlive() {
		return alive;
	}
	private void setAlive(boolean alive) {
		this.alive = alive;
	}
	public Map<Profession, Double> getEmploymentPreferences() {
		return employmentPreferences;
	}
	public Double getEmploymentPreference(Profession profession) {
		return employmentPreferences.get(profession);
	}
	public void setEmploymentPreference(Profession profession, Double weight) {
		this.employmentPreferences.put(profession, weight);
	}
	public Map<Race, Double> getRacialPreferences() {
		return racialPreferences;
	}
	public Double getRacialPreference(Race race) {
		return racialPreferences.get(race);
	}
	public void setRacialPreference(Race race, Double weight) {
		this.racialPreferences.put(race, (race.equals(this.race) ? 1.0 : weight));
	}
	public Map<Profession, Integer> getJobKnowledges() {
		return jobKnowledges;
	}
	public void setJobKnowledge(Profession profession, Integer knowledge) {
		this.jobKnowledges.put(profession, knowledge);
	}
	private void incrementJobKnowledge(Profession profession) {
		this.jobKnowledges.put(profession, jobKnowledges.getOrDefault(profession, 0) + 1);
	}
	public Profession getApprenticeship() {
		return apprenticeship;
	}
	private void setApprenticeship(Profession apprenticeship) {
		homeCity.unregisterApprentice(this);
		this.apprenticeship = apprenticeship;
		if (apprenticeship != null) {
			events.add(new Event("Started apprenticeship as " + apprenticeship.getApprenticeName()));
		}
		homeCity.registerApprentice(this);
	}
	public Person getMentor() {
		return mentor;
	}
	private void setMentor(Person mentor) {
		if (this.mentor != null) {
			this.oldMentors.add(this.mentor);
		}
		this.mentor = mentor;
		if (mentor != null) {
			events.add(new Event("Became apprentice to " + mentor.getFullName()));
		}
	}
	public List<Person> getOldMentors() {
		return oldMentors;
	}
	public Profession getProfession() {
		return profession;
	}
	private void setProfession(Profession profession) {
		if (this.profession != null) {
			events.add(new Event("Stopped working as " + this.profession.getName()));
		}
		homeCity.unregisterProfessional(this);
		this.profession = profession;
		if (this.profession != null) {
			events.add(new Event("Started working as " + this.profession.getName()));
		}
		homeCity.registerProfessional(this);
		if (this.mentor != null) {
			setApprenticeship(null);
			this.mentor.removeApprentice(this);
		}
	}
	public List<Person> getApprentices() {
		return apprentices;
	}
	private void addApprentice(Person apprentice) {
		apprentice.setMentor(this);
		this.apprentices.add(apprentice);
		events.add(new Event("Became mentor to " + apprentice.getFullName()));
	}
	private void removeApprentice(Person apprentice) {
		apprentice.setMentor(null);
		this.oldApprentices.add(apprentice);
		this.apprentices.remove(apprentice);
	}
	public List<Person> getOldApprentices() {
		return oldApprentices;
	}
	public int getAvailableApprenticeships() {
		if (this.profession == null) {
			return 0;
		}
		return this.profession.getNumberOfApprentices() - this.apprentices.size();
	}

	public Building getPlaceOfEmployment() {
		return placeOfEmployment;
	}

	private void setPlaceOfEmployment(Building placeOfEmployment) {
		if (this.placeOfEmployment != null) {
			this.placeOfEmployment.getEmployees().remove(this);
			events.add(new Event("Stopped working at " + this.placeOfEmployment.getName()));
		}
		if (placeOfEmployment != null) {
			placeOfEmployment.addEmployee(this);
			events.add(new Event("Started working at " + placeOfEmployment.getName()));
		}
		this.placeOfEmployment = placeOfEmployment;
	}

	public List<Building> getPlacesFounded() {
		return placesFounded;
	}
	public void addPlaceFounded(Building building) {
		placesFounded.add(building);
		events.add(new Event("Founded " + building.getName()));
	}

	public City getHomeCity() {
		return homeCity;
	}
	private void setHomeCity(City homeCity) {
		if (homeCity != null) {
			homeCity.addLivingCitizen(this);
		}
		if (this.homeCity != null) {
			this.homeCity.getLivingCitizens().remove(this);
		}
		this.homeCity = homeCity;
	}

	public Building getHome() {
		return home;
	}

	public void setHome(Building home, boolean generateEvents) {
		if (generateEvents) {
			if (home == null) {
				generateEvents = this.home != null;
			} else {
				generateEvents = !home.equals(this.home);
			}
		}
		StringBuilder eventDescription = new StringBuilder("Moved ");
		if (home == null || !home.getBuildingType().getName().equals("Graveyard")) {
			for (Person child : offspring) {
				if (child.getHome() == null || child.getHome().equals(this.home)) {
					child.setHome(home, generateEvents);
				}
			}
		}
		if (this.home != null) {
			eventDescription.append(" out of " + this.home.getName() + " and");
			this.home.getOccupants().remove(this);
		}
		eventDescription.append(" into ");
		this.home = home;
		if (home != null) {
			eventDescription.append(home.getName());
			home.addOccupant(this);
			homeCity.removeOrphan(this);
		} else {
			eventDescription.append("the streets");
		}
		if (generateEvents) {
			events.add(new Event(eventDescription.toString()));
		}
	}
	
	public boolean isLivingInParentsHouse() {
		if (this.home != null) {
			for (Person parent : this.parents) {
				if (this.home.equals(parent.getHome())) {
					return true;
				}
			}
		}
		return false;
	}

	public List<ResourceAdjustment> getResourceAdjustments() {
		return this.resourceAdjustments;
	}
	
	private int getResourceAdjustment(String resource) {
		//How many of each are consumed over the course of a year
		switch (resource) {
		case "Beer":
		case "Spirit":
		case "Wine":
			return 50;
		case "Bread":
		case "Cheese":
		case "Egg":
		case "Fruit":
		case "Meat":
		case "Milk":
		case "Vegetable":
			return 300;
		case "Chicken":
		case "Fish":
			return 100;
		case "Money":
			//super vague, who knows?
			return 10;
		case "Clothing":
		case "Leadership":
		case "LuxuryItem":
		case "Magic":
		case "Medicine":
		case "Pottery":
		case "Religion":
		case "Security":
		case "Shoe":
		case "Wood":
			return 6;
		case "Traffic":
		case "Citizen":
			return -1;
		default:
			return 1;
		}
	}
	
	public Integer getBirthYear() {
		return birthYear;
	}
	public Integer getDeathYear() {
		return deathYear;
	}

	public List<Event> getEvents() {
		return events;
	}

	public void addYear() {
		if (!alive) {
			//Do nothing
			return;
		}
		int age = getAge();
		
		//Roll for death
		double oddsOfDeath = Math.pow(new Double(age) / new Double(race.getMaxAge()), 6);
		if (Randomizer.rollAgainstOdds(oddsOfDeath)) {
			Date start = new Date();
			handleDeath();
			Date finish = new Date();
			TIME_SPENT_HANDLING_DEATH += (finish.getTime() - start.getTime());
			return;
		}
		
		//Roll for babies; only roll for females with living husbands
		if (gender.equals(Gender.Female)
				&& spouse != null
				&& spouse.isAlive()
				&& age < race.getMaxMatingAge()
				&& spouse.getAge() < spouse.getRace().getMaxMatingAge()
				&& Randomizer.rollAgainstOdds(race.getBreedingRate())) {
			Date start = new Date();
			Person child = PersonGenerator.generateChild(this, spouse);
			events.add(new Event("Gave birth to " + child.getFullName()));
			Date finish = new Date();
			TIME_SPENT_MAKING_BABIES += (finish.getTime() - start.getTime());
		}
		
		//Become an apprentice
		if (mentor == null && profession == null && age > race.getAgeOfApprenticeship()) {
			Date start = new Date();
			chooseApprenticeship();
			Date finish = new Date();
			TIME_SPENT_CHOOSING_APPRENTICESHIPS += (finish.getTime() - start.getTime());
		}
		
		//Move out, get a job
		if (profession == null && age > race.getAgeOfAdulthood() && Randomizer.rollAgainstOdds(race.getMoveOutRate())) {
			Date start = new Date();
			chooseProfession();
			Date finish = new Date();
			TIME_SPENT_CHOOSING_PROFESSIONS += (finish.getTime() - start.getTime());
			if (spouse == null) {
				Date start1 = new Date();
				findHouse(true);
				Date finish1 = new Date();
				TIME_SPENT_MOVING_OUT += (finish1.getTime() - start1.getTime());
			}
		}
		
		//Handle marriage
		if ((spouse == null || !spouse.isAlive()) && home != null && !isLivingInParentsHouse() && Randomizer.rollAgainstOdds(0.2)) {
			Date start = new Date();
			findSpouse();
			Date finish = new Date();
			TIME_SPENT_FINDING_SPOUSES += (finish.getTime() - start.getTime());
		}
		
		//Job knowledge
		Date start = new Date();
		incrementJobKnowledge(profession);
		if (profession != null) {
			for (Person apprentice : apprentices) {
				for (String professionTaught : profession.getProfessionsTaught()) {
					apprentice.incrementJobKnowledge(WorldConfig.getProfessionByName(professionTaught));
				}
			}
		}
		Date finish = new Date();
		TIME_SPENT_HANDLING_JOB_KNOWLEDGE += (finish.getTime() - start.getTime());
	}
	
	//Returns a sorted list of professions with most favorable first
	private Stack<Profession> considerProfessions(Map<String, Integer> cityResources, boolean considerTraining) {
		List<Pair<Profession, Double>> weightedProfessions = new ArrayList<>();
		for (Profession profession : WorldConfig.getProfessions()) {
			double preference = employmentPreferences.getOrDefault(profession, 0.0);
			for (ResourceAdjustment resourceAdjustment : profession.getResourceAdjustments()) {
				//If the profession requires an unavailable resource, reject it.
				if (resourceAdjustment.getValue() < 0 && cityResources.get(resourceAdjustment.getName()) + resourceAdjustment.getValue() < 0) {
					preference = -1;
					break;
				}
				
				//There is more profit to be made by creating a resource that is in high demand and short supply
				if (resourceAdjustment.getValue() > 0 && cityResources.get(resourceAdjustment.getName()) < 0) {
//					preference *= Math.pow(1.1, (resourceAdjustment.getValue() * (0 - cityResources.get(resourceAdjustment.getName())) *.0001));
					preference *= (1.0 + (resourceAdjustment.getValue() * (0 - cityResources.get(resourceAdjustment.getName())) *.0001));
				}
			}
			if (considerTraining && this.getJobKnowledges().getOrDefault(profession, 0) < profession.getMinTrainingRequired()) {
				//Much less likely to choose a job that you don't know how to do
				preference *= 0.2;
			}
			if (preference > 0) {
				weightedProfessions.add(new Pair<>(profession, preference));
			}
		}
		weightedProfessions.sort(Comparator.comparing(Pair::getValue));
//		if (weightedProfessions.get(weightedProfessions.size() - 1).getKey().getName().equals("Hunter")) {
//			System.out.println("another hunter...");
//		}
		return weightedProfessions.stream().map(weightedProfession -> weightedProfession.getKey()).collect(Collectors.toCollection(Stack::new)); 
	}
	
	public void chooseApprenticeship() {
		Date start1 = new Date();
		Stack<Profession> professionPreferences = considerProfessions(homeCity.getAvailableResources(), false);
		Date finish1 = new Date();
		TIME_SPENT_CONSIDERING_PROFESSIONS += (finish1.getTime() - start1.getTime());
		Person chosenMentor = null;
		Profession chosenProfession = null;
		while (chosenMentor == null && !professionPreferences.isEmpty()) {
//			System.out.println("Finding mentor for " + consideredProfession.getName());
			Date start = new Date();
			chosenProfession = professionPreferences.pop();
			chosenMentor = chooseMentor(chosenProfession);
			Date finish = new Date();
			TIME_SPENT_LOOKING_FOR_MENTORS += (finish.getTime() - start.getTime());
		}
		if (chosenMentor != null) {
			setApprenticeship(chosenProfession);
			chosenMentor.addApprentice(this);
		}
	}
	
	private Person chooseMentor(Profession profession) {
		if (profession.getNumberOfApprentices() < 1) {
			return null;
		}
		List<Person> mentors = homeCity.getApprenticeshipOpportunities(profession);
		double bestMatch = -1.0;
		Person chosenMentor = null;

		for (Person potentialMentor : mentors) {
			double match = potentialMentor.getImpressionOf(this) * this.getImpressionOf(potentialMentor);
			if (match > bestMatch) {
				bestMatch = match;
				chosenMentor = potentialMentor;
			}
		}
		return chosenMentor;
	}
	
	public void chooseProfession() {
		Stack<Profession> professionPreferences = considerProfessions(homeCity.getAvailableResources(), true);
		Map<Profession, List<Building>> employmentOpportunities = homeCity.getEmploymentOpportunities();
		Building placeOfEmployment = null;
		Profession consideredProfession = null;
		while (placeOfEmployment == null && !professionPreferences.isEmpty()) {
			consideredProfession = professionPreferences.pop();
//			System.out.println("Finding place to work for " + consideredProfession.getName());
			placeOfEmployment = choosePlaceOfEmployment(consideredProfession, employmentOpportunities.getOrDefault(consideredProfession, new ArrayList<>()));
			if (!consideredProfession.isBuildingRequired()) {
				//Whether you found a place to work or not, exit loop
				break;
			}
		}
		if (placeOfEmployment != null || !consideredProfession.isBuildingRequired()) {
			setPlaceOfEmployment(placeOfEmployment);
			setProfession(consideredProfession);
		}
	}
	
	public Building choosePlaceOfEmployment(Profession profession, List<Building> opportunities) {
		Building placeOfEmployment = null;
		double bestScore = 0;
		for (Building opportunity : opportunities) {
			double match = opportunity.considerEmploymentFit(this);
			if (match > bestScore) {
				bestScore = match;
				placeOfEmployment = opportunity;
			}
		}
		if (placeOfEmployment == null && profession.getProprietorOf() != null && !profession.getProprietorOf().getName().equals("Graveyard")) {
			//Start your own business!
			placeOfEmployment = BuildingGenerator.generateBuilding(profession.getProprietorOf(), this);
			homeCity.addBuilding(placeOfEmployment);
		}
		return placeOfEmployment;
	}
	
	public void findHouse(boolean generateEvent) {
		if (this.homeCity != null) {
			if (this.profession != null 
					&& this.placeOfEmployment != null 
					&& this.placeOfEmployment.getBuildingType().isHouseIncluded()
					&& this.profession.getProprietorOf() != null
					&& this.profession.getProprietorOf().equals(placeOfEmployment.getBuildingType())
					&& this.placeOfEmployment.getOccupants().isEmpty()) {
				this.setHome(placeOfEmployment, true);
				return;
			}
			for (Building building : this.homeCity.getBuildings()) {
				if (building.getBuildingType().isHouse() && building.getOccupants().isEmpty()) {
					this.setHome(building, true);
					building.setName(BuildingGenerator.generateBuildingName(WorldConfig.getBuildingTypeByName("House"), this));
					return;
				}
			}
			Building newHouse = BuildingGenerator.generateBuilding(WorldConfig.getBuildingTypeByName("House"), this);
			this.setHome(newHouse, generateEvent);
			this.homeCity.addBuilding(newHouse);
		}
	}
	
	public void findSpouse() {
		Person newSpouse = null;
		if (this.homeCity != null) {
			Set<Person> siblings = getSiblings();
			
			//Generate min and max ages based on how far along in adulthood they are; e.g. a middle-aged human can marry a middle-aged elf
			double adulthoodRatio = new Double(this.getAge() - this.getRace().getAgeOfAdulthood())
					/ new Double(this.getRace().getMaxAge() - this.getRace().getAgeOfAdulthood());
			double bestMatch = 0.0;
			for (Person person : this.homeCity.getLivingCitizens()) {
				//Don't even consider partners who are dead, the same gender, married, genetically incompatible, or directly related.
				if (!person.isAlive()
						|| person.getGender().equals(this.gender)
						|| person.getSpouse() != null
						|| !person.getRace().getMatingRaces().contains(this.getRace())
						|| parents.contains(person)
						|| siblings.contains(person)
						|| offspring.contains(person)) {
					continue;
				}

				int adulthoodLength = person.getRace().getMaxAge() - person.getRace().getAgeOfAdulthood();
				int minAge = (int) (adulthoodRatio * adulthoodLength * (1/PersonGenerator.MAX_AGE_RATIO)) + person.getRace().getAgeOfAdulthood();
				int maxAge = (int) (adulthoodRatio * adulthoodLength * PersonGenerator.MAX_AGE_RATIO) + person.getRace().getAgeOfAdulthood();
				maxAge = maxAge < person.getRace().getMaxAge() ? maxAge : person.getRace().getMaxAge();
				if (minAge <= person.getAge() && person.getAge() <= maxAge) {
					double match = this.getImpressionOf(person) * person.getImpressionOf(this);
					if (match > bestMatch) {
						bestMatch = match;
						newSpouse = person;
					}
				}
			}
		}
		this.marry(newSpouse);
	}
	
	public void marry(Person spouse) {
		if (spouse != null) {
			this.setSpouse(spouse);
			spouse.setSpouse(this);
			Building newHome = this.home;
			if (home.getBuildingType().isHouse() && spouse.home != null && !spouse.home.getBuildingType().isHouse()) {
				//if you live in a house and your spouse lives in a building with house included, move in with them.
				newHome = spouse.home;
			}
			this.setHome(newHome, true);
			spouse.setHome(newHome, true);
			events.add(new Event("Married " + spouse));
			spouse.events.add(new Event("Married " + this));
			if (gender.equals(Gender.Male)) {
				spouse.setFamilyName(familyName);
			} else {
				setFamilyName(spouse.getFamilyName());
				if (home.getBuildingType().isHouse()) {
					home.setName(BuildingGenerator.generateBuildingName(home.getBuildingType(), this));
				}
			}
		}
	}
	
	public double getImpressionOf(Person person) {
		if (getImmediateFamily().contains(person)) {
			return 1.5;
		}
		return racialPreferences.get(person.getRace());
	}
	
	private void handleDeath() {
		setAlive(false);
		homeCity.processCitizenDeath(this);
		//There is a bug somewhere in here that is resulting in homeless professional adults
//		if (home != null && !offspring.isEmpty() && (spouse == null || !spouse.isAlive())) {
//			//For now, orphans who were living with parents become homeless.
//			List<Person> childrenLivingAtHome = offspring.stream().filter(child -> home.equals(child.home)).collect(Collectors.toList());
//			for (Person child : childrenLivingAtHome) {
//				child.setHome(null, true);
//				homeCity.addOrphan(child);
//			}
//		}
		Building oldHome = home;
		setHome(homeCity.getGraveyard(), false);
		homeCity.unregisterProfessional(this);
		setPlaceOfEmployment(null);
		
		List<Person> currentApprentices = new ArrayList<>(apprentices);
		for (Person apprentice : currentApprentices) {
			removeApprentice(apprentice);
			apprentice.chooseApprenticeship();
		}
		
		if (oldHome != null && oldHome.getOccupants().isEmpty()) {
			for (Person employee : oldHome.getEmployees()) {
				if (employee.profession.getProprietorOf() != null 
						&& employee.profession.getProprietorOf().equals(oldHome.getBuildingType())
						&& (employee.getHome() == null || employee.getHome().getBuildingType().isHouse())) {
					employee.setHome(oldHome, true);
					if (employee.getSpouse() != null && employee.getSpouse().isAlive()) {
						employee.getSpouse().setHome(oldHome, true);
					}
					break;
				}
			}
		}
		this.deathYear = WorldConfig.getYear();
		events.add(new Event("Died"));
	}
	
	public void adjustYears(int years) {
		this.birthYear += years;
		if (this.deathYear != null) {
			this.deathYear += years;
		}
		for (Event event : events) {
			event.adjustYear(years);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getFullName());
		sb.append(", " + race.getName());
		sb.append(" " + gender.name());
		sb.append(profession == null ? "" : " " + profession.getName());
		sb.append(apprenticeship == null ? "" : " " + apprenticeship.getApprenticeName());
		sb.append(", " + getAge());
		sb.append(alive ? "" : " (" + birthYear + " - " + (deathYear == null ? "Present" : deathYear) + ")");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object object) {
		return object != null && object.hashCode() == this.hashCode();
	}
	
	@Override
	public int hashCode() {
		return this.id;
	}
}
