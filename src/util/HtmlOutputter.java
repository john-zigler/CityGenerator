package util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import building.Building;
import building.BuildingType;
import city.City;
import event.Event;
import exceptions.InitializationException;
import person.Person;
import person.profession.Profession;
import person.race.Race;

public class HtmlOutputter {
	private String folderPath;
	private static final String HTML_TOP = "<!DOCTYPE html><html><head>"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
			+ "<style>.prefTD{height:100px; width:50px;}</style>"
			+ "</head><body>";
	private static final String HTML_BOTTOM = "</body></html>";
	
	public HtmlOutputter(String folderPath) throws InitializationException, IOException {
		this.folderPath = folderPath;
		if (!this.folderPath.endsWith("/")) {
			this.folderPath += "/";
		}
		Files.createDirectories(Paths.get(this.folderPath));
		Files.createDirectories(Paths.get(this.folderPath + "buildings/"));
		Files.createDirectories(Paths.get(this.folderPath + "citizens/"));
	}

	private String getCityPath(City city) {
		return stripUnwantedChars(city.getName() + ".html");
	}
	public void outputCity(City city) {
		System.out.println("Outputting files...");
		StringBuilder fileContents = new StringBuilder(HTML_TOP);
		fileContents.append("<h1>The City of " + city.getName() + "!</h1>");
		fileContents.append("<h4>Year: " + WorldConfig.getYear() + "</h4>");
		fileContents.append("<h4>Population: <a href='citizens/all.html'>" + city.getLivingCitizens().size() + "</a></h4>");
		Map<BuildingType, List<Building>> buildingMap = new HashMap<>();
		for (Building building : city.getBuildings()) {
			outputBuilding(building, city);
			buildingMap.computeIfAbsent(building.getBuildingType(), b -> new ArrayList<>()).add(building);
		}
		fileContents.append("<h2>Buildings by type:</h2><ul>");
		for (Entry<BuildingType, List<Building>> entry : buildingMap.entrySet()) {
			if (entry.getKey().isHouse()) {
				//Skip houses for now
				continue;
			}
			fileContents.append("<h3>" + entry.getKey().getName() + ":</h3><ul>");
			for (Building building : entry.getValue()) {
				fileContents.append("<li>" + getBuildingLink(building, false) + "</li>");
			}
			fileContents.append("</ul>");
		}
		fileContents.append("<h2>Wilderness/Street Professionals:</h2><ul>");
		for (Profession profession : WorldConfig.getProfessions()) {
			if (!profession.isBuildingRequired()) {
				for (Person person : city.getRegisteredProfessionals(profession)) {
					if (person.getPlaceOfEmployment() == null) {
						fileContents.append("<li>" + getPersonLink(person, false) + "</li>");
					}
				}
			}
		}
		fileContents.append("</ul><h2>Unemployed:</h2><ul>");
		for (Person person : city.getLivingCitizens()) {
			if (person.getProfession() == null
					&& person.getApprenticeship() == null
					&& person.getAge() > person.getRace().getAgeOfAdulthood()
					&& !person.isLivingInParentsHouse()) {
				fileContents.append("<li>" + getPersonLink(person, false) + "</li>");
			}
		}
		fileContents.append("</ul><h2>Homeless:</h2><ul>");
		for (Person person : city.getLivingCitizens()) {
			if (person.getHome() == null) {
				fileContents.append("<li>" + getPersonLink(person, false) + "</li>");
			}
		}
		outputLivingCitizens(city);
		for (Person person : city.getDeadCitizens()) {
			outputPerson(person, city);
		}
		fileContents.append("</ul>");
		fileContents.append(HTML_BOTTOM);
		outputHtmlFile(fileContents.toString(), getCityPath(city));
		System.out.println("Done!");
	}

	
	private String getBuildingLink(Building building, boolean upOneLevel) {
		return building == null ? "None" : "<a href='" + (upOneLevel ? "../" : "") + getBuildingPath(building) + "'>" + building.getName() + "</a>";
	}
	private String getBuildingPath(Building building) {
		return stripUnwantedChars("buildings/" + building.getName() + "_" + building.hashCode() + ".html");
	}
	private void outputBuilding(Building building, City city) {
		StringBuilder fileContents = new StringBuilder(HTML_TOP);
		fileContents.append("<h1>" + building.getName() + "</h1>");
		fileContents.append("<h2>" + building.getBuildingType().getName() + "</h2>");
		fileContents.append("<h3>Founder: " + getPersonLink(building.getFounder(), true) + "</h3>");
		if (!building.getEmployees().isEmpty()) {
			fileContents.append("<h3>Employees:</h3><ul>");
			for (Person person : building.getEmployees()) {
				fileContents.append("<li>" + getPersonLink(person, true) + "</li>");
				if (!person.getApprentices().isEmpty()) {
					fileContents.append("<ul>");
					for (Person apprentice : person.getApprentices()) {
						fileContents.append("<li>" + getPersonLink(apprentice, true) + "</li>");
					}
					fileContents.append("</ul>");
				}
			}
			fileContents.append("</ul>");
		}
		if (!building.getOccupants().isEmpty()) {
			fileContents.append(getPersonList("Occupants", building.getOccupants(), true));
		}
		fileContents.append("<br/><a href='../" + getCityPath(city) + "'>Return to " + city.getName() + "</a>");
		fileContents.append(HTML_BOTTOM);
		outputHtmlFile(fileContents.toString(), getBuildingPath(building));
	}

