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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;
import ro.mihai.util.Formatting;

public class Station extends PersistentEntity implements INamedEntity, Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String extId, name, shortName, niceName, lat, lng;
	private List<Path> paths;
	private Junction junction;
	
	private Station(int id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.paths = new ArrayList<Path>(2);
	}

	protected Station(int id, String extId, String name) {
		this(id,-1, null);
		this.paths = new ArrayList<Path>(2);
		this.name = name;
	}
	
	@Override
	public String toString() {
		String lineNames = getLineNames();
		return "Station: "+id+" - "+getShortName()+"["+lineNames+"]("+Integer.toHexString(hashCode())+")";
	}

	public String getLineNames() {
		TreeSet<String> lineNameSet = new TreeSet<String>();
		for(Path path : paths) 
			lineNameSet.add(path.getLineName());
		return Formatting.join(", ", lineNameSet);
	}

	public int getId() {
		return id;
	}

	public String getExtId() {
		return extId;
	}
	
	public void addPath(Path p) {
		if (!paths.contains(p))
			paths.add(p);
	}
	
	public List<Path> getPaths() {
		ensureLoaded();
		return paths;
	}	
	
	public String getName() {
		ensureLoaded();
		return name;
	}
	public String getJunctionName() {
		ensureLoaded();
		return junction!=null ? junction.getName() : "";
	}
	

	
	public void setCoords(String lat, String lng) {
		this.lat = lat;
		this.lng = lng;
	}
	public String getLat() {
		ensureLoaded();
		return lat;
	}
	public String getLng() {
		ensureLoaded();
		return lng;
	}
	
	public void setNiceName(String niceName) {
		this.niceName = niceName;
	}
	public String getNiceName() {
		ensureLoaded();
		return niceName;
	}
	public boolean hasNiceName() {
		ensureLoaded();
		return niceName!=null && niceName.trim().length() > 0;
	}
	public String getNicestNamePossible() {
		if(hasNiceName()) return getNiceName();
		return name;
	}
	
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getShortName() {
		ensureLoaded();
		return shortName;
	}
	public boolean hasShortName() {
		ensureLoaded();
		return shortName!=null && shortName.trim().length() > 0;
	}
	
	public void setJunction(Junction junction) {
		this.junction = junction;
	}
	public Junction getJunction() {
		ensureLoaded();
		return junction;
	}
	public boolean hasJunctionName() {
		ensureLoaded();
		return junction!=null && junction.getName()!=null && junction.getName().trim().length() > 0;
	}
	
	@Override
	protected void loadLazyResources(BPInputStream lazy) throws IOException {
		this.name = lazy.readString();
		this.niceName = lazy.readString();
		this.shortName = lazy.readString();
		this.junction = city.getJunctionById(lazy.readInt());
		this.lat = lazy.readString();

		this.lng = lazy.readString();
		
		int pathCount = lazy.readInt();
		for(int i=0;i<pathCount;i++) {
			int pathId = lazy.readInt();
			paths.add(city.getPathById(pathId));
		}
	}
	
	@Override
	protected void saveLazyResources(BPMemoryOutputStream res) throws IOException {
		ensureLoaded();
		
		// lazy station resources
		res.writeString(getName());
		res.writeString(getNiceName());
		res.writeString(getShortName());

		if (junction!=null)
			res.writeInt(junction.getId());
		else
			res.writeInt(0);

		res.writeString(getLat());
		res.writeString(getLng());
		
		res.writeInt(paths.size());
		for(Path p : paths) {
			res.writeInt(p.getId());
		}
	}
	
	@Override
	public void saveEager(BPOutputStream eager) throws IOException {
		eager.writeObjectId(id);
	}
	
	public static Station loadEager(BPInputStream eager, int resId, City city) throws IOException {
		return new Station(eager.readObjectId(), resId, city);
	}
	
	public int distanceTo(Station b) {
		Station a = this;
		if (a.getLat().length()==0 || a.getLng().length()==0 || 
			a.getLat().length()==0 || a.getLng().length()==0 )
			return -1;
		
		try {
			double lat1 = Double.parseDouble(a.getLat())*Math.PI/180;
			double lat2 = Double.parseDouble(b.getLat())*Math.PI/180;
			double lon1 = Double.parseDouble(a.getLng())*Math.PI/180;
			double lon2 = Double.parseDouble(b.getLng())*Math.PI/180;
			
			double R = 6371; // km
			double d = Math.acos(Math.sin(lat1)*Math.sin(lat2) + 
			                  Math.cos(lat1)*Math.cos(lat2) *
			                  Math.cos(lon2-lon1)) * R;	
			return (int)(d*1000); // m
		} catch(NumberFormatException e) {
			return -1;
		}
	}
	
}
