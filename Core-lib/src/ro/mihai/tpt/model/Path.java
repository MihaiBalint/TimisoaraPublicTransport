/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011-2013  Mihai Balint

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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
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
import ro.mihai.util.DetachableStream;
import ro.mihai.util.IPrefs;
import static ro.mihai.util.Formatting.*;

public class Path extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String extId, name, niceName;
	private List<Segment> segments;
	private Line line;
	
	private List<Station> stationsByPath;
	private List<Station> stationsByTime;
	private Map<Station,Estimate> est;
	
	private final StationTimesComp timesComp = new StationTimesComp();
	
	private Path(Line line, int id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.line = line;
		this.segments = new ArrayList<Segment>();
		this.stationsByPath = new ArrayList<Station>();
		this.stationsByTime = new ArrayList<Station>();
		this.est = new HashMap<Station, Estimate>();
	}
	
	public Path(Line line, int id, String extId, String name) {
		this(line, id, -1, null);
		this.name = name;
		this.extId = extId;
	}
	
	
	public int getId() {
		return id;
	}
	
	public String getExtId() {
		return extId;
	}

	public boolean isFake() {
		return extId!=null && extId.startsWith("F");
	}
	
	public String getName() {
		ensureLoaded();
		return name;
	}
	
	public String getNiceName() {
		ensureLoaded();
		return niceName;
	}
	
	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
	
	public String getLineName() {
		return line.getName();
	}
	
	public Line getLine() {
		return line;
	}
	
	private void addSegment(Segment s) {
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
		est.put(s, new Estimate(this, s, stationsByPath.size()-1));
	}
	
	public String getLabel() {
		return getLineName()+">"+getNiceName();
	}	

	public List<Station> getStationsByPath() {
		ensureLoaded();
		return stationsByPath;
	}
	public List<Station> getStationsByTime() {
		ensureLoaded();
		return stationsByTime;
	}
	
	public Estimate getEstimate(Station s) {
		ensureLoaded();
		return est.get(s);
	}
	
	/*
	 * 
	 */

	public void updateAllStations(IPrefs prefs) {
		ensureLoaded();
		int ec = 0;
		for(Station s:stationsByTime) {
			est.get(s).startUpdate();
			ec = updateStation(prefs, ec, s);
		}
		ArrayList<Station> newSort = new ArrayList<Station>(stationsByTime);
		Collections.sort(newSort, timesComp);
		stationsByTime = newSort;
	}

	public void clearAllUpdates() {
		ensureLoaded();
		for(Estimate e:est.values())
			e.clearUpdate();
	}
	
	public int updateStation(IPrefs prefs, int ec, Station s) {
		ensureLoaded();
		Estimate e = est.get(s);
		try {
			if(ec<3) {
				String[] t = RATT.downloadTimes(prefs, extId, s.getId());
				e.putTime(t[0], t[1]);
			} else
				e.setStatus(Estimate.Status.UpdateCanceled);
		} catch(IOException exc) {
			e.setStatus(Estimate.Status.NetworkError);
			ec++;
		}
		return ec;
	}

	/*
	 * 
	 */
	
	public void reOrder() {
		ensureLoaded();
		try {
			stationCheck();
			orderRead();
			orderCheck();
		}catch(IOException e) {
			System.err.println(getLineName() + " ("+name+"): "+e);
		}catch(NoSuchElementException e) {
			System.err.println(getLineName() + " ("+name+"): "+e);
		}catch(AssertionError e) {
			System.out.println("*** "+getLineName() + " ("+name+"): "+e);
		}
	}

	private void stationCheck() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+getLineName()+" ("+name+").txt")));
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
					System.err.println(getLineName()+" ("+name+"): Missing in order: "+st);
				else
					System.out.println(getLineName()+" ("+name+"): Duplicated: "+st);
			}
		}
	}

	private void orderRead() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+getLineName()+" ("+name+").txt")));
		String l;
		
		List<Station> stations = stationsByPath;
		
		this.temp = null;
		this.segments = new ArrayList<Segment>();
		this.stationsByPath = new ArrayList<Station>();
		this.stationsByTime = new ArrayList<Station>();
		
		while(null!=(l=br.readLine())) {
			
			for(Station s:stations) {
				String st = s.getNicestNamePossible().trim();
				if(st.equals(l.trim())) 
					concatenate(s);
			}
		}
	}
	
	private void orderCheck() throws IOException, NoSuchElementException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("order/"+getLineName()+" ("+name+").txt")));
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
		ensureLoaded();
		ArrayList<String> times = new ArrayList<String>();
		for(Station s:stationsByTime) {
			StringBuilder b = new StringBuilder();
			Estimate e = est.get(s);
			b.append(s.getName());
			b.append(" - \t");
			b.append(formatTime(e.getTimes1()));
			b.append(", \t");
			b.append(formatTime(e.getTimes2()));
			String er = e.getStatus().toString();
			if(er.length()>0) {
				b.append("\t - ");
				b.append(er);
			}
			b.append("\n");
		}
		return times;
	}
	
	@Override
	protected void loadLazyResources(DetachableStream res, DataVersion version) throws IOException {
		this.extId = res.readString();
		this.name = res.readString();
		this.niceName = res.readString();
		
		int stationCount = res.readInt();
		for(int j=0;j<stationCount;j++) {
			String stationId = res.readString();
			
			Station s = city.getStation(stationId);
			concatenate(s);
		}
	}
	
	private void persistLazy(DataOutputStream lazy) throws IOException {
		byte[] b;
		// lazy path resources
		b = extId.getBytes();
		lazy.writeInt(b.length); lazy.write(b);
		
		b = getName().getBytes();
		lazy.writeInt(b.length); lazy.write(b);

		b = getNiceName().getBytes();
		lazy.writeInt(b.length); lazy.write(b);
		
		lazy.writeInt(getStationsByPath().size());
		for(Station s:getStationsByPath()) {
			b = s.getId().getBytes();
			lazy.writeInt(b.length); lazy.write(b);
		}
		lazy.flush();
	}	

	@Override
	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		byte[] b;
		
		// eager path resources
		eager.writeInt(id);
		assert line.getPaths().contains(this);
		b = getLineName().getBytes();
		eager.writeInt(b.length); eager.write(b);

		eager.writeInt(lazyId);

		persistLazy(lazy);
	}

	public static Path loadEager(DetachableStream eager, City city) throws IOException {
		int pathId = eager.readInt();
		String lineName = eager.readString();
		Line line = city.getLineByName(lineName);
		Path path = new Path(line, pathId, eager.readInt(), city);
		line.addEagerPath(path);
		return path;
	}
	
	@Override
	public String toString() {
		return "Path: "+id+"["+getLineName()+"]("+Integer.toHexString(hashCode())+")";
	}
	
	
	/*
	 * 
	 */
	
	private class StationTimesComp implements Comparator<Station>, Serializable {
		private static final long serialVersionUID = 1L;

		public int compare(Station s1, Station s2) {
			Estimate e1 = est.get(s1);
			Estimate e2 = est.get(s2);
			
			String 
				t11 = e1.getTimes1(),
				t12 = e1.getTimes2(),
				t21 = e2.getTimes1(),
				t22 = e2.getTimes2();
			int c1 = compareTimes(t11, t21);
			if(c1!=0) return c1;
			
			int c2 = compareTimes(t12, t22);
			if(c2!=0) return c2;
			
			int c3 = e1.getStatus().compareTo(e2.getStatus()); 
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
	
	public static class LabelComparator implements Comparator<Path> {

		@Override
		public int compare(Path p1, Path p2) {
			return p1.getLabel().compareTo(p2.getLabel());
		}
		
	}
}
