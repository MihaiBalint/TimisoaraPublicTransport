/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011-2014  Mihai Balint

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
import java.io.InputStream;
import java.net.*;
import java.util.*;

import ro.mihai.tpt.model.*;
import ro.mihai.util.*;

public class RATT {
	public static final int CITY_DB_ENTRIES = 823;
	public static final String root = "http://www.ratt.ro/txt/";
	
	public static List<Station> downloadStations(IPrefs prefs, IMonitor mon, City c) throws IOException {
		return new StationReader(new URL(prefs.getBaseUrl()+"select_statie.php"), c).readAll(mon);
	}

	public static String[] downloadTimes(IPrefs prefs, String pathId, String stationId) throws IOException {
		URL url = new URL(prefs.getBaseUrl()+"afis_msg.php?id_traseu="+pathId+"&id_statie="+stationId);
		FormattedTextReader rd = new FormattedTextReader(url.openStream());
		String lineName = rd.readString("Linia: </font>", "<");
		String timestamp = rd.readString("<br><br>", "<");
		String time1 = rd.readString("Sosire1: ", "<");
		String time2 = rd.readString("Sosire2: ", "<");
		rd.close();
		return new String[]{time1, time2, timestamp, lineName};
	}
	
	public static City downloadCity(IPrefs prefs, IMonitor mon) throws IOException {
		City c = new City();
		mon.setMax(1200);
		List<Station> stations = new StationReader(new URL(prefs.getBaseUrl()+"select_statie.php"), c).readAll(mon);
		int cnt = 0;
		for(Station s : stations) { 
			new LineReader(c,s, new URL(prefs.getBaseUrl()+"select_traseu.php?id_statie="+s.getId())).readAll(new NullMonitor());
			cnt++;
			if((cnt % 10)==0)
				System.out.println(cnt + "/" + stations.size());
			mon.workComplete();
		}
		
		return c;
	}
	
	public static void addLatLongCoords(City c, InputStream in) throws IOException {
		StationsXMLReader rd = new StationsXMLReader(new FormattedTextReader(in));
		String[] stCoords;
		
		HashMap<String, Station> stationExtIdMap = new HashMap<String, Station>();
		for(Station s:c.getStations())
			stationExtIdMap.put(s.getExtId(), s);
		
		while(null != (stCoords = rd.readStationCoords())) {
			// coords = {name, id, lat,lng}
			Station s = stationExtIdMap.get(stCoords[1]);
			if(s==null) {
				System.out.println(stCoords[1]+" - "+stCoords[0]+": "+stCoords[2]+"x"+stCoords[3]);
				continue;
			}
			assert(s!=null);
			if(!s.getName().trim().equals(stCoords[0].trim())) {
				System.out.println("Name Diff: "+stCoords[1]+" - "+stCoords[0]+": "+s.getId()+" - "+s.getName());
			}
			s.setCoords(stCoords[2], stCoords[3]);
		}
		rd.close();
	}
	
	public static class StationReader extends OptValBuilder<Station> {
		private City c; 
		public StationReader(FormattedTextReader in, City c) { super(in); this.c = c; }
		public StationReader(URL url, City c) throws IOException { super(url); this.c = c; }
		
		protected Station create(String val, String opt) {
			Station s = c.newStation(val, opt); 
			return s; 
		}
	}

	public static class LineReader extends OptValBuilder<Line> {
		private Station st;
		private City c;
		public LineReader(City c, Station st, URL url) throws IOException { 
			super(url);
			this.st = st;
			this.c = c;
		}
		public LineReader(City c, Station st, FormattedTextReader in) throws IOException { 
			super(in);
			this.st = st;
			this.c = c;
		}
		
		protected Line create(String val, String opt) {
			if (opt.startsWith(" [0]  ") || opt.startsWith(" [1]  "))
				opt = opt.substring(6);
			Line l = c.getLineByName(opt);
			if (null==l)
				l = c.newLine(opt);
			Path p;
			if (l.getPaths().isEmpty()) {
				p = c.newPath(l, val, "");
				l.addPath(p);
			} else
				p = l.getFirstPath();
			p.concatenate(st, new HourlyPlan());
			st.addPath(p);
			return l; 
		}
	}

	public static class Est {
		public String lineName, pathFrom, pathTo, stopCrypticName, stopEst;
		public int stopNo;
		
		public Est(String lineName, String pathFrom, String pathTo, String stopCrypticName, int stopNo, String stopEst) {
			this.lineName = lineName;
			this.pathFrom = pathFrom;
			this.pathTo = pathTo;
			this.stopCrypticName = stopCrypticName;
			this.stopNo = stopNo;
			this.stopEst = stopEst;
		}
	}
	public static class EstimateIterator implements Enumeration<Est> {
		private FormattedTextReader rd;
		private String lineName, pathFrom, pathTo;
		private int stopNo;
		private Est currentStop;
		
		public EstimateIterator(FormattedTextReader rd, String lineName) {
			this.rd = rd;
			this.lineName = lineName;
			this.currentStop = null;
			nextStop();
		}

		@Override
		public boolean hasMoreElements() {
			return currentStop != null;
		}

		@Override
		public Est nextElement() {
			Est stop = currentStop;
			nextStop();
			return stop;
		}
		
		private boolean nextPath() {
			try {
				String pathFrom = rd.readString("<b>", "--->");
				String pathTo = rd.readString("--->", "</b></td>");
				if (pathFrom == null || pathTo == null) 
					return false;
				this.pathFrom = pathFrom.trim();
				this.pathTo = pathTo.trim();
				return true;
			} catch (IOException e) {
				return false;
			}
		}
		
		private boolean nextStop() {
			try {
				if (currentStop == null || currentStop.stopCrypticName.equalsIgnoreCase(pathTo)) {
					stopNo = 0;
					if (!nextPath()) 
						throw new IOException();
				}
				if (!rd.skipAfter("<b>"+lineName+"</b></td>", true))
					throw new IOException("File format error");
				String stopCrypticName = rd.readString("<b>", "</b></td>");
				String stopEst = rd.readString("<b>", "</b></td>");
				if (stopCrypticName == null || stopEst == null)
					throw new IOException("File format error");
				currentStop = new Est(lineName, pathFrom, pathTo, stopCrypticName.trim(), stopNo, stopEst.trim());
				stopNo += 1;
				return true;
			} catch (IOException e) {
				this.currentStop = null;
				return false;
			}
		}
		
	}
	
	public static Enumeration<Est> downloadTimes2(IPrefs prefs, String lineName, String pathId) throws IOException {
		URL url = new URL("http://86.122.170.105:61978/html/timpi/sens0.php?param1="+pathId);
		FormattedTextReader rd = new FormattedTextReader(url.openStream());
		if (!rd.skipAfter("<table", true))
			throw new IOException("'File format error");
		return new EstimateIterator(rd, lineName);
	}
	
	
	public static String[] downloadTimes2(IPrefs prefs, String lineName, String pathId, String stationId) throws IOException {
		URL url = new URL("http://86.122.170.105:61978/html/timpi/sens0.php?param1="+pathId);
		FormattedTextReader rd = new FormattedTextReader(url.openStream());
		if (!rd.skipAfter("<table", true))
			throw new IOException("'File format error");
		Enumeration<Est> est = new EstimateIterator(rd, lineName);
		while(est.hasMoreElements()) {
			Est e = est.nextElement();
			if (stationId.equals(e.stopCrypticName))
				return new String[]{e.stopEst, "", "", lineName};
		}
		return new String[]{""};
	}
	
}
