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
import ro.mihai.tpt.SelectLinePath;
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
    private ArrayList<Path> paths;
	
    public PathListViewAdapter(Activity context, City city, Iterator<Path> pathIterator) {
    	this.context = context;
    	this.city = city;
    	paths = new ArrayList<Path>();
    	while (pathIterator.hasNext())
    		paths.add(pathIterator.next());
    }
    
	public int getCount() {
		return paths.size();
	}

	public boolean isEmpty() {
		return paths.isEmpty();
	}

	public Object getItem(int position) {
		return paths.get(position);
	}

	public long getItemId(int position) {
		// TODO is this correct, I wonder?
		return position;
	}

	public int getItemViewType(int position) {
		return 0;
	}
	public int getViewTypeCount() {
		return 1;
	}


	public View getView(int position, View convertView, ViewGroup parent) {
		View pathView = convertView;

		if (pathView == null) {
			pathView = PathView.createPathView(context.getLayoutInflater(), null); 
		}
		Path path = paths.get(position);
		boolean isOddItem = (position % 2) != 0;
		OnClickListener onClick = new SelectLinePath(this.context, ViewTimes.class, city, path.getLine()); 
		return PathView.fillPathView(pathView, parent, path, onClick, isOddItem);
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

}
