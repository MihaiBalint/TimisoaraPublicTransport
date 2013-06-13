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

import ro.mihai.tpt.model.Path;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
	private static final String STATS_PREFIX = "stats-";

	public static void recordUsePath(Context ctx, Path p) {
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
	
	public static List<String> getTopLines(Context ctx) {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
		int prefixLen = STATS_PREFIX.length();
		ArrayList<String> lines = new ArrayList<String>();
		for(String key: sharedPref.getAll().keySet())
			if (key.startsWith(STATS_PREFIX) && key.lastIndexOf("-") < prefixLen) {
				lines.add(key);
			}
		Collections.sort(lines, new UsageComparator(sharedPref));
		ArrayList<String> sortedNames = new ArrayList<String>();
		for(String l: lines)
			sortedNames.add(l.substring(prefixLen));
		
		return sortedNames;
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
