package ro.mihai.tpt.util;

import ro.mihai.tpt.RATT;
import ro.mihai.util.IPrefs;

public class TestPrefs implements IPrefs {

	@Override
	public String getBaseUrl() {
		return RATT.root;
	}

}
