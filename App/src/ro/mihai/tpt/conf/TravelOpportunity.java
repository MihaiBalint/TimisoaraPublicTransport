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
import ro.mihai.tpt.utils.LineKindAndroidEx;
import ro.mihai.util.LineKind;

public class TravelOpportunity {
	private Path path;
	private List<ChangeOpportunity> disembarkOpportunities;
	
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

	public TravelOpportunity(Path path) {
		this.path = path;
		this.disembarkOpportunities = new ArrayList<ChangeOpportunity>();
	}
	
	public List<ChangeOpportunity> getDisembarkOpportunities() {
		return disembarkOpportunities;
	}
	public Path getPath() {
		return path;
	}
	public void clearAllUpdates() {
		path.clearAllUpdates();
		for(ChangeOpportunity sel : disembarkOpportunities)
			sel.clearAllUpdates();
	}
	
	public LineKind getLineKind() {
		return path.getLine().getKind();
	}
	
	public String getLineNameLabel() {
		return LineKindAndroidEx.getLineNameLabel(path.getLine());
	}
	
	public String getPathLabel(int index) {
		List<String> pathNames = path.getLine().getSortedPathNames();
		if (index<0 || index>= pathNames.size())
			return "";
		return pathNames.get(index);
	}
	
	public String getDepartureStationName() {
		if (!disembarkOpportunities.isEmpty())
			return disembarkOpportunities.get(0).getNiceName();
		List<Estimate> stations = path.getStationsByPath(); 
		if (!stations.isEmpty())
			return stations.get(0).getStation().getNiceName();
		return "";
	}
	
	public String getDestinationStationName() {
		if (!disembarkOpportunities.isEmpty())
			return disembarkOpportunities.get(disembarkOpportunities.size()-1).getNiceName();
		List<Estimate> stations = path.getStationsByPath(); 
		if (!stations.isEmpty())
			return stations.get(stations.size()-1).getStation().getNiceName();
		return path.getNiceName();
	}
	
	public int getCurrentPath() {
		List<String> pathNames = path.getLine().getSortedPathNames();
		return pathNames.indexOf(path.getNiceName());
	}

	public String getLabel() {
		return path.getLabel();
	}

	public void clearSelection() {
		this.disembarkOpportunities.clear();
	}
	
	public void selectAllStations() {
		List<Estimate> stations = path.getStationsByPath();
		for(Estimate s : stations)
			this.disembarkOpportunities.add(new ChangeOpportunity(s));
	}
	
	public void clearConnections() {
		for(ChangeOpportunity sel : disembarkOpportunities) 
			sel.clearConnections();
	}
	
	public void addConnections(Path p) {
		if (p==path) return;
		
		for(ChangeOpportunity sel : disembarkOpportunities) {
			Station s = sel.getDisembarkEstimate().getStation();
			
			for(Estimate e : p.getStationsByPath()) {
				Station o = e.getStation(); 
				if (o.getJunction() == s.getJunction())
					if(s.distanceTo(o) < Constants.MAX_CONNECTION_DIST)
						sel.addConnection(e);
			}
		}
	}
	
}
