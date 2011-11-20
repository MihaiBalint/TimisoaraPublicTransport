package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.model.*;

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
	
}
