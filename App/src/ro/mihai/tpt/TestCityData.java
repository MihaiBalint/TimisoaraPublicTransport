/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

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
package ro.mihai.tpt;

import java.io.FileOutputStream;
import ro.mihai.tpt.model.City;
import ro.mihai.util.IMonitor;
import junit.framework.TestCase;

public class TestCityData extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCityData() throws Exception {
		City c = RATT.downloadCity(new TestMonitor());
		
		assertEquals(200, c.getStations().size());
		
		c.saveToFile(new FileOutputStream("citylines2.txt"));
	}

	
	public static class TestMonitor implements IMonitor {
		public void workComplete() {
			// TODO Auto-generated method stub
			
		}
		
		public void setMax(int max) {
			// TODO Auto-generated method stub
			
		}
	}
}
