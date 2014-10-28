/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

import ro.mihai.tpt.SaveFileException;
import ro.mihai.tpt.RATT.LineReader;
import ro.mihai.tpt.RATT.StationReader;
import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;
import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.IMonitor;

public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	DataVersion version;
	private Map<String, Station> stations;
	private Map<String, Line> lineNameMap;
	private ArrayList<Path> pathIdMap;
	
	private Map<Integer, Junction> junctionMap;
	
	private BPInputStream in;
	
	public City() {
		this.lineNameMap = new HashMap<String, Line>();
		this.pathIdMap = new ArrayList<Path>();
		this.junctionMap = new HashMap<Integer, Junction>();
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

	public void setJunctions(List<Junction> junctions) {
		this.junctionMap = new HashMap<Integer, Junction>();
		for(Junction j:junctions)
			this.junctionMap.put(j.getId(), j);
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
		Path p = new Path(line, pathIdMap.size(), extId, name);
		pathIdMap.add(p);
		return p;
	}
	
	protected Junction getJunctionById(int id) {
		return junctionMap.get(id);
	}
	
	public Collection<Station> getStations() {
		return stations.values();
	}
	
	public Collection<Junction> getJunctions() {
		return junctionMap.values();
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
		os.writeMagic("CityLineCache = 4.0.0;");

		// collections of entities are stored in blocks
		// each collection is split in two parts 
		// (a) a mandatory information part - loaded at startup 
		// (b) a deferred loading part - loaded as needed

		BPMemoryOutputStream lazyRes = BPMemoryOutputStream.usingByteArray();

		os.writeEntityCollection(lineNameMap.values(), lazyRes);
		os.writeEntityCollection(pathIdMap, lazyRes);
		os.writeEntityCollection(stations.values(), lazyRes);
		os.writeEntityCollection(junctionMap.values(), lazyRes);

		os.writeLazyBlock(lazyRes);
		
		os.flush();
		os.close();
	}
	
	public BPInputStream getDetachableInputStream() {
		return in;
	}
	
	public void loadFromStream(BPInputStream in, IMonitor mon, int dbEntries) throws IOException {
		String sigStr;
		try {
			sigStr = in.readFixedLengthString(22);
		} catch(IOException e) {
			throw new IOException("Failed to read signature before stream ended.");
		}

		if(!sigStr.startsWith("CityLineCache = "))
			throw new IOException("Signature expected, something else found, assuming wrong file.");
		if(sigStr.contains("1.0.0")) {
			version = DataVersion.Version1;
			loadFromFile1Rest(mon, new FormattedTextReader(in.getInputStream()));
			throw new SaveFileException();
		} else if(sigStr.contains("2.0.0")) {
			version = DataVersion.Version2;
			loadFromFile2Rest(mon, in);
			throw new SaveFileException();
		} else if(sigStr.contains("3.0.0")) {
			version = DataVersion.Version3;
		} else if(sigStr.contains("4.0.0")) {
			version = DataVersion.Version5;
		} else {
			assert(sigStr.contains("5.0.0"));
			version = DataVersion.Version6;
		}
		this.in = in;
		mon.setMax(dbEntries);
		
		int monitorCount = 0;
		Iterator<?> it;
		
		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Line l = Line.loadEager(in, this);
			lineNameMap.put(l.getName(), l);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Path p = Path.loadEager(in, this);
			
			while(pathIdMap.size() <= p.getId())
				pathIdMap.add(null);
			pathIdMap.set(p.getId(), p);
			mon.workComplete();
		}
		
		stations = new HashMap<String, Station>();
		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Station s = Station.loadEager(in, this);
			stations.put(s.getId(), s);
			mon.workComplete();
		}

		it = in.readEntityCollection();
		while (it.hasNext()) {
			it.next(); monitorCount++;
			Junction s = Junction.loadEager(in, this);
			junctionMap.put(s.getId(), s);
			mon.workComplete();
		}
		assert mon.getMax() == monitorCount: "Max: "+(monitorCount)+"!="+mon.getMax();

		in.mark(in.skipToLazyBlock());
	}
		
	@Deprecated
	public void loadFromFile1(InputStream in,IMonitor mon) throws IOException {
		FormattedTextReader rd = new FormattedTextReader(in);
		String version = rd.readString("CityLineCache = ", ";");
		assert(version.equals("1.0.0"));
		
		loadFromFile1Rest(mon, rd);
	}

	@Deprecated
	private void loadFromFile1Rest(IMonitor mon, FormattedTextReader rd) throws IOException {
		StationReader str = new StationReader(rd);
		int stationCount = Integer.parseInt(rd.readString("StationCount = ", ";"));
		mon.setMax(stationCount*2);
		stations = new HashMap<String, Station>();
		for(int i=0;i<stationCount;i++) { 
			Station s = str.read();
			stations.put(s.getId(), s);
			mon.workComplete();
		}
		
		for(Station s : stations.values()) {
			String stationId = rd.readString("StationId = ", ";");
			assert(stationId.equals(s.getId()));
			
			int lineCount = Integer.parseInt(rd.readString("LineCount = ", ";"));
			for(int i=0;i<lineCount;i++)
				new LineReader(this,s,rd).read();
			mon.workComplete();
		}
	}
	
	@Deprecated
	private void loadFromFile2Rest(IMonitor mon, BPInputStream in) throws IOException {
		int lineCount = in.readInt();
		for(int i=0;i<lineCount;i++) {
			String id = in.readString();
			
			String name = in.readString();
			Line l = newLine(name);
			Path p = newPath(l,id,"");
			l.addPath(p); 
			// Ver 2.0.0 does not suppor`t multiple paths per line, only a single one
		}
		
		int stationCount = in.readInt();
		stations = new HashMap<String, Station>();
		mon.setMax(stationCount);
		for(int i=0;i<stationCount;i++) {
			String id = in.readString();
			String name = in.readString();
			Station s = new Station(id, name);
			
			lineCount = in.readInt();
			for(int j=0;j<lineCount;j++) {
				String extId = in.readString();
				for(Path p : pathIdMap)
					if (p.getExtId().equals(extId)) {
						p.concatenate(s);
						s.addPath(p);
						break;
					}
			}
			stations.put(s.getId(), s);
			mon.workComplete();
		}
	}	
}
