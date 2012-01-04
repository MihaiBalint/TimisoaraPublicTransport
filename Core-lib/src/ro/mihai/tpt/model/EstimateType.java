package ro.mihai.tpt.model;

public enum EstimateType {
	None, Scheduled, GPS;
	
	public boolean isGPS() { return this==GPS; }
	public boolean isNone() { return this==None; }
	public boolean isNotNone() { return this!=None; }
}