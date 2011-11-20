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
package ro.mihai.tpt.retired_test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;

import ro.mihai.tpt.JavaCityLoader;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.INamedEntity;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Station;
import ro.mihai.util.IMonitor;
import junit.framework.TestCase;

public class TestCityData extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void test_CityUpdateAndRegressions() throws Exception {
		//City c = RATT.downloadCity(new TestMonitor());
		City c = JavaCityLoader.loadCachedCityOrDownloadAndCache();
		
		ArrayList<String> names = new ArrayList<String>();
		for(INamedEntity s : c.getLines()) names.add(s.getName());
		Collections.sort(names);
		
		
		for(String name : names) {
			Line l = c.getLine(name);
			String safeName = l.getName().replace("-", "_");
			System.out.println("\tpublic void testLine_"+safeName+"_StationCount() {");
			System.out.println("\t\tassertEquals("+l.getStations().size()+", c.getLine(\""+l.getName()+"\").getStations().size());");
			System.out.println("\t}");
			System.out.println("\tpublic void testLine_"+safeName+"_StationNames() {");
			System.out.println("\t\tassertEquals(\""+NamedEntityCollection.sortedNames(l.getStations())+"\",\n" +
					"\t\t\t NamedEntityCollection.sortedNames(c.getLine(\""+l.getName()+"\").getStations())\n" +
					"\t\t);");
			System.out.println("\t}");
			System.out.println("\tpublic void testLine_"+safeName+"_StationIDs() {");
			System.out.println("\t\tassertEquals(\""+NamedEntityCollection.sortedIDs(l.getStations())+"\",\n" +
					"\t\t\t NamedEntityCollection.sortedIDs(c.getLine(\""+l.getName()+"\").getStations())\n" +
					"\t\t);");
			System.out.println("\t}");
		}

		assertEquals(597, c.getStations().size());
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
