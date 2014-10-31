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
package ro.mihai.tpt.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

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
	public void saveEager(BPOutputStream eager) throws IOException {
		// NOOP
	}
	
	@Override
	protected void saveLazyResources(BPMemoryOutputStream lazy) throws IOException {
		savePlanTable(lazy, hourly);
	}
	
	@Override
	protected void loadLazyResources(BPInputStream res) throws IOException {
		this.hourly = loadPlanTable(res);
	}
	
	private static void savePlanTable(BPMemoryOutputStream lazy, int[][] hourlyTable) throws IOException {
		int workingHoursMask = 0;
		for (int h=0;h<hourlyTable.length;h++)
			if (hourlyTable[h]!=null && hourlyTable[h].length>0)
				workingHoursMask = workingHoursMask | (1<<h);
		lazy.writeInt(workingHoursMask);
		for (int h=0;h<hourlyTable.length;h++) {
			int[] minutes = hourlyTable[h];
			if (minutes!=null && minutes.length>0) {
				long bitHours = 0;
				for (int m=0;m<minutes.length;m++)
					bitHours = bitHours | (1l<<minutes[m]);
				lazy.writeLong(bitHours);
			}
		}
		
	}
	
	private static int[][] loadPlanTable(BPInputStream res) throws IOException {
		int[][] hourlyTable = new int[HOURS][];
		int[] minutes = new int[60];
		BitIterator workingHours = new BitIterator(res.readInt(), HOURS);
		while (workingHours.hasNext()) {
			int h = workingHours.next();
			BitIterator bi = new BitIterator(res.readLong(), 60);
			int mi=0;
			while(bi.hasNext())
				minutes[mi++] = bi.next();
			hourlyTable[h] = Arrays.copyOf(minutes, mi);
		}
		return hourlyTable;
	}
	
	private static class BitIterator implements Iterator<Integer> {
		private long data;
		private int position, max;
		
		public BitIterator(long data, int max) {
			position = -1;
			this.data = data;
			this.max = max;
		}
		
		@Override
		public Integer next() {
			if (!hasNext())
				throw new NoSuchElementException();
			long mask;
			do {
				position++;
				mask = 1l << position;
			} while ((data & mask) == 0);
			data = data & ~mask;
			return position;
		}
		@Override
		public boolean hasNext() {
			return data!=0 && (position+1) < max;
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
