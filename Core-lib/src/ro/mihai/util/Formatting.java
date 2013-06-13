package ro.mihai.util;

import java.util.Arrays;
import java.util.Collection;

public class Formatting {

	public static String formatTime(String time) {
		if(time.contains(":")) return time;
		try {
			int min = Integer.parseInt(time);
			return formatMinutes(min);
		} catch(NumberFormatException e) {
			return time;
		}
	}

	public static String formatMinutes(int min) {
		return min<10 
			? min+" min"
			: min+"min";
	}
	
	public static boolean isInteger(String str) {
		try {
			if (null==str) return false;
			str = str.trim();
			if (str.length()==0) return false;
			
			Integer.parseInt(str);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	public static boolean isMinutes(String str) {
		if (null==str) return false;
		str = str.trim().toLowerCase();
		if (!str.endsWith("min.")) return false;
		String n = str.substring(0,str.indexOf("min.")).trim();
		return isInteger(n);
	}
	
	public static String parseMinutes(String str) {
		str = str.trim().toLowerCase();
		return str.substring(0,str.indexOf("min.")).trim();
	}
	
	public static <T> String join(String sep, T... items) {
		return join(sep, Arrays.asList(items));
	}
	
	public static <T> String join(String sep, Collection<T> items) {
		boolean first = true;
		String result = "";
		for(T item : items)
			if (first) {
				result += item;
				first = false;
			} else
				result += sep+item;
		return result;
	}
	
	public static String capitalize(String str) {
		return Character.toUpperCase(str.charAt(0)) + str.substring(1);
	}
	
	public static String zeroLeadingInteger(int i, int minFixedWidth) {
		String value = Integer.toString((i<0) ? 0-i : i);
		String sign = (i<0) ? "-" : "";
		while ((sign+value).length() < minFixedWidth)
			value = "0"+value;
		return sign+value;
	}
}
