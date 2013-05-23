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

import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.utils.LineKindUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PathView {

	public static View newPathView(LayoutInflater inflater, ViewGroup parent, Path linePath, View.OnClickListener clickListener) {
		View pathView = inflater.inflate(R.layout.frag_path2, parent, false);
		PathStationsSelection path = new PathStationsSelection(linePath);
		path.selectAllStations();
		
		TextView lineKind = (TextView)pathView.findViewById(R.id.LineKind);
		lineKind.setText(LineKindUtils.getShortLabelId(path.getLineKind()));
		
		TextView lineName = (TextView)pathView.findViewById(R.id.LineName);
		lineName.setText(path.getLineNameLabel());
		
		TextView lineDirection1 = (TextView)pathView.findViewById(R.id.LineDirection1);
		lineDirection1.setText(path.getPathLabel(0));
		
		TextView lineDirection2 = (TextView)pathView.findViewById(R.id.LineDirection2);
		lineDirection2.setText(path.getPathLabel(1));
		
		pathView.setOnClickListener(clickListener);
		if (parent.getChildCount() % 2 != 0) 
			pathView.setBackgroundColor(0xFFEEEEEE);
		return pathView;
	}
}
