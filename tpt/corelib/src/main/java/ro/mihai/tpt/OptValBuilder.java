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
package ro.mihai.tpt;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ro.mihai.util.FormattedTextReader;
import ro.mihai.util.HtmlReader;
import ro.mihai.util.IMonitor;

public abstract class OptValBuilder<T> {
	private HtmlReader rd;
	
	public OptValBuilder(URL url) throws IOException {
		this.rd = new HtmlReader(new FormattedTextReader(url.openStream()));
	}

	public OptValBuilder(FormattedTextReader in) {
		this.rd = new HtmlReader(in);
	}
	
	public final T read() throws IOException {
		String[] vo = rd.readSelectOption();
		if(vo==null) return null;
		
		return create(vo[0],vo[1]);
	}
	
	protected abstract T create(String val, String opt);
	
	public final List<T> readAll(IMonitor mon) throws IOException {
		List<T> stations = new ArrayList<T>();
		T s;
		while(null != (s = read())) {
			stations.add(s);
			mon.workComplete();
		}
		return stations;
	}
	
	
}
