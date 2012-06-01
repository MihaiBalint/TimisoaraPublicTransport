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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.res.Resources;

import ro.mihai.tpt.R;
import ro.mihai.tpt.RATT;
import ro.mihai.tpt.SaveFileException;
import ro.mihai.tpt.model.City;
import ro.mihai.util.IMonitor;
import ro.mihai.util.IPrefs;
import ro.mihai.util.NullMonitor;

public class AndroidCityLoader {
	private static final String cityCacheFileName = "citylines.dat"; 

	public static City loadFromAppResources(Context ctx) throws IOException {
		City c = new City();
		c.loadFromStream(new AndroidDetachableStream.FromRawResource(ctx,R.raw.citylines), new NullMonitor());
		return c;
	}
	
	public static City loadStoredCityOrDownloadAndCache(IPrefs prefs, Context ctx, IMonitor mon) throws IOException {
		City c = new City();
		try {
			// read resources file
			c.loadFromStream(new AndroidDetachableStream.FromRawResource(ctx,R.raw.citylines),mon);
		} catch(Resources.NotFoundException ex) {
			try {
				// read the proper cache
				c.loadFromStream(new AndroidDetachableStream.FromFile(ctx,cityCacheFileName),mon);
			} catch(FileNotFoundException e) {
				// download and parse new stuff
				c = RATT.downloadCity(prefs, mon);
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
