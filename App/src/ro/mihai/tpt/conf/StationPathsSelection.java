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

import java.util.ArrayList;
import java.util.List;

import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;

public class StationPathsSelection {
	private Station station;
	private List<Node> connections;
	
	public StationPathsSelection(Station station) {
		this.station = station;
		this.connections = new ArrayList<Node>();
	}

	public Station getStation() {
		return station;
	}
	
	public void clearAllUpdates() {
		for(Node connection : this.getConnections())
			connection.path.getEstimate(connection.station).clearUpdate();
	}
		
	public List<Node> getConnections() {
		return connections;
	}
	
	public void addConnection(Station s, Path p) {
		Node n = new Node(s,p);
		if (!this.connections.contains(n))
			this.connections.add(n);
	}
	
	public void clearConnections() {
		connections.clear();
	}
	
	public static class Node {
		public Station station;
		public Path path;
		
		public Node(Station station, Path path) {
			this.station = station;
			this.path = path;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + path.hashCode();
			result = prime * result + station.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			return path == other.path && station == other.station;
		}
		
	}
	
}
