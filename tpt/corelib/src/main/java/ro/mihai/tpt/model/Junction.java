/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2014  Mihai Balint

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
import java.util.HashSet;
import java.util.Set;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;

public class Junction extends PersistentEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Set<Station> stations;
	
	private Junction(int id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.stations = new HashSet<Station>();
	}
	
	protected Junction(int id, String name) {
		this(id, -1, null);
		this.name = name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		ensureLoaded();
		return name;
	}
	
	public void addStation(Station s) {
		ensureLoaded();
		stations.add(s);
	}
	
	public Set<Station> getStations() {
		ensureLoaded();
		return stations;
	}

	@Override
	protected void loadLazyResources(BPInputStream res) throws IOException {
		this.name = res.readString();

		int stationCount = res.readInt();
		for(int j=0;j<stationCount;j++) {
			Station s = city.getStationById(res.readObjectId());
			stations.add(s);
		}
	}

	@Override
	protected void saveLazyResources(BPMemoryOutputStream lazy) throws IOException {
		ensureLoaded();
		// lazy junction resources
		lazy.writeString(getName());
		
		lazy.writeInt(stations.size());
		for(Station s:stations) {
			lazy.writeObjectId(s.getId());
		}
	}
	
	@Override
	public void saveEager(BPOutputStream eager) throws IOException {
		eager.writeObjectId(id); 
	}
	
	public static Junction loadEager(BPInputStream eager, int resId, City city) throws IOException {
		return new Junction(eager.readObjectId(), resId, city);
	}
	
}
