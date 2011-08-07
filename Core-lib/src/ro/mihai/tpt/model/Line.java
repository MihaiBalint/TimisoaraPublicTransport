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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Line implements INamedEntity {
	private long resId;
	private boolean loaded;
	private City city;
	private String name, id;
	private Map<String,Path> paths;
	private Path first;
	
	public Line(String id, String name, long resId, City city) {
		this.id = id;
		this.name = name;
		this.paths = new HashMap<String, Path>();

		this.resId = resId;
		this.city = city;
		this.loaded = false;
		
	}
	
	public Line(String id, String name) {
		this(id,name,-1,null);
		
		this.loaded = true;
	}
	
	
	public Path getPath(String name) {
		ensureLoaded();		
		return paths.get(name);
	}
	
	public Path getFirstPath() {
		ensureLoaded();		
		assert(first!=null);
		return first;
	}
	
	public Collection<Path> getPaths() {
		ensureLoaded();		
		return paths.values();
	}
	
	public void addPath(Path p) {
		if(paths.isEmpty()) 
			first = p;
		paths.put(p.getName(), p);
	}
	
	// merges stations from the empty-name-path to the other paths
	public void pathMerge() {
		if(getPaths().size()<=1) return;
		Path tailsPath = getPath(""); 
		if(tailsPath==null) return;
		
		paths.remove("");
		for(Path p:paths.values()) {
			for(Station s:tailsPath.getStationsByPath())
				p.concatenate(s);
		}
	} 
	
	public Set<Station> getStations() {
		ensureLoaded();		
		Set<Station> all = new LinkedHashSet<Station>();
		for(Path p: paths.values())
			all.addAll(p.getStationsByPath());
		return all;
	}
	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public long getResId() {
		return resId;
	}
	
	private synchronized void load() {
		if (loaded) return;
		city.loadLineResources(this);
		loaded = true;
	}
	private void ensureLoaded() {
		if (loaded) return;
		load();
	}
	
	protected void readResources(DataInputStream res) throws IOException {
		int bc;
		byte[] b;
		
		int pathCount = res.readInt();
		for(int i=0;i<pathCount;i++) {
			bc = res.readInt(); b = new byte[bc]; res.readFully(b);
			String pathName = new String(b);

			bc = res.readInt(); b = new byte[bc]; res.readFully(b);
			String pathNiceName = new String(b);
			
			Path p = new Path(this, pathName);
			p.setNiceName(pathNiceName);
			
			int stationCount = res.readInt();
			for(int j=0;j<stationCount;j++) {
				bc = res.readInt(); b = new byte[bc]; res.readFully(b);
				String stationId = new String(b);
				
				p.concatenate(city.getStation(stationId));
			}
			addPath(p);
		}
	}

	protected void writeResources(DataOutputStream res) throws IOException {
		ensureLoaded();		
		byte[] b;
		
		res.writeInt(paths.size());
		for(Path p:paths.values()) {
			b = p.getName().getBytes();
			res.writeInt(b.length); res.write(b);

			b = p.getNiceName().getBytes();
			res.writeInt(b.length); res.write(b);
			
			res.writeInt(p.getStationsByPath().size());
			for(Station s:p.getStationsByPath()) {
				b = s.getId().getBytes();
				res.writeInt(b.length); res.write(b);
			}
		}
	}
}

