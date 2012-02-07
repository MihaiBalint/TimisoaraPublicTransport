package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.model.*;
import ro.mihai.util.LineKind;
import sun.security.util.DisabledAlgorithmConstraints;

public class CityTest {
	private City c;

	@Before
	public void setUp() throws IOException {
		c = JavaCityLoader.loadCachedCityOrDownloadAndCache();
	}
	
	@Test
	public void test_line_33_arta_connections() {
		Line l33 = c.getLine("33");
		Station a1 = c.getStation("2690");
		Station a2 = c.getStation("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertTrue(l33.getStations().contains(a1));
		assertTrue(l33.getStations().contains(a2));
		
		Path p1 = l33.getPath("Catedrala");
		assertTrue(p1.getStationsByPath().contains(a1));

		Path p2 = l33.getPath("Real");
		assertTrue(p2.getStationsByPath().contains(a2));
	}

	@Test
	public void test_line_E1_arta_connections() {
		Line lE1 = c.getLine("E1");
		Station a1 = c.getStation("2690");
		Station a2 = c.getStation("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertTrue(lE1.getStations().contains(a1));
		assertTrue(lE1.getStations().contains(a2));
		
		Path p1 = lE1.getPath("Selgros");
		assertTrue(p1.getStationsByPath().contains(a1));

		Path p2 = lE1.getPath("Pod C. Sagului");
		assertTrue(p2.getStationsByPath().contains(a2));
	}

	@Test
	public void test_arta_connections() {
		Station a1 = c.getStation("2690");
		Station a2 = c.getStation("2669");
		
		assertEquals("Arta Textila", a1.getNicestNamePossible().trim());
		assertEquals("Arta Textila", a2.getNicestNamePossible().trim());
		
		assertEquals(4, a1.getLines().size());
		assertEquals(4, a2.getLines().size());
	}
	
	@Test
	public void test_empty_junction() {
		for (Junction j:c.getJunctions())
			assertNotNull("Junction name should not be null", j.getName());
		
		for (Junction j:c.getJunctions())
			assertTrue("Junction name should not be empty", j.getName().length()>0);

		for (Junction j:c.getJunctions())
			assertTrue("Junction should contain at least one station", j.getStations().size() > 0);
	}

	@Test
	public void test_all_stations_have_junction() {
		for (Station s:c.getStations())
			assertNotNull("Junction name should not be null", s.getJunction());
	}
	
	private void assertOthersNotKind(LineKind kind) {
		for(Line l : c.getLines()) 
			if (!kind.contains(l.getName()))
				assertTrue(l.getKind() != kind);
	}
	
	@Test
	public void test_all_trams_types() {
		assertTrue(c.getLine("Tv1").getKind().isTram());
		assertTrue(c.getLine("Tv2").getKind().isTram());
		assertTrue(c.getLine("Tv4").getKind().isTram());
		assertTrue(c.getLine("Tv5").getKind().isTram());
		assertTrue(c.getLine("Tv6").getKind().isTram());
		assertTrue(c.getLine("Tv7a").getKind().isTram());
		assertTrue(c.getLine("Tv7b").getKind().isTram());
		assertTrue(c.getLine("Tv8").getKind().isTram());
		assertTrue(c.getLine("Tv9").getKind().isTram());
		assertOthersNotKind(LineKind.TRAM);
	}

	@Test
	public void test_all_trolley_types() {
		assertTrue(c.getLine("Tb11").getKind().isTrolley());
		assertTrue(c.getLine("Tb14").getKind().isTrolley());
		assertTrue(c.getLine("Tb15").getKind().isTrolley());
		assertTrue(c.getLine("Tb16").getKind().isTrolley());
		assertTrue(c.getLine("Tb17").getKind().isTrolley());
		assertTrue(c.getLine("Tb18").getKind().isTrolley());
		assertTrue(c.getLine("Tb19").getKind().isTrolley());
		assertOthersNotKind(LineKind.TROLLEY);
	}
	
	@Test
	public void test_all_express_types() {
		assertTrue(c.getLine("E1").getKind().isBusExpress());
		assertTrue(c.getLine("E2").getKind().isBusExpress());
		assertTrue(c.getLine("E3").getKind().isBusExpress());
		assertTrue(c.getLine("E4").getKind().isBusExpress());
		assertTrue(c.getLine("E5").getKind().isBusExpress());
		assertTrue(c.getLine("E6").getKind().isBusExpress());
		assertTrue(c.getLine("E7").getKind().isBusExpress());
		assertTrue(c.getLine("E7b").getKind().isBusExpress());
		assertTrue(c.getLine("E8").getKind().isBusExpress());
		assertOthersNotKind(LineKind.EXPRESS);
	}

	@Test
	public void test_all_metro_types() {
		assertTrue(c.getLine("M30").getKind().isBusMetro());
		assertTrue(c.getLine("M35").getKind().isBusMetro());
		assertTrue(c.getLine("M36").getKind().isBusMetro());
		assertOthersNotKind(LineKind.METRO);
	}

	@Test
	public void test_all_city_bus_types() {
		assertTrue(c.getLine("3").getKind().isBus());
		assertTrue(c.getLine("13").getKind().isBus());
		assertTrue(c.getLine("21").getKind().isBus());
		assertTrue(c.getLine("28").getKind().isBus());
		assertTrue(c.getLine("32").getKind().isBus());
		assertTrue(c.getLine("33").getKind().isBus());
		assertTrue(c.getLine("40").getKind().isBus());
		assertTrue(c.getLine("46").getKind().isBus());
		assertOthersNotKind(LineKind.BUS);
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

	@Test
	public void test_known_distance() {
		Station s33 = c.getLine("33").getPath("Real").getStationsByPath().get(0);
		for(Station s:s33.getJunction().getStations()) {
			System.out.println(s.getId()+":"+s.getNiceName()+" - "+s.getLat()+"-"+s.getLng());
			for(Line l:s.getLines())
				System.out.println("\t"+l.getId()+":"+l.getName());
				
		}
		printDistance(c.getStation("4640"), c.getStation("3163"));
		printDistance(c.getStation("2799"), c.getStation("5300"));
		printDistance(c.getStation("2799"), c.getStation("3200"));
		printDistance(c.getStation("5300"), c.getStation("3200"));

		printDistance(c.getStation("4640"), c.getStation("2799"));
		assertEquals(3,s33.getJunction().getStations().size());
	}

	public static void printDistance(Station a, Station b) {
		System.out.println("Distance: "+(long)a.distanceTo(b) + "m");
		System.out.println("    "+a.getId()+":"+a.getNiceName()+" - "+a.getLat()+"-"+a.getLng());
		for(Line l:a.getLines())
			System.out.println("\t"+l.getId()+":"+l.getName());
		System.out.println("    "+b.getId()+":"+b.getNiceName()+" - "+b.getLat()+"-"+b.getLng());
		for(Line l:b.getLines())
			System.out.println("\t"+l.getId()+":"+l.getName());
		
	}
 	
	@Test
	public void test_junction_station_coords() {
		double dsum=0, dcount=0;
		String e500="", e1000="", ep1000="";
		for(Junction j:c.getJunctions()) 
			for(Station a:j.getStations()) {
				if(a.getLat().trim().isEmpty() || a.getLng().trim().isEmpty()) continue;				
				for(Station b:j.getStations()) {
					if(b.getLat().trim().isEmpty() || b.getLng().trim().isEmpty()) continue;
					if(a==b) continue;
					
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
}
