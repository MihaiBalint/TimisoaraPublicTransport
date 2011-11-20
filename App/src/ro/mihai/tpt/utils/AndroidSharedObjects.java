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

import ro.mihai.tpt.model.*;

public class AndroidSharedObjects {
	private City city;
	private Path linePath;

	public City getCity() {
		return city;
	}
	public void setCity(City city) {
		this.city = city;
	}
	
	public Path getLinePath() {
		return linePath;
	}
	public void setLinePath(Path linePath) {
		this.linePath = linePath;
	}
	
	
	private static AndroidSharedObjects shared = new AndroidSharedObjects();
	
	public static AndroidSharedObjects instance() {
		return shared;
	}
}
