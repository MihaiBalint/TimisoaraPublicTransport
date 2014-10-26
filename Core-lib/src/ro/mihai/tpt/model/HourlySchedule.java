package ro.mihai.tpt.model;

import java.util.Arrays;

public class HourlySchedule {
	private static final int[] NONE = new int[]{};
	private int[][] hourly;
	
	public HourlySchedule() {
		hourly = new int[23][];
	}
	
	public int[] getSchedule(int hour) {
		int[] minutes = hourly[hour];
		if (minutes == null)
			return NONE;
		return minutes;
	}

	public void setSchedule(int hour, int[] minutes) {
		Arrays.sort(minutes);
		hourly[hour] = minutes;
	}
	
	public int[] getNextMinute(int hour, int minute) {
		int[] minutes = getSchedule(hour);
		int pos = Arrays.binarySearch(minutes, minute);
		if (pos<0) {
			pos = 0 - (pos+1);
			if (pos >= minutes.length) {
				int h = hour;
				do {
					h++; 
					minutes = getSchedule(hour);
				} while (h<24 && minutes.length==0);
				if (minutes.length==0) {
					h = 0;
					do {
						h++; 
						minutes = getSchedule(hour);
					} while (h<hour && minutes.length==0);
					if (minutes.length==0) 
						return new int[]{-1, -1};
				}
				return new int[]{h, minutes[0]};	
			}
		}
		return new int[]{hour, minutes[pos]};
	}
}
