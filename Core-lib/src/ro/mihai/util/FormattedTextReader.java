/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2011-2014  Mihai Balint

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
import java.io.InputStream;

public class FormattedTextReader {
	private InputStream in;
	private StringBuilder buf;
	private int index;
	private boolean caseSensitive = true;
	
	public FormattedTextReader(InputStream in) {
		this.in = in;
		buf = new StringBuilder();
		index = 0;
	}
	
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
	public boolean skipUntil(String str, boolean destructive) throws IOException {
		if(null==str) throw new NullPointerException();
		if(str.length()==0) return true;
		
		if(!caseSensitive)
			str = str.toLowerCase();
		
		buf.delete(0, index);
		index = 0;
		
		int p = caseSensitive
			? buf.indexOf(str)
			: buf.toString().toLowerCase().indexOf(str);
			
		int len = buf.length();
		
		while(p<0) {
			if (len>str.length()) {
				if (destructive) {
					buf.delete(0,len-str.length());
					index = 0;
				} else 
					index = len-str.length();
			}
			
			byte[] b = new byte[16384]; 
			int red = in.read(b);
			if (red<0) 
				return false;
			
			// TODO character to byte conversion is bad here
			for(int i=0;i<red;i++)
				buf.append((char)b[i]);
			
			len += red;
			p = caseSensitive 
				? buf.indexOf(str, index)
				: buf.toString().toLowerCase().indexOf(str, index);
		} 
		index = p;
		return true;
	}

	public boolean skipAfter(String str, boolean destructive) throws IOException {
		if(!skipUntil(str, destructive))
			return false;
		
		assert(index+str.length() <= buf.length());
		index += str.length();
		
		return true;
	}
	
	public String readUntil(String str) throws IOException {
		if(null==str) throw new NullPointerException();
		if(str.length()==0) return "";

		if(!skipUntil(str, false)) 
			return null;

		return buf.substring(0, index);
	}

	public String readString(String startAfter, String stopBefore) throws IOException {
		if (!skipAfter(startAfter,true))
			throw new IOException("Start sequence not found before stream ended.");
		return readUntil(stopBefore);
	}

	public void close() throws IOException {
		in.close();
	}
}
