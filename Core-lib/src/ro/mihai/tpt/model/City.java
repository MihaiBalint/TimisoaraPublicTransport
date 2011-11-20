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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.*;

import ro.mihai.tpt.SaveFileException;
import ro.mihai.tpt.RATT.LineReader;
import ro.mihai.tpt.RATT.StationReader;
import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.IMonitor;
import ro.mihai.util.DetachableStream;

public class City implements Serializable {
	private static final long serialVersionUID = 1L;
	private DataVersion version;
	private Map<String, Station> stations;
	private Map<String,Line> lineMap;
	private Map<String, Junction> junctionMap;
	
	private DetachableStream in;
	private long lineResStart, stationResStart;
	
	public City() {
		this.lineMap = new HashMap<String, Line>();
		this.junctionMap = new HashMap<String, Junction>();
	}
	
	public Collection<Line> getLines() {
		return lineMap.values();
	}
	
	public Collection<String> getLineNamesSorted() {
		ArrayList<String> names = new ArrayList<String>();
		for(INamedEntity s : lineMap.values()) names.add(s.getName());
		Collections.sort(names);
		return names;
	}
	
	public void setStations(List<Station> stations) {
		this.stations = new HashMap<String, Station>();
		for(Station s:stations)
			this.stations.put(s.getId(), s);
	}

	public void setJunctions(List<Junction> junctions) {
		this.junctionMap = new HashMap<String, Junction>();
		for(Junction j:junctions)
			this.junctionMap.put(j.getName(), j);
	}

	
	public Line getOrCreateLine(String id, String name, boolean singlePath) {
		Line l = lineMap.get(id);
		if(null==l) {
			l = new Line(id,name);
			if (singlePath) {
				l.addPath(new Path(l,""));
			}
			lineMap.put(id, l);
		}
		return l;
	}
	
	protected Junction getOrCreateJunction(String name) {
		Junction j = junctionMap.get(name);
		if(null==j) {
			j = new Junction(name);
			junctionMap.put(name, j);
		}
		return j;
	}
	
	public Collection<Station> getStations() {
		return stations.values();
	}
	
	public Collection<Junction> getJunctions() {
		return junctionMap.values();
	}
	
	private int fakeLines = 0;
	public Line getLine(String name) {
		for(Line l:lineMap.values()) 
			if(l.getName().equals(name))
				return l;
		fakeLines++;
		return getOrCreateLine("F"+fakeLines, name, true);
	}
	
	public Line getLineById(String lineId) throws IOException {
		Line l = lineMap.get(lineId);
		if(null==l) throw new IOException();
		return l;
	}
	
	public Station getStation(String id) {
		return stations.get(id);
	}
	
	public String linesAndStationsToString() {
		StringBuilder b = new StringBuilder();
		for(Line l:lineMap.values()) {
			b.append(l.getName());
			if(l.getPaths().size() == 1) {
				b.append(" - ");
				for(Station s : l.getFirstPath().getStationsByPath()) {
					b.append(s.getName());
					b.append(", ");
				}
				b.append("\n");
			} else if(l.getPaths().isEmpty()) {
				b.append(": no stations.\n");
			} else {
				b.append(" "+l.getPaths().size()+" paths.\n");
				for(Path p : l.getPaths()) {
					b.append("\t"+p.getName()+" - ");
					for(Station s : p.getStationsByPath()) {
						b.append(s.getName());
						b.append(", ");
					}
					b.append("\n");
				}
			}
		}
		return b.toString();
	}
	
	
	public void saveToFile(OutputStream out) throws IOException {
		byte[] b;
		
		DataOutputStream os = new DataOutputStream(out);
		os.write("CityLineCache = 4.0.0;".getBytes());

		ByteArrayOutputStream lineResBuf = new ByteArrayOutputStream();
		DataOutputStream lnRes = new DataOutputStream(lineResBuf);
		
		os.writeInt(lineMap.size());
		for(Line l:lineMap.values()) {
			// line resources
			int resId = lineResBuf.size();
			l.writeResources(lnRes);
			lnRes.flush();
			
			b = l.getId().getBytes();
			os.writeInt(b.length); os.write(b);
			
			b = l.getName().getBytes();
			os.writeInt(b.length); os.write(b);

			os.writeInt(resId);
		}
		
		ByteArrayOutputStream stationResBuf = new ByteArrayOutputStream();
		DataOutputStream stRes = new DataOutputStream(stationResBuf);
		
		os.writeInt(stations.size());
		for(Station s: stations.values()) {
			// station resources
			int resId = stationResBuf.size();
			s.writeResources(stRes);
			stRes.flush();
			
			b = s.getId().getBytes();
			os.writeInt(b.length); os.write(b);

			os.writeInt(resId); 
		}
		
		// the data indexe
		os.writeInt(lineResBuf.size());
		os.writeInt(stationResBuf.size());
		
		// the data
		os.write(lineResBuf.toByteArray());
		os.write(stationResBuf.toByteArray());
		
		os.flush();
		os.close();
	}
	
	public DetachableStream getDetachableInputStream() {
		return in;
	}
	
	public void loadFromStream(DetachableStream in, IMonitor mon) throws IOException {
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
			loadFromFile2Rest(mon, in.getInputStream());
			throw new SaveFileException();
		} else if(sigStr.contains("3.0.0")) {
			version = DataVersion.Version3;
		} else {
			assert(sigStr.contains("4.0.0"));
			version = DataVersion.Version4;
		}
		this.in = in;
		
		int lineCount = in.readInt();
		for(int i=0;i<lineCount;i++) {
			String id = in.readString();

			String name = in.readString();
			
			Line l = new Line(id, name, in.readInt(), this);
			lineMap.put(id, l);
		}

		int stationCount = in.readInt();
		stations = new HashMap<String, Station>();
		mon.setMax(stationCount);
		for(int i=0;i<stationCount;i++) {
			String id = in.readString();
			
			Station s = new Station(id, in.readInt(), this);
			stations.put(s.getId(), s);
			mon.workComplete();
		}
		
		int lnResSize = in.readInt();
		int stResSize = in.readInt();
		
		lineResStart = 0;
		stationResStart = lnResSize;
		
		in.mark(lnResSize+stResSize);
	}
		
	public synchronized void loadStationResources(Station s) {
		try {
			in.reset();
			in.sureSkip(stationResStart+s.getResId());
			s.readResources(in, version);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized void loadLineResources(Line l) {
		try {
			in.reset();
			in.sureSkip(lineResStart+l.getResId());
			l.readResources(in, version);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
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
	private void loadFromFile2Rest(IMonitor mon, DataInputStream in) throws IOException {
		int bc;
		byte[] b;
		int lineCount = in.readInt();
		for(int i=0;i<lineCount;i++) {
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String id = new String(b);
			
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String name = new String(b);
			Line l = new Line(id,name);
			l.addPath(new Path(l,"")); 
			// Ver 2.0.0 does not suppor`t multiple paths per line, only a single one
			lineMap.put(id, l);
		}
		
		int stationCount = in.readInt();
		stations = new HashMap<String, Station>();
		mon.setMax(stationCount);
		for(int i=0;i<stationCount;i++) {
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String id = new String(b);
			
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String name = new String(b);
			Station s = new Station(id, name);
			
			lineCount = in.readInt();
			for(int j=0;j<lineCount;j++) {
				bc = in.readInt(); b = new byte[bc]; in.readFully(b);
				
				Line l = lineMap.get(new String(b));
				Path p = l.getFirstPath(); 
				p.concatenate(s);
				s.addLine(l);
			}
			stations.put(s.getId(), s);
			mon.workComplete();
		}
	}	
}
