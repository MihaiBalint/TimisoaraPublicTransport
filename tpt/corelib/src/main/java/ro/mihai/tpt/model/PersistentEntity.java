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
