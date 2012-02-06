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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ro.mihai.util.DetachableStream;
import ro.mihai.util.LineKind;

public class Line extends PersistentEntity implements INamedEntity, Serializable {
	private static final long serialVersionUID = 1L;
	private String name, id;
	private Map<String,Path> paths;
	private Path first;
	
	public Line(String id, String name, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.name = name;
		this.paths = new HashMap<String, Path>();
	}
	
	public Line(String id, String name) {
		this(id,name,-1,null);
	}
	
	public LineKind getKind() {
		return LineKind.getKind(this);
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

	protected void loadLazyResources(DetachableStream res, DataVersion version) throws IOException {
		int pathCount = res.readInt();
		for(int i=0;i<pathCount;i++) {
			String pathName = res.readString();

			String pathNiceName = res.readString();
			
			Path p = new Path(this, pathName);
			p.setNiceName(pathNiceName);
			
			int stationCount = res.readInt();
			for(int j=0;j<stationCount;j++) {
				String stationId = res.readString();
				
				Station s = city.getStation(stationId);
				p.concatenate(s);
			}
			addPath(p);
		}
	}


	private void persistLazy(DataOutputStream lazy) throws IOException {
		byte[] b;
		// lazy line resources
		lazy.writeInt(paths.size());
		for(Path p:paths.values()) {
			b = p.getName().getBytes();
			lazy.writeInt(b.length); lazy.write(b);

			b = p.getNiceName().getBytes();
			lazy.writeInt(b.length); lazy.write(b);
			
			lazy.writeInt(p.getStationsByPath().size());
			for(Station s:p.getStationsByPath()) {
				b = s.getId().getBytes();
				lazy.writeInt(b.length); lazy.write(b);
			}
		}
		lazy.flush();
	}

	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		byte[] b;
		
		// eager line resources
		b = id.getBytes();
		eager.writeInt(b.length); eager.write(b);
		
		b = name.getBytes();
		eager.writeInt(b.length); eager.write(b);

		eager.writeInt(lazyId);

		persistLazy(lazy);
	}

	public static Line loadEager(DetachableStream eager, City city) throws IOException {
		String id = eager.readString();

		String name = eager.readString();
		
		return new Line(id, name, eager.readInt(), city);
	}
}

