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
import ro.mihai.tpt.model.Line;
import ro.mihai.util.LineKind;

public enum LineKindAndroidEx {
	TRAM(LineKind.TRAM, 
			R.string.lblTrams, R.string.lblShortTrams, 
			R.color.vehicle_tram,
			R.drawable.line_top_blue, R.drawable.line_middle_blue,
			R.drawable.line_bottom_blue, R.drawable.line_bullet_blue,
			R.drawable.btn_tram_dark
			), 
	TROLLEY(LineKind.TROLLEY, 
			R.string.lblTrolleys, R.string.lblShortTrolleys, 
			R.color.vehicle_trolley,
			R.drawable.line_top_purple, R.drawable.line_middle_purple,
			R.drawable.line_bottom_purple, R.drawable.line_bullet_purple,
			R.drawable.btn_trolleybus_dark
			),
	BUS(LineKind.BUS, 
			R.string.lblBus, R.string.lblShortBus, 
			R.color.vehicle_bus,
			R.drawable.line_top_red, R.drawable.line_middle_red,
			R.drawable.line_bottom_red, R.drawable.line_bullet_red,
			R.drawable.btn_bus_dark
			),
	EXPRESS(LineKind.EXPRESS, 
			R.string.lblExpress, R.string.lblShortExpress, 
			R.color.vehicle_express,
			R.drawable.line_top_green, R.drawable.line_middle_green,
			R.drawable.line_bottom_green, R.drawable.line_bullet_green,
			R.drawable.btn_bus_dark
			),
	METRO(LineKind.METRO, 
			R.string.lblMetro, R.string.lblShortMetro, 
			R.color.vehicle_metro,
			R.drawable.line_top_orange, R.drawable.line_middle_orange,
			R.drawable.line_bottom_orange, R.drawable.line_bullet_orange,
			R.drawable.btn_bus_dark
			);

	public static String[] MOST_USED = {"33", "40", "Tb14", "Tv2", "Tv4", "Tb15"};
	public final LineKind originalKind;
	public final int labelId, shortLabelId;
	public final int colorId;
	
	public final int line_top, line_middle, line_bottom, line_bullet;
	public final int dark_icon;

	private LineKindAndroidEx(LineKind originalKind, int labelId, int shortLabelId, int colorId,
			int line_top, int line_middle, int line_bottom, int line_bullet, int dark_icon) {
		this.originalKind = originalKind;
		this.labelId = labelId;
		this.shortLabelId = shortLabelId;
		this.colorId = colorId;
		
		this.line_top = line_top;
		this.line_middle = line_middle;
		this.line_bottom = line_bottom;
		this.line_bullet = line_bullet;
		this.dark_icon = dark_icon;
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

	public static String getLongerLineNameLabel(Line line) {
		String lineName = line.getName();
		if (lineName.startsWith("Tv") || lineName.startsWith("Tb"))
			lineName = lineName.substring(2);
		if (lineName.endsWith("b"))
			lineName = lineName.substring(0, lineName.length()-1);
		return lineName;
	}

	public static String getLineNameLabel(Line line) {
		String lineName = getLongerLineNameLabel(line);
		if (lineName.startsWith("M"))
			lineName = lineName.substring(1);
		return lineName;
	}

}
