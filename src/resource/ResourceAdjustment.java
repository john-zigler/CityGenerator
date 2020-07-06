package resource;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ResourceAdjustment {
	@XmlAttribute
	private String name;
	@XmlAttribute
	private int value;
	
	public ResourceAdjustment() {
		//Default constructor
	}
	
	public ResourceAdjustment(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}
	public int getValue() {
		return value;
	}
}
