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
package ro.mihai.tpt.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.mihai.tpt.RATT;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

public class Line {
	private String name, id;
	private Set<Station> stations;
	private ArrayList<Station> sortedStations;
	
	private Map<Station,String> times1, times2, errs;
	private final StationTimesComp timesComp = new StationTimesComp();
	
	public Line(String id, String name) {
		this.id = id;
		this.name = name;
		this.stations = new HashSet<Station>();
		this.sortedStations = new ArrayList<Station>();
		this.times1 = new HashMap<Station, String>();
		this.times2 = new HashMap<Station, String>();
		this.errs = new HashMap<Station, String>();
	}
	
	public void addStation(Station s) {
		stations.add(s);
		sortedStations.add(s);
		times1.put(s, "update");	
		times2.put(s, "update");	
		errs.put(s, "");	
	}
	
	public Set<Station> getStations() {
		return stations;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	
	public void updateAllStations() {
		int ec = 0;
		for(Station s:stations) {
			startUpdate(s);
			ec = updateStation(ec, s);
		}
		updateSort(); 
	}

	public void updateSort() {
		ArrayList<Station> newSort = new ArrayList<Station>(stations);
		Collections.sort(newSort, timesComp);
		sortedStations = newSort;
	}
	
	public List<Station> getSortedStations() {
		return sortedStations;
	}
	
	
	public void startUpdate(Station s) {
		putErr(s, "upd");
	}

	private synchronized void putErr(Station s, String err) {
		errs.put(s, err);
	}

	private synchronized void putTime(Station s, String t1, String t2) {
		times1.put(s,t1);
		times2.put(s,t2);
		errs.put(s, "");
	}

	public int updateStation(int ec, Station s) {
		try {
			if(ec<3) {
				String[] t = RATT.downloadTimes(this, s);
				putTime(s, t[0], t[1]);
			} else  
				putErr(s, "upd-canceled");
		} catch(IOException e) {
			putErr(s,"io-err");
			ec++;
		}
		return ec;
	}
	
	private String format(String time) {
		if(time.contains(":")) return time;
		try {
			int min = Integer.parseInt(time);
			return min<10 
				? min+" min"
				: min+"min";
		} catch(NumberFormatException e) {
			return time;
		}
	}

	private static final int textSize = 16;
	public List<TableRow> timeView(Context ctx) {
		ArrayList<TableRow> rows = new ArrayList<TableRow>();
		for(Station s:sortedStations) {
			TableRow r = new TableRow(ctx);
			r.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));		

			TextView t;
			String t1,t2,e;
			synchronized(this) {
				t1 = times1.get(s);
				t2 = times2.get(s);
				e = errs.get(s);
			}
			
			t = new TextView(ctx);
			t.setTextSize(textSize);
			t.setText(s.getName());
			r.addView(t);

			t = new TextView(ctx);
			t.setTextSize(textSize);
			t.setText("|"+format(t1));
			r.addView(t);
			
			t = new TextView(ctx);
			t.setTextSize(textSize);
			t.setText("|"+format(t2)+"   ");
			r.addView(t);

			t = new TextView(ctx);
			t.setTextSize(textSize);
			if(e.length()>0) {
				t.setBackgroundColor(Color.argb(200, 160, 0, 0));
				t.setTextColor(Color.argb(250, 255, 255, 255));
				if ("upd".equals(e))
					r.setBackgroundColor(Color.argb(200, 101, 55, 0));
				t.setText("  "+e+"  ");
			} else
				t.setText("");
			r.addView(t);
			
			rows.add(r);
		}
		return rows;
	}
	
	public List<String> timesToString() {
		ArrayList<String> times = new ArrayList<String>();
		for(Station s:sortedStations) {
			StringBuilder b = new StringBuilder();
			b.append(s.getName());
			b.append(" - \t");
			b.append(format(times1.get(s)));
			b.append(", \t");
			b.append(format(times2.get(s)));
			String er = errs.get(s);
			if(er.length()>0) {
				b.append("\t - ");
				b.append(er);
			}
			b.append("\n");
		}
		return times;
	}
	
	
	
	private class StationTimesComp implements Comparator<Station> {
		
		public int compare(Station s1, Station s2) {
			String 
				t11 = times1.get(s1),
				t12 = times2.get(s1),
				t21 = times1.get(s2),
				t22 = times2.get(s2);
			int c1 = compareTimes(t11, t21);
			if(c1!=0) return c1;
			
			int c2 = compareTimes(t12, t22);
			if(c2!=0) return c2;
			
			int c3 = errs.get(s1).compareTo(errs.get(s2)); 
			if(c3!=0) return c3;
			
			return s1.getName().compareTo(s2.getName());
		}
		
		private int compareTimes(String t1, String t2) {
			// s1 < s2
			// ret < 0
			boolean 
				t1arr = t1.contains(">>"),
				t2arr = t2.contains(">>");
			
			if(t1arr || t2arr) 
				return t1arr ? (t2arr ? 0 : -1) : 1;

			int min1=Integer.MAX_VALUE, min2=Integer.MAX_VALUE;
			try { min1 = Integer.parseInt(t1); } catch(NumberFormatException e) {}
			try { min2 = Integer.parseInt(t2); } catch(NumberFormatException e) {}
			
			if(min1!=min2) 
				return min1<min2 ? -1 : 1;
			
			// at this point either both are absolute times, or something else
			// string comparison will work in both cases

			return t1.compareTo(t2);
		}
	}
	
}
