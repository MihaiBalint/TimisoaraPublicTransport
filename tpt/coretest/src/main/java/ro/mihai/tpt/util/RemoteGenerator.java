package ro.mihai.tpt.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.HourlyPlan;
import ro.mihai.tpt.model.Junction;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;
import ro.mihai.util.DetachableStream;
import ro.mihai.util.NullMonitor;
import junit.framework.TestCase;

public class RemoteGenerator extends TestCase {
	private String url = "https://aeliptus.com/rest/v1/";
	
	private JSONObject getJSON(String relativeURL) throws IOException {
		URL u = new URL(url+relativeURL);
		URLConnection con = u.openConnection();
		
		con.setDoOutput(false);
		con.setAllowUserInteraction(false);
        
		StringBuilder builder = new StringBuilder();		
		BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String responseLine;
        while ((responseLine = br.readLine()) != null) 
        	builder.append(responseLine);
        br.close();

		return new JSONObject(builder.toString());
	}
	
	public ArrayList<Station> getStations(City c) throws IOException {
		JSONObject json = getJSON("stops?stop_id&stop_extid&name&short_name&lat&lng");
		assertEquals("success", json.get("status"));
		JSONArray stationsJSON = json.getJSONArray("stops");
		ArrayList<Station> stations = new ArrayList<Station>(stationsJSON.length());
		
		for (int i=0;i<stationsJSON.length();i++) {
			JSONObject station = stationsJSON.getJSONObject(i);
			int stationId = station.getInt("stop_id");
			String stationName=station.getString("name").trim();
			Station st = c.newStation(stationId, station.getString("stop_extid").trim(), stationName);
			st.setNiceName(stationName);
			st.setShortName(station.getString("short_name").trim());
			st.setCoords(station.getString("lat").trim(), station.getString("lng").trim());
			stations.set(stationId, st);
		}
		return stations;
	}
	
	public ArrayList<Junction> getJunctions(City c) throws IOException {
		JSONObject json = getJSON("junctions?junction_id&name&short_name&lat&lng");
		assertEquals("success", json.get("status"));
		JSONArray junctionsJSON = json.getJSONArray("junctions");
		ArrayList<Junction> junctions = new ArrayList<Junction>(junctionsJSON.length());
		for (int i=0;i<junctionsJSON.length();i++) {
			JSONObject junction = junctionsJSON.getJSONObject(i);
			int junctionId = junction.getInt("junction_id");
			junctions.set(junctionId, c.newJunction(junctionId, junction.getString("name").trim()));
		}
		return junctions;
	}
	
	public void linkJunctionStations(ArrayList<Junction> junctions, ArrayList<Station> stations) throws IOException {
		for (Junction junction : junctions) {
			JSONObject json = getJSON("junctions/"+junction.getId()+"/stops?stop_id");
			assertEquals("success", json.get("status"));
			JSONArray junctionStationsJSON = json.getJSONArray("junctions");
			for (int i=0;i<junctionStationsJSON.length();i++) {
				JSONObject junctionStation = junctionStationsJSON.getJSONObject(i);
				Station st = stations.get(junctionStation.getInt("stop_od"));
				st.setJunction(junction);
				junction.addStation(st);
				
			}
		}
	}
	
	public void testGenerator() throws Exception {
		City c = new City();
		
		ArrayList<Station> stMap = getStations(c);
		ArrayList<Junction> jMap = getJunctions(c);
		linkJunctionStations(jMap, stMap);
		
		// SAVE
		String fileName = "citylines.dat";
		FileOutputStream fos = new FileOutputStream(fileName); 
		c.saveToFile(fos);
		fos.close();
		
		// LOAD
		City c1 = new City();
		DetachableStream fis = new DetachableStream.FromFile(fileName);
		c1.loadFromStream(fis, new NullMonitor(), RATT.CITY_DB_ENTRIES);
		
		// TODO assertEquals(c1, c)  the contents anyway, see V3Generator
		
		// stream must stay open to allow for lazy stuff to load
		fis.close();
		
	}

}
