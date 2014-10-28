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
				loadLazyResources(in, city.version);
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
	
	protected abstract void loadLazyResources(BPInputStream res, DataVersion version) throws IOException;
	
	protected abstract void persistEager(BPOutputStream eager) throws IOException;
	protected abstract void persistLazy(BPMemoryOutputStream lazy) throws IOException;
	
	public void persist(BPOutputStream eager, BPMemoryOutputStream lazy) throws IOException {
		persistEager(eager);
		eager.writeInt(lazy.size());
		persistLazy(lazy);
		lazy.flush();
	}
	
}
