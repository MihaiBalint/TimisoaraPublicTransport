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
package ro.mihai.tpt.analytics;

import ro.mihai.util.Formatting;
import ro.mihai.util.IPrefs;

public class Collector {
	private StringBuilder data;
	private int cacheSize, prefCacheSize;
	private String deviceId;
	private IPrefs prefs;
	
	public Collector(IPrefs prefs, String deviceId) {
		this(prefs, deviceId, 8192);
	}
	public Collector(IPrefs prefs, String deviceId, int cacheSize) {
		this.prefs = prefs;
		this.deviceId = deviceId;
		this.cacheSize = cacheSize;
		this.prefCacheSize = 256;
		data = new StringBuilder();
	}

	/**
	 * Publish data to analytics service
	 * Does not block 
	 */
	public synchronized void record(String time1, String time2, String timestamp, String pathId, String stationId) {
		String line = Formatting.join("\", \"", time1, time2, timestamp, pathId, stationId);
		data.append("\"");
		data.append(line);
		data.append("\"\n");
		if (data.length() > prefCacheSize) {
			StringBuilder oldData = data;
			data = new StringBuilder();
			String bigData = prefs.addCachedAnalytics(oldData.toString());
			if (bigData.length() > cacheSize) {
				prefs.setCachedAnalytics("");
				new Publisher(bigData);
			}
		}
	}
	
	/**
	 * Return the device id
	 * May block waiting for netowrk response
	 * @return device id
	 */
	private String generateDeviceId() {
		if (deviceId.equals(IPrefs.DEFAULT_DEVICE_ID)) {
			try {
				deviceId = prefs.getAnalyticsService().generateDeviceId();
				prefs.setDeviceId(deviceId);
			} catch(Exception e) { 
				// should be java.io.IOException, but I really 
				// don't like crashes esp. for something like analytics
				// use the default device_id
			}
		}
		return deviceId;
	}
	
	/**
	 * Publish data to analytics service
	 * Blocks waiting for network
	 */
	private void publish(String data) {
		try {
			prefs.getAnalyticsService().postTimesBundle(generateDeviceId(), data);
		} catch(Exception e) {
			// should be java.io.IOException, but I really 
			// don't like crashes esp. for something like analytics
			// win some loose some
		}
	}
	
	private class Publisher implements Runnable {
		private String data;
		
		public Publisher(String data) {
			this.data = data;
			new Thread(this).start();
		}
		
		@Override
		public void run() {
			publish(data);
		}
	}
}
