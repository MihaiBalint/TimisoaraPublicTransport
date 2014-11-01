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
package ro.mihai.util;

import java.io.ByteArrayOutputStream;

public class BPMemoryOutputStream extends BPOutputStream {
	private ByteArrayOutputStream stream;

	public BPMemoryOutputStream(ByteArrayOutputStream stream) {
		super(stream);
		this.stream = stream;
	}

	public int size() {
		return stream.size();
	}
	
	byte[] toByteArray() {
		return stream.toByteArray();
	}
	
	public static BPMemoryOutputStream usingByteArray() {
		return new BPMemoryOutputStream(new ByteArrayOutputStream());
	}
}
