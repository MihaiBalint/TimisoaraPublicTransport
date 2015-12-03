/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2014  Mihai Balint

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. 
*/
package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Enumeration;

import junit.framework.JUnit4TestAdapter;
import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Estimate;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;
import ro.mihai.tpt.util.TestPrefs;


@RunWith(AllTests.class)
public class RATTTest {
	
	public static class BasicTest {
		@Test
		public void testGetLine() throws IOException {
			String[] result = RATT.downloadTimes2(null, "Tv1", "1106", "Tv_P-ta Maria 3");
			assertNotNull(result);
			assertTrue(result.length > 0);
			assertTrue(result[0].length() > 0);
		}
	}

	public static class FullCityTest extends TestCase {
		private String lineName;
		private City c;
		
		public FullCityTest(String line, City c) {
			super("testLine_Stations");
			this.lineName = line;
			this.c = c;
		}
		
		@Override
		public String getName() {
			return super.getName().replaceFirst("Line_", "_"+lineName+"_");
		}

		public void testLine_Stations() throws IOException {
			Line l = c.getLineByName(lineName);
			
			Enumeration<RATT.Est> result = RATT.downloadTimes2(null, lineName, l.getFirstPath().getExtId());
			assertNotNull(result);
			assertTrue(result.hasMoreElements());
			
			Path path = null;
			RATT.Est est = result.nextElement();
			for (Path p: l.getPaths()) {
				String from = p.getEstimateByPath(0).getStation().getName();
				if (from.equals(est.pathFrom)) {
					path = p;
					break;
				}
			}
			assertNotNull(path);
			String cityStops = "";
			for(Estimate e :path.getEstimatesByPath()) 
				cityStops += e.getStation().getName() + "\n";
			
			String ratt2Stops = est.stopCrypticName + "\n";;
			while (result.hasMoreElements()) {
				RATT.Est e = result.nextElement();
				if (e.pathFrom != est.pathFrom)
					break;
				ratt2Stops += e.stopCrypticName + "\n";
			}
			
			assertEquals(cityStops, ratt2Stops);
			
		}
	}
	
	public static TestSuite suite() throws IOException {
		City c = JavaCityLoader.loadCachedCityOrDownloadAndCache(new TestPrefs());
		TestSuite suite = new TestSuite();
		suite.addTest(new JUnit4TestAdapter(BasicTest.class));
		
		for(Line l: c.getLines()) {
			String lineName = l.getName();
			suite.addTest(new FullCityTest(lineName, c));
		}
		
		return suite;
	}

}
