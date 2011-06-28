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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Station;
import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.IMonitor;
import ro.mihai.util.NullMonitor;

import android.content.Context;
import android.content.res.Resources;

public class RATT {
	private static final String root = "http://www.ratt.ro/txt/";
	
	private static final String stationIdParamName = "id_statie"; 
	private static final String lineIdParamName = "id_traseu";
	
	private static final String stationList = "select_statie.php";
	
	// ?stationIdParamName=...
	private static final String linesInStationList = "select_traseu.php";
	
	// ?id_traseu=...&id_statie=...
	private static final String timesOflinesInStation = "afis_msg.php";
	
	private static final String cityCacheFileName = "citylines.txt"; 

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

	public static City loadFromAppResources(Context ctx) throws IOException {
		InputStream is = ctx.getResources().openRawResource(R.raw.citylines);
		City c = new City();
		c.loadFromFile(is, new NullMonitor());
		return c;
	}
	
	public static City loadStoredCityOrDownloadAndCache(Context ctx, IMonitor mon) throws IOException {
		City c = new City();
		try {
			// read the proper cache
			InputStream in = ctx.openFileInput(cityCacheFileName);
			c.loadFromFile(in,mon);
		} catch(FileNotFoundException e) {
			try {
				// read resources file
				InputStream in = ctx.getResources().openRawResource(R.raw.citylines);
				c.loadFromFile(in,mon);
			} catch(Resources.NotFoundException ex) {
				// download and parse new stuff
				c = downloadCity(mon);
			} finally {
				OutputStream os = ctx.openFileOutput(cityCacheFileName, Context.MODE_PRIVATE);
				c.saveToFile(os);
			} 
		} catch(SaveFileException e) {
			// the file must be saved again because it was in an older format
			OutputStream os = ctx.openFileOutput(cityCacheFileName, Context.MODE_PRIVATE);
			c.saveToFile(os);
		}
		return c;
	}
	
	/**
	 * Not for running on androids
	 * @return
	 * @throws IOException
	 */
	public static City loadCachedCityOrDownloadAndCache() throws IOException {
		City c;
		File cache = new File(cityCacheFileName);
		if(cache.isFile() && cache.exists() && cache.canRead()) {
			c = new City();
			c.loadFromFile(new FileInputStream(cache), new NullMonitor());
		} else {
			c = downloadCity(new NullMonitor());
			c.saveToFile(new FileOutputStream(cache));
		}
		
		return c;
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
	
	public static class StationReader extends OptValBuilder<Station> {
		public StationReader(FormattedTextReader in) { super(in); }
		public StationReader(URL url) throws IOException { super(url); }
		
		protected Station create(String val, String opt) { return new Station(val,opt); }
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
			Line l = c.getOrCreateLine(val, opt);
			l.addStation(st);
			st.addLine(l);
			return l; 
		}
	}
}
