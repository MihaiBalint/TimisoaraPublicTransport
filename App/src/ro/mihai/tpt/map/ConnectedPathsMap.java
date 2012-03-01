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
package ro.mihai.tpt.map;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.mihai.tpt_light.R;
import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.conf.StationPathsSelection;
import ro.mihai.tpt.conf.StationPathsSelection.Node;
import ro.mihai.tpt.model.*;

import android.content.Context;

import com.google.android.maps.Overlay;

public class ConnectedPathsMap {
	private Map<Line, SinglePathOverlay> paths;
	private Context context;
	
	public ConnectedPathsMap(Context context, PathStationsSelection path) {
		// TODO Auto-generated constructor stub
		this.context = context;
		this.paths = new HashMap<Line, SinglePathOverlay>();
		SinglePathOverlay main = new SinglePathOverlay(context, R.drawable.tv4);
		paths.put(path.getPath().getLine(), main);
		
		for(StationPathsSelection sel: path.getStations()) {
			Station s = sel.getStation();
			main.addStationOverlay(s);
	    	for(Node connection : sel.getConnections()) {
	    		Path connPath = connection.path;
	    		SinglePathOverlay con = paths.get(connPath.getLine());
	    		if(null==con) {
	    			con = new SinglePathOverlay(context, R.drawable.tb14);
	    			paths.put(connPath.getLine(), con);
	    		}
	    		con.addStationOverlay(connection.station);
	    	}
		}
	}
	
	public Collection<SinglePathOverlay> getPaths() {
		return paths.values();
	}
	
}
