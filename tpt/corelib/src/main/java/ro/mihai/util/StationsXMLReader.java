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
