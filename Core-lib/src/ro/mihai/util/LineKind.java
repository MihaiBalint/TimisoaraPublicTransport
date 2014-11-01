/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2014  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.util;

import java.util.Iterator;

import ro.mihai.tpt.model.Line;

public enum LineKind {
	TRAM("Tv1","Tv2","Tv4","Tv5","Tv6","Tv7","Tv8","Tv9"), 
	TROLLEY("Tb11","Tb14","Tb15","Tb16","Tb17","Tb18"), 
	BUS("3","13","21","22","28","29","32","33","33b","40","46"),
	EXPRESS("E1","E2","E3","E4","E4b","E6","E7", "E33"),
	METRO("M30","M35","M43","M44", "M45");
	
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
	
	public Iterator<String> getLineNameIterator() {
		return new ArrayIterator<String>(names);
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
