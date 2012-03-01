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

import java.util.List;

import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.map.ConnectedPathsMap;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.utils.AndroidDetachableStream;
import ro.mihai.tpt.utils.AndroidSharedObjects;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.tpt_light.R;

import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import android.os.Bundle;

public class MapTimes extends com.google.android.maps.MapActivity {
	private City city = null;
	private PathStationsSelection path;
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
		try {
			if(null==city) {
				city = AndroidSharedObjects.instance().getCity();
				AndroidDetachableStream dataStream = ((AndroidDetachableStream)city.getDetachableInputStream());
				dataStream.setContext(this);
		    	path = AndroidSharedObjects.instance().getPathSelection();
			}
			onCreateCityActivity(savedInstanceState);
		} catch(CityNotLoadedException e) {
	    	new StartActivity(this, LoadCity.class)
				.start();
	    	finish();
		}
	}
	
	private void onCreateCityActivity(Bundle savedInstanceState) {
	    setContentView(R.layout.map_times);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);	  
	    
	    ConnectedPathsMap pathOverlay = new ConnectedPathsMap(this, path);

	    List<Overlay> mapOverlays = mapView.getOverlays();
	    mapOverlays.addAll(pathOverlay.getPaths());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
