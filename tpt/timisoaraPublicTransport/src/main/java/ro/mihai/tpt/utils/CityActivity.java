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
import ro.mihai.tpt.R;
import ro.mihai.tpt.ViewUserCredits;
import ro.mihai.tpt.data.Achievements;
import ro.mihai.tpt.model.City;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CityActivity extends Activity {
	private City city = null;
	private AppPreferences prefs = null;
	
	protected final City getCity() throws CityNotLoadedException {
		if(null==city) {
			city = AndroidSharedObjects.instance().getCity();
			AndroidDetachableStream dataStream = ((AndroidDetachableStream)city.getDetachableInputStream());
			dataStream.setContext(this);
		}
		return city;
	}
	
	protected final AppPreferences getAppPreferences() {
		if(null==prefs) {
			prefs = new AppPreferences(this);
		}
		return prefs;
	}
	
	@Override
	protected final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			this.onCreateCityActivity(savedInstanceState);
            new Upgrade(this).upgrade();
            addAchievements();
		} catch(CityNotLoadedException e) {
			reboot();
			finish();
		}
	}

	private void addAchievements() {
		Achievements achieve = getAppPreferences().getTopAchieve(); 
		ImageView badge = (ImageView)findViewById(R.id.badge);
		if (achieve.isVisible()) {
			badge.setImageResource(achieve.getBadgeId());
		    badge.setVisibility(View.VISIBLE);
		} else {
		    badge.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected final void onResume() {
		super.onResume();
		try {
			this.onResumeCityActivity();
		} catch(CityNotLoadedException e) {
			reboot();
			finish();
		}
	}
	
	public void finishActivity(View trigger) {
		finish();
	}
	
	public void openContextMenu(View trigger) {
		openOptionsMenu();
	}

	public void openAchievementsView(View trigger) {
		Intent userCredits = new Intent().setClass(this, ViewUserCredits.class);
        startActivity(userCredits);
	}
	
	protected void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
		// nop
	}

	protected void onResumeCityActivity() throws CityNotLoadedException {
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
        	getAppPreferences().refreshBaseUrl();
        	addAchievements();
        	break;
        }
    }
  
	protected void reboot() {
    	new StartActivity(this, LoadCity.class)
		.start();
	}
	
	public static Typeface regularFont = null, boldFont = null;
	public void setDefaultViewFont(View view) {
		if (null==regularFont)
			regularFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");
		if (null==boldFont)
			boldFont = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Bold.ttf");
		setViewFont(view, new Typeface[]{regularFont, boldFont});
	}
	
	private static final void setViewFont(View view, Typeface[] font) {
		if (view == null) {
			return;
		} else if (view instanceof TextView) {
        	TextView text = (TextView) view;
        	int fontStyle = 0;
        	if (text.getTypeface() != null)
        		fontStyle = text.getTypeface().getStyle();
            text.setTypeface(font[fontStyle]);
        } else if (view instanceof ViewGroup) {
        	ViewGroup container = (ViewGroup) view;
    	    int mCount = container.getChildCount();
    	    for (int i=0; i<mCount; i++) 
    	    	setViewFont(container.getChildAt(i), font);
        }
	}	
}