package building;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NamingRules {
	@XmlElement(name="noun")
	private List<String> nouns = new ArrayList<>();
	@XmlElement(name="adjective")
	private List<String> adjectives = new ArrayList<>();
	@XmlElement(name="product")
	private List<String> products = new ArrayList<>();
	@XmlElement(name="buildingType")
	private List<String> buildingTypes = new ArrayList<>();
	@XmlElement(name="namingConvention")
	private List<String> namingConventions = new ArrayList<>();

	public List<String> getNouns() {
		return nouns;
	}
	public List<String> getAdjectives() {
		return adjectives;
	}
	public List<String> getProducts() {
		return products;
	}
	public List<String> getBuildingTypes() {
		return buildingTypes;
	}
	public List<String> getNamingConventions() {
		return namingConventions;
	}
}
