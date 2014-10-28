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
	protected abstract void saveLazyResources(BPMemoryOutputStream lazy) throws IOException;
	
	protected abstract void saveEager(BPOutputStream eager) throws IOException;
	
	public void saveEagerAndLazy(BPOutputStream eager, BPMemoryOutputStream lazy) throws IOException {
		eager.writeInt(lazy.size());
		saveEager(eager);
		saveLazyResources(lazy);
		lazy.flush();
	}
	
	public static Station loadEagerStation(BPInputStream eager, City c) throws IOException {
		return Station.loadEager(eager, eager.readInt(), c);
	}
	public static Junction loadEagerJunction(BPInputStream eager, City c) throws IOException {
		return Junction.loadEager(eager, eager.readInt(), c);
	}
	public static Line loadEagerLine(BPInputStream eager, City c) throws IOException {
		return Line.loadEager(eager, eager.readInt(), c);
	}
	public static Path loadEagerPath(BPInputStream eager, City c) throws IOException {
		return Path.loadEager(eager, eager.readInt(), c);
	}	
	public static HourlyPlan loadEagerHourlyPlan(BPInputStream eager, City c) throws IOException {
		return HourlyPlan.loadEager(eager, eager.readInt(), c);
	}	
}
