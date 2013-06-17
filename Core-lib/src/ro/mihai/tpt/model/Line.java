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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.mihai.util.DetachableStream;
import ro.mihai.util.LineKind;

public class Line extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Set<Path> paths;
	private Map<String,Path> pathNames;
	private Path first;
	
	private Line(String name, long resId, City city) {
		super(resId, city);
		this.name = name;
		this.paths = new HashSet<Path>();
		this.pathNames = new HashMap<String, Path>();
	}
	
	public Line(String name) {
		this(name,-1,null);
	}
	
	public List<String> getSortedPathNames() {
		ensureLoaded();		
		List<String> pathNames = new ArrayList<String>();
		for(Path p : paths)
			pathNames.add(p.getNiceName());
		Collections.sort(pathNames);
		return pathNames;
	}
	
	public String getPathNames() {
		boolean first = true;
		String pathNames = "";
		for(String pathName : getSortedPathNames())
			if (first) {
				pathNames += pathName;
				first = false;
			} else
				pathNames += ", "+pathName;
		return pathNames;
	}
	
	
	@Override
	public String toString() {
		return "Line: "+name+"["+getPathNames()+"]("+Integer.toHexString(hashCode())+")";
	}
	
	public LineKind getKind() {
		return LineKind.getKind(this);
	}
	
	public Path getPath(String name) {
		ensureLoaded();		
		return pathNames.get(name);
	}
	
	public Path getFirstPath() {
		ensureLoaded();		
		assert(first!=null);
		return first;
	}
	
	public Collection<Path> getPaths() {
		ensureLoaded();		
		return paths;
	}

	public void addPath(Path p) {
		addEagerPath(p);
		pathNames.put(p.getName(), p);
	}
	
	void addEagerPath(Path p) {
		if(paths.isEmpty()) 
			first = p;
		assert p.getLineName().equals(name);
		paths.add(p);
	}
	
	// merges stations from the empty-name-path to the other paths
	public void pathMerge() {
		if(getPaths().size()<=1) return;
		Path tailsPath = getPath(""); 
		if(tailsPath==null) return;
		
		paths.remove(tailsPath);
		pathNames.remove("");
		for(Path p:paths) {
			for(Estimate e : tailsPath.getEstimatesByPath())
				p.concatenate(e.getStation());
		}
	} 
	
	public Set<Station> getStations() {
		ensureLoaded();		
		Set<Station> all = new LinkedHashSet<Station>();
		for(Path p: paths)
			for(Estimate e : p.getEstimatesByPath())
				all.add(e.getStation());
		return all;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isFake() {
		for (Path p: paths)
			if (p.isFake())
				return true;
		return false;
	}

	protected void loadLazyResources(DetachableStream res, DataVersion version) throws IOException {
		for(Path p: paths)
			pathNames.put(p.getName(), p);
	}


	private void persistLazy(DataOutputStream lazy) throws IOException {
		for(Path p:paths) {
			assert p.getLineName().equals(name);
		}
	}

	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		byte[] b;
		
		// eager line resources
		b = name.getBytes();
		eager.writeInt(b.length); eager.write(b);

		eager.writeInt(lazyId);

		persistLazy(lazy);
	}

	public static Line loadEager(DetachableStream eager, City city) throws IOException {
		String name = eager.readString();
		
		return new Line(name, eager.readInt(), city);
	}
}

