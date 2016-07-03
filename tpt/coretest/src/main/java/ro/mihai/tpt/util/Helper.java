package ro.mihai.tpt.util;

import java.util.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.StationsXMLReader;

import com.opencsv.CSVReader;

public class Helper {
	private HashMap<String,Set<String>> names = new HashMap<String, Set<String>>();
	private HashMap<String,Set<String>> simple = new HashMap<String, Set<String>>();
	private HashMap<String,Set<String>> junction = new HashMap<String, Set<String>>();
	private HashMap<String,Set<String>> junctionMap = new HashMap<String, Set<String>>();
	
	private HashMap<String,Set<Coords>> coords = new HashMap<String, Set<Coords>>();
	private HashMap<String,Set<Coords>> stXMLCoords = new HashMap<String, Set<Coords>>();

	public Helper(String fileName) throws IOException {
		InputStream inp = new FileInputStream(fileName);
		CSVReader rd = new CSVReader(new InputStreamReader(inp));
		String[] row;

		rd.readNext(); // ignore header row
		while(null!=(row=rd.readNext())) {
			if(row.length<3) continue;
			String id = row[2];
			
			String stName=null;
			if(row.length>4) {
				int pi = row[4].lastIndexOf('(');
				stName = pi>=0 ? row[4].substring(0, pi).trim() : row[4].trim();
				String stDir = pi>=0 ? row[4].substring(pi).trim() : "";
				add(names, id, stName);
			}
			if(row.length>5)
				add(simple, id, row[5]);
			if(row.length>6) {
				add(junction, id, row[6]);
				add(junctionMap, row[6], id+"-"+row[1]+"-"+row[4]);
			}
			if(row.length>8)
				add(coords, id, new Coords(row[7], row[8]));
		}
		rd.close();
		
		StationsXMLReader crd = new StationsXMLReader(new FormattedTextReader(new FileInputStream("coretest/stations.xml")));
		while(null!=(row=crd.readStationCoords())) 
			add(stXMLCoords,row[1],new Coords(row[2],row[3]));
		crd.close();
	}
	
	public Set<String> getNiceName(String id) { return names.get(id); }
	public Set<String> getShort(String id) { return simple.get(id); }
	public Set<String> getJunction(String id) { return junction.get(id); }
	public Set<String> getJunctionStations(String jid) { return junctionMap.get(jid); }
	
	public Set<Coords> getCoords(String id) { return coords.get(id); }
	public Set<Coords> getXMLCoords(String id) { 
		Set<Coords> xml = stXMLCoords.get(id);
		return xml != null ? xml : new HashSet<Coords>();
	}
	
	private static <K, T> void add(Map<K,Set<T>> map, K key, T val) {
		Set<T> n = map.get(key);
		if(null==n) {
			n = new LinkedHashSet<T>();
			map.put(key, n);
		}
		n.add(val);
	}
	
	public String toString() {
		String result = "";
		for(Map.Entry<String, Set<String>> e : names.entrySet()) {
			if (e.getValue().size() > 1) {
				result += e.getKey() + " - "+e.getValue()+"\n";
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException {
		Helper h = new Helper("coretest/linestations-20111127.csv");
		System.out.println(h);
	}
	
	public static class Coords {
		private String lat, lng;
		public Coords(String lat, String lng) {
			this.lat = lat;
			this.lng = lng;
		}
		
		public String getLat() {
			return lat;
		}
		public String getLng() {
			return lng;
		}
		
		@Override
		public String toString() {
			return "["+lat+", "+lng+"]";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((lat == null) ? 0 : lat.hashCode());
			result = prime * result + ((lng == null) ? 0 : lng.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Coords other = (Coords) obj;
			if (lat == null) {
				if (other.lat != null)
					return false;
			} else if (!lat.equals(other.lat))
				return false;
			if (lng == null) {
				if (other.lng != null)
					return false;
			} else if (!lng.equals(other.lng))
				return false;
			return true;
		}
		
	}
}
