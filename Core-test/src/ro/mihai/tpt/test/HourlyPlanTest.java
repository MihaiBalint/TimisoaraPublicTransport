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
