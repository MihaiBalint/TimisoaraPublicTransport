package ro.mihai.tpt.regression_test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.INamedEntity;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static ro.mihai.tpt.regression_test.BlackListed.*;

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
	
	private static Collection<INamedEntity> wrap(Collection<Line> lines) {
		// TODO Kill me and replace me with path based tests
		Collection<INamedEntity> wrapLines = new ArrayList<INamedEntity>();
		for(Line l : lines) {
			HashSet<String> ids = new HashSet<String>();
			for(Path p : l.getPaths())
				if (!ids.contains(p.getId())) {
					ids.add(p.getExtId());
					wrapLines.add(bl(l.getFirstPath().getExtId(), l.getName()));
				}
		}
		return wrapLines;
	}
	
	public void testLines() {
		assertEquals("",
			RegressionTests.diffEntities(wrap(cExpected.getLines()), wrap(cActual.getLines()), blExpectedLines, blActualLines),"");
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
