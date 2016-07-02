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
