package ro.mihai.tpt.util;

import ro.mihai.tpt.RATT;
import ro.mihai.tpt.analytics.Collector;
import ro.mihai.tpt.analytics.IAnalyticsService;
import ro.mihai.tpt.analytics.NoAnalyticsService;
import ro.mihai.util.IPrefs;

public class TestPrefs implements IPrefs {

    @Override
	public String getBaseUrl() {
		return "http://ratt-txt.aeliptus.com/txt/";
	}

	@Override
	public Collector getAnalyticsCollector() {
		return new Collector(this, getDeviceId());
	}

	@Override
	public IAnalyticsService getAnalyticsService() {
		return new NoAnalyticsService();
	}

	@Override
	public String getDeviceId() {
		return IPrefs.DEFAULT_DEVICE_ID;
	}

	@Override
	public void setDeviceId(String deviceId) {
	}

	@Override
	public void setCachedAnalytics(String analytics) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String addCachedAnalytics(String analytics) {
		// TODO Auto-generated method stub
		return null;
	}

}
