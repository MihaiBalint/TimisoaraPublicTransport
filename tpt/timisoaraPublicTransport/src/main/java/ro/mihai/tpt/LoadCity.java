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

import java.io.IOException;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.utils.AndroidCityLoader;
import ro.mihai.tpt.utils.AppPreferences;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.util.IMonitor;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadCity extends Activity implements Runnable, IMonitor {
	private City city = null;
	private AppPreferences prefs = null;
	private ProgressBar prgress;
	private TextView status;
	private int work, max=-1, workMax;
	private long last=0;;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // this makes it completely full-screen
        // we commented it since we want partially full (system bar is visible, window bar hidden)
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.loading);
        
        // apply pref defaults
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
		prgress = (ProgressBar)findViewById(R.id.ProgressBar);
        status = (TextView)findViewById(R.id.StatusText);
        new Thread(this).start();
    }

	public void run() {
        try {
        	if (null==city)
        		city = AndroidCityLoader.loadStoredCityOrDownloadAndCache(
        				this.getAppPreferences(), LoadCity.this, this);
        	new StartActivity(this, ViewCatFavorites.class)
        		.addCity(city)
        		.replace();
        	finish();
        } catch(IOException e) {
        	setStatus("Err: "+e.getMessage());
        	city = new City();
        } 
	}    
	
	public int getMax() {
		// TODO Auto-generated method stub
		return workMax;
	}
	public void setMax(int max) {
		this.workMax = max;
		this.max = (max*105)/100;
		prgress.setMax(this.max);
	}

	private void setStatus(final String statusText) {
		runOnUiThread(new Runnable() { public void run() {
			status.setText(statusText);
		}});
	}

	public void workComplete() {
		work++;
		if(work>=max) return;
		
		long crt = System.currentTimeMillis(); 
		
		if(crt>last && (crt-last)>=300) {
			last = crt;
			runOnUiThread(new Runnable() { public void run() {
				prgress.setProgress(work);
			}});
		}
	}

	protected final AppPreferences getAppPreferences() {
		if(null==prefs) {
			prefs = new AppPreferences(this);
		}
		return prefs;
	}
}