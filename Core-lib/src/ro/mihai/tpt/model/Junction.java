package ro.mihai.tpt.model;

import java.util.HashSet;
import java.util.Set;

public class Junction {
	private String name;
	private Set<Station> stations;
	
	public Junction(String name) {
		this.name = name;
		this.stations = new HashSet<Station>();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
