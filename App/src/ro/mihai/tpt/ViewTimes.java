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
import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.conf.StationPathsSelection;
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
	private PathStationsSelection path;
	private UpdateTimes updater;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_times);
    	city = getCity();
    	city.getClass();
    	
    	path = new PathStationsSelection(AndroidSharedObjects.instance().getLinePath());
    	path.selectAllStations();
		
    	Button update = (Button)findViewById(R.id.UpdateButton);
    	update.setOnClickListener(updater = new UpdateTimes());
    	update.setText(path.getLabel());
    	
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
		List<StationPathsSelection> stations = path.getStations();

		for(StationPathsSelection sel: stations) {
			Station s = sel.getStation();
			Estimate est = path.getEstimate(s);
			
	    	if (est.isVehicleHere()) {
	    		timesTable.addView(inflater.inflate(R.layout.times_station_vehicle, timesTable, false));
	    	}
	    	timesTable.addView(newStationEstimateView(est, s.getNicestNamePossible()));
	    	
	    	for(Path connection : sel.getConnections()) {
	    		est = connection.getEstimate(s);
	    		String label = connection.getLine().getName()+" ("+connection.getNiceName()+")";

	    		timesTable.addView(newConnectionEstimateView(est, label));
	    	}
		}
	}

	private View newStationEstimateView(Estimate est, String label) {
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
		stationLabel.setText("|"+label);
		
		TextView stationTime = (TextView)timesRow.findViewById(R.id.StationTime);
		stationTime.setText(est.estimateTimeString());

		// TextView stationStatus = (TextView)timesRow.findViewById(R.id.StationStatus);
		// stationStatus.setText(status);
		return timesRow;
	}

	private View newConnectionEstimateView(Estimate est, String label) {
		// TODO create own view
		return newStationEstimateView(est, label);
	}
	
	private class UpdateTimes implements Runnable, OnClickListener {
		private AtomicBoolean running = new AtomicBoolean(false);
		private AtomicBoolean hasErrors = new AtomicBoolean(false);
		
		public void run() {
			int ec = 0;
			UpdateView viewUpdater = new UpdateView();
			for(StationPathsSelection sel: path.getStations()) {
				if(!running.get()) return;
				Station s = sel.getStation();
				ec = update(ec, viewUpdater, path.getPath(), s);
				for(Path connection : sel.getConnections()) {
					if(!running.get()) return;
					ec = update(ec, viewUpdater, connection, s);
				}
			}
			killUpdate();
			runOnUiThread(new UpdateView());
			if (hasErrors.compareAndSet(true, false))
				runOnUiThread(new ReportError());
		}

		private int update(int ec, UpdateView viewUpdater, Path path, Station s) {
			path.getEstimate(s).startUpdate();
			runOnUiThread(viewUpdater);
			ec = path.updateStation(ec, s);
			return ec;
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