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

public class ChangeOpportunity {
	private Estimate disembark;
	private List<Estimate> connections;
	
	public ChangeOpportunity(Estimate disembark) {
		this.disembark = disembark;
		this.connections = new ArrayList<Estimate>();
	}

	public Estimate getDisembarkEstimate() {
		return disembark;
	}

	public String getNiceName() {
		return disembark.getStation().getNiceName();
	}
	
	public void clearAllUpdates() {
		for(Estimate connection : this.getConnections())
			connection.clearUpdate();
	}
		
	public List<Estimate> getConnections() {
		return connections;
	}
	
	public void addConnection(Estimate connectionEstimate) {
		if (!this.connections.contains(connectionEstimate))
			this.connections.add(connectionEstimate);
	}
	
	public void clearConnections() {
		connections.clear();
	}
}
