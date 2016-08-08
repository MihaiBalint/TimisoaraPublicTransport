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

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class Upgrade {
	private Context context;
	
	public Upgrade(Context context) {
		this.context = context;
	}
	
	public void upgrade() {
		try {
			unsafeUpgrade();
		} catch(NameNotFoundException e) {
			// TODO report error
		}
	}

	private void unsafeUpgrade() throws NameNotFoundException {
    	AppPreferences prefs = new AppPreferences(context);
		if (prefs.getCurrentVersion(0) < 24) {
			upgrade23to24(prefs);
		}
		if (prefs.getCurrentVersion(0) < 25) {
			// DO NOTHING
		}
	}
	
	private void upgrade23to24(final AppPreferences prefs) {
		prefs.setCurrentVersion(24);
	}
}
