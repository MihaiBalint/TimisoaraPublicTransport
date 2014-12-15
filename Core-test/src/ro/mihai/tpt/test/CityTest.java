package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.model.*;
import ro.mihai.tpt.util.TestPrefs;
import ro.mihai.util.LineKind;

public class CityTest {
	private City c;
	private List<String> app_lines;
	private HashMap<String, Station> stationExtIdMap;

	@Before
	public void setUp() throws IOException {
		c = JavaCityLoader.loadCachedCityOrDownloadAndCache(new TestPrefs());

		app_lines = new ArrayList<String>();
		for(LineKind kind : LineKind.values())
			app_lines.addAll(Arrays.asList(kind.getLineNames()));
		
		stationExtIdMap = new HashMap<String, Station>();
		for(Station s:c.getStations())
			stationExtIdMap.put(s.getExtId(), s);
	}
	
	private Station getStationByExtId(String extId) {
		return stationExtIdMap.get(extId);
	}
	
	@Test
	public void test_city_has_all_kinds() {
		String missingLines = "";
		for(String line : app_lines)
			if (c.getLineByName(line) == null || c.getLineByName(line).isFake())
				missingLines += "City does not have line "+line+"\n"; 
		assertEquals("", missingLines);
	}

	@Test
	public void test_app_bus_panel_city_coverage() {
		String hiddenLines = "";
		for(Line l : c.getLines())
			if (!app_lines.contains(l.getName()))
				hiddenLines += "App does not show line "+l.getName()+"\n"; 
		assertEquals("", hiddenLines);
	}
	
	private static List<Station> stations(List<Estimate> estimates) {
		List<Station> st = new ArrayList<Station>();
		for (Estimate est : estimates) {
			st.add(est.getStation());
		}
		return st;
	}
	
