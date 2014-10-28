package ro.mihai.tpt.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;

public class HourlyPlan extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final int HOURS = 24;
	
	private static final int[] NONE = new int[]{};
	private int[][] hourly;
	
	private HourlyPlan(long resId, City city) {
		super(resId, city);
		hourly = new int[HOURS][];
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
					minutes = getHourSchedule(h);
				} while (h<HOURS-1 && minutes.length==0);
				if (minutes.length==0) {
					h = -1;
					do {
						h++; 
						minutes = getHourSchedule(h);
					} while (h<hour-1 && minutes.length==0);
					if (minutes.length==0) 
						return new int[]{-1, -1};
				}
				return new int[]{h, minutes[0]};	
			}
		}
		return new int[]{hour, minutes[pos]};
	}
	
	static HourlyPlan loadEager(BPInputStream eager, int resId, City city) throws IOException {
		return new HourlyPlan(resId, city);
	}

	@Override
	public void persistEager(BPOutputStream eager) throws IOException {
		// NOP
	}
	
	@Override
	protected void persistLazy(BPMemoryOutputStream lazy) throws IOException {
		lazy.writeInt(hourly.length);
		for (int h=0;h<hourly.length;h++) {
			long bitHours = 0;
			int[] minutes = hourly[h];
			if (minutes != null) {
				assert minutes.length < 60;
				for (int m=0;m<minutes.length;m++)
					bitHours = bitHours | (1l<<minutes[m]);
			}
			lazy.writeLong(bitHours);
		}
	}
	
	@Override
	protected void loadLazyResources(BPInputStream res, DataVersion version) throws IOException {
		hourly = new int[res.readInt()][];
		int[] minutes = new int[60];
		for (int h=0;h<hourly.length;h++) {
			long bitHours = res.readLong();
			if (bitHours==0) {
				hourly[h] = null;
				continue;
			}
			int m=0, mi=0;
			while (bitHours!=0 && m<60) {
				long p = 1l<<m;
				if ((bitHours & p) != 0) {
					bitHours = bitHours & ~p;
					minutes[mi] = m;
					mi++;
				}
				m++;
			}
			hourly[h] = Arrays.copyOf(minutes, mi);
		}
	}
}
