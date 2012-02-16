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

import ro.mihai.tpt_light.R;
import ro.mihai.util.LineKind;

public class LineKindUtils {

	public static int getLabelId(LineKind k) {
		switch(k) {
		case TRAM: return R.string.lblTrams;
		case TROLLEY: return R.string.lblTrolleys;
		case BUS: return R.string.lblBus;
		case EXPRESS: return R.string.lblExpress;
		case METRO: return R.string.lblMetro;
		default: return R.string.lblOther;
		}
	}
}
