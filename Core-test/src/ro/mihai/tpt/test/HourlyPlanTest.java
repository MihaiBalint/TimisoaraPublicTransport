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

import org.junit.Test;

import ro.mihai.tpt.model.HourlyPlan;

public class HourlyPlanTest {

	@Test
	public void test() {
		HourlyPlan plan = new HourlyPlan();
		plan.setHourSchedule( 5, new int[]{28, 39, 53});
		plan.setHourSchedule( 7, new int[]{ 8, 21, 30, 38, 48, 58});
		
		assertArrayEquals(new int[]{5, 28}, plan.getNextMinute(0, 0));
		
		assertArrayEquals(new int[]{5, 28}, plan.getNextMinute(5, 0));
		assertArrayEquals(new int[]{5, 28}, plan.getNextMinute(5, 28));
		assertArrayEquals(new int[]{5, 39}, plan.getNextMinute(5, 29));
		
		assertArrayEquals(new int[]{7,  8}, plan.getNextMinute(5, 54));
		assertArrayEquals(new int[]{5, 28}, plan.getNextMinute(7, 59));
	}

}
