package ro.mihai.tpt.model;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import ro.mihai.util.DetachableStream;

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
	protected void loadLazyResources(DetachableStream res, DataVersion version) throws IOException {
		this.name = res.readString();

		int stationCount = res.readInt();
		for(int j=0;j<stationCount;j++) {
			String stationId = res.readString();
			
			Station s = city.getStation(stationId);
			stations.add(s);
		}
	}

	private void persistLazy(DataOutputStream lazy) throws IOException {
		ensureLoaded();
		byte[] b;
		
		// lazy junction resources
		b = getName().getBytes();
		lazy.writeInt(b.length); lazy.write(b);
		
		lazy.writeInt(stations.size());
		for(Station s:stations) {
			b = s.getId().getBytes();
			lazy.writeInt(b.length); lazy.write(b);
		}
	}
	
	@Override
	public void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException {
		eager.writeInt(id); 
		eager.writeInt(lazyId); 
		
		// lazy station resources
		persistLazy(lazy);
		lazy.flush();
	}
	
	public static Junction loadEager(DetachableStream eager, City city) throws IOException {
		int id = eager.readInt();

		return new Junction(id, eager.readInt(), city);
	}
	
}
