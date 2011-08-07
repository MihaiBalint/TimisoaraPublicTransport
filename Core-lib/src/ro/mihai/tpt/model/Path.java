package ro.mihai.tpt.model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import ro.mihai.tpt.RATT;
import static ro.mihai.util.Formatting.*;

public class Path {
	private String name, niceName;
	private List<Segment> segments;
	private Line line;
	

	private List<Station> stationsByPath;
	private List<Station> stationsByTime;
	
	private Map<Station,String> times1, times2, errs;
	private final StationTimesComp timesComp = new StationTimesComp();
	
	public Path(Line line, String name) {
		this.name = name;
		this.line = line;
		this.segments = new ArrayList<Segment>();
		this.stationsByPath = new ArrayList<Station>();
		this.stationsByTime = new ArrayList<Station>();
		
		this.times1 = new HashMap<Station, String>();
		this.times2 = new HashMap<Station, String>();
		this.errs = new HashMap<Station, String>();
		
	}
	
	public String getName() {
		return name;
	}
	
	public String getNiceName() {
		return niceName;
	}
	
	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
	
	public Line getLine() {
		return line;
	}
	
	public List<Segment> getSegments() {
		return segments;
	}
	
	public void addSegment(Segment s) {
		if(segments.isEmpty()) 
			addStation(s.getFrom());
		segments.add(s);
		addStation(s.getTo());
	}

	private Station temp;
	public void concatenate(Station s) {
		if(temp==null) {
			temp = s;
			return;
		} 
		addSegment(new Segment(temp, s));
		temp = s;
	}
	
	private void addStation(Station s) {
		stationsByPath.add(s);
		stationsByTime.add(s);
		times1.put(s, "update");	
		times2.put(s, "update");	
		errs.put(s, "");	
	}
	

	public List<Station> getStationsByPath() {
		return stationsByPath;
	}
	public List<Station> getStationsByTime() {
		return stationsByTime;
	}
	
	/*
	 * 
	 */

	public void updateAllStations() {
		int ec = 0;
		for(Station s:stationsByTime) {
			startUpdate(s);
			ec = updateStation(ec, s);
		}
		updateSort(); 
	}

	public void startUpdate(Station s) {
		putErr(s, "upd");
	}
	
	public int updateStation(int ec, Station s) {
		try {
			if(ec<3) {
				String[] t = RATT.downloadTimes(line, s);
				putTime(s, t[0], t[1]);
			} else  
				putErr(s, "upd-canceled");
		} catch(IOException e) {
			putErr(s,"io-err");
			ec++;
		}
		return ec;
	}

	public void updateSort() {
		ArrayList<Station> newSort = new ArrayList<Station>(stationsByTime);
		Collections.sort(newSort, timesComp);
		stationsByTime = newSort;
	}
	
	/*
	 * 
	 */
	
	private synchronized void putErr(Station s, String err) {
		errs.put(s, err);
	}

	private synchronized void putTime(Station s, String t1, String t2) {
		times1.put(s,t1);
		times2.put(s,t2);
		errs.put(s, "");
	}
	
	public synchronized String getTime1(Station s) {
		return formatTime(times1.get(s));
	} 

	public synchronized String getTime2(Station s) {
		return formatTime(times2.get(s));
	} 
	
	public synchronized String getErrs(Station s) {
		return errs.get(s);
	}
	
	/*
	 * 
	 */
	public void reOrder() {
		try {
			stationCheck();
			orderRead();
			orderCheck();
		}catch(IOException e) {
			System.err.println(line.getName() + " ("+name+"): "+e);
		}catch(NoSuchElementException e) {
			System.err.println(line.getName() + " ("+name+"): "+e);
		}catch(AssertionError e) {
			System.out.println("*** "+line.getName() + " ("+name+"): "+e);
		}
	}

