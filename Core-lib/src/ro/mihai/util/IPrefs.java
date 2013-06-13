package ro.mihai.util;

import ro.mihai.tpt.analytics.Collector;
import ro.mihai.tpt.analytics.IAnalyticsService;

public interface IPrefs {
	public static String DEFAULT_DEVICE_ID = "NONE";
	
	String getBaseUrl();
	Collector getAnalyticsCollector();
	IAnalyticsService getAnalyticsService();
	
	String getDeviceId();
	void setDeviceId(String deviceId);
	
	String addCachedAnalytics(String analytics);
	void setCachedAnalytics(String analytics);
}
