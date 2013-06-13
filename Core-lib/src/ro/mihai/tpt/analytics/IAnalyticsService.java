package ro.mihai.tpt.analytics;

import java.io.IOException;

public interface IAnalyticsService {
	
	public String generateDeviceId() throws IOException;
	public void postTimesBundle(String deviceId, String data) throws IOException;

}
