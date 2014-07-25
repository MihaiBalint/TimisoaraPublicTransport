package ro.mihai.util;

import java.io.IOException;

public class StationsXMLReader {
	private FormattedTextReader rd;
	
	public StationsXMLReader(FormattedTextReader rd) {
		this.rd = rd;
	}
	
	/**
	 * 
	 * @return string array with four elements: {name, id, lat,lng}
	 * @throws IOException
	 */
	public String[] readStationCoords() throws IOException {
		if (!rd.skipUntil("<marker", true)) return null;
		
		if (!rd.skipAfter("station='", true)) return null;
		String name = rd.readUntil("'");
		if(null==name) return null;
		
		
		if (!rd.skipAfter("lat='", true)) return null;
		String lat = rd.readUntil("'");
		if(null==lat) return null;
		
		if (!rd.skipAfter("lng='", true)) return null;
		String lng = rd.readUntil("'");
		if(null==lng) return null;

		if (!rd.skipAfter("id_st='", true)) return null;
		String id = rd.readUntil("'");
		if(null==id) return null;
		
		return new String[]{name, id, lat,lng};
	}
	
	public void close() throws IOException {
		rd.close();
	}
}
