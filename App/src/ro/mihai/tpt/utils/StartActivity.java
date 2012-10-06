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

import ro.mihai.tpt.conf.PathStationsSelection;
import ro.mihai.tpt.model.*;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

public class StartActivity implements OnClickListener {
	private Class<?> activity;
	private Activity parent;
	
	public StartActivity(Activity parent, Class<?> activity) {
		this.activity = activity;
		this.parent = parent;
	}
	
	public StartActivity addCity(City c) {
		AndroidSharedObjects.instance().setCity(c);
		return this;
	}
	
	public StartActivity addLinePath(Path p) {
		AndroidSharedObjects.instance().setLinePath(p);
		return this;
	}

	public StartActivity addLinePath(PathStationsSelection p) {
		AndroidSharedObjects.instance().setLinePath(p);
		return this;
	}
	
	public void replace() {
		parent.startActivityForResult(new Intent(parent, activity), CityActivity.REQUEST_CODE_REPLACE);
	}
	
	public void start() {
		parent.startActivity(new Intent(parent, activity));
	}

	public final void onClick(View v) {
		start();
	}

}
