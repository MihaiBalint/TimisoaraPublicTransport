package ro.mihai.tpt.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;

public class HourlyPlan extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final int[] NONE = new int[]{};
	private int[][] hourly;
	
	private HourlyPlan(long resId, City city) {
		super(resId, city);
		hourly = new int[23][];
	}
	
	public HourlyPlan() {
		this(-1, null);
	}
	
	public int[] getHourSchedule(int hour) {
		ensureLoaded();
		int[] minutes = hourly[hour];
		if (minutes == null)
			return NONE;
		return minutes;
	}

	public void setHourSchedule(int hour, int[] minutes) {
		ensureLoaded();
		Arrays.sort(minutes);
		hourly[hour] = minutes;
	}
	
	public int[] getNextMinute(int hour, int minute) {
		int[] minutes = getHourSchedule(hour);
		int pos = Arrays.binarySearch(minutes, minute);
		if (pos<0) {
			pos = 0 - (pos+1);
			if (pos >= minutes.length) {
				int h = hour;
				do {
					h++; 
					minutes = getHourSchedule(hour);
				} while (h<24 && minutes.length==0);
				if (minutes.length==0) {
					h = 0;
					do {
						h++; 
						minutes = getHourSchedule(hour);
					} while (h<hour && minutes.length==0);
					if (minutes.length==0) 
						return new int[]{-1, -1};
				}
				return new int[]{h, minutes[0]};	
			}
		}
		return new int[]{hour, minutes[pos]};
	}
	
	public static HourlyPlan loadEager(BPInputStream eager, City city) throws IOException {
		return new HourlyPlan(eager.readInt(), city);
	}

	@Override
	public void persistEager(BPOutputStream eager) throws IOException {
		// NOP
	}
	
	@Override
	protected void persistLazy(BPMemoryOutputStream lazy) throws IOException {
		// TODO
	}
	
	@Override
	protected void loadLazyResources(BPInputStream res, DataVersion version) throws IOException {
		// TODO
	}
}
