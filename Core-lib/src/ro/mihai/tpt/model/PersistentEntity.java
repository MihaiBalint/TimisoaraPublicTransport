package ro.mihai.tpt.model;

import java.io.IOException;

import ro.mihai.util.BPInputStream;
import ro.mihai.util.BPMemoryOutputStream;
import ro.mihai.util.BPOutputStream;

public abstract class PersistentEntity {
	private long resId;
	private boolean loaded;
	protected final City city;

	protected PersistentEntity(long resId,  City city) {
		this.resId = resId;
		this.city = city;
		loaded = resId < 0;
	}
	
	private final synchronized void load() {
		if (loaded) return;
		try {
			BPInputStream in = city.getDetachableInputStream();
			synchronized (in) {
				in.reset();
				in.sureSkip(resId);
				loadLazyResources(in);
			}
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
		loaded = true;
	}
	protected final void ensureLoaded() {
		if (loaded) return;
		load();
	}
	
	protected abstract void loadLazyResources(BPInputStream res) throws IOException;
	
	protected abstract void persistEager(BPOutputStream eager) throws IOException;
	protected abstract void persistLazy(BPMemoryOutputStream lazy) throws IOException;
	
	public void persist(BPOutputStream eager, BPMemoryOutputStream lazy) throws IOException {
		eager.writeInt(lazy.size());
		persistEager(eager);
		persistLazy(lazy);
		lazy.flush();
	}
	
	public static Station createStation(BPInputStream eager, City c) throws IOException {
		return Station.loadEager(eager, eager.readInt(), c);
	}
	public static Junction createJunction(BPInputStream eager, City c) throws IOException {
		return Junction.loadEager(eager, eager.readInt(), c);
	}
	public static Line createLine(BPInputStream eager, City c) throws IOException {
		return Line.loadEager(eager, eager.readInt(), c);
	}
	public static Path createPath(BPInputStream eager, City c) throws IOException {
		return Path.loadEager(eager, eager.readInt(), c);
	}	
	public static HourlyPlan createHourlyPlan(BPInputStream eager, City c) throws IOException {
		return HourlyPlan.loadEager(eager, eager.readInt(), c);
	}	
}
