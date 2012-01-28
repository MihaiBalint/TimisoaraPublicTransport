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

public class Station implements INamedEntity, Serializable {
	private static final long serialVersionUID = 1L;
	private long resId;
	private boolean loaded;
	private City city;
	private String name, id;
	private List<Line> lines;
	private Junction junction;
	private String lat, lng;
	private String niceName, shortName;
	
	public Station(String id, long resId, City city) {
		this.id = id;
		this.resId = resId;
		this.city = city;
		this.loaded = false;
		this.lines = new ArrayList<Line>(2);
	}

	public Station(String id, String name) {
		this(id,-1, null);
		this.loaded = true;
		this.lines = new ArrayList<Line>(2);
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public long getResId() {
		return resId;
	}
	
	public void addLine(Line l) {
		if (!lines.contains(l))
			lines.add(l);
	}
	
	public List<Line> getLines() {
		return lines;
	}	
	
	private synchronized void load() {
		if (loaded) return;
		city.loadStationResources(this);
		loaded = true;
	}
	private void ensureLoaded() {
		if (loaded) return;
		load();
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
	
	protected void readResources(DetachableStream res, DataVersion version) throws IOException {
		this.name = res.readString();
		this.niceName = res.readString();
		this.shortName = res.readString();
		this.junction = city.getOrCreateJunction(res.readString());
		this.junction.addStation(this);
		this.lat = res.readString();

		this.lng = res.readString();
		
		if (version.lessThan(DataVersion.Version4)) return;
		
		int lineCount = res.readInt();
		for(int i=0;i<lineCount;i++) {
			String lineId = res.readString();
			lines.add(city.getLineById(lineId));
		}
	}
	
	protected void writeResources(DataOutputStream res) throws IOException {
		ensureLoaded();
		byte[] b;
		
		b = getName().getBytes();
		res.writeInt(b.length); res.write(b);
		
		b = getNiceName().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getShortName().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getJunctionName().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getLat().getBytes();
		res.writeInt(b.length); res.write(b);

		b = getLng().getBytes();
		res.writeInt(b.length); res.write(b);
		
		res.writeInt(lines.size());
		for(Line l : lines) {
			b = l.getId().getBytes();
			res.writeInt(b.length); res.write(b);
		}
	}
}
