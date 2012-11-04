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
import java.util.List;

import ro.mihai.tpt.R;
import ro.mihai.tpt.model.Path;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
	private static final String STATS_VERSION_KEY = "version";
	private static final int STATS_VERSION = 1;
	private static final String STATS_PREFIX = "stats-";
	private static final int STATS_PREFIX_LEN = STATS_PREFIX.length();

	public static String readBaseDownloadUrl(Context ctx) {
		// Read a sample value they have set
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String defaultUrl = ctx.getString(R.string.pref_base_download_url_default);
		
		return sharedPref.getString(ctx.getString(R.string.pref_base_download_url), defaultUrl);
	}
	
	public static String makeLineKey(String lineName) {
		return STATS_PREFIX+lineName;
	}
	public static String makePathKey(String lineName, String pathName) {
		return STATS_PREFIX+lineName+"-"+pathName;
	}
	
	public static void recordUsePath(Context ctx, Path p) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String lineKey = makeLineKey(p.getLine().getName());
		String pathKey = makePathKey(p.getLine().getName(), p.getName());
		int lineStats = sharedPref.getInt(lineKey, 0) + 1;
		int pathStats = sharedPref.getInt(pathKey, 0) + 1;
		
		sharedPref.edit()
			.putInt(lineKey, lineStats)
			.putInt(pathKey, pathStats)
			.putInt(STATS_VERSION_KEY, STATS_VERSION)
			.commit();
	}
	
	private static boolean isLineStat(String key) {
		return key.startsWith(STATS_PREFIX) && key.lastIndexOf("-") < STATS_PREFIX_LEN;
	}
	
	public static List<String> getTopLines(Context ctx) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		upgradeStats(sharedPref);
		ArrayList<String> lines = new ArrayList<String>();
		for(String key: sharedPref.getAll().keySet())
			if (isLineStat(key)) {
				lines.add(key);
			}
		Collections.sort(lines, new UsageComparator(sharedPref));
		ArrayList<String> sortedNames = new ArrayList<String>();
		for(String l: lines)
			sortedNames.add(l.substring(STATS_PREFIX_LEN));
		
		return sortedNames;
	}
	
	private static void upgradeStats(SharedPreferences sharedPref) {
		switch (sharedPref.getInt(STATS_VERSION_KEY, 0)) {
		case 0: upgrade00to01(sharedPref);
		}
	}
	
	private static void upgrade00to01(SharedPreferences sharedPref) {
		String line7a = makeLineKey("Tv7a");
		String line7b = makeLineKey("Tv7b");
		String line7 = makeLineKey("Tv7");
		int tv7a = sharedPref.getInt(line7a, 0);
		int tv7b = sharedPref.getInt(line7b, 0);
		sharedPref.edit()
			.putInt(STATS_VERSION_KEY, 1)
			.putInt(line7, tv7a+tv7b)
			.remove(line7a)
			.remove(line7b)
			.commit();
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
