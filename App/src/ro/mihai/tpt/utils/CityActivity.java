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
import ro.mihai.tpt.Preferences;
import ro.mihai.tpt.model.City;
import ro.mihai.util.IPrefs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CityActivity extends Activity implements IPrefs {
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
	
	private static final int REQUEST_CODE_PREFERENCES = 1;
	public static final int REQUEST_CODE_REPLACE = 99;
	protected void launchPrefs() {
        // When the button is clicked, launch an activity through this intent
        Intent launchPreferencesIntent = new Intent().setClass(this, Preferences.class);

        // Make it a subactivity so we know when it returns
        startActivityForResult(launchPreferencesIntent, REQUEST_CODE_PREFERENCES);
	}
	
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
        case REQUEST_CODE_REPLACE: 
        	this.finish(); 
        	break;
        case REQUEST_CODE_PREFERENCES: 
            // The preferences returned if the request code is what we had given
            // earlier in startSubActivity
        	baseUrl = Utils.readBaseDownloadUrl(this);
        	break;
        }
    }
  
    private String baseUrl = null;
    public String getBaseUrl() {
    	if(baseUrl==null) 
    		baseUrl = Utils.readBaseDownloadUrl(this);
		return baseUrl;
	}
    
    
	protected void reboot() {
    	new StartActivity(this, LoadCity.class)
		.start();
	}
}