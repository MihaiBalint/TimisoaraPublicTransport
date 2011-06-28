package ro.mihai.tpt.model;

public class Segment {
	private Station from, to;
	private int[] duration;
	
	public Segment() {
		duration = new int[24];
	}
}
