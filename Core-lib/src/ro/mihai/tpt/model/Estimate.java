package ro.mihai.tpt.model;

import static ro.mihai.util.Formatting.*;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Estimate implements Serializable {
	public static class TimeEstimate {
		public String asString;
		public Calendar asTime;
		
		public TimeEstimate(String asString, Calendar asTime) {
			this.asString = asString;
			this.asTime = asTime;
		}
	}
	
	private static final long serialVersionUID = 1L;
	private Station station;
	private int stationIndex;
	private Path path;
	private String times1, times2, err;
	private EstimateType type;
	private long updateTimeMilis;

	public Estimate(Path path, Station station, int stationIndex) {
		this.path = path;
		this.station = station;
		this.stationIndex = stationIndex;
		this.err = "";
		this.times1 = "update";
		this.times2 = "update";
		this.type = EstimateType.None;
	}
	
	public void putErr(String err) {
		this.err = err;
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
		this.err = "";
		
		if (t1!=null && t1.trim().length()==5 && t1.charAt(2)==':') 
			this.type = EstimateType.Scheduled;
		else if(">>".equals(times1) || isInteger(times1)) 
			this.type = EstimateType.GPS;
		else if(isMinutes(times1)) {
			this.type = EstimateType.GPS;
			this.times1 = parseMinutes(times1);
		} else
			this.type = EstimateType.None;
	}
	
	public String estimateTimeString() {
		return this.estimateTime().asString;
	}
	
	private TimeEstimate estimateTime() {
		if (this.type.isGPS() || this.type.isNotNone())
			return getEstimate1();
		
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

	TimeEstimate getEstimate1() {
		return new TimeEstimate(getTimes1(), parseEstimate(getTimes1(), updateTimeMilis));
	}
	Calendar getEstimate2() {
		return parseEstimate(getTimes2(), updateTimeMilis);
	}

	public String getErr() {
		return err;
	}

	public void startUpdate() {
		err = "upd";
	}
	public void clearUpdate() {
		if (isUpdating())
			err = "";
	}
	public boolean isUpdating() {
		return "upd".equals(err);
	}
	public boolean hasErrors() {
		return err!=null && err.length() > 0;
	}
	public boolean isVehicleHere() {
		if (">>".equals(times1.trim())) 
			return true;
		List<Station> stations = path.getStationsByPath();
		
		if(stationIndex==0) // first station
			return false;
		
		Estimate prev = path.getEstimate(stations.get(stationIndex-1));
		return prev.after(this, false); 
	}
	
	public boolean updatedAfter(Estimate other) {
		return this.updateTimeMilis > other.updateTimeMilis;
	}

	public long updateDelta(Estimate other) {
		return this.updateTimeMilis - other.updateTimeMilis;
	}
	
	public boolean after(Estimate other, boolean defaultIfNone) {
		Calendar 
			thisEst = this.estimateTime().asTime,
			otherEst = other.estimateTime().asTime;
		
		if (thisEst==null || otherEst==null) return defaultIfNone;

		// linear regression of estimate, maybe not the best but we have no other
		// X(t) = e1
		// Y(t+k) = e2  =>  X(t+k) = e1-k
		// this -> x, other -> y
		
		long delta = this.updateTimeMilis - other.updateTimeMilis;
		thisEst.add(Calendar.MILLISECOND, (int)delta);
		
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
