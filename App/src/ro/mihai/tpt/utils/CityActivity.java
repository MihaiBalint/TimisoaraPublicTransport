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

import ro.mihai.tpt.LoadCity;
import ro.mihai.tpt.model.City;

import android.app.Activity;
import android.os.Bundle;

public class CityActivity extends Activity {
	private City city = null;
	
	protected final City getCity() throws CityNotLoadedException {
		if(null==city) {
			city = AndroidSharedObjects.instance().getCity();
			AndroidDetachableStream dataStream = ((AndroidDetachableStream)city.getDetachableInputStream());
			dataStream.setContext(this);
		}
		return city;
	}
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			this.onCreateCityActivity(savedInstanceState);
		} catch(CityNotLoadedException e) {
			reboot();
			finish();
		}
	}

	protected void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
		// nop
	}

	
	protected void reboot() {
    	new StartActivity(this, LoadCity.class)
		.start();
	}
}