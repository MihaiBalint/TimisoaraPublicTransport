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
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import ro.mihai.tpt.model.*;
import ro.mihai.util.*;

public class RATT {
	private static final String root = "http://www.ratt.ro/txt/";
	
	private static final String stationIdParamName = "id_statie"; 
	private static final String lineIdParamName = "id_traseu";
	
	private static final String stationList = "select_statie.php";
	
	// ?stationIdParamName=...
	private static final String linesInStationList = "select_traseu.php";
	
	// ?id_traseu=...&id_statie=...
	private static final String timesOflinesInStation = "afis_msg.php";
	
	public static List<Station> downloadStations(IMonitor mon) throws IOException {
		return new StationReader(new URL(root+stationList)).readAll(mon);
	}
	
	public static String[] downloadTimes(Line l, Station s) throws IOException {
		URL url = new URL(root+timesOflinesInStation+"?"+lineIdParamName+"="+l.getId()+"&"+stationIdParamName+"="+s.getId());
		FormattedTextReader rd = new FormattedTextReader(url.openStream());
		String lineName = rd.readString("Linia: ", "<br");
		assert(lineName.equals(l.getName()));
		String time1 = rd.readString("Sosire1: ", "<");
		String time2 = rd.readString("Sosire2: ", "<");
		rd.close();
		return new String[]{time1, time2};
	}
	
	public static City downloadCity(IMonitor mon) throws IOException {
		City c = new City();
		mon.setMax(1200);
		List<Station> stations = new StationReader(new URL(root+stationList)).readAll(mon);
		c.setStations(stations);
		int cnt = 0;
		for(Station s : stations) { 
			new LineReader(c,s, new URL(root+linesInStationList+"?"+stationIdParamName+"="+s.getId())).readAll(new NullMonitor());
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
		while(null != (stCoords = rd.readStationCoords())) {
			// coords = {name, id, lat,lng}
			Station s = c.getStation(stCoords[1]);
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
		public StationReader(FormattedTextReader in) { super(in); }
		public StationReader(URL url) throws IOException { super(url); }
		
		protected Station create(String val, String opt) {
			Station s = new Station(val,opt); 
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
			Line l = c.getOrCreateLine(val, opt, true);
			Path p = l.getFirstPath(); 
			p.concatenate(st);
			st.addLine(l);
			return l; 
		}
	}
}
