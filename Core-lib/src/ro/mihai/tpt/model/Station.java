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
import java.util.HashSet;
import java.util.Set;

public class Station implements INamedEntity {
	private long resId;
	private boolean loaded;
	private City city;
	private String name, id;
	private Set<Line> lines;
	private Junction junction;
	private String lat, lng;
	private String niceName, shortName;
	
	public Station(String id, long resId, City city) {
		this.id = id;
		this.resId = resId;
		this.city = city;
		this.loaded = false;
		this.lines = new HashSet<Line>();
	}

	public Station(String id, String name) {
		this(id,-1, null);
		this.loaded = true;
		this.lines = new HashSet<Line>();
		this.name = name;
	}

	public String getId() {
		return id;
	}
	
	public long getResId() {
		return resId;
	}
	
	public void addLine(Line l) {
		lines.add(l);
	}
	
	public Set<Line> getLines() {
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
	
	protected void readResources(DataInputStream res) throws IOException {
		int bc;
		byte[] b;
		
		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.name = new String(b);
		
		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.niceName = new String(b);

		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.shortName = new String(b);

		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.junction = city.getOrCreateJunction(new String(b));
		
		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.lat = new String(b);

		bc = res.readInt(); b = new byte[bc]; res.readFully(b);
		this.lng = new String(b);
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
	}
}
