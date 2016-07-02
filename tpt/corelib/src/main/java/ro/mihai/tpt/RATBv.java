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
package ro.mihai.tpt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.HourlyPlan;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.model.Station;
import ro.mihai.util.DetachableStream;
import ro.mihai.util.NullMonitor;

public class RATBv {
	public static final int CITY_DB_ENTRIES = 24;
	
	
	public static void main(String[] args) throws IOException {
		// Sample on how to build a very basic transport configuration
		// Data taken from http://www.ratbv.ro/afisaje/1-dus.html
		// 
		City c = new City();
		
		Station d1Start = c.newStation("1", "Livada Postei");
		Station d1End = c.newStation("2", "Triaj");
		Station[] dir1 = {
			d1Start,
			c.newStation("3", "Dramatic"),
			c.newStation("4", "Patria"),
			c.newStation("5", "Hidro A"),
			c.newStation("6", "Toamnei"),
			c.newStation("7", "IUS"),
			c.newStation("8", "Vlahuta"),
			c.newStation("9", "Autogara 3"),
			c.newStation("10", "RAT Brasov"),
			c.newStation("11", "Baza MTTC"),
			d1End,
		};
		Station[] dir2 = {
			d1End,
			c.newStation("12", "Baza MTTC"),
			c.newStation("13", "RAT Brasov"),
			c.newStation("14", "Autogara 3"),
			c.newStation("15", "Vlahuta"),
			c.newStation("16", "IUS"),
			c.newStation("17", "CEC"),
			c.newStation("18", "Liceul Mesota"),
			c.newStation("19", "Camera de Comert"),
			c.newStation("20", "Sanitas"),
			c.newStation("21", "Primarie"),
			d1Start
		};

		ArrayList<Station> allStations = new ArrayList<Station>();
		allStations.addAll(Arrays.asList(dir1));
		allStations.addAll(Arrays.asList(dir2));
		for (Station s: allStations) {
			s.setNiceName(s.getName());
		}
		
		
		Line l = c.newLine("1");

		assert l.getPath("Livada Postei - Triaj") == null;
		Path p1 = c.newPath(l, "insert_RATBv_route_id_here(or not)", "Livada Postei - Triaj");
		p1.setNiceName(p1.getName());
		for (Station s: dir1) {
			p1.concatenate(s, new HourlyPlan());
			s.addPath(p1);
		}
		l.addPath(p1);
		
		assert l.getPath("Triaj - Livada Postei") == null;
		Path p2 = c.newPath(l, "", "Triaj - Livada Postei");
		p2.setNiceName(p2.getName());
		for (Station s: dir2) {
			p2.concatenate(s, new HourlyPlan());
			s.addPath(p2);
		}
		l.addPath(p2);
		
		HourlyPlan dir1station1 = p1.getEstimateByPath(0).getPlan();
		dir1station1.setHourSchedule( 5, new int[]{28, 39, 53});
		dir1station1.setHourSchedule( 6, new int[]{ 8, 21, 30, 38, 48, 58});
		dir1station1.setHourSchedule( 7, new int[]{ 6, 13, 20, 28, 35, 43, 51, 58});
		dir1station1.setHourSchedule( 8, new int[]{ 5, 13, 20, 30, 42, 55});

		HourlyPlan dir1station2 = p1.getEstimateByPath(1).getPlan();
		dir1station2.setHourSchedule( 5, new int[]{43, 54});
		dir1station2.setHourSchedule( 6, new int[]{ 8, 23, 36, 45, 53});
		// ... and so on
		
		// Open your own stream here (possibly to a file)
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		c.saveToFile(buffer);
		
		
		testCityFromBytes(buffer.toByteArray());
	}

	public static void testCityFromBytes(byte[] bytes) throws IOException {
		// Load a city object from your stream
		DetachableStream inBuffer = new DetachableStream.FromBytes(bytes);
		City loadedCity = new City();
		loadedCity.loadFromStream(inBuffer, new NullMonitor(), CITY_DB_ENTRIES);
		
		Path p = loadedCity.getLineByName("1").getPath("Livada Postei - Triaj");
		HourlyPlan plan = p.getEstimateByPath(0).getPlan();
		int[] nextHourMin = plan.getNextMinute(7, 10);
		
		int nextHour = nextHourMin[0];
		int nextMin = nextHourMin[1];
		
		assert nextHour == 7;
		assert nextMin == 13;
		
		nextHourMin = plan.getNextMinute(7, 14);
		assert nextHourMin[0] == 7 && nextHourMin[1] == 20;
	}
	
}