	@Test
	public void test_line_33_arta_connections() {
		Line l33 = c.getLineByName("33");
		Station a1 = getStationByExtId("2690");
		Station a2 = getStationByExtId("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertTrue(l33.getStations().contains(a1));
		assertTrue(l33.getStations().contains(a2));
		
		Path p1 = l33.getPath("Catedrala");
		assertTrue(stations(p1.getEstimatesByPath()).contains(a1));

		Path p2 = l33.getPath("Pod C. Sagului");
		assertTrue(stations(p2.getEstimatesByPath()).contains(a2));
	}

	@Test
	public void test_line_E1_arta_connections() {
		Line lE1 = c.getLineByName("E1");
		Station a1 = getStationByExtId("2690");
		Station a2 = getStationByExtId("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertTrue(lE1.getStations().contains(a1));
		assertTrue(lE1.getStations().contains(a2));
		
		Path p1 = lE1.getPath("Selgros");
		assertTrue(stations(p1.getEstimatesByPath()).contains(a1));

		Path p2 = lE1.getPath("Pod C. Sagului");
		assertTrue(stations(p2.getEstimatesByPath()).contains(a2));
	}

	@Test
	public void test_arta_connections() {
		Station a1 = getStationByExtId("2690");
		Station a2 = getStationByExtId("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertEquals("33, 33b, E1, E33", a1.getLineNames());
		assertEquals("33, 33b, E1, E33", a2.getLineNames());
	}
	
	@Test
	public void test_empty_junction() {
		for (Junction j:c.getJunctions())
			assertNotNull("Junction name should not be null", j.getName());
		
		for (Junction j:c.getJunctions())
			assertTrue("Junction should contain at least one station ("+j.getName()+")", j.getStations().size() > 0);

		for (Junction j:c.getJunctions())
			assertTrue("Junction name should not be empty ("+j.getStations()+")", j.getName().length()>0);
	}

	@Test
	public void test_all_stations_have_junction() {
		for (Station s:c.getStations())
			assertNotNull("Junction name should not be null", s.getJunction());
	}
	
	private void assertKind(LineKind kind) {
		for(String lineName : kind.getLineNames())
			assertEquals(c.getLineByName(lineName).getKind(), kind);
		
		for(Line l : c.getLines()) 
			if (!kind.contains(l.getName()))
				assertTrue(l.getName()+" is not a "+kind, l.getKind() != kind);
	}
	private void assertOthersNotKind(LineKind kind) {
		for(Line l : c.getLines()) 
			if (!kind.contains(l.getName()))
				assertTrue(l.getName()+" is not a "+kind, l.getKind() != kind);
	}

	@Test
	public void test_Tv9() {
		Line tv9 = c.getLineByName("Tv9");
		assertTrue(tv9.getKind().isTram());
		assertTrue(tv9.getPaths().size()>0);
		for(Path p : tv9.getPaths())
			assertTrue(p.getEstimatesByPath().size()>0);
		assertEquals(2,tv9.getPaths().size());
	}
	
	@Test
	public void test_all_line_types() {
		assertKind(LineKind.TRAM);
		assertOthersNotKind(LineKind.TRAM);

		assertKind(LineKind.TROLLEY);
		assertOthersNotKind(LineKind.TROLLEY);

		assertKind(LineKind.EXPRESS);
		assertOthersNotKind(LineKind.EXPRESS);

		assertKind(LineKind.METRO);
		assertOthersNotKind(LineKind.METRO);

		assertKind(LineKind.BUS);
		assertOthersNotKind(LineKind.BUS);
	}

	@Test
	public void test_city_has_unknown_kind() {
		String missingKinds = "";
		List<String> knownKinds = new ArrayList<String>();
		for(LineKind k : LineKind.values())
			for(String line : k.getLineNames())
				knownKinds.add(line);
		for(Line l : c.getLines())
			if (!knownKinds.contains(l.getName()))
				missingKinds += "City has line of unknown kind: "+l.getName()+"\n";
		assertEquals("", missingKinds);
	}
	
	@Test
	public void test_missing_coords() {
		String errors = "";
		for(Station s:c.getStations()) {
			if(s.getLat().trim().isEmpty() || s.getLng().trim().isEmpty()) 
				errors += s.getId()+" "+s.getNiceName()+" Lat:"+s.getLat()+" Lng:"+s.getLng()+"\n";
		}
		assertEquals("Following stations are missing coords:", "", errors);
	}

	@Test
	public void test_coords_usability() {
		String errors = "";
		for(Station s:c.getStations()) {
			try {
				// don't care for missing coords
				if(s.getLat().trim().isEmpty() || s.getLng().trim().isEmpty()) continue;
				Double.parseDouble(s.getLat());
				Double.parseDouble(s.getLng());
			} catch(NumberFormatException e) {
				errors += s.getId()+" "+s.getNiceName()+" Lat:"+s.getLat()+" Lng:"+s.getLng()+"\n";
			}
		}
		assertEquals("Following stations have unparsable coords:", "", errors);
	}
	
	public void assertLineInStation(String stationName, String lineName) {
		HashSet<Line> lines = new HashSet<Line>();
		Station st = getStationByExtId(stationName);
		for(Path p:st.getPaths())
				lines.add(p.getLine());
		assertTrue(lines.contains(c.getLineByName(lineName)));
	}

	@Test
	public void test_known_distance() {
		Station s33 = c.getLineByName("33").getPath("Pod C. Sagului").getEstimatesByPath().get(0).getStation();
		
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("4640")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("3105")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("3102")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("3163")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("2799")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("5300")));
		assertTrue(s33.getJunction().getStations().contains(getStationByExtId("3200")));
		assertEquals(7,s33.getJunction().getStations().size());

		assertLineInStation("4640", "Tv1");
		assertLineInStation("4640", "Tv2");
		assertLineInStation("4640", "Tv5");
		assertLineInStation("3163", "Tv1");
		assertLineInStation("3163", "Tv2");
		assertLineInStation("3163", "Tv6");
		assertTrue(getStationByExtId("4640").distanceTo(getStationByExtId("3163")) < 40); // 33
		
		assertLineInStation("2799", "33");
		assertLineInStation("5300", "E3");		
		assertTrue(getStationByExtId("2799").distanceTo(getStationByExtId("5300")) < 230); // 221
		
		assertLineInStation("2799", "33");
		assertLineInStation("3200", "E3");
		assertTrue(getStationByExtId("2799").distanceTo(getStationByExtId("3200")) < 10); // 4
		
		assertLineInStation("5300", "E3");
		assertLineInStation("3200", "E3");
		assertTrue(getStationByExtId("5300").distanceTo(getStationByExtId("3200")) < 230); // 224

		assertLineInStation("4640", "Tv1");
		assertLineInStation("2799", "33");
		assertTrue(getStationByExtId("4640").distanceTo(getStationByExtId("2799")) < 120); // 112
	}
	
	@Test
	public void test_known_distance_Arta_textila() {
		Station s33 = c.getLineByName("33").getPath("Pod C. Sagului").getEstimatesByPath().get(4).getStation();
		for(Station s : s33.getJunction().getStations()) {
			System.out.println(""+s.getId()+":"+s.getNiceName());
		}
	}
	

	public static void printDistance(Station a, Station b) {
		System.out.println("Distance: "+(long)a.distanceTo(b) + "m");
		System.out.println("    "+a.getId()+":"+a.getNiceName()+" - "+a.getLat()+"-"+a.getLng());
		for(Path p:a.getPaths())
			System.out.println("\t"+p.getId()+":"+p.getLineName());
		System.out.println("    "+b.getId()+":"+b.getNiceName()+" - "+b.getLat()+"-"+b.getLng());
		for(Path p:b.getPaths())
			System.out.println("\t"+p.getId()+":"+p.getLineName());	
	}
 	
	@Test
	public void test_junction_stations_too_far() {
		Set<String> checked = new HashSet<String>();
		double dsum=0, dcount=0;
		String e500="", e1000="", ep1000="";
		for(Junction j:c.getJunctions()) 
			for(Station a:j.getStations()) {
				if(a.getLat().trim().isEmpty() || a.getLng().trim().isEmpty()) continue;				
				for(Station b:j.getStations()) {
					if(b.getLat().trim().isEmpty() || b.getLng().trim().isEmpty()) continue;
					if(a==b) continue;
					String pair = a.getId()+"-"+b.getId();
					if (checked.contains(pair)) continue;
					
					checked.add(pair);
					checked.add(b.getId()+"-"+a.getId());
					double d = a.distanceTo(b);
					dsum+=d;
					dcount+=1;
					String err = "In "+j.getName()+", "+
							a.getId()+":"+a.getNiceName() +" and "+
							b.getId()+":"+b.getNiceName() +" are "+(long)d+"m appart\n";
					if(d > 1000) 
						ep1000 += err;
					else if(d > 500)
						e1000 += err;
					else if(d > 200)
						e500 += err;
				}
			}
		System.out.println("Average dist between stations is: "+(dsum/dcount));
		assertEquals("Following junctions have stations that are too far appart:", "", ep1000+e1000+e500);
	}

	@Test
	public void test_junction_stations_too_near() {
		Set<String> checked = new HashSet<String>();
		String e225="";		
		String e300="";		
		for(Station a:c.getStations()) 
			for(Station b:c.getStations()) {
				if (a==b) continue;
				if (a.getJunction().getStations().contains(b)) continue;
				String pair = a.getId()+"-"+b.getId();
				if (checked.contains(pair)) continue;
				
				checked.add(pair);
				checked.add(b.getId()+"-"+a.getId());
				int d = a.distanceTo(b);
				String err = "Stations should be in junction("+(long)d+"): "+
						a.getId()+":"+a.getNiceName() +":"+ a.getJunctionName()+", "+
						b.getId()+":"+b.getNiceName() +":"+ b.getJunctionName()+"\n";
				if (d<0)
					continue;
				else if(d < 225) 
					e225 += err;
				else if (d<300)
					e300 += err;
			}
		assertEquals("Following stations should be connected by a junction:", "", e225+e300);
	}
}
