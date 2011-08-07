package ro.mihai.util;

public class Formatting {

	public static String formatTime(String time) {
		if(time.contains(":")) return time;
		try {
			int min = Integer.parseInt(time);
			return min<10 
				? min+" min"
				: min+"min";
		} catch(NumberFormatException e) {
			return time;
		}
	}
	
}
