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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.tpt.utils.Utils;
import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;

public class SelectLinePath implements OnClickListener, DialogInterface.OnClickListener {
	private Line selectable;
	private List<Path> pathList;
	private City city;
	private Activity parent;
	private Class<?> activity;
	
	public SelectLinePath(Activity parent, Class<?> activity, City city, Line selectable) {
		this(parent, activity, city, selectable, selectable.getPaths());
	}

	public SelectLinePath(Activity parent, Class<?> activity, City city, Line selectable, Collection<Path> pathList) {
		this.selectable = selectable;
		this.city = city;
		this.parent = parent;
		this.activity = activity;
		this.pathList = new ArrayList<Path>(pathList);
	}
	
	public void onClick(View v) {
		pathList = new ArrayList<Path>(selectable.getPaths());
		assert( !pathList.isEmpty() );
		onClick(null, 0);
	}

    public void onClick(DialogInterface dialog, int item) {
    	Path selectedPath = pathList.get(item);
    	Utils.recordUsePath(parent, selectedPath);
    	new StartActivity(parent, activity)
    		.addCity(city)
    		.addLinePath(selectedPath)
    		.start();
    }

}
