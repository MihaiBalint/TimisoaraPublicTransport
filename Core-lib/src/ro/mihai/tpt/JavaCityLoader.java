package ro.mihai.tpt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import ro.mihai.tpt.model.City;
import ro.mihai.util.DetachableStream;
import ro.mihai.util.NullMonitor;

public class JavaCityLoader {
	public static final String cityCacheFileName = "citylines.dat"; 

	/**
	 * Not for running on androids
	 * @return
	 * @throws IOException
	 */
	public static City loadCachedCityOrDownloadAndCache() throws IOException {
		City c;
		File cache = new File(cityCacheFileName);
		if(cache.isFile() && cache.exists() && cache.canRead()) {
			c = new City();
			c.loadFromStream(new DetachableStream.FromFile(cityCacheFileName), new NullMonitor());
		} else {
			c = RATT.downloadCity(new NullMonitor());
			c.saveToFile(new FileOutputStream(cache));
		}
		
		return c;
	}
}
