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

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.Resources;

import ro.mihai.tpt.model.City;
import ro.mihai.util.IMonitor;
import ro.mihai.util.NullMonitor;

public class AndroidCityLoader {
	private static final String cityCacheFileName = "citylines.dat"; 

	public static City loadFromAppResources(Context ctx) throws IOException {
		InputStream is = ctx.getResources().openRawResource(R.raw.citylines);
		City c = new City();
		c.loadFromFile(is, new NullMonitor());
		return c;
	}
	
	public static City loadStoredCityOrDownloadAndCache(Context ctx, IMonitor mon) throws IOException {
		City c = new City();
		try {
			// read resources file
			InputStream in = ctx.getResources().openRawResource(R.raw.citylines);
			c.loadFromFile(new BufferedInputStream(in),mon);
		} catch(Resources.NotFoundException ex) {
			try {
				// read the proper cache
				InputStream in = ctx.openFileInput(cityCacheFileName);
				c.loadFromFile(new BufferedInputStream(in),mon);
			} catch(FileNotFoundException e) {
				// download and parse new stuff
				c = RATT.downloadCity(mon);
			} finally {
				OutputStream os = ctx.openFileOutput(cityCacheFileName, Context.MODE_PRIVATE);
				c.saveToFile(os);
			} 
		} catch(SaveFileException e) {
			// the file must be saved again because it was in an older format
			OutputStream os = ctx.openFileOutput(cityCacheFileName, Context.MODE_PRIVATE);
			c.saveToFile(os);
		}

		return c;
	}	
}
