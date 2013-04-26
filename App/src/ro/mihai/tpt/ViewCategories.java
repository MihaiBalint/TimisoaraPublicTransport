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
import java.util.List;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.LineKindUtils;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.tpt.utils.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;

public class ViewCategories extends CityActivity {

	/** Called when the activity is first created. */
    @Override
	protected void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	City city = getCity();
    	setContentView(R.layout.list_favorites);
    	addMenuAction();
    	findViewById(R.id.tram_button).setOnClickListener(new StartActivity(this, ViewTrams.class).addCity(city));
    	findViewById(R.id.bus_button).setOnClickListener(new StartActivity(this, ViewBusses.class).addCity(city));
    	findViewById(R.id.trolleybus_button).setOnClickListener(new StartActivity(this, ViewTrolleys.class).addCity(city));
    	
    	List<String> sortedNames = Utils.getTopLines(this);
    	for (String l : LineKindUtils.MOST_USED)
    		if (!sortedNames.contains(l))
    			sortedNames.add(l);
    	int[] ids = {R.id.opt11, R.id.opt12, R.id.opt13, R.id.opt21, R.id.opt22, R.id.opt23};
    	Iterator<String> nameIt = sortedNames.iterator();
    	for(int id: ids) {
    		String name = nameIt.next();
    		Line line = city.getLine(name);
    		while (line.isFake() && nameIt.hasNext()) {
    			name = nameIt.next();
    			line = city.getLine(name);
    		}
    		Button btn = (Button)findViewById(id);
    		btn.setText(" "+name+" ");
    		btn.setOnClickListener(new SelectLinePath(this, ViewTimes.class, city, line));
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.about_menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
    

}