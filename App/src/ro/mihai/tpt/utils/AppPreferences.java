/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011  Mihai Balint

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
package ro.mihai.tpt.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import ro.mihai.tpt.R;
import ro.mihai.tpt.analytics.AnalyticsService;
import ro.mihai.tpt.analytics.Collector;
import ro.mihai.tpt.analytics.IAnalyticsService;
import ro.mihai.tpt.analytics.NoAnalyticsService;
import ro.mihai.tpt.data.Achievements;
import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.util.IPrefs;
import ro.mihai.util.PathList;

public class AppPreferences implements IPrefs {
	private static final String STATS_PREFIX = "stats-";

	private Context ctx;
	private Collector collector;
	
	private String pref_device_id;
	private String pref_base_download_url; 
	private String pref_analytics_enabled;
	private String pref_analytics_cache;
	private String pref_current_version;
	private String pref_favorite_paths;
	private String pref_achieve_name;
	
	public AppPreferences(Context ctx) {
		this.ctx = ctx;
		pref_device_id = ctx.getString(R.string.pref_device_id);
		pref_base_download_url = ctx.getString(R.string.pref_base_download_url);
		pref_analytics_enabled = ctx.getString(R.string.pref_analytics_enabled);
		pref_analytics_cache = ctx.getString(R.string.pref_analytics_cache);
		pref_current_version = ctx.getString(R.string.pref_current_version);
		pref_favorite_paths = ctx.getString(R.string.pref_favorite_paths);
		pref_achieve_name = ctx.getString(R.string.pref_achieve_name);
		this.collector = new Collector(this, getDeviceId());
	}
	
    private String baseUrl = null;
	public void refreshBaseUrl() {
    	baseUrl = readBaseDownloadUrl(ctx);
	}
	
    public String getBaseUrl() {
    	if(baseUrl==null) 
    		baseUrl = readBaseDownloadUrl(ctx);
		return baseUrl;
	}

	public Collector getAnalyticsCollector() {
		return collector;
	}
	
	private String readBaseDownloadUrl(Context ctx) {
		// Read a sample value they have set
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String defaultUrl = ctx.getString(R.string.pref_base_download_url_default);
		
		return sharedPref.getString(pref_base_download_url, defaultUrl);
	}
	
	public String getDeviceId() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPref.getString(pref_device_id, IPrefs.DEFAULT_DEVICE_ID);
	}
	
	public void setDeviceId(String deviceId) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putString(pref_device_id, deviceId)
			.commit();
	}
	
	public void setAnalyticsEnabled(boolean enabled) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putBoolean(pref_analytics_enabled, enabled)
			.commit();
	}

	public Achievements getTopAchieve() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		if (sharedPref.getBoolean(pref_analytics_enabled, false)) {
			return Achievements.Contributor;
		} 
		return Achievements.None;
	}
	
	public String getAchieveUserName() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPref.getString(pref_achieve_name, "");
		
	}
	
	public void setAchieveUserName(String name) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putString(pref_achieve_name, name)
			.commit();
	}
	
	private IAnalyticsService service = null;
	public synchronized IAnalyticsService getAnalyticsService() {
		if (service == null) {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
			if (sharedPref.getBoolean(pref_analytics_enabled, true)) {
				String agent = "App."+getAppVersion();
				service = new AnalyticsService(ctx.getString(R.string.pref_analytics_url), agent);
			} else {
				service = new NoAnalyticsService();
			}
		}
		return service;
	}
	
	public int getAppVersion() {
		try {
			return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
		} catch(Exception e) {
			return 0;
		}
	}
	
	public int getCurrentVersion(int missingVersionCode) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sharedPref.getInt(pref_current_version, missingVersionCode);
	}
	
	public void setCurrentVersion(int versionCode) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putInt(pref_current_version, versionCode)
			.commit();
	}
	

	public void setCachedAnalytics(String analytics) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		sharedPref.edit()
			.putString(pref_analytics_cache, analytics)
			.commit();
	}

	public String addCachedAnalytics(String analytics) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String cachedAnalytics = sharedPref.getString(pref_analytics_cache, "") + analytics;
		sharedPref.edit()
			.putString(pref_analytics_cache, cachedAnalytics)
			.commit();
		return cachedAnalytics;
	}
	
	public void addFavoritePath(Path path) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		PathList favorites = new PathList(sharedPref.getString(pref_favorite_paths, ""));
		sharedPref.edit()
			.putString(pref_favorite_paths, favorites.addPath(path).write())
			.commit();
	}
	
	public void removeFavoritePath(Path path) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		PathList favorites = new PathList(sharedPref.getString(pref_favorite_paths, ""));
		sharedPref.edit()
			.putString(pref_favorite_paths, favorites.removePath(path).write())
			.commit();
	}
	
	public boolean isFavoritePath(Path path) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		PathList favorites = new PathList(sharedPref.getString(pref_favorite_paths, ""));
		return favorites.containsPath(path);
		
	}
	
	public List<Path> getFavoritePaths(City c, int max) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		PathList favorites = new PathList(sharedPref.getString(pref_favorite_paths, ""));
		List<Path> favoritePaths = favorites.readPaths(c);
		if (favoritePaths.size() >= max)
			return favoritePaths;
		
		int prefixLen = STATS_PREFIX.length();
		ArrayList<String> pathStats = new ArrayList<String>();
		for(String key: sharedPref.getAll().keySet())
			if (key.startsWith(STATS_PREFIX) && key.indexOf("-", prefixLen) > 0) {
				pathStats.add(key);
			}
		Collections.sort(pathStats, new UsageComparator(sharedPref));
		for(String pathStat: pathStats) {
			Path path = favorites.read(pathStat.substring(prefixLen), c);
			if (path==null || path.getLine().isFake() || favoritePaths.contains(path)) 
				continue;
			favoritePaths.add(path);
			if (favoritePaths.size() >= max)
				return favoritePaths;
		}
		
    	MOSTUSED: for (String line : LineKindAndroidEx.MOST_USED) {
    		for(Path p : favoritePaths)
    			if (p.getLineName().equals(line))
    				continue MOSTUSED;
    		Line cityLine = c.getLine(line);
    		if (cityLine.isFake())
    			continue;
			favoritePaths.add(cityLine.getFirstPath());
			if (favoritePaths.size() >= max)
				return favoritePaths;
    		
    	}
		return favoritePaths;
	} 

	public void recordPathUse(Path p) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String lineKey = STATS_PREFIX+p.getLine().getName();
		String pathKey = lineKey+"-"+p.getName();
		int lineStats = sharedPref.getInt(lineKey, 0) + 1;
		int pathStats = sharedPref.getInt(pathKey, 0) + 1;
		
		sharedPref.edit()
			.putInt(lineKey, lineStats)
			.putInt(pathKey, pathStats)
			.commit();
	}
	
	public void reset() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		HashSet<String> prefKeys = new HashSet<String>(sharedPref.getAll().keySet());
		SharedPreferences.Editor ed = sharedPref.edit();
		for(String key: prefKeys) 
			ed.remove(key);
		ed.commit();
	}
	
	private static class UsageComparator implements Comparator<String> {
		private SharedPreferences usageMap;
		
		public UsageComparator(SharedPreferences usageMap) {
			this.usageMap = usageMap;
		}
		
		private int get(String l) {
			return usageMap.getInt(l, 0);
		}
		
		public int compare(String l1, String l2) {
			return get(l2) - get(l1);
		}
	}
	
}
