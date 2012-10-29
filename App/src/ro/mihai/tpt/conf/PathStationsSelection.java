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

import ro.mihai.tpt.model.Estimate;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;

public class PathStationsSelection {
	private Path path;
	private List<StationPathsSelection> stations;
	
	//  hh:mm MainStation1 [hide/show]
	//			hh:mm Line1 (dir) [hide/show]
	//			hh:mm Line2 (dir) [hide/show]
	//			hh:mm Line3 (dir) [hide/show]
	//  hh:mm MainStation2 [hide/show]
	//			hh:mm Line2 (dir) [hide/show]
	//			hh:mm Line4 (dir) [hide/show]
	//  hh:mm MainStation3 [hide/show]
	//  hh:mm MainStation4 [hide/show]
	//			hh:mm Line1 (dir) [hide/show]
	//  hh:mm MainStation5 [hide/show]
	//  hh:mm MainStation6 [hide/show]

	public PathStationsSelection(Path path) {
		this.path = path;
		this.stations = new ArrayList<StationPathsSelection>();
	}
	
	public List<StationPathsSelection> getStations() {
		return stations;
	}
	public Path getPath() {
		return path;
	}
	public Estimate getEstimate(Station s) {
		return path.getEstimate(s);
	}
	public void clearAllUpdates() {
		path.clearAllUpdates();
		for(StationPathsSelection sel : stations)
			sel.clearAllUpdates();
	}
	
	public String getLineKindLabel() {
		switch(path.getLine().getKind()) {
		case BUS: return "B U S";
		case EXPRESS: return "E X P R E S S";
		case TRAM: return "T R A M";
		case TROLLEY: return "T R O L L E Y";
		case METRO: return "M E T R O";
		default: return "";
		}
	}

	public String getLineNameLabel() {
		String lineName = path.getLine().getName();
		if (lineName.startsWith("Tv") || lineName.startsWith("Tb")) 
			lineName = lineName.substring(2);
		return lineName;
	}
	
	public String getPathLabel(int index) {
		List<String> pathNames = path.getLine().getSortedPathNames();
		if (index<0 || index>= pathNames.size())
			return "";
		return "Dir. "+pathNames.get(index);
	}
	
	public int getCurrentPath() {
		List<String> pathNames = path.getLine().getSortedPathNames();
		return pathNames.indexOf(path.getNiceName());
	}

	public String getLabel() {
		return path.getLabel();
	}

	public void clearSelection() {
		this.stations.clear();
	}
	
	public void selectAllStations() {
		List<Station> stations = path.getStationsByPath();
		for(Station s : stations)
			this.stations.add(new StationPathsSelection(s));
	}
	
	public void clearConnections() {
		for(StationPathsSelection sel : stations) 
			sel.clearConnections();
	}
	
	public void addConnections(Path p) {
		if (p==path) return;
		
		for(StationPathsSelection sel : stations) {
			Station s = sel.getStation();
			
			for(Station o : p.getStationsByPath())
				if (o.getJunction() == s.getJunction())
					if(s.distanceTo(o) < Constants.MAX_CONNECTION_DIST)
						sel.addConnection(o, p);
		}
	}
	
}
