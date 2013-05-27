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

import java.util.NoSuchElementException;

import ro.mihai.tpt.R;
import ro.mihai.util.LineKind;

public enum LineKindAndroidEx {
	TRAM(LineKind.TRAM, 
			R.string.lblTrams, R.string.lblShortTrams, 
			R.color.vehicle_tram), 
	TROLLEY(LineKind.TROLLEY, 
			R.string.lblTrolleys, R.string.lblShortTrolleys, 
			R.color.vehicle_trolley), 
	BUS(LineKind.BUS, 
			R.string.lblBus, R.string.lblShortBus, 
			R.color.vehicle_bus), 
	EXPRESS(LineKind.EXPRESS, 
			R.string.lblExpress, R.string.lblShortExpress, 
			R.color.vehicle_express), 
	METRO(LineKind.METRO, 
			R.string.lblMetro, R.string.lblShortMetro, 
			R.color.vehicle_metro);

	public static String[] MOST_USED = {"33", "40", "Tb14", "Tv2", "Tv4", "Tb15"};
	public final LineKind originalKind;
	public final int labelId, shortLabelId;
	public final int colorId;
	
	private LineKindAndroidEx(LineKind originalKind, int labelId, int shortLabelId, int colorId) {
		this.originalKind = originalKind;
		this.labelId = labelId;
		this.shortLabelId = shortLabelId;
		this.colorId = colorId;
	}

	public static LineKindAndroidEx getAndroidEx(LineKind kind) {
		for(LineKindAndroidEx ex: values())
			if (ex.originalKind == kind)
				return ex;
		throw new NoSuchElementException();
	} 

	public static int getColorId(LineKind k) {
		return getAndroidEx(k).colorId;
	}

	public static int getLabelId(LineKind k) {
		return getAndroidEx(k).labelId;
	}

	public static int getShortLabelId(LineKind k) {
		return getAndroidEx(k).shortLabelId;
	}
	
}
