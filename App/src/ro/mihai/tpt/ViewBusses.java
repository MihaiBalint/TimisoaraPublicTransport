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

import ro.mihai.tpt_light.R;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.utils.CityActivity;

import android.os.Bundle;
import android.view.Window;

public class ViewBusses extends CityActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_busses);

    	City c = getCity();
    	findViewById(R.id.bE1).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E1")));
    	findViewById(R.id.bE2).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E2")));
    	findViewById(R.id.bE3).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E3")));
    	findViewById(R.id.bE4).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E4")));
    	findViewById(R.id.bE5).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E5")));
    	findViewById(R.id.bE6).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E6")));
    	findViewById(R.id.bE7).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E7")));
    	findViewById(R.id.bE7b).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E7b")));
    	findViewById(R.id.bE8).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("E8")));
    	
    	findViewById(R.id.b3).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("3")));
    	findViewById(R.id.b13).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("13")));
    	findViewById(R.id.b21).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("21")));
    	findViewById(R.id.b28).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("28")));

    	findViewById(R.id.b32).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("32")));
    	findViewById(R.id.b33).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("33")));
    	findViewById(R.id.b40).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("40")));
    	findViewById(R.id.b46).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("46")));

    	findViewById(R.id.bM30).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("M30")));
    	findViewById(R.id.bM35).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("M35")));
    	findViewById(R.id.bM36).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("M36")));
    }
}