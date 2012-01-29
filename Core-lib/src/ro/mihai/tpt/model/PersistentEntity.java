package ro.mihai.tpt.model;

import java.io.DataOutputStream;
import java.io.IOException;

import ro.mihai.util.DetachableStream;

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
		city.loadLazyResources(this, resId);
		loaded = true;
	}
	protected final void ensureLoaded() {
		if (loaded) return;
		load();
	}
	
	protected abstract void loadLazyResources(DetachableStream res, DataVersion version) throws IOException;
	
	public abstract void persist(DataOutputStream eager, DataOutputStream lazy, int lazyId) throws IOException;
}
