package building;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BuildingType {
//	Inn (Arrays.asList("InnKeeper", "Maid", "Maid")),
//	Tavern (Arrays.asList("Tavernkeep", "Barmaid", "Barmaid", "Bard", "Bard")),
//	LumberMill (Arrays.asList(Profession.Lumberjack, Profession.Lumberjack, Profession.Lumberjack)),
//	Cooper (Arrays.asList(Profession.Cooper, Profession.Apprentice)),
//	Farm (Arrays.asList("Farmer", "Farmer", "Farmer", "Farmer")),
//	Ranch (Arrays.asList()),
//	Orchard (Arrays.asList()),
//	GeneralStore (Arrays.asList()),
//	Blacksmith (Arrays.asList()),
//	Silversmith (Arrays.asList()),
//	Goldsmith (Arrays.asList()),
//	Jeweler (Arrays.asList()),
//	Castle (Arrays.asList()),
//	Barracks (Arrays.asList()),
//	Church (Arrays.asList()),
//	Stonemason (Arrays.asList()),
//	Carpenter (Arrays.asList()),
//	Dentist (Arrays.asList()),
//	Hospital (Arrays.asList()),
//	Restaraunt (Arrays.asList()),
//	Fishmonger (Arrays.asList()),
//	Butcher (Arrays.asList()),
//	Cobbler (Arrays.asList()),
//	Weaver (Arrays.asList()),
//	Tailor (Arrays.asList()),
//	Shipyard (Arrays.asList()),
//	Bank (Arrays.asList()),
//	Glassblower (Arrays.asList()),
//	Potter (Arrays.asList()),
//	Locksmith (Arrays.asList()),
//	Tanner (Arrays.asList()),
//	Dyer (Arrays.asList()),
//	Quilter (Arrays.asList()),
//	Bowyer (Arrays.asList()),
//	Gunsmith (Arrays.asList()),
//	Foundry (Arrays.asList()),
//	Alchemist (Arrays.asList()),
//	Library (Arrays.asList()),
//	Tinker (Arrays.asList()),
//	Printer (Arrays.asList()),
//	Bookbinder (Arrays.asList()),
//	Brewery (Arrays.asList()),
//	Bellfoundry (Arrays.asList()),
//	Gemcutter (Arrays.asList()),
//	Cemetary (Arrays.asList()),
//	PostOffice (Arrays.asList()),
//	Barber (Arrays.asList()),
//	Cheesemaker (Arrays.asList()),
//	Basketweaver (Arrays.asList()),
//	InstrumentStore (Arrays.asList()),
//	Courthouse (Arrays.asList()),
//	MagesCollege (Arrays.asList()),
//	BardsCollege (Arrays.asList()),
//	MilitaryAcademy (Arrays.asList()),
//	Guildhouse (Arrays.asList()),
//	TownHall (Arrays.asList()),
//	Apothecary (Arrays.asList()),
//	MagicItemShop (Arrays.asList()),
//	Stable (Arrays.asList()),
//	Mine (Arrays.asList()),
//	Quarry (Arrays.asList()),
//	Wheelwright (Arrays.asList()),
//	Wagonwright (Arrays.asList()),
//	Leatherworker (Arrays.asList()),
//	Mill (Arrays.asList()),
//	Sailmaker (Arrays.asList()),
//	Ropemaker (Arrays.asList()),
//	ArtStudio (Arrays.asList()),
//	Theater (Arrays.asList()),
//	Bakery (Arrays.asList()),
//	CandleShop (Arrays.asList()),
//	Beekeeper (Arrays.asList()),
//	House (Arrays.asList()),
//	School (Arrays.asList()),
//	Orphanage (Arrays.asList()),
//	Wilderness (Arrays.asList());

	@XmlElement
	private String name;
	@XmlElement(name="position")
	private List<String> positions = new ArrayList<>();
	@XmlElement
	private NamingRules namingRules;
	@XmlElement
	private boolean houseIncluded = false;

	public String getName() {
		return name;
	}
	public List<String> getPositions() {
		return positions;
	}
	public NamingRules getNamingRules() {
		return namingRules;
	}
	public boolean isHouseIncluded() {
		return houseIncluded;
	}
	public boolean isHouse() {
		return name.equals("House");
	}
}
