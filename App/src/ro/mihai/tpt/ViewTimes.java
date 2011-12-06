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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.*;
import ro.mihai.tpt.utils.AndroidSharedObjects;
import ro.mihai.tpt.utils.CityActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class ViewTimes extends CityActivity {
	private TableLayout timesTable;
	private LayoutInflater inflater;
	private City city;
	private Path path;
	private UpdateTimes updater;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_times);
    	city = getCity();
    	city.getClass();
    	
		path = AndroidSharedObjects.instance().getLinePath();
		
    	String labelText = path.getLine().getName()+" ("+path.getNiceName()+")";
    	Button update = (Button)findViewById(R.id.UpdateButton);
    	update.setOnClickListener(updater = new UpdateTimes());
    	update.setText(labelText);
    	
    	timesTable = (TableLayout)findViewById(R.id.StationTimesTable);
    	inflater = this.getLayoutInflater();
    	inflateTable();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	updater.killUpdate();
    }

	private void inflateTable() {
		timesTable.removeAllViews();
		List<Station> stations = path.getStationsByPath();
		int index = 0;
		for(Station s: stations) {
			Estimate est = path.getEstimate(s);
			int rowLayout;
			if (est.isUpdating())
				rowLayout = R.layout.times_station_updating;
			else if (est.hasErrors()) {
				rowLayout = R.layout.times_station_err;
				updater.setHasErrors();
			} else
				rowLayout = R.layout.times_station;
			
	    	View timesRow = inflater.inflate(rowLayout, timesTable, false);
	    	
	    	TextView stationLabel = (TextView)timesRow.findViewById(R.id.StationLabel);
			String label = s.getNicestNamePossible(); 
			// label = label.substring(0,Math.min(30, label.length()));
	    	stationLabel.setText("|"+label);
	    	
	    	TextView stationTime = (TextView)timesRow.findViewById(R.id.StationTime);
	    	stationTime.setText(est.getTimes1());

	    	// TextView stationStatus = (TextView)timesRow.findViewById(R.id.StationStatus);
	    	// stationStatus.setText(status);
	    	
	    	timesTable.addView(timesRow);
	    	if (est.isVehicleHere()) {
	    		timesTable.addView(inflater.inflate(R.layout.times_station_vehicle, timesTable, false));
	    	}
	    	index++;
		}
	}
	
	private class UpdateTimes implements Runnable, OnClickListener {
		private AtomicBoolean running = new AtomicBoolean(false);
		private AtomicBoolean hasErrors = new AtomicBoolean(false);
		
		public void run() {
			int ec = 0;
			UpdateView viewUpdater = new UpdateView();
			for(Station s: path.getStationsByPath()) {
				if(!running.get()) return;
				path.getEstimate(s).startUpdate();
				runOnUiThread(viewUpdater);
				ec = path.updateStation(ec, s);
			}
			killUpdate();
			runOnUiThread(new UpdateView());
			if (hasErrors.compareAndSet(true, false))
				runOnUiThread(new ReportError());
		}
		
		public void onClick(View v) {
			if (running.compareAndSet(false, true)) 
				new Thread(this).start();
		}
		
		public void killUpdate() {
			running.set(false);
			path.clearAllUpdates();
		}
		
		public void setHasErrors() {
			hasErrors.set(true);
		}
	}
	
    private class UpdateView implements Runnable {
    	private long last=0;
    	
		public void run() {
			long crt = System.currentTimeMillis(); 
			if(crt>last && (crt-last)<500) return;
			last = crt;
			inflateTable();
		}
	}
    
    private class ReportError implements Runnable, DialogInterface.OnClickListener {
    	public void run() {
			new AlertDialog.Builder(ViewTimes.this)
				.setMessage(R.string.upd_error)
				.setPositiveButton("Ok", this)
				.create()
				.show();
		}
    	
		public void onClick(DialogInterface dialog, int which) {
			// NOP
		}
    }
}