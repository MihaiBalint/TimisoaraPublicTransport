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

import ro.mihai.tpt.model.LineMetal;
import ro.mihai.tpt.model.Path;

public enum LineResources {
	Tv1(R.drawable.tv1), 
	Tv2(R.drawable.tv2), 
	Tv4(R.drawable.tv4), 
	Tv5(R.drawable.tv5), 
	Tv6(R.drawable.tv6), 
	Tv7a(R.drawable.tv7), 
	Tv7b(R.drawable.tv7), 
	Tv8(R.drawable.tv8), 
	Tv9(R.drawable.tv9), 

	Tb11(R.drawable.tb11), 
	Tb14(R.drawable.tb14), 
	Tb15(R.drawable.tb15), 
	Tb16(R.drawable.tb16), 
	Tb17(R.drawable.tb17), 
	Tb18(R.drawable.tb18), 
	Tb19(R.drawable.tb19), 
	OTHER(R.drawable.tv1);
	
	int mapIconId;
	String name;
	
	private LineResources(int mapIconId) {
		this.mapIconId = mapIconId;
		this.name = this.name();
	}
	private LineResources(String name, int mapIconId) {
		this.mapIconId = mapIconId;
		this.name = name;
	}
	
	public int getMapIconId() {
		return mapIconId;
	}
	
	public static LineResources value(Path path) {
		return value(path.getLine());
	}
	
	public static LineResources value(LineMetal line) {
		return value(line.getName());
	}
	
	public static LineResources value(String name) {
		for(LineResources r : values())
			if(name.equals(r.name))
				return r;
		return OTHER;
	}
}
