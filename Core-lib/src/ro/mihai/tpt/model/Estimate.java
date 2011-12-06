package ro.mihai.tpt.model;

import static ro.mihai.util.Formatting.formatTime;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Estimate implements Serializable {
	private static final long serialVersionUID = 1L;
	private Station station;
	private int stationIndex;
	private Path path;
	private String times1, times2, err;
	private long updateTimeMilis;

	public Estimate(Path path, Station station, int stationIndex) {
		this.path = path;
		this.station = station;
		this.stationIndex = stationIndex;
		this.err = "";
		this.times1 = "update";
		this.times2 = "update";
	}
	
	public void putErr(String err) {
		this.err = err;
		// this.updateTimeMilis = System.currentTimeMillis();
	}
	
	public void putTime(String t1, String t2) {
		this.updateTimeMilis = System.currentTimeMillis();
		this.times1 = t1;
		this.times2 = t2;
		this.err = "";
	}
	
	public String getTimes1() {
		return formatTime(times1);
	}
	public String getTimes2() {
		return formatTime(times2);
	}

	public Calendar getEstimate1() {
		return parseEstimate(times1, updateTimeMilis);
	}
	public Calendar getEstimate2() {
		return parseEstimate(times1, updateTimeMilis);
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
		
		if(stationIndex>=stations.size()-1) // last station
			return false;
		
		Estimate next = path.getEstimate(stations.get(stationIndex+1));
		Calendar 
			thisEst = getEstimate1(),
			nextEst = next.getEstimate1();
		if (thisEst==null || nextEst==null) return false;
		if (thisEst.after(nextEst))
			return true;
		
		return false;
	}
	
	
	private static Calendar parseEstimate(String time, long updateTimeMilis) {
		Calendar timeOfUpdate = Calendar.getInstance();
		timeOfUpdate.setTimeInMillis(updateTimeMilis);
		String[] ids = TimeZone.getAvailableIDs();
		TimeZone tz = TimeZone.getTimeZone("Europe/Bucharest");
		ids.toString();
		Calendar est = Calendar.getInstance(tz);
		time = time.trim();
		if (time.endsWith("min")) {
			String minString = time.substring(0, time.length()-3);
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
		} else if (time.charAt(2)==':') {
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
