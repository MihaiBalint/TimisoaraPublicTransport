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

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.StartActivity;

import android.os.Bundle;
import android.view.Window;

public class ViewCategories extends CityActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	City c = getCity();
    	setContentView(R.layout.list_categories);
    	findViewById(R.id.tram_button).setOnClickListener(new StartActivity(this, ViewTrams.class).addCity(c));
    	findViewById(R.id.bus_button).setOnClickListener(new StartActivity(this, ViewBusses.class).addCity(c));
    	findViewById(R.id.trolleybus_button).setOnClickListener(new StartActivity(this, ViewTrolleys.class).addCity(c));
    	
    	findViewById(R.id.b33).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("33")));
    	findViewById(R.id.b40).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("40")));
    	findViewById(R.id.bTb14).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb14")));
    	findViewById(R.id.bTb15).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb15")));
    	findViewById(R.id.bTv2).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tv2")));
    	findViewById(R.id.bTv4).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tv4")));
    }
}