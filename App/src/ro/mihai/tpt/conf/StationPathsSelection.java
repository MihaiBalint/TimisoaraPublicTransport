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
package ro.mihai.tpt.conf;

import java.util.Set;
import java.util.TreeSet;

import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;

public class StationPathsSelection {
	private Station station;
	private Set<Path> connections;
	
	public StationPathsSelection(Station station) {
		this.station = station;
		this.connections = new TreeSet<Path>();
	}

	public Station getStation() {
		return station;
	}
	public Set<Path> getConnections() {
		return connections;
	}
}
