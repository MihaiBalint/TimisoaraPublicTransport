package ro.mihai.tpt.util;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	public void testGenerator() throws Exception {
		City c = new City();
		
		Map<String, Station> stMap = new HashMap<String, Station>();
		Map<String, Junction> jMap = new HashMap<String, Junction>();
		
		String 
			stationId=null, stationName=null, // TODO get via API call
			stationNiceName=null, stationShortName=null,
			stationLat=null, stationLng=null; 
		Station st = new Station(stationId.trim(), stationName.trim());
		st.setNiceName(stationNiceName.trim());
		st.setShortName(stationShortName.trim());
		st.setCoords(stationLat.trim(), stationLng.trim());
		
		String junctionName=null; // TODO get via API call
		Junction j = new Junction(junctionName, c); 
		st.setJunction(j);
		j.addStation(st);
		
		String 
			lineName=null, // TODO get via API call
			pathName=null, pathNiceName=null, pathExtId=null;
		Line l = c.getOrCreateLine(lineName.trim());
		Path p = l.getPath(pathName);
		if (null==p) {
			p = c.newPath(l, pathExtId.trim(), pathName);
			p.setNiceName(pathNiceName.trim());
			l.addPath(p);
		}
		p.concatenate(st, new HourlyPlan());
		st.addPath(p);

		c.setStations(new ArrayList<Station>(stMap.values()));
		c.setJunctions(new ArrayList<Junction>(jMap.values()));

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