	private void outputLivingCitizens(City city) {
		for (Person person : city.getLivingCitizens()) {
			outputPerson(person, city);
		}
		StringBuilder fileContents = new StringBuilder(HTML_TOP);
		fileContents.append("<h1>Citizens of " + city.getName() + "</h1>");
		fileContents.append(getPersonList("Citizens", city.getLivingCitizens(), true));
		fileContents.append("<br/><a href='../" + getCityPath(city) + "'>Return to " + city.getName() + "</a>");
		fileContents.append(HTML_BOTTOM);
		outputHtmlFile(fileContents.toString(), "citizens/all.html");
	}
	
	private String getPersonList(String listName, Collection<Person> personList, boolean upOneLevel) {
		StringBuilder listString = new StringBuilder("<h3>" + listName + ":</h3><ul>");
		for (Person person : personList) {
			listString.append("<li>" + getPersonLink(person, upOneLevel) + "</li>");
		}
		listString.append("</ul>");
		return listString.toString();
	}
	private String getPersonLink(Person person, boolean upOneLevel) {
		return person == null ? "None" : "<a href='" + (upOneLevel ? "../" : "") + getPersonPath(person) + "'>" + person.toString() + "</a>";
	}
	private String getPersonPath(Person person) {
		return stripUnwantedChars("citizens/" + person.getFullName() + "_" + person.hashCode() + ".html");
	}
	private void outputPerson(Person person, City city) {
		StringBuilder fileContents = new StringBuilder(HTML_TOP);
		fileContents.append("<h1>" + person.getFullName() + "</h1>");
		fileContents.append("<h2>" + person.getGender().name() + " " + person.getRace().getName() + "</h2>");
		
		fileContents.append("<h3>Profession: ");
		if (person.getProfession() == null) {
			fileContents.append("None");
		} else {
			fileContents.append(person.getProfession().getName());
			if (person.getPlaceOfEmployment() != null) {
				fileContents.append(" at " + getBuildingLink(person.getPlaceOfEmployment(), true));
			}
		}
		fileContents.append("</h3>");
		
		fileContents.append("<h3>Apprenticeship: ");
		if (person.getApprenticeship() == null) {
			fileContents.append("None");
		} else {
			fileContents.append(person.getApprenticeship().getApprenticeName());
		}
		fileContents.append("</h3>");
		if (person.getMentor() != null) {
			fileContents.append("<h3>Mentor: " + getPersonLink(person.getMentor(), true) + "</h3>");
		}
		
		fileContents.append("<h3>Age: " + person.getAge() + "  (" + person.getBirthYear() + " - " + (person.getDeathYear() == null ? "Present" : person.getDeathYear()) + ")</h3>");
		fileContents.append("<h3>Spouse: " + getPersonLink(person.getSpouse(), true) + "</h3>");
		fileContents.append("<h3>Home: " + getBuildingLink(person.getHome(), true) + "</h3>");
		if (!person.getPlacesFounded().isEmpty()) {
			fileContents.append("<h3>Founder of: ");
			for (int i = 0; i < person.getPlacesFounded().size(); i++) {
				if (i > 0) {
					fileContents.append(", ");
				}
				fileContents.append(getBuildingLink(person.getPlacesFounded().get(i), true));
			}
			fileContents.append("</h3>");
		}
		if (!person.getParents().isEmpty()) {
			fileContents.append(getPersonList("Parents", person.getParents(), true));
		}
		if (!person.getOffspring().isEmpty()) {
			fileContents.append(getPersonList("Offspring", person.getOffspring(), true));
		}
		if (!person.getOldMentors().isEmpty()) {
			fileContents.append(getPersonList("Old Mentors", person.getOldMentors(), true));
		}
		if (!person.getApprentices().isEmpty()) {
			fileContents.append(getPersonList("Apprentices", person.getApprentices(), true));
		}
		if (!person.getOldApprentices().isEmpty()) {
			fileContents.append(getPersonList("Old Apprentices", person.getOldApprentices(), true));
		}
		fileContents.append(getPreferences(person));
		fileContents.append("<h3>Life Events:</h3><ul>");
		for (Event event : person.getEvents()) {
			fileContents.append("<li>" + event.toString() + "</li>");
		}
		fileContents.append("</ul><br/><a href='../" + getCityPath(city) + "'>Return to " + city.getName() + "</a>");
		fileContents.append(HTML_BOTTOM);
		outputHtmlFile(fileContents.toString(), getPersonPath(person));
	}
	
