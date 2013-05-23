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

import java.util.Iterator;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.util.LineKind;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;

public abstract class ViewCategories extends CityActivity {

	protected abstract Iterator<Path> getLinePathIterator(City city);
	protected OnClickListener getCategoryClickListener(StartActivity activity) {
		return activity.startOnClick();
	}
	
	/** Called when the activity is first created. */
    @Override
	protected final void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	City city = getCity();
    	setContentView(R.layout.list_categories);
    	
    	addMenuAction();
    	findViewById(R.id.tram_button).setOnClickListener(getCategoryClickListener(
    			new StartActivity(this, ViewCatTrams.class).addCity(city)));
    	findViewById(R.id.bus_button).setOnClickListener(getCategoryClickListener(
    			new StartActivity(this, ViewCatBusses.class).addCity(city)));
    	findViewById(R.id.trolleybus_button).setOnClickListener(getCategoryClickListener(
    			new StartActivity(this, ViewCatTrolleys.class).addCity(city)));
    	
    	ViewGroup favoritesView = (ViewGroup)findViewById(R.id.favorite_content);
    	LayoutInflater inflater = this.getLayoutInflater();
    	
    	Iterator<Path> pathIt = getLinePathIterator(city);
    	while(pathIt.hasNext()) {
    		Path path = pathIt.next();
    		favoritesView.addView(PathView.newPathView(inflater, favoritesView, path,
    				new SelectLinePath(this, ViewTimes.class, city, path.getLine())));
    	}
    }
    
    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }
    
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.app_credits:
        	startActivity(new Intent(this, ViewCredits.class));
            return true;
        case R.id.app_settings: {
        	launchPrefs();
        	return true;
        }
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
	public static class LineKindIterator implements Iterator<Path> {
		private final Iterator<String> lineNameIterator;
		private final City city;
		
		public LineKindIterator(City city, Iterator<String> lineNameIterator) {
			this.city = city;
			this.lineNameIterator = lineNameIterator;
		}
		
		public LineKindIterator(City city, LineKind lineKind) {
			this(city, lineKind.getLineNameIterator());
		}
		
		public boolean hasNext() {
			return this.lineNameIterator.hasNext();
		}
		public Path next() {
			return city.getLine(lineNameIterator.next()).getFirstPath();
		}
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}