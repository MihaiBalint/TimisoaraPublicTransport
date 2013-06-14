package ro.mihai.util;

import java.util.ArrayList;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;

public class PathList {
	private String parcel;
	
	public PathList(String parcel) {
		this.parcel = parcel;
	}
	
	private String getPathCode(Path path) {
		return path.getLineName() + "-" + path.getName() + "\n";
	}
	
	public Path read(String pathCode, City c) {
		int sep = pathCode.indexOf("-");
		if (sep<=0)
			return null;
		Line line = c.getLine(pathCode.substring(0, sep));
		Path path = line.getPath(pathCode.substring(sep+1));
		return path;
	}

	public ArrayList<Path> readPaths(City c) {
		ArrayList<Path> paths = new ArrayList<Path>();
		for(String pathCode : parcel.split("\n")) {
			Path path = this.read(pathCode, c);
			if (path==null || path.getLine().isFake())
				continue;
			paths.add(path);
		}
		return paths;
	}
	
	public PathList addPath(Path path) {
		String pathCode = getPathCode(path);
		if (parcel.contains(pathCode))
			return this;
		parcel = pathCode + parcel;
		return this;
	}
	
	public PathList removePath(Path path) {
		String pathCode = getPathCode(path);
		int pos = parcel.indexOf(pathCode);
		if (pos < 0)
			return this;
		parcel = parcel.substring(0, pos) + parcel.substring(pos + pathCode.length());
		return this;
	}
	
	public boolean containsPath(Path path) {
		String pathCode = getPathCode(path);
		return parcel.contains(pathCode);
	}
	
	public String write() {
		return parcel;
	}
}
