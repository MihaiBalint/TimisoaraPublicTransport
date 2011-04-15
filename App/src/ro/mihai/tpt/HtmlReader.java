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
package ro.mihai.tpt;

import java.io.IOException;

public class HtmlReader {
	private FormattedTextReader rd;
	
	public HtmlReader(FormattedTextReader rd) {
		this.rd = rd;
		rd.setCaseSensitive(false);
	}
	
	public String[] readSelectOption() throws IOException {
		if (!rd.skipUntil("<option", true)) return null;

		if (!rd.skipAfter("value=\"", true)) return null;
		String value = rd.readUntil("\"");
		if(null==value) return null;

		if (!rd.skipAfter(">", true)) return null;
		String option = rd.readUntil("</option>");
		if(null==option) return null;
		
		return new String[]{value, option};
	}

}
