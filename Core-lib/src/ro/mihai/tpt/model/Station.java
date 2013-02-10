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
import java.util.List;

import ro.mihai.util.DetachableStream;

public class Station extends PersistentEntity implements INamedEntity, Serializable {
	private static final long serialVersionUID = 1L;
	private String name, id;
	private List<Path> lines;
	private Junction junction;
	private String lat, lng;
	private String niceName, shortName;
	
	public Station(String id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.lines = new ArrayList<Path>(2);
	}

	public Station(String id, String name) {
		this(id,-1, null);
		this.lines = new ArrayList<Path>(2);
		this.name = name;
	}
	
	@Override
	public String toString() {
		String lineNames = getLineNames();
		return "Station: "+id+" - "+getShortName()+"["+lineNames+"]("+Integer.toHexString(hashCode())+")";
	}

	public String getLineNames() {
		String lineNames = "";
		boolean first = true;
		for(Path line : lines)
			if (first) {
				lineNames += line.getName();
				first = false;
			} else
				lineNames += ", "+line.getName();
		return lineNames;
	}

	public String getId() {
		return id;
	}
	
	public void addLine(Path l) {
		if (!lines.contains(l))
			lines.add(l);
	}
	
	public List<Path> getLines() {
		ensureLoaded();
		return lines;
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
	
	protected void loadLazyResources(DetachableStream lazy, DataVersion version) throws IOException {
		this.name = lazy.readString();
		this.niceName = lazy.readString();
		this.shortName = lazy.readString();
		this.junction = city.getJunctionById(lazy.readInt());
		this.lat = lazy.readString();

		this.lng = lazy.readString();
		
		if (version.lessThan(DataVersion.Version4)) return;
		int lineCount = lazy.readInt();
		for(int i=0;i<lineCount;i++) {
			String lineId = lazy.readString();
			lines.add(city.getLineById(lineId));
		}
	}
	
	private void persistLazy(DataOutputStream res) throws IOException {
		ensureLoaded();
		byte[] b;
		
		// lazy station resources
		b = getName().getBytes();
		res.writeInt(b.length); res.write(b);
		
		b = getNiceName().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getShortName().getBytes();
		res.writeInt(b.length); res.write(b);

		res.writeInt(junction.getId());

		b = getLat().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getLng().getBytes();
		res.writeInt(b.length); res.write(b);
		
		res.writeInt(lines.size());
		for(Path l : lines) {
			b = l.getId().getBytes();
			res.writeInt(b.length); res.write(b);
		}
	}
	
	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		byte[] b;
		
		// eager station resources
		b = id.getBytes();
		eager.writeInt(b.length); eager.write(b);

		eager.writeInt(lazyId); 
		
		// lazy station resources
		persistLazy(lazy);
		lazy.flush();
	}
	
	public static Station loadEager(DetachableStream eager, City city) throws IOException {
		String id = eager.readString();
		
		return new Station(id, eager.readInt(), city);
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
