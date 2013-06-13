package ro.mihai.tpt.analytics;

import java.io.IOException;

public class NoAnalyticsService implements IAnalyticsService {

	@Override
	public String generateDeviceId() throws IOException {
		throw new IOException("not supported");
	}

	@Override
	public void postTimesBundle(String deviceId, String data) throws IOException {
		throw new IOException("not supported");
	}

}
