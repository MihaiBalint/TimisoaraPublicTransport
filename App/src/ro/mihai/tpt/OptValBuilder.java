package ro.mihai.tpt;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
