package ro.mihai.tpt.test;

import java.util.ArrayList;
import java.util.List;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.NamedEntityCollection;
import ro.mihai.util.NullMonitor;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This file is mostly generated. It's sole purpose is to identify changes 
 * within the RATT web site: line/station renaming or changing IDs or adding new/removing old lines/stations.
 * @author mihai
 *
 */
public class LineRegressions extends TestCase {
	private City cActual = null, cExpected = null;
	private String lineName;
	
	public LineRegressions(String testMethod, City cActual, City cExpected, String lineName) {
		super(testMethod);
		this.cActual = cActual;
		this.cExpected = cExpected;
		this.lineName = lineName;
	}
	
	public void testLine_StationCount() {
		Line expected = cExpected.getLine(lineName); 
		Line actual = cActual.getLine(lineName);
		
		assertEquals(
			lineName +" - "+ expected.getStations().size(), 
			lineName +" - "+ actual.getStations().size()
		);
	}
	
	public void testLine_StationNames() {
		Line expected = cExpected.getLine(lineName); 
		Line actual = cActual.getLine(lineName);
		
		assertEquals(
			lineName +" - "+ NamedEntityCollection.sortedNames(expected.getStations()),
			lineName +" - "+ NamedEntityCollection.sortedNames(actual.getStations())
		);
	}
	
	public void testLine_StationIDs() {
		Line expected = cExpected.getLine(lineName); 
		Line actual = cActual.getLine(lineName);
		
		assertEquals(
			 lineName +" - "+ NamedEntityCollection.sortedIDs(expected.getStations()),
			 lineName +" - "+ NamedEntityCollection.sortedIDs(actual.getStations())
		);
	}

	
	public static void addTests(TestSuite suite, City cActual, City cExpected) {
		for(Line l : cExpected.getLines()) {
			TestSuite s = new TestSuite("Line "+l.getName());
			for(String t : RegressionTests.testMethods(LineRegressions.class))
				s.addTest(new LineRegressions(t, cActual, cExpected, l.getName()));
			suite.addTest(s);
		}
	}
}
