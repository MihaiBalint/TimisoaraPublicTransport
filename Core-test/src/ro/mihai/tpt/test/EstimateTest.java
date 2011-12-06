package ro.mihai.tpt.test;

import java.util.TimeZone;

import org.junit.Test;

public class EstimateTest {

	@Test
	public void estimateTimeZone() {
		for(String tz : TimeZone.getAvailableIDs())
			System.out.println(tz);
	}
}
