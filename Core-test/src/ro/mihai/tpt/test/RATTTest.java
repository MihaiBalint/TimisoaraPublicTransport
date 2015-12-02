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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;
import ro.mihai.tpt.util.TestPrefs;

public class RATTTest {

	@Test
	public void testGetLine() throws IOException {
		String[] result = RATT.downloadTimes2(null, "Tv1", "1106", "Tv_P-ta Maria 3");
		assertNotNull(result);
		assertTrue(result.length > 0);
		assertTrue(result[0].length() > 0);
	}

	@Test
	public void testGetLines() throws IOException {
		City c = JavaCityLoader.loadCachedCityOrDownloadAndCache(new TestPrefs());
		String lineName = "Tv1";

		Enumeration<RATT.Est> result = RATT.downloadTimes2(null, lineName, "1106");
		assertNotNull(result);
		assertTrue(result.hasMoreElements());
		
		Line l = c.getLineByName(lineName);
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

		while (result.hasMoreElements()) {
			RATT.Est e = result.nextElement();
			Station s = path.getEstimateByPath(e.stopNo).getStation();
			assertEquals(e.stopCrypticName, s.getName());
			if (e.pathFrom != est.pathFrom)
				break;
		}
	}
}
