package ro.mihai.tpt.model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ro.mihai.util.DetachableStream;
import ro.mihai.util.LineKind;

public class Line extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private String name;
	private Map<String, Path> paths;
	private Path first;

	public Line(String name, int resId, City city) {
		super(resId, city);
		this.name = name;
		this.paths = new HashMap<String, Path>();
	}

	public Line(String name, City city) {
		this(name, -1, city);
	}
	
	public Line(String name) {
		this(name, null);
	}
	public boolean isFake() {
		for (Path p : paths.values())
			if (p.isFake())
				return true;
		return false;
	}

	public List<String> getSortedPathNames() {
		List<String> pathNames = new ArrayList<String>();
		for(Path p : paths.values())
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
	
	public Set<Station> getStations() {
		ensureLoaded();		
		Set<Station> all = new LinkedHashSet<Station>();
		for(Path p: paths.values())
			all.addAll(p.getStationsByPath());
		return all;
	}
	
	public LineKind getKind() {
		return LineKind.getKind(this);
	}

	@Override
	public String toString() {
		return "Line: "+name+"["+getPathNames()+"]("+Integer.toHexString(hashCode())+")";
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

	public String getName() {
		return name;
	}

	/*
	 * 
	 */

	protected void loadLazyResources(DetachableStream res, DataVersion version) throws IOException {
		int pathCount = res.readInt();
		for(int i=0;i<pathCount;i++) {
			String pathId = res.readString();
			String pathName = res.readString();
			String pathNiceName = res.readString();
			
			Path p = new Path(pathId, pathName, this);
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
			b = p.getId().getBytes();
			lazy.writeInt(b.length); lazy.write(b);
			
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