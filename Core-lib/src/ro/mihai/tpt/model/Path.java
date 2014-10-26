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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ro.mihai.util.DetachableStream;
import ro.mihai.util.IPrefs;
import static ro.mihai.util.Formatting.*;

public class Path extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String extId, name, niceName;
	private List<Segment> segments;
	private Line line;
	
	private List<Estimate> estimatesByPath;
	
	private Path(Line line, int id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.line = line;
		this.segments = new ArrayList<Segment>();
		this.estimatesByPath = new ArrayList<Estimate>();
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
		estimatesByPath.add(new Estimate(this, s, estimatesByPath.size()));
	}
	
	public String getLabel() {
		return getLineName()+">"+getNiceName();
	}	

	public List<Estimate> getEstimatesByPath() {
		ensureLoaded();
		return estimatesByPath;
	}

	public Estimate getEstimateByPath(int stationIndex) {
		return getEstimatesByPath().get(stationIndex);
	}
	
	/*
	 * 
	 */

	public void clearAllUpdates() {
		ensureLoaded();
		for(Estimate e:estimatesByPath)
			e.clearUpdate();
	}
	
	public int updateStation(IPrefs prefs, int ec, Estimate e) {
		ensureLoaded();
		return e.updateStation(prefs, ec);
	}

	/*
	 * 
	 */
	
	public List<String> timesToString() {
		ensureLoaded();
		ArrayList<String> times = new ArrayList<String>();
		for(Estimate e:estimatesByPath) {
			StringBuilder b = new StringBuilder();
			b.append(e.getStation().getName());
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
		
		lazy.writeInt(getEstimatesByPath().size());
		for(Estimate e:getEstimatesByPath()) {
			b = e.getStation().getId().getBytes();
			lazy.writeInt(b.length); lazy.write(b);
		}
		lazy.flush();
	}	

	@Override
	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		byte[] b;
		
		// eager path resources
		eager.writeInt(id);
		assert line.getPaths().contains(this) : line+" does not contain "+this.toString();
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
	
	public static class LabelComparator implements Comparator<Path> {

		@Override
		public int compare(Path p1, Path p2) {
			return p1.getLabel().compareTo(p2.getLabel());
		}
		
	}
}
