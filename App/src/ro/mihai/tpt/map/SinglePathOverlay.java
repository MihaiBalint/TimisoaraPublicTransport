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

import java.util.ArrayList;

import ro.mihai.tpt.model.Station;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class SinglePathOverlay extends ItemizedOverlay {

	private ArrayList<OverlayItem> mOverlays;
	private Context mContext;

	public SinglePathOverlay(Context context, int defaultMarkerId) {
		this(context, context.getResources().getDrawable(defaultMarkerId));
	}

	public SinglePathOverlay(Context context, Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mOverlays = new ArrayList<OverlayItem>();
	}
	
	public void addStationOverlay(Station s) {
		try {
		    GeoPoint point = new GeoPoint(toMicroDeg(s.getLat()), toMicroDeg(s.getLng()));
		    addOverlay(new OverlayItem(point, s.getShortName(), s.getNiceName()));	    
		} catch(NumberFormatException e) {
			// nop
		}
	}
	
	 private void addOverlay(OverlayItem overlay) {
	    mOverlays.add(overlay);
	    populate();
	}

	public int toMicroDeg(String deg) {
		return (int)(Double.parseDouble(deg)*1000000);
	}
	
	
	
	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}
	
	@Override
	public int size() {
		return mOverlays.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = mOverlays.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}	
	
}
