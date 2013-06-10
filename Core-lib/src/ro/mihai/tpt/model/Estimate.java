package ro.mihai.tpt.model;

import static ro.mihai.util.Formatting.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

	public Estimate(Path path, Station station, int stationIndex) {
		this.path = path;
		this.station = station;
		this.stationIndex = stationIndex;
		this.status = Status.AllIsGood;
		this.times1 = EMPTY;
		this.times2 = EMPTY;
		this.type = EstimateType.None;
	}
	
	public void setStatus(Status status) {
		this.status = status;
		// this.updateTimeMilis = System.currentTimeMillis();
	}
	
	public Station getStation() {
		return station;
	}
	
	public Path getPath() {
		return path;
	}
	
	public void putTime(String t1, String t2) {
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
	
	private boolean isBoarding() {
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
		
		List<Station> stations = path.getStationsByPath();
		
		if(stationIndex > 0 && this.type.isGPS()) {
			// fist station is never arriving
			// for now, vehicles are only arriving when on GPS estimate
			Estimate prev = path.getEstimate(stations.get(stationIndex-1));
			if (prev.type.isGPS() == false || (!prev.isBoarding() && prev.after(this, false))) {
				if (stationIndex < stations.size()-1) {
					Estimate next = path.getEstimate(stations.get(stationIndex+1));
					if (next.type.isGPS() && !next.isBoarding() && this.after(next, false))
						return VehicleStatus.ArrivingDeparting;
				}
				return VehicleStatus.Arriving;
			}
		}
		if (stationIndex < stations.size()-1) {
			// last station is never departing
			Estimate next = path.getEstimate(stations.get(stationIndex+1));
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
	
	public boolean after(Estimate other, boolean defaultIfNone) {
		long now = System.currentTimeMillis();
		Calendar 
			thisEst = this.estimateTime(now).asTime,
			otherEst = other.estimateTime(now).asTime;
		
		if (thisEst==null || otherEst==null) return defaultIfNone;

		return thisEst.after(otherEst);
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
