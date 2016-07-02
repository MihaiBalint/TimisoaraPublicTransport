/*
    TimisoaraPublicTransport - display public transport information on your device
    Copyright (C) 2014  Mihai Balint

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
package ro.mihai.tpt.model;

import java.io.Serializable;

public class Segment implements Serializable {
	private static final long serialVersionUID = 1L;
	private Station from, to;
	private int[] duration; // in seconds
	
	public Segment(Station from, Station to) {
		duration = new int[24];
		this.from = from;
		this.to = to;
	}
	
	public Station getFrom() {
		return from;
	}
	
	public Station getTo() {
		return to;
	}
	
	public int getDuration(int timeOfDay) {
		assert(timeOfDay>=0 && timeOfDay<duration.length);
		return duration[timeOfDay];
	}
	
	
}
