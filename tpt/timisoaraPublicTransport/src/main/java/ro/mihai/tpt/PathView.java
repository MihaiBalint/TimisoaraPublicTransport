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

import ro.mihai.tpt.conf.TravelOpportunity;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.utils.LineKindAndroidEx;
import ro.mihai.tpt.utils.MapKind;
import ro.mihai.util.LineKind;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class PathView {

	public static View newPathView(LayoutInflater inflater, ViewGroup parent,
			Path linePath, View.OnClickListener clickListener, boolean isOddItem) {
		View pathView = createPathView(inflater, parent);
		return fillPathView(pathView, parent.getResources(), linePath, clickListener, isOddItem);
	}
	
	public static View createPathView(LayoutInflater inflater, ViewGroup parent) {
		return inflater.inflate(R.layout.frag_times_path2, parent, false);
	}
	
	public static View fillPathView(View pathView, Resources res, 
			Path linePath, View.OnClickListener clickListener, boolean isOddItem) {
		TravelOpportunity path = new TravelOpportunity(linePath);
		path.selectAllStations();
		return fillPathView(pathView, res, path, clickListener, isOddItem);
	}
	
	public static View fillPathView(View pathView, Resources res, 
			TravelOpportunity path, View.OnClickListener clickListener, boolean isOddItem) {
		
		Line pathLine = path.getPath().getLine();
		LineKind lineKind = pathLine.getKind();
		
		TextView lineKindLabel = (TextView)pathView.findViewById(R.id.LineKind);
		lineKindLabel.setTextColor(res.getColor(LineKindAndroidEx.getColorId(lineKind)));
		lineKindLabel.setText(LineKindAndroidEx.getShortLabelId(lineKind));
		
		TextView lineName = (TextView)pathView.findViewById(R.id.LineName);
		lineName.setTextColor(res.getColor(LineKindAndroidEx.getColorId(lineKind)));
		lineName.setText(LineKindAndroidEx.getLineNameLabel(pathLine));

		pathView.findViewById(R.id.LineBarredKind).setVisibility(
				pathLine.isBarred() ? View.VISIBLE : View.GONE);
		
		TextView lineDirection1 = (TextView)pathView.findViewById(R.id.StationLabel);
		lineDirection1.setText(path.getDepartureStationName());
		
		TextView estimateTime = (TextView)pathView.findViewById(R.id.StationTime);
		estimateTime.setTextColor(res.getColor(LineKindAndroidEx.getColorId(lineKind)));
		
		TextView lineDirection2 = (TextView)pathView.findViewById(R.id.DestinationStationLabel);
		lineDirection2.setText(path.getDestinationStationName());
		
		pathView.setOnClickListener(clickListener);
		//int evenOddColor = isOddItem ? R.color.frag_path_odd : R.color.frag_path_even;
		//pathView.setBackgroundColor(res.getColor(evenOddColor));
		int evenOddColor = isOddItem ? R.drawable.row_back_odd : R.drawable.row_back_even;
		pathView.setBackgroundResource(evenOddColor);
		return pathView;
	}
	
	public static void addDepartureClickListener(View pathView, View.OnClickListener clickListener) {
		pathView.findViewById(R.id.DepartureStation).setOnClickListener(clickListener); 
	}

	public static View createMapView(LayoutInflater inflater, ViewGroup parent) {
		return inflater.inflate(R.layout.frag_map, parent, false);
	}

    public static View fillMapView(View mapView, Resources res, MapKind mapKind, View.OnClickListener onClick) {
        ImageView img = (ImageView) mapView.findViewById(R.id.collapsedMapButtom);
		img.setOnClickListener(onClick);
        img.setImageResource(mapKind.collapsedId);
        img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return mapView;
    }
}
