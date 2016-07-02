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
	private ArrayList<Line> lines;
	private TreeMap<String, Line> orderedLineNameMap;
	private ArrayList<Path> paths;
	private ArrayList<Station> stations;
	private ArrayList<Junction> junctions;
	
	private BPInputStream in;
	
	public City() {
		this.lines = new ArrayList<Line>();
		this.orderedLineNameMap = new TreeMap<String, Line>();
		
		this.paths = new ArrayList<Path>();
		this.junctions = new ArrayList<Junction>();
		this.stations = new ArrayList<Station>();
	}
	
	public Collection<Line> getLines() {
		return lines;
	}
	
	public Collection<String> getLineNamesSorted() {
		return orderedLineNameMap.keySet();
	}

	public Collection<Line> getLinesSorted() {
		return orderedLineNameMap.values();
	}

	public Collection<Path> getPaths() {
		return paths;
	}

	public Collection<Station> getStations() {
		return stations;
	}
	
	public Collection<Junction> getJunctions() {
		return junctions;
	}
	
	
	
	public Line newLine(String name) {
		return newLine(lines.size(), name);
	}
	
	public Line newLine(int lineId, String name) {
		Line l = new Line(lineId, name);
		setAt(lines, lineId, l);
		orderedLineNameMap.put(name, l);
		return l;
	}

	public Path newPath(Line line, String extId, String name) {
		return newPath(paths.size(), line, extId, name);
	}
	
	public Path newPath(int pathId, Line line, String extId, String name) {
		Path p = new Path(pathId, line, extId, name);
		setAt(paths, pathId, p);
		return p;
	}

	public Station newStation(String extId, String name) {
		return newStation(stations.size(), extId, name);
		
	}
	public Station newStation(int stationId, String extId, String name) {
		Station s = new Station(stationId, extId, name);
		setAt(stations, stationId, s);
		return s;
	}

	public Junction newJunction(String name) {
		return newJunction(junctions.size(), name);
	}
	
	public Junction newJunction(int junctionId, String name) {
		Junction junction = new Junction(junctionId, name);
		setAt(junctions, junctionId, junction);
		return junction;
	}

	protected Line getLineById(int lineId) throws IOException {
		Line l = lines.get(lineId);
		if(null==l) 
			throw new IOException();
		return l;
	}
	
	public Line getLineByName(String lineName) {
		return orderedLineNameMap.get(lineName);
	}
	
	protected Path getPathById(int pathId) throws IOException {
		Path p = paths.get(pathId);
		if(null==p) 
			throw new IOException();
		return p;
	}
	
	protected Junction getJunctionById(int id) {
		return junctions.get(id);
	}

	protected Station getStationById(int id) {
		return stations.get(id);
	}
	
	public String linesAndStationsToString() {
		StringBuilder b = new StringBuilder();
		for(Line l:lines) {
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

		os.writeEntityCollection(lines, lazyRes);
		os.writeEntityCollection(paths, lazyRes);
		os.writeEntityCollection(stations, lazyRes);
		os.writeEntityCollection(junctions, lazyRes);

		os.writeLazyBlock(lazyRes);
		
		os.flush();
		os.close();
	}
	
	public BPInputStream getDetachableInputStream() {
		return in;
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
			setAt(lines, l.getId(), l);
			orderedLineNameMap.put(l.getName(), l);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Path p = PersistentEntity.loadEagerPath(in, this);
			setAt(paths, p.getId(), p);
			mon.workComplete();
		}
		
		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Station s = PersistentEntity.loadEagerStation(in, this);
			setAt(stations, s.getId(), s);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Junction j = PersistentEntity.loadEagerJunction(in, this);
			setAt(junctions, j.getId(), j);
			mon.workComplete();
		}
		assert mon.getMax() == monitorCount: "Max: "+(monitorCount)+"!="+mon.getMax();

		in.mark(in.skipToLazyBlock());
	}

	private static <T> void setAt(List<T> list, int index, T item) {
		while(list.size() <= index)
			list.add(null);
		assert(list.get(index) == null);
		list.set(index, item);
	}
}
