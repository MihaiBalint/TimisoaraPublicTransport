package ro.mihai.tpt.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.TimeZone;

import org.junit.Test;

import ro.mihai.tpt.model.Estimate;

public class EstimateTest {

	@Test
	public void estimateTimeZone() {
		for(String tz : TimeZone.getAvailableIDs())
			System.out.println(tz);
		Calendar est = Calendar.getInstance(TimeZone.getTimeZone("Europe/Bucharest"));
		assertNotNull(est);
	}
	
	public void afterTest() {
		Estimate e1 = new Estimate(null, null, 0);
		Estimate e2 = new Estimate(null, null, 0);
		e1.putTime("13:30", "13:30");
		e2.putTime("13:40", "13:40");

		assertTrue(e2.after(e1, false));
		assertFalse(e1.after(e2, true));
	}

	public void afterTest2() {
		Estimate e1 = new Estimate(null, null, 0);
		Estimate e2 = new Estimate(null, null, 0);
		e1.putTime("13:50", "13:50");
		e2.putTime("13:20", "13:20");

		assertTrue(e1.after(e2, false));
		assertFalse(e2.after(e1, true));
	}
}