	private void stationCheck() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+line.getName()+" ("+name+").txt")));
		String l;
		Set<String> stations = new HashSet<String>(); 
		while(null!=(l=br.readLine())) {
			stations.add(l.trim());
			Station found = null;
			String tried = "";
			for(Station s:stationsByPath) {
				tried += s.getNicestNamePossible().trim()+"\n";
				if(s.getNicestNamePossible().trim().equals(l.trim())) {
					found = s; break;
				}
			}
			assert(null!=found) : "No station found for: "+l+", Tried: \n"+tried;
		}
		br.close();
		
		Set<String> stationsCopy = new HashSet<String>(stations);
		for(Station s:stationsByPath) {
			String st = s.getNicestNamePossible().trim();
			if(stations.contains(st)) {
				stations.remove(st);
			} else {
				if(!stationsCopy.contains(st))
					System.err.println(line.getName()+" ("+name+"): Missing in order: "+st);
				else
					System.out.println(line.getName()+" ("+name+"): Duplicated: "+st);
			}
		}
	}

	private void orderRead() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+line.getName()+" ("+name+").txt")));
		String l;
		
		List<Station> stations = stationsByPath;
		
		this.temp = null;
		this.segments = new ArrayList<Segment>();
		this.stationsByPath = new ArrayList<Station>();
		this.stationsByTime = new ArrayList<Station>();
		
		Iterator<Station> it = stationsByPath.iterator();
		while(null!=(l=br.readLine())) {
			
			for(Station s:stations) {
				String st = s.getNicestNamePossible().trim();
				if(st.equals(l.trim())) 
					concatenate(s);
			}
		}
		
	}
	
	private void orderCheck() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+line.getName()+" ("+name+").txt")));
		String l;
		Iterator<Station> it = stationsByPath.iterator();
		Station s = it.next();
		
		while(null!=(l=br.readLine())) {
			l=l.trim();
			assert(s.getNicestNamePossible().trim().equals(l))
				: s.getNicestNamePossible().trim()+" is not "+l;
			
			while(it.hasNext() && s.getNicestNamePossible().trim().equals(l))
				s=it.next();
		}
		br.close();
	}
	
	public List<String> timesToString() {
		ArrayList<String> times = new ArrayList<String>();
		for(Station s:stationsByTime) {
			StringBuilder b = new StringBuilder();
			b.append(s.getName());
			b.append(" - \t");
			b.append(formatTime(times1.get(s)));
			b.append(", \t");
			b.append(formatTime(times2.get(s)));
			String er = errs.get(s);
			if(er.length()>0) {
				b.append("\t - ");
				b.append(er);
			}
			b.append("\n");
		}
		return times;
	}
	
	/*
	 * 
	 */
	
	private class StationTimesComp implements Comparator<Station> {
		
		public int compare(Station s1, Station s2) {
			String 
				t11 = times1.get(s1),
				t12 = times2.get(s1),
				t21 = times1.get(s2),
				t22 = times2.get(s2);
			int c1 = compareTimes(t11, t21);
			if(c1!=0) return c1;
			
			int c2 = compareTimes(t12, t22);
			if(c2!=0) return c2;
			
			int c3 = errs.get(s1).compareTo(errs.get(s2)); 
			if(c3!=0) return c3;
			
			return s1.getName().compareTo(s2.getName());
		}
		
		private int compareTimes(String t1, String t2) {
			// s1 < s2
			// ret < 0
			boolean 
				t1arr = t1.contains(">>"),
				t2arr = t2.contains(">>");
			
			if(t1arr || t2arr) 
				return t1arr ? (t2arr ? 0 : -1) : 1;

			int min1=Integer.MAX_VALUE, min2=Integer.MAX_VALUE;
			try { min1 = Integer.parseInt(t1); } catch(NumberFormatException e) {}
			try { min2 = Integer.parseInt(t2); } catch(NumberFormatException e) {}
			
			if(min1!=min2) 
				return min1<min2 ? -1 : 1;
			
			// at this point either both are absolute times, or something else
			// string comparison will work in both cases

			return t1.compareTo(t2);
		}
	}	
}
