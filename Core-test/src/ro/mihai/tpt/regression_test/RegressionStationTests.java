package ro.mihai.tpt.regression_test;

import ro.mihai.tpt.model.City;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * This file's sole purpose is to identify changes 
 * within the RATT web site: line/station renaming or 
 * changing IDs or adding new/removing old lines/stations.
 * @author mihai
 *
 */
public class RegressionStationTests extends TestCase {
	private City cActual = null, cExpected = null;
	
	public RegressionStationTests(String testMethod, City cActual, City cExpected) {
		super(testMethod);
		this.cActual = cActual;
		this.cExpected = cExpected;
	}
	
	public void testLineCount() {
		assertEquals("",
			cExpected.getLines().size(), 
			cActual.getLines().size()
		);
	}
	public void testLines() {
		assertEquals("",
			RegressionTests.diffEntities(cExpected.getLines(), cActual.getLines()),"");
	}

	
	public void testStationCount() {
		assertEquals("",cExpected.getStations().size(), cActual.getStations().size());
	}
	public void testStations() {
		assertEquals("",
			RegressionTests.diffEntities(cExpected.getStations(), cActual.getStations()),"");
	}
	
	
	public static void addTests(TestSuite suite, City cActual, City cExpected) {
		for(String t : RegressionTests.testMethods(RegressionStationTests.class))
			suite.addTest(new RegressionStationTests(t, cActual, cExpected));
	}
}
