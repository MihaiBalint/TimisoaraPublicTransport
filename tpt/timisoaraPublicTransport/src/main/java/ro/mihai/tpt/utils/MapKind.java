/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2016  Mihai Balint

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

import ro.mihai.tpt.R;

/**
 * Created by Mihai Balint on 8/15/16.
 */
public enum MapKind {
    BUS(R.drawable.map_bus_collapsed, R.drawable.map_bus_expanded),
    ELECTRIC(R.drawable.map_electric_collapsed, R.drawable.map_electric_expanded);

    public final int collapsedId, expandedId;

    private MapKind(int collapsedId, int expandedId) {
        this.collapsedId = collapsedId;
        this.expandedId = expandedId;
    }
}