	private String getPreferences(Person person) {
		StringBuilder jobLabels = new StringBuilder("<h3>Job preferences:</h3><table><tr>");
		StringBuilder jobValues = new StringBuilder("</tr><tr>");
		for (Profession profession : WorldConfig.getProfessions()) {
			jobLabels.append(getPreferenceLabel(profession.getName()));
			jobValues.append(getPreferenceDisplay(person.getEmploymentPreference(profession)));
		}
		StringBuilder raceLabels = new StringBuilder("<h3>Racial preferences:</h3><table><tr>");
		StringBuilder raceValues = new StringBuilder("</tr><tr>");
		for (Race race : WorldConfig.getRaces()) {
			raceLabels.append(getPreferenceLabel(race.getName()));
			raceValues.append(getPreferenceDisplay(person.getRacialPreference(race)));
		}
		String tableEnd = "</tr></table>";
		return jobLabels.toString() + jobValues.toString() + tableEnd + raceLabels.toString() + raceValues.toString() + tableEnd;
	}
	private String getPreferenceLabel(String preferenceName) {
		return "<th>" + preferenceName + "</th>";
	}
	private String getPreferenceDisplay(double preferenceValue) {
		return "<td class='prefTD'>"
				+ "<div style='height:100%; background-color:" + getColorForPreference(preferenceValue) + "'>"
				+ "<div style='height:" + (int)(100-(100*preferenceValue)) + "%; background-color:white'>"
				+ "</div></div>" + (int)(100*preferenceValue) + "</td>";
	}
	private String getColorForPreference(double preferenceValue) {
		String redValue = Integer.toHexString((int) (255 * (1.0 - preferenceValue)));
		String greenValue = Integer.toHexString((int) (255 * (preferenceValue)));
		return "#" + (redValue.length() == 2 ? redValue : "0" + redValue) + (greenValue.length() == 2 ? greenValue : "0" + greenValue) + "00";
	}
	
	private String stripUnwantedChars(String input) {
		return input
				.replaceAll(" ", "")
				.replaceAll("'", "")
				.replaceAll("\"", "");
	}
	
	private void outputHtmlFile(String fileContents, String filePath) {
		try {
			Files.write(Paths.get(folderPath + filePath), fileContents.getBytes());
		} catch (IOException e) {
			System.err.println("Error occurred while attempting to write to " + folderPath + filePath);
			e.printStackTrace();
		}
	}
}
