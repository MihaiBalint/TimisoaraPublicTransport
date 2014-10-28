package ro.mihai.tpt.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;

public class Junction extends PersistentEntity implements Serializable {
	private static AtomicInteger ids = new AtomicInteger(0);
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private Set<Station> stations;
	
	public Junction(String name, City city) {
		this(ids.getAndIncrement(), -1, city);
		this.name = name;
	}
	
	public Junction(int id, long resId, City city) {
		super(resId, city);
		this.id = id;
		this.stations = new HashSet<Station>();
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
			String stationId = res.readString();
			
			Station s = city.getStation(stationId);
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
			lazy.writeString(s.getId());
		}
	}
	
	@Override
	public void saveEager(BPOutputStream eager) throws IOException {
		eager.writeInt(id); 
	}
	
	public static Junction loadEager(BPInputStream eager, int resId, City city) throws IOException {
		int id = eager.readInt();
		return new Junction(id, resId, city);
	}
	
}
