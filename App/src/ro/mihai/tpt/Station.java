/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

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
