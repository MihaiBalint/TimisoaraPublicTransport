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

import ro.mihai.tpt.map.PathOverlay;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.utils.AndroidDetachableStream;
import ro.mihai.tpt.utils.AndroidSharedObjects;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.StartActivity;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class MapTimes extends com.google.android.maps.MapActivity {
	private City city = null;
	
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		try {
			if(null==city) {
				city = AndroidSharedObjects.instance().getCity();
				// this happens when re-entering the app in certain cases.
				if(null==city) 
					throw new CityNotLoadedException();
				AndroidDetachableStream dataStream = ((AndroidDetachableStream)city.getDetachableInputStream());
				if (null==dataStream)
					throw new CityNotLoadedException();
				dataStream.setContext(this);
			}
			onCreateCityActivity(savedInstanceState);
		} catch(CityNotLoadedException e) {
	    	new StartActivity(this, LoadCity.class)
				.start();
	    	finish();
		}
	}
	
	private void onCreateCityActivity(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map_times);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);	  
	    
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    Drawable drawable = this.getResources().getDrawable(R.drawable.tb15);
	    PathOverlay itemizedoverlay = new PathOverlay(drawable, this);	
	    
	    GeoPoint point = new GeoPoint(19240000,-99120000);
	    OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
	    
	    GeoPoint point2 = new GeoPoint(35410000, 139460000);
	    OverlayItem overlayitem2 = new OverlayItem(point2, "Sekai, konichiwa!", "I'm in Japan!");	    
	    
	    itemizedoverlay.addOverlay(overlayitem);
	    itemizedoverlay.addOverlay(overlayitem2);
	    mapOverlays.add(itemizedoverlay);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public int toMicroDeg(int degrees) {
		return (degrees * 1000000);
	}
}
