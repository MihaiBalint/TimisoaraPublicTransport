/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011-2014  Mihai Balint

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;
import ro.mihai.util.IPrefs;
import static ro.mihai.util.Formatting.*;

public class Path extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String extId, name, niceName;
	private List<Segment> segments;
	private Line line;
	
	private List<Estimate> estimatesByPath;
	
	private Path(int id, long resId, City city, Line line) {
		super(resId, city);
		this.id = id;
		this.line = line;
		this.segments = new ArrayList<Segment>();
		this.estimatesByPath = new ArrayList<Estimate>();
	}
	
	protected Path(int id, Line line, String extId, String name) {
		this(id, -1, null, line);
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
	
	private Station temp;
	public void concatenate(Station s, HourlyPlan plan) {
		estimatesByPath.add(new Estimate(this, s, estimatesByPath.size(), plan));
		if(temp==null) {
			temp = s;
			return;
		} 
		segments.add(new Segment(temp, s));
		temp = s;
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
	public void saveEagerAndLazy(BPOutputStream eager, BPMemoryOutputStream lazy) throws IOException {
		super.saveEagerAndLazy(eager, lazy);
		
		eager.writeInt(getEstimatesByPath().size());
		for(Estimate e:getEstimatesByPath())
			e.getPlan().saveEagerAndLazy(eager, lazy);
	}

	private HourlyPlan[] eagerPlans;
	static Path loadEager(BPInputStream eager, int resId, City city) throws IOException {
		int pathId = eager.readObjectId();
		Line line = city.getLineById(eager.readObjectId());
		Path path = new Path(pathId, resId, city, line);
		line.addEagerPath(path);
		
		int planCount = eager.readInt();
		path.eagerPlans = new HourlyPlan[planCount];
		for (int i=0;i<planCount;i++)
			path.eagerPlans[i] = PersistentEntity.loadEagerHourlyPlan(eager, city);
		return path;
	}
	
	@Override
	protected void saveEager(BPOutputStream eager) throws IOException {
		// eager path resources
		assert line.getPaths().contains(this) : line+" does not contain "+this.toString();
		
		eager.writeObjectId(id);
		eager.writeObjectId(line.getId());
	}

	@Override
	protected void loadLazyResources(BPInputStream res) throws IOException {
		this.extId = res.readString();
		this.name = res.readString();
		this.niceName = res.readString();
		
		int stationCount = res.readInt();
		assert stationCount == eagerPlans.length;
		
		for(int j=0;j<stationCount;j++) {
			Station s = city.getStationById(res.readObjectId());
			concatenate(s, eagerPlans[j]);
		}
	}
	
	@Override
	protected void saveLazyResources(BPMemoryOutputStream lazy) throws IOException {
		// lazy path resources
		lazy.writeString(extId);
		lazy.writeString(getName());
		lazy.writeString(getNiceName());
		
		lazy.writeInt(getEstimatesByPath().size());
		for(Estimate e:getEstimatesByPath()) { 
			lazy.writeObjectId(e.getStation().getId());
		}
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
