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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import ro.mihai.tpt.R;
import ro.mihai.tpt.conf.Constants;
import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.conf.StationPathsSelection;
import ro.mihai.tpt.conf.StationPathsSelection.Node;
import ro.mihai.tpt.model.*;
import ro.mihai.tpt.utils.AndroidSharedObjects;
import ro.mihai.tpt.utils.CityActivity;
import ro.mihai.tpt.utils.CityNotLoadedException;
import ro.mihai.tpt.utils.LineKindUtils;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.util.LineKind;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ViewTimes extends CityActivity {
	private TableLayout timesTable;
	private LayoutInflater inflater;
	private City city;
	private PathStationsSelection path;
	private UpdateTimes updater;
	private UpdateQueue queue;

	/** Called when the activity is first created. */
    @Override
	protected void onCreateCityActivity(Bundle savedInstanceState) throws CityNotLoadedException {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
    	setContentView(R.layout.list_times);
    	city = getCity();
    	city.getClass();
    	
    	path = AndroidSharedObjects.instance().getPathSelection();
		queue = new UpdateQueue();
		
		TextView lineKind = (TextView)findViewById(R.id.LineKind);
		lineKind.setText(path.getLineKindLabel());
		
		TextView lineName = (TextView)findViewById(R.id.LineName);
		lineName.setText(path.getLineNameLabel());
		
		TextView lineDirection1 = (TextView)findViewById(R.id.LineDirection1);
		lineDirection1.setText(path.getPathLabel(0));
		
		TextView lineDirection2 = (TextView)findViewById(R.id.LineDirection2);
		lineDirection2.setText(path.getPathLabel(1));
		
    	Button update = (Button)findViewById(R.id.UpdateButton);
    	update.setOnClickListener(updater = new UpdateTimes());
    	update.setText("Update");
    	
    	int[] radio_ids = new int[]{R.id.LineDirectionRadio1, R.id.LineDirectionRadio2};
    	View radio = (View)findViewById(radio_ids[path.getCurrentPath()]);
    	radio.setBackgroundColor(0xFFb2ff36);
    	
    	LinearLayout lineDirectionView = (LinearLayout)findViewById(R.id.LineDirectionView);
    	lineDirectionView.setOnClickListener(new PathSwitcher());

    	Button connections = (Button)findViewById(R.id.ConnectionsButton);
    	connections.setOnClickListener(new SelectConnectionKinds());
    	
    	timesTable = (TableLayout)findViewById(R.id.StationTimesTable);
    	inflater = this.getLayoutInflater();
    	inflateTable();
    	updater.onClick(null); // start update immediately after starting the activity
    }
    
    public void queueUIUpdate(Runnable r) {
    	queue.add(r);
    	runOnUiThread(queue);
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	updater.killUpdate();
    }
    
	private void inflateTable() {
		timesTable.removeAllViews();
		List<StationPathsSelection> stations = path.getStations();

		boolean evenRow = true; // java is zero based, therefore first = even
		for(StationPathsSelection sel: stations) {
			Station s = sel.getStation();
			Estimate est = path.getEstimate(s);
			
	    	//if (est.isVehicleHere()) {
	    	//	timesTable.addView(inflater.inflate(R.layout.times_station_vehicle, timesTable, false));
	    	//}
	    	timesTable.addView(newStationEstimateView(est, evenRow));
	    	evenRow = !evenRow;
	    	
	    	for(Node connection : sel.getConnections()) {
	    		Path connectingPath = connection.path;
	    		est = connectingPath.getEstimate(connection.station);

	    		timesTable.addView(newConnectionEstimateView(est, evenRow));
		    	evenRow = !evenRow;
	    	}
		}
	}

	private View newStationEstimateView(Estimate est, boolean evenRow) {
		int rowLayout;
		boolean alternateBackground = false;
		if (est.isUpdating())
			rowLayout = R.layout.times_station_updating;
		else if (est.hasErrors()) {
			rowLayout = R.layout.times_station_err;
			updater.setHasErrors();
			alternateBackground = true;
		} else {
			rowLayout = R.layout.times_station;
			alternateBackground = true;
		}

		View timesRow = inflater.inflate(rowLayout, timesTable, false);
		TextView stationLabel = (TextView)timesRow.findViewById(R.id.StationLabel);
		stationLabel.setText(est.getStation().getNicestNamePossible());
		
		TextView stationTime = (TextView)timesRow.findViewById(R.id.StationTime);
		stationTime.setText(est.estimateTimeString());
		
		if(alternateBackground) {
			View tableRow = timesRow.findViewById(R.id.StationStatusRow);
			tableRow.setBackgroundColor(evenRow ? 0xff282828 : 0xff000000);
		}

		return timesRow;
	}

	private View newConnectionEstimateView(Estimate est, boolean evenRow) {
		int rowLayout;
		if (est.isUpdating())
			rowLayout = R.layout.times_connection_updating;
		else if (est.hasErrors()) {
			rowLayout = R.layout.times_connection_err;
			updater.setHasErrors();
		} else
			rowLayout = R.layout.times_connection;

		View timesRow = inflater.inflate(rowLayout, timesTable, false);
		
		Path connectingPath = est.getPath();
		
		TextView lineNameLabel = (TextView)timesRow.findViewById(R.id.LineName);
		lineNameLabel.setText(connectingPath.getLine().getName());

		TextView lineDirectionLabel = (TextView)timesRow.findViewById(R.id.LineDirection);
		lineDirectionLabel.setText(connectingPath.getNiceName());
		
		TextView stationTime = (TextView)timesRow.findViewById(R.id.StationTime);
		stationTime.setText(est.estimateTimeString());

		return timesRow;
	}
	
	private class UpdateTimes implements Runnable, OnClickListener {
		private AtomicBoolean running = new AtomicBoolean(false);
		private AtomicBoolean hasErrors = new AtomicBoolean(false);
		
		public void run() {
			int ec = 0, index = 0;
			for(StationPathsSelection sel: path.getStations()) {
				if(!running.get()) return;
				Station s = sel.getStation();
				ec = updateStationRowView(ec, index, path.getPath(), s);
				index++;
				for(Node connection : sel.getConnections()) {
					if(!running.get()) return;
					ec = updateConnectionRowView(ec, index, connection.path, connection.station);
					index++;
				}
			}
			killUpdate();
			runOnUiThread(new UpdateView());
			if (hasErrors.compareAndSet(true, false))
				runOnUiThread(new ReportError());
		}

		private int updateConnectionRowView(int ec, final int rowIndex, Path path, Station s) {
			final Estimate est = path.getEstimate(s);
			final boolean even = (rowIndex & 1) == 0; // this is zero based, therefore first = even
			Runnable upd = new Runnable() {
				public void run() {
					timesTable.removeViewAt(rowIndex);
		    		timesTable.addView(newConnectionEstimateView(est, even), rowIndex);
				}
			};
			est.startUpdate();
			queueUIUpdate(upd);
			ec = path.updateStation(ViewTimes.this, ec, s);
			queueUIUpdate(upd);
			return ec;
		}

		private int updateStationRowView(int ec, final int rowIndex, Path path, Station s) {
			final Estimate est = path.getEstimate(s);
			final boolean even = (rowIndex & 1) == 0; // this is zero based, therefore first = even
			Runnable upd = new Runnable() {
				public void run() {
					// ****************************************************************
					boolean _even = even;
					int _rowIndex = rowIndex;
		    		System.out.println(_even+"+"+_rowIndex);
					timesTable.removeViewAt(rowIndex);
		    		timesTable.addView(newStationEstimateView(est, even), rowIndex);
				}
			};
			est.startUpdate();
			queueUIUpdate(upd);
			ec = path.updateStation(ViewTimes.this, ec, s);
			queueUIUpdate(upd);
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
    
    private class SelectConnectionKinds implements OnClickListener, DialogInterface.OnClickListener {
    	private List<List<Path>> pathList;
    	
		public void onClick(View v) {
			pathList = new ArrayList<List<Path>>();
		   	Set<Path> connections = new TreeSet<Path>(new Path.LabelComparator());
	    	for(StationPathsSelection sel : path.getStations()) {
    			Station selStation = sel.getStation();
	    		// (1) the paths passing through this exact same station
	    		Set<Line> stationLines = new HashSet<Line>(selStation.getLines());
    			for(Line l:stationLines)
    				for(Path p:l.getPaths())
    					if (p!=path.getPath() && p.getStationsByPath().contains(selStation))
    						connections.add(p);
    			// (2) not the empty junction (contains unrelated stations)
    			// Note that right now we actually do not have a junction with an empty name
    			// So the correct way of doing this would be to actually check that the distance 
    			// between sel.getStation() and any of sel.getStation().getJunction().getStations()
    			// is smaller than some given constant
    			if (selStation.getJunctionName().trim().length() == 0)
	    			continue;
    			// (3) the paths passing through the stations of the junction
	    		for(Station s:selStation.getJunction().getStations()) {
	    			boolean haveDistance = false;
	    			int dist = 0;
	    			if (s!=selStation) 
	    				for(Line l:s.getLines())
	    					if (!stationLines.contains(l))
	    						for(Path p:l.getPaths())
	    							if (p!=path.getPath() && p.getStationsByPath().contains(s)) {
	    								if(!haveDistance) {
	    									haveDistance = true;
	    									dist = selStation.distanceTo(s);
	    								}
	    								if (dist < Constants.MAX_CONNECTION_DIST)
	    									connections.add(p);
	    							}
	    		}
	    	}
	    	
	    	List<String> labels = new ArrayList<String>();
	    	for(LineKind k : LineKind.values()) {
	    		List<Path> paths = new ArrayList<Path>();
	    		for(Path p:connections)
	    			if (k==p.getLine().getKind())
	    				paths.add(p);
	    		if(paths.size()>0) {
	    			// add all paths for this connection type
	    			pathList.add(paths);
	    			labels.add(  paths.size()>1 
	    				? getString(LineKindUtils.getLabelId(k)) // more than one? give generic label
	    				: paths.get(0).getLabel()); // single one? use it's own label
	    		}
	    	}
	    	
			final CharSequence[] items = new CharSequence[labels.size()];
	    					
	    	Iterator<String> it = labels.iterator();
			for(int i=0;i<items.length;i++) 
				items[i] = it.next();

			new AlertDialog.Builder(ViewTimes.this)
				.setTitle(getString(R.string.selConnections))
				.setItems(items, this)
				.create()
				.show();
		}

		public void onClick(DialogInterface dialog, int which) {
			List<Path> connections = pathList.get(which);
			if (connections.size() > 1) {
				// more than one to select
				final CharSequence[] items = new CharSequence[connections.size()];
				
		    	Iterator<Path> it = connections.iterator();
				for(int i=0;i<items.length;i++) {
					Path p = it.next();
					items[i] = p.getLabel();
				}

				new AlertDialog.Builder(ViewTimes.this)
					.setTitle(getString(R.string.selConnections))
					.setItems(items, new SelectConnections(connections))
					.create()
					.show();
				
			} else {
				// a single connection of this type, no use popping a new dialog
		    	path.addConnections(connections.get(0));
		    	runOnUiThread(new UpdateView());
			}
		}
    }
    
    private class SelectConnections implements DialogInterface.OnClickListener {
    	private List<Path> pathList;
    	
    	public SelectConnections(List<Path> pathList) {
    		this.pathList = pathList;
		}
    	
		public void onClick(DialogInterface dialog, int which) {
	    	path.addConnections(pathList.get(which));
	    	runOnUiThread(new UpdateView());
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
    
    private class PathSwitcher implements View.OnClickListener {
		public void onClick(View v) {
        	Path p = path.getPath();
        	ArrayList<Path> paths = new ArrayList<Path>(p.getLine().getPaths());
        	paths.remove(p);
        	if(paths.size()==1) {
        		Path opposite = paths.get(0);
            	new StartActivity(ViewTimes.this, ViewTimes.class)
    	    		.addCity(city)
    	    		.addLinePath(opposite)
    	    		.replace();
        	} 
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.times_menu, menu);
        return true;
    }    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.switch_direction:
        	Path p = path.getPath();
        	ArrayList<Path> paths = new ArrayList<Path>(p.getLine().getPaths());
        	paths.remove(p);
        	if(paths.size()==1) {
        		Path opposite = paths.get(0); 
            	new StartActivity(this, ViewTimes.class)
    	    		.addCity(city)
    	    		.addLinePath(opposite)
    	    		.replace();
        	} 
            return true;
        case R.id.app_settings: 
        	launchPrefs();
        	return true;
        /*            
        case R.id.view_map:
        	new StartActivity(this, MapTimes.class)
        		.addCity(city)
        		.addLinePath(path)
        		.start();
            return true;
 		*/
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
}