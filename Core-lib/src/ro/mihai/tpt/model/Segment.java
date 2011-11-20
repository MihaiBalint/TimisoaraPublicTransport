package ro.mihai.tpt.model;

import java.io.Serializable;

public class Segment implements Serializable {
	private static final long serialVersionUID = 1L;
	private Station from, to;
	private int[] duration; // in seconds
	
	public Segment(Station from, Station to) {
		duration = new int[24];
		this.from = from;
		this.to = to;
	}
	
	public Station getFrom() {
		return from;
	}
	
	public Station getTo() {
		return to;
	}
	
	public int getDuration(int timeOfDay) {
		assert(timeOfDay>=0 && timeOfDay<duration.length);
		return duration[timeOfDay];
	}
	
	
}
