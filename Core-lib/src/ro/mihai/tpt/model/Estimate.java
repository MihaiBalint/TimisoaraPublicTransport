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

import static ro.mihai.util.Formatting.*;

import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import ro.mihai.tpt.RATT;
import ro.mihai.util.IPrefs;

public class Estimate implements Serializable {
	public static enum Status {
		AllIsGood, WaitingToUpdate, NetworkError, UpdateCanceled;
		
		public boolean isError() {
			return this != AllIsGood && this != WaitingToUpdate;
		}
		
		public boolean isUpdating() {
			return this == WaitingToUpdate;
		}
	}
	
	public static enum VehicleStatus {
		Away, Boarding, Arriving, Departing, ArrivingDeparting;
		
		public boolean isArriving() {
			return this == Arriving || this == ArrivingDeparting;
		}
		public boolean isDeparting() {
			return this == Departing || this == ArrivingDeparting;
		}
		public boolean isBoarding() {
			return this == Boarding;
		}
	}
	
	public static class TimeEstimate {
		public String asString;
		public Calendar asTime;
		
		public TimeEstimate(String asString, Calendar asTime) {
			this.asString = asString;
			this.asTime = asTime;
		}
	}
	
	private static final long serialVersionUID = 1L;
	private static final String EMPTY = "--:--";
	private Station station;
	private int stationIndex;
	private Path path;
	private String times1, times2;
	private Status status;
	private EstimateType type;
	private long updateTimeMilis;
	private HourlyPlan plan;

	public Estimate(Path path, Station station, int stationIndex, HourlyPlan plan) {
		this.path = path;
		this.station = station;
		this.stationIndex = stationIndex;
		this.status = Status.AllIsGood;
		this.times1 = EMPTY;
		this.times2 = EMPTY;
		this.type = EstimateType.None;
		this.plan = plan;
	}
	
	public void setStatus(Status status) {
		this.status = status;
		// this.updateTimeMilis = System.currentTimeMillis();
	}
	
	public HourlyPlan getPlan() {
		return plan;
	}
	
	public Station getStation() {
		return station;
	}
	
	public Path getPath() {
		return path;
	}
	
	public int updateStation(IPrefs prefs, int ec) {
		try {
			if(ec<3) {
				String extLineId = path.getExtId();
				String extStationId = station.getExtId();
				String[] t = RATT.downloadTimes(prefs, extLineId, extStationId);
				prefs.getAnalyticsCollector().record(t[0], t[1], t[2], extLineId, extStationId);
				putTime(t[0], t[1], t[2]);
			} else
				setStatus(Estimate.Status.UpdateCanceled);
		} catch(IOException exc) {
			setStatus(Estimate.Status.NetworkError);
			ec++;
		}
		return ec;
	}
	
	public void putTime(String t1, String t2, String timestamp) {
		this.updateTimeMilis = System.currentTimeMillis();
		this.times1 = t1!=null ? t1.trim() : "";
		this.times2 = t2!=null ? t2.trim() : "";
		this.status = Status.AllIsGood;
		
		if (t1!=null && t1.trim().length()==5 && t1.charAt(2)==':')
			this.type = isInteger(t1.substring(0,2)) && isInteger(t1.substring(3, 5))
					? EstimateType.Scheduled
					: EstimateType.None;
		else if(">>".equals(times1) || isInteger(times1)) 
			this.type = EstimateType.GPS;
		else if(isMinutes(times1)) {
			this.type = EstimateType.GPS;
			this.times1 = parseMinutes(times1);
		} else
			this.type = EstimateType.None;
	}
	
	public String estimateTimeString() {
		return this.estimateTime(updateTimeMilis).asString;
	}
	
	private TimeEstimate estimateTime(long updateTime) {
		if (this.type.isGPS() || this.type.isNotNone())
			return getEstimate1(updateTime);
		
		assert(this.type.isNone());
		return new TimeEstimate(getTimes1(), null);
	}
	
	public EstimateType getEstimateType() {
		return type;
	}
	
	String getTimes1() {
		return formatTime(times1);
	}
	String getTimes2() {
		return formatTime(times2);
	}

	TimeEstimate getEstimate1(long updateTime) {
		return new TimeEstimate(getTimes1(), parseEstimate(getTimes1(), updateTime));
	}
	Calendar getEstimate2() {
		return parseEstimate(getTimes2(), updateTimeMilis);
	}

	public Status getStatus() {
		return status;
	}

	public void startUpdate() {
		status = Status.WaitingToUpdate;
	}
	public void clearUpdate() {
		if (isUpdating())
			status = Status.AllIsGood;
	}
	public boolean isUpdating() {
		return status.isUpdating();
	}
	public boolean hasErrors() {
		return status!=null && status.isError();
	}
	
	public boolean isBoarding() {
		return (">>".equals(times1.trim())); 
	}
	
