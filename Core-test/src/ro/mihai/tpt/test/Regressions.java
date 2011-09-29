package ro.mihai.tpt.test;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.NamedEntityCollection;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This file is mostly generated. It's sole purpose is to identify changes 
 * within the RATT web site: line/station renaming or changing IDs or adding new/removing old lines/stations.
 * @author mihai
 *
 */
public class Regressions extends TestCase {
	private City cActual = null, cExpected = null;
	
	public Regressions(String testMethod, City cActual, City cExpected) {
		super(testMethod);
		this.cActual = cActual;
		this.cExpected = cExpected;
	}
	
	public void testLineCount() {
		assertEquals(cExpected.getLines().size(), cActual.getLines().size());
	}
	public void testLineNames() {
		assertEquals(
			NamedEntityCollection.sortedNames(cExpected.getLines()),
			NamedEntityCollection.sortedNames(cActual.getLines())
		);
	}
	public void testLineIDs() {
		assertEquals(
			NamedEntityCollection.sortedIDs(cExpected.getLines()),
			NamedEntityCollection.sortedIDs(cActual.getLines())
		);
	}

	
	public void testStationCount() {
		assertEquals(cExpected.getStations().size(), cActual.getStations().size());
	}
	public void testStationNames() {
		assertEquals(
			NamedEntityCollection.sortedNames(cExpected.getStations()),
			NamedEntityCollection.sortedNames(cActual.getStations())
		);
	}
	public void testStationIDs() {
		assertEquals(
			NamedEntityCollection.sortedIDs(cExpected.getStations()),
			NamedEntityCollection.sortedIDs(cActual.getStations())
		);
	}
	
	
	public static void addTests(TestSuite suite, City cActual, City cExpected) {
		for(String t : RegressionTests.testMethods(Regressions.class))
			suite.addTest(new Regressions(t, cActual, cExpected));
	}
	
}
