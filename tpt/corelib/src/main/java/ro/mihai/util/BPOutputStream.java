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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import ro.mihai.tpt.model.PersistentEntity;

public class BPOutputStream {
	private DataOutputStream stream;
	
	public BPOutputStream(OutputStream stream) {
		this(new DataOutputStream(stream));
	}
	
	public BPOutputStream(DataOutputStream stream) {
		this.stream = stream;
	}

	public void writeString(String data) throws IOException {
		if (data == null) {
			stream.writeInt(-1);
			return;
		}
		byte[] b = data.getBytes();
		stream.writeInt(b.length); stream.write(b);
	}
	
	public void writeObjectId(int objectId) throws IOException {
		stream.writeInt(objectId);
	}
	
	public void writeInt(int data) throws IOException {
		stream.writeInt(data);
	}

	public void writeLong(long data) throws IOException {
		stream.writeLong(data);
	}
	
	public void writeMagic(String magic) throws IOException {
		stream.write(magic.getBytes());
	}
	
	public <T extends PersistentEntity> void writeEntityCollection(Collection<T> items, BPMemoryOutputStream lazy) throws IOException {
		BPMemoryOutputStream itemStream = BPMemoryOutputStream.usingByteArray();
		for(T s: items) {
			s.saveEagerAndLazy(itemStream, lazy);
			lazy.flush();
		}
		itemStream.flush();
		
		stream.writeInt(1);
		stream.writeInt(itemStream.size());
		stream.writeInt(items.size());
		stream.write(itemStream.toByteArray());
		
	}
	
	public void writeLazyBlock(BPMemoryOutputStream data) throws IOException {
		stream.writeInt(2);
		stream.writeInt(data.size());
		stream.write(data.toByteArray());
	}
	
	public void flush() throws IOException {
		stream.flush();
	}
	
	public void close() throws IOException {
		stream.close();
	}
	
	
}
