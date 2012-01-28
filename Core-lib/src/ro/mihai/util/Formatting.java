package ro.mihai.util;

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
	
}
