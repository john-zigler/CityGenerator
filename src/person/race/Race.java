package person.race;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import util.WorldConfig;

@XmlRootElement
public class Race {
	@XmlElement
	private String name;
	@XmlElement
	private int likelihood;
	@XmlElement
	private int ageOfApprenticeship;
	@XmlElement
	private int ageOfAdulthood;
	@XmlElement
	private int maxMatingAge;
	@XmlElement
	private int maxAge;
	@XmlElement
	private double moveOutRate;
	@XmlElement
	private double breedingRate;
	@XmlElement(name="maleName")
	private List<String> maleNames = new ArrayList<>();
	@XmlElement(name="femaleName")
	private List<String> femaleNames = new ArrayList<>();
	@XmlElement(name="unisexName")
	private List<String> unisexNames = new ArrayList<>();
	@XmlElement(name="familyName")
	private List<String> familyNames = new ArrayList<>();
	@XmlElement
	private String namingConvention = "%firstName% %familyName%";
	@XmlElement(name="matingRace")
	private List<MatingRace> matingRaces = new ArrayList<>();
	//Initialized when first called
	private Map<Race, Race> matingRaceMap = null;
	
	public String getName() {
		return name;
	}
	public int getLikelihood() {
		return likelihood;
	}
	public int getAgeOfApprenticeship() {
		return ageOfApprenticeship;
	}
	public int getAgeOfAdulthood() {
		return ageOfAdulthood;
	}
	public int getMaxMatingAge() {
		return maxMatingAge;
	}
	public int getMaxAge() {
		return maxAge;
	}
	public double getMoveOutRate() {
		return moveOutRate;
	}
	public double getBreedingRate() {
		return breedingRate;
	}
	public List<String> getMaleNames() {
		return maleNames;
	}
	public List<String> getFemaleNames() {
		return femaleNames;
	}
	public List<String> getUnisexNames() {
		return unisexNames;
	}
	public List<String> getFamilyNames() {
		return familyNames;
	}
	public String getNamingConvention() {
		return namingConvention;
	}
	public List<Race> getMatingRaces() {
		if (matingRaceMap == null) {
			initializeMatingRaceMap();
		}
		return new ArrayList<>(matingRaceMap.keySet());
	}
	public Race getOffspringRace(Race mate) {
		if (matingRaceMap == null) {
			initializeMatingRaceMap();
		}
		return matingRaceMap.get(mate);
	}
	private void initializeMatingRaceMap() {
		matingRaceMap = new HashMap<>();
		//Just in case the file doesn't include it, allow each race to mate with themselves
		matingRaceMap.put(this, this);
		for (MatingRace matingRace : matingRaces) {
			matingRaceMap.put(WorldConfig.getRaceByName(matingRace.raceName), WorldConfig.getRaceByName(matingRace.offspringRaceName));
		}
	}

	@XmlRootElement
	private static class MatingRace {
		@XmlAttribute
		private String raceName;
		@XmlAttribute
		private String offspringRaceName;
	}
}
