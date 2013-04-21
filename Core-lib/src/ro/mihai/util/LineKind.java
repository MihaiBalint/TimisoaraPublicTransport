package ro.mihai.util;

import ro.mihai.tpt.model.Line;

public enum LineKind {
	TRAM("Tv1","Tv2","Tv4","Tv5","Tv6","Tv7","Tv8","Tv9"), 
	TROLLEY("Tb11","Tb14","Tb15","Tb16","Tb17","Tb18","Tb19"), 
	BUS("3","13","13b","21","22","28","32","33","33b","40","46"),
	EXPRESS("E1","E2","E3","E4","E4b","E6","E7","E8", "E33"),
	METRO("M30","M35","M36","M44");
	
	private final String[] names;
	
	private LineKind(String... names) {
		this.names = names;
	}
	
	public boolean contains(String name) {
		for(String s:names)
			if (s.equals(name))
				return true;
		return false;
	}
	
	public boolean isTram() { return this==TRAM; }
	public boolean isTrolley() { return this==TROLLEY; }
	public boolean isBus() { return this==BUS; }
	public boolean isBusExpress() { return this==EXPRESS; }
	public boolean isBusMetro() { return this==METRO; }
	public boolean isBusAny() { return this==METRO || this==EXPRESS || this==BUS; }
	
	public String[] getLineNames() {
		return names;
	}

	public static LineKind getKind(Line line) {
		if (TRAM.contains(line.getName())) return TRAM;
		if (TROLLEY.contains(line.getName())) return TROLLEY;
		if (BUS.contains(line.getName())) return BUS;
		if (EXPRESS.contains(line.getName())) return EXPRESS;
		if (METRO.contains(line.getName())) return METRO;
		
		if(line.getName().toLowerCase().startsWith("tv")) return TRAM;
		if(line.getName().toLowerCase().startsWith("tb")) return TROLLEY;
		if(line.getName().startsWith("E")) return EXPRESS;
		if(line.getName().startsWith("M")) return METRO;
		
		return BUS;
	} 
}
