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

import android.os.Bundle;
import android.view.Window;

public class ViewTrolleys extends CityActivity {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_trolleys);

    	City c = getCity();
    	findViewById(R.id.bTb11).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb11")));
    	findViewById(R.id.bTb14).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb14")));
    	findViewById(R.id.bTb15).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb15")));
    	findViewById(R.id.bTb16).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb16")));
    	findViewById(R.id.bTb17).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb17")));
    	findViewById(R.id.bTb18).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb18")));
    	findViewById(R.id.bTb19).setOnClickListener(new SelectLinePath(this, ViewTimes.class, c, c.getLine("Tb19")));
    }
}