	public VehicleStatus getVehicleStatus() {
		//Arriving?
		// 		true	true	false	true		
		// n-1 	11:11	--:--	4 min	7 min
		// n	6 min	6 min	6 min	6 min
		//
		//Arriving&Departing?
		// 		A&D		A&D		pass	A&D		A		A		pass	A		A		A		pass	A
		// n-1	11:11	--:--	4 min	7 min	11:11	--:--	4 min	7 min	11:11	--:--	4 min	7 min
		// n 	6 min	6 min	6 min	6 min	6 min	6 min	6 min	6 min	6 min	6 min	6 min	6 min
		// n+1	4 min	4 min	4 min	4 min	7 min	7 min	7 min	7 min	>> 		>>		>>		>>
		//
		//Departing?
		//		true	true	false	true	
		// n	11:11	--:--	4 min	7 min
		// n+1	6 min	6 min	6 min	6 min
		//
		
		
		if (isBoarding()) 
			return VehicleStatus.Boarding;
		
		List<Estimate> estimates = path.getEstimatesByPath();
		
		if(stationIndex > 0 && this.type.isGPS()) {
			// fist station is never arriving
			// for now, vehicles are only arriving when on GPS estimate
			Estimate prev = estimates.get(stationIndex-1);
			if (prev.type.isGPS() == false || (!prev.isBoarding() && prev.after(this, false))) {
				if (stationIndex < estimates.size()-1) {
					Estimate next = estimates.get(stationIndex+1);
					if (next.type.isGPS() && !next.isBoarding() && this.after(next, false))
						return VehicleStatus.ArrivingDeparting;
				}
				return VehicleStatus.Arriving;
			}
		}
		if (stationIndex < estimates.size()-1) {
			// last station is never departing
			Estimate next = estimates.get(stationIndex+1);
			if (next.type.isGPS() && !next.isBoarding()) {
				if (this.type.isGPS() == false || this.after(next, false))
					return VehicleStatus.Departing;
			}
		}
		return VehicleStatus.Away;
	}
	
	public boolean updatedAfter(Estimate other) {
		return this.updateTimeMilis > other.updateTimeMilis;
	}

	public long updateDelta(Estimate other) {
		return this.updateTimeMilis - other.updateTimeMilis;
	}

	/**
	 * Compare two estimates with a precision of 30 seconds.
	 * This assumes that the estimate is given only with minute precision.
	 * For stability, always call this method with other being the estimate
	 * of the station further ahead
	 * @param other
	 * @param defaultIfNone
	 * @return
	 */
	public boolean after(Estimate other, boolean defaultIfNone) {
		Calendar 
			thisEst = this.estimateTime(this.updateTimeMilis).asTime,
			otherEst = other.estimateTime(this.updateTimeMilis).asTime;
		
		if (thisEst==null || otherEst==null) return defaultIfNone;
		otherEst.add(Calendar.SECOND, 30);

		return thisEst.after(otherEst);
	}
	
	public static long parseUpdateTime(String timestamp, long failDefault) {
		// timestamp is: 2013-06-11 07:19:21
		long time = failDefault; 
		String[] split = timestamp.split("[ :-]");
		if (split.length != 6)
			return time;
		try {
			Calendar est = Calendar.getInstance(TimeZone.getTimeZone("Europe/Bucharest"));
			est.set(Calendar.YEAR, Integer.parseInt(split[0]));
			est.set(Calendar.MONTH, Integer.parseInt(split[1])-1);
			est.set(Calendar.DAY_OF_MONTH, Integer.parseInt(split[2]));

			est.set(Calendar.HOUR_OF_DAY, Integer.parseInt(split[3]));
			est.set(Calendar.MINUTE, Integer.parseInt(split[4]));
			est.set(Calendar.SECOND, Integer.parseInt(split[5]));
			est.set(Calendar.MILLISECOND, 0);
			return est.getTimeInMillis();
		} catch(NumberFormatException e) {
			return time;
		}
	}
	
	private static Calendar parseEstimate(String time, long updateTimeMilis) {
		Calendar timeOfUpdate = Calendar.getInstance();
		timeOfUpdate.setTimeInMillis(updateTimeMilis);
		Calendar est = Calendar.getInstance(TimeZone.getTimeZone("Europe/Bucharest"));
		time = time.trim();
		if (time.endsWith("min") || time.endsWith("min.")) {
			int endIndex = time.endsWith("min.") ? time.length()-4 : time.length()-3;
			String minString = time.substring(0, endIndex).trim();
			try {
				est.add(Calendar.MINUTE, Integer.parseInt(minString));
				return est;
			} catch (NumberFormatException e) {
				return null;
			}
		} else if (time.equals("..")) {
			return null;
		} else if (time.equals(">>")) {
			return est;
		} else if (time.length()>=5 && time.charAt(2)==':') {
			String hourString = time.substring(0,2);
			String minString = time.substring(3);
			try {
				est.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourString));
				est.set(Calendar.MINUTE, Integer.parseInt(minString));
				// all estimates should be after time-of-update
				timeOfUpdate.add(Calendar.MINUTE, -3);
				if(est.before(timeOfUpdate)) // next day 
					est.add(Calendar.HOUR_OF_DAY, 24);
				timeOfUpdate.add(Calendar.MINUTE, 3);
				
				return est;
			} catch (NumberFormatException e) {
				return null;
			}
		} else
			return null;
	}
	
}
