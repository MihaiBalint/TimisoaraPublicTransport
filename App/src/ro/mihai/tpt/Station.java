package ro.mihai.tpt;

import java.util.HashSet;
import java.util.Set;

public class Station {
	private String name, id;
	private Set<Line> lines;
	
	public Station(String id, String name) {
		this.id = id;
		this.name = name;
		this.lines = new HashSet<Line>();
	}
	
	public String getName() {
		return name;
	}
	public String getId() {
		return id;
	}
	
	public void addLine(Line l) {
		lines.add(l);
	}
	
	public Set<Line> getLines() {
		return lines;
	}
	
}
