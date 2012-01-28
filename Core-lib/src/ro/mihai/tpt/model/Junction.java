package ro.mihai.tpt.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Junction implements Serializable {
	private static final long serialVersionUID = 1L;
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
	
	public void addStation(Station s) {
		stations.add(s);
	}
	
	public Set<Station> getStations() {
		return stations;
	}
	
}
