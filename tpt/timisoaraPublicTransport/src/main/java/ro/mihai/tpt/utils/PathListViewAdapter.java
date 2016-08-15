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
package ro.mihai.tpt.utils;

import java.util.ArrayList;
import java.util.Iterator;

import ro.mihai.tpt.PathView;
import ro.mihai.tpt.ViewTimes;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Path;
import android.app.Activity;
import android.database.DataSetObserver;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class PathListViewAdapter implements ListAdapter {

    private Activity context;
    private City city;
	private MapKind mapKind;
    private ArrayList<Path> paths;
	
    public PathListViewAdapter(Activity context, City city, MapKind mapKind, Iterator<Path> pathIterator) {
    	this.context = context;
    	this.city = city;
		this.mapKind = mapKind;
    	paths = new ArrayList<Path>();
    	while (pathIterator.hasNext())
    		paths.add(pathIterator.next());
    }
    
	public int getCount() {
		return 1 + paths.size();
	}

	public boolean isEmpty() {
		return paths.isEmpty();
	}

	public Object getItem(int position) {
		return position == 0 ? "map": paths.get(position);
	}

	public long getItemId(int position) {
		// TODO is this correct, I wonder?
		return position;
	}

	public int getItemViewType(int position) {
		return position==0 ? 0 : 1;
	}
	public int getViewTypeCount() {
		return 2;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
        if (position==0) {
            return this.getMapView(convertView, parent);
        } else {
            return this.getPathView(
                    convertView, parent, paths.get(position - 1), (position % 2) != 1);
        }
	}

    private View getPathView(View convertView, ViewGroup parent, Path path, boolean isOddItem) {
        View pathView = convertView;

        if (pathView == null) {
            pathView = PathView.createPathView(context.getLayoutInflater(), null);
        }
        OnClickListener onClick = new SelectPath(ViewTimes.class, path);
        return PathView.fillPathView(pathView, parent.getResources(), path, onClick, isOddItem);
    }

    private View getMapView(View convertView, ViewGroup parent) {
        View mapView = convertView;

        if (mapView == null) {
            mapView = PathView.createMapView(context.getLayoutInflater(), null);
        }
        PathView.fillMapView(mapView, parent.getResources(), this.mapKind);
        return mapView;
    }

	public void registerDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
	}

	public void unregisterDataSetObserver(DataSetObserver observer) {
		// TODO Auto-generated method stub
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean areAllItemsEnabled() {
		return true;
	}

	public boolean isEnabled(int position) {
		return true;
	}

	public class SelectPath implements OnClickListener {
		private Path selectedPath;
		private Class<?> activity;
		
		public SelectPath(Class<?> activity, Path selectedPath) {
			this.selectedPath = selectedPath;
			this.activity = activity;
		}
		
		public void onClick(View v) {
	    	Utils.recordUsePath(context, selectedPath);
	    	new StartActivity(context, activity)
	    		.addCity(city)
	    		.addLinePath(selectedPath)
	    		.start();
	    }
		
	} 
}
