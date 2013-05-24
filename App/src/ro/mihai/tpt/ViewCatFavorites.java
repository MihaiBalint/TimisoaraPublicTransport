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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.view.View.OnClickListener;

import ro.mihai.tpt.model.City;
import ro.mihai.tpt.model.Line;
import ro.mihai.tpt.model.Path;
import ro.mihai.tpt.utils.LineKindAndroidEx;
import ro.mihai.tpt.utils.StartActivity;
import ro.mihai.tpt.utils.Utils;

public class ViewCatFavorites extends ViewCategories {

	protected Iterator<Path> getLinePathIterator(City city) {
		List<Path> paths = new ArrayList<Path>();

		List<String> sortedNames = Utils.getTopLines(this);
    	for (String l : LineKindAndroidEx.MOST_USED)
    		if (!sortedNames.contains(l))
    			sortedNames.add(l);
    	int favorites = 6;
    	Iterator<String> nameIt = sortedNames.iterator();
    	for(; favorites>0; favorites--) {
    		String name = nameIt.next();
    		Line line = city.getLine(name);
    		while (line.isFake() && nameIt.hasNext()) {
    			name = nameIt.next();
    			line = city.getLine(name);
    		}
    		paths.add(line.getFirstPath());
    	}
		return paths.iterator();
	}
}