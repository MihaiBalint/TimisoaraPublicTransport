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
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;
import ro.mihai.util.IMonitor;

public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Station> stations;
	private Map<String, Line> lineNameMap;
	private ArrayList<Path> pathIdMap;
	private ArrayList<Junction> junctionMap;
	
	private BPInputStream in;
	
	public City() {
		this.lineNameMap = new HashMap<String, Line>();
		this.pathIdMap = new ArrayList<Path>();
		this.junctionMap = new ArrayList<Junction>();
	}
	
	public Collection<Line> getLines() {
		return lineNameMap.values();
	}
	
	public Collection<Path> getPaths() {
		return pathIdMap;
	}
	
	public Collection<String> getLineNamesSorted() {
		ArrayList<String> names = new ArrayList<String>();
		for(Line s : lineNameMap.values()) names.add(s.getName());
		Collections.sort(names);
		return names;
	}
	
	public void setStations(List<Station> stations) {
		this.stations = new HashMap<String, Station>();
		for(Station s:stations)
			this.stations.put(s.getId(), s);
	}

	private Line newLine(String name) {
		Line l = new Line(name);
		lineNameMap.put(name, l);
		return l;
	}

	public Line getOrCreateLine(String name) {
		Line l = lineNameMap.get(name);
		if(null==l) 
			l = newLine(name);
		return l;
	}

	public Path newPath(Line line, String extId, String name) {
		return newPath(line, pathIdMap.size(), extId, name);
	}
	
	public Path newPath(Line line, int pathId, String extId, String name) {
		Path p = new Path(line, pathId, extId, name);
		setAt(pathIdMap, pathId, p);
		return p;
	}

	public Junction newJunction(String name) {
		return newJunction(junctionMap.size(), name);
	}
	
	public Junction newJunction(int junctionId, String name) {
		Junction junction = new Junction(junctionId, name);
		setAt(junctionMap, junctionId, junction);
		return junction;
	}

	protected Junction getJunctionById(int id) {
		return junctionMap.get(id);
	}
	
	public Collection<Station> getStations() {
		return stations.values();
	}
	
	public Collection<Junction> getJunctions() {
		return junctionMap;
	}
	
	private int fakePaths = 0;
	public Line getLine(String name) {
		Line l = lineNameMap.get(name);
		if(l != null)
			return l;
		fakePaths++;
		l = newLine(name);
		l.addPath(newPath(l, "F"+fakePaths, ""));
		return l;
	}
	
	public Line getLineByName(String lineName) throws IOException {
		Line l = lineNameMap.get(lineName);
		if(null==l) throw new IOException();
		return l;
	}
	
	public Path getPathById(int pathId) throws IOException {
		Path p = pathIdMap.get(pathId);
		if(null==p) 
			throw new IOException();
		return p;
	}
	
	public Station getStation(String id) {
		return stations.get(id);
	}
	
	public String linesAndStationsToString() {
		StringBuilder b = new StringBuilder();
		for(Line l:lineNameMap.values()) {
			b.append(l.getName());
			if(l.getPaths().size() == 1) {
				b.append(" - ");
				for(Estimate e : l.getFirstPath().getEstimatesByPath()) {
					b.append(e.getStation().getName());
					b.append(", ");
				}
				b.append("\n");
			} else if(l.getPaths().isEmpty()) {
				b.append(": no stations.\n");
			} else {
				b.append(" "+l.getPaths().size()+" paths.\n");
				for(Path p : l.getPaths()) {
					b.append("\t"+p.getName()+" - ");
					for(Estimate e : p.getEstimatesByPath()) {
						b.append(e.getStation().getName());
						b.append(", ");
					}
					b.append("\n");
				}
			}
		}
		return b.toString();
	}
	
	
	public void saveToFile(OutputStream out) throws IOException {
		BPOutputStream os = new BPOutputStream(out);
		os.writeMagic("CityLineCache = 5.0.0;");

		// collections of entities are stored in blocks
		// each collection is split in two parts 
		// (a) a mandatory information part - loaded at startup 
		// (b) a deferred loading part - loaded as needed

		BPMemoryOutputStream lazyRes = BPMemoryOutputStream.usingByteArray();

		os.writeEntityCollection(lineNameMap.values(), lazyRes);
		os.writeEntityCollection(pathIdMap, lazyRes);
		os.writeEntityCollection(stations.values(), lazyRes);
		os.writeEntityCollection(junctionMap, lazyRes);

		os.writeLazyBlock(lazyRes);
		
		os.flush();
		os.close();
	}
	
	public BPInputStream getDetachableInputStream() {
		return in;
	}
	
	private static <T> void setAt(List<T> list, int index, T item) {
		while(list.size() <= index)
			list.add(null);
		assert(list.get(index) == null);
		list.set(index, item);
	}
	
	public void loadFromStream(BPInputStream in, IMonitor mon, int dbEntries) throws IOException {
		String magic;
		try {
			magic = in.readFixedLengthString(22);
		} catch(IOException e) {
			throw new IOException("Failed to read signature before stream ended.");
		}

		if(!magic.startsWith("CityLineCache = 5.0.0;"))
			throw new IOException("Signature expected, something else found, assuming wrong file.");
		
		this.in = in;
		mon.setMax(dbEntries);
		
		int monitorCount = 0;
		Iterator<?> it;
		
		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Line l = PersistentEntity.loadEagerLine(in, this);
			lineNameMap.put(l.getName(), l);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Path p = PersistentEntity.loadEagerPath(in, this);
			setAt(pathIdMap, p.getId(), p);
			mon.workComplete();
		}
		
		stations = new HashMap<String, Station>();
		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Station s = PersistentEntity.loadEagerStation(in, this);
			stations.put(s.getId(), s);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Junction j = PersistentEntity.loadEagerJunction(in, this);
			setAt(junctionMap, j.getId(), j);
			mon.workComplete();
		}
		assert mon.getMax() == monitorCount: "Max: "+(monitorCount)+"!="+mon.getMax();

		in.mark(in.skipToLazyBlock());
	}
}
