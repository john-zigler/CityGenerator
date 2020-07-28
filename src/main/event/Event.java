package main.event;

import main.util.WorldConfig;

public class Event {
	private int year;
	private String description;

	public Event(int year, String description) {
		this.year = year;
		this.description = description;
	}
	public Event(String description) {
		this.year = WorldConfig.getYear();
		this.description = description;
	}
	public void adjustYear(int offset) {
		this.year += offset;
	}
	public String toString() {
		return year + ": " + description;
	}
}
