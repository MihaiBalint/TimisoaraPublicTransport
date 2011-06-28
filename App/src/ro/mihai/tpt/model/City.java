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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ro.mihai.tpt.SaveFileException;
import ro.mihai.tpt.RATT.LineReader;
import ro.mihai.tpt.RATT.StationReader;
import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.IMonitor;

public class City {
	private List<Station> stations;
	private Map<String,Line> lineMap;
	
	public City() {
		this.lineMap = new HashMap<String, Line>();
	}
	
	public void setStations(List<Station> stations) {
		this.stations = stations;
	}
	
	public Line getOrCreateLine(String id, String name) {
		Line l = lineMap.get(id);
		if(null==l) {
			l = new Line(id,name);
			lineMap.put(id, l);
		}
		return l;
	}
	
	public List<Station> getStations() {
		return stations;
	}
	
	private int fakeLines = 0;
	public Line getLine(String name) {
		for(Line l:lineMap.values()) 
			if(l.getName().equals(name))
				return l;
		fakeLines++;
		return getOrCreateLine("F"+fakeLines, name);
	}
	
	public String linesAndStationsToString() {
		StringBuilder b = new StringBuilder();
		for(Line l:lineMap.values()) {
			b.append(l.getName());
			b.append(" - ");
			for(Station s : l.getStations()) {
				b.append(s.getName());
				b.append(", ");
			}
			b.append("\n");
		}
		return b.toString();
	}
	
	
	public void saveToFile1(OutputStream os) throws IOException {
		os.write("CityLineCache = 1.0.0;\n".getBytes());
		os.write(("StationCount = "+stations.size()+";\n").getBytes());
		
		for(Station s: stations) {
			String ser = "\t<option value=\""+s.getId()+"\">"+s.getName()+"</option>\n";
			os.write(ser.getBytes());
		}
		for(Station s: stations) {
			os.write(("StationId = "+s.getId()+";\n").getBytes());
			os.write(("LineCount = "+s.getLines().size()+";\n").getBytes());
			for(Line l:s.getLines()) {
				String ser = "\t<option value=\""+l.getId()+"\">"+l.getName()+"</option>\n";
				os.write(ser.getBytes());
			}
		}
		
		os.flush();
		os.close();
	}
	

	public void saveToFile(OutputStream out) throws IOException {
		DataOutputStream os = new DataOutputStream(out);
		os.write("CityLineCache = 2.0.0;".getBytes());
		byte[] b;
		
		os.writeInt(lineMap.size());
		for(Line l:lineMap.values()) {
			b = l.getId().getBytes();
			os.writeInt(b.length); os.write(b);
			
			b = l.getName().getBytes();
			os.writeInt(b.length); os.write(b);
		}
		
		os.writeInt(stations.size());
		for(Station s: stations) {
			b = s.getId().getBytes();
			os.writeInt(b.length); os.write(b);
			
			b = s.getName().getBytes();
			os.writeInt(b.length); os.write(b);
			
			os.writeInt(s.getLines().size());
			for(Line l:s.getLines()) {
				b = l.getId().getBytes();
				os.writeInt(b.length); os.write(b);
			}
		}
		
		os.flush();
		os.close();
	}
	
	public void loadFromFile(InputStream is,IMonitor mon) throws IOException {
		DataInputStream in = new DataInputStream(is);
		byte sig[] = new byte[22];
		int r=0;
		while(r<sig.length) {
			int rd = in.read(sig);
			if (rd<0) throw new IOException("Failed to read signature before stream ended.");
			r+=rd;
		}
		String sigStr = new String(sig);
		if(!sigStr.startsWith("CityLineCache = "))
			throw new IOException("Signature expected, something else found, assuming wrong file.");
		if(sigStr.contains("1.0.0")) {
			loadFromFile1Rest(mon, new FormattedTextReader(in));
			throw new SaveFileException();
		} 
		assert(sigStr.contains("2.0.0"));
		
		int bc;
		byte[] b;
		int lineCount = in.readInt();
		for(int i=0;i<lineCount;i++) {
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String id = new String(b);
			
			bc = in.readInt(); b = new byte[bc]; in.readFully(b);
			String name = new String(b);
			lineMap.put(id, new Line(id,name));
		}
		
		int stationCount = in.readInt();
		stations = new ArrayList<Station>(stationCount);
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
				l.addStation(s);
				s.addLine(l);
			}
			stations.add(s);
			mon.workComplete();
		}
	}
	
	public void loadFromFile1(InputStream in,IMonitor mon) throws IOException {
		FormattedTextReader rd = new FormattedTextReader(in);
		String version = rd.readString("CityLineCache = ", ";");
		assert(version.equals("1.0.0"));
		
		loadFromFile1Rest(mon, rd);
	}

	private void loadFromFile1Rest(IMonitor mon, FormattedTextReader rd) throws IOException {
		StationReader str = new StationReader(rd);
		int stationCount = Integer.parseInt(rd.readString("StationCount = ", ";"));
		mon.setMax(stationCount*2);
		stations = new ArrayList<Station>(stationCount);
		for(int i=0;i<stationCount;i++) { 
			stations.add(str.read());
			mon.workComplete();
		}
		
		for(Station s : stations) {
			String stationId = rd.readString("StationId = ", ";");
			assert(stationId.equals(s.getId()));
			
			int lineCount = Integer.parseInt(rd.readString("LineCount = ", ";"));
			for(int i=0;i<lineCount;i++)
				new LineReader(this,s,rd).read();
			mon.workComplete();
		}
	}
}
