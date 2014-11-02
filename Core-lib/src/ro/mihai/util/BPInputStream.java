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

import java.io.DataInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class BPInputStream implements Serializable {
	private static final long serialVersionUID = 1L;
	protected long position = 0, mark = 0;
	protected int markReadLimit = 0;
	
	@Deprecated
	public DataInputStream getInputStream() throws IOException {
		return ensureStream();
	}

	protected abstract DataInputStream ensureStream() throws IOException;
	
	static void skipOver(DataInputStream in, long p) throws IOException {
		int errs = 3;
		while(p>0) {
			long sp = in.skip(p);
			if(sp>0)
				p -= sp;
			else if(sp==0) {
				if(errs<=0)
					throw new IOException("Skip returned zero too many times.");
				errs --;
			} else
				throw new IOException("Skip returned negative.");
		}
	}	
	
	public synchronized int readObjectId() throws IOException {
		return readInt();
	}
	
	public synchronized int readInt() throws IOException {
		int result = ensureStream().readInt();
		position+=4;
		return result;
	}

	public synchronized long readLong() throws IOException {
		long result = ensureStream().readLong();
		position+=8;
		return result;
	}
	
	public synchronized String readString() throws IOException {
		int bc = ensureStream().readInt();
		position+=4;
		if (bc < 0)
			return null;
		byte[] b = new byte[bc];
		ensureStream().readFully(b);
		position+=bc;
		return new String(b);
	}

	public synchronized String readFixedLengthString(int byteLength) throws IOException {
		byte[] b = new byte[byteLength]; 
		ensureStream().readFully(b);
		position+=byteLength;
		return new String(b);
	}
	
	public synchronized void mark(int readlimit) throws IOException {
		ensureStream().mark(readlimit);
		this.mark = this.position;
		this.markReadLimit = readlimit;
	}

	public synchronized void reset() throws IOException {
		ensureStream().reset();
		this.position = this.mark;
	}
	
	public synchronized void sureSkip(long p) throws IOException {
		skipOver(ensureStream(), p);
		position+=p;
	}
	
	public synchronized void close() throws IOException {
		ensureStream().close();
	}
	
	public EntityIterator readEntityCollection() throws IOException {
		int blType = readInt();
		assert blType == 1;

		int blLength = readInt();
		assert blLength >= 0;
		
		int count = readInt();
		assert count >= 0;
		return new EntityIterator(count);
	}
	
	public synchronized int skipToLazyBlock() throws IOException {
		int blType = 0, blLength = 0;
		while (blType!=2) {
			blType = readInt();
			blLength = readInt();
			if (blType==2) break;
			sureSkip(blLength);
		}

		if (blType!=2) 
			throw new IOException("Failed to read deferred resource data before stream ended.");
		return blLength;
	}
	
	public static class EntityIterator implements Iterator<Integer> {
		private int count, size; 
		public EntityIterator(int size) {
			this.count = -1;
			this.size = size;
		}
		@Override
		public Integer next() {
			if (!hasNext())
				throw new NoSuchElementException();
			this.count++;
			return this.count;
		}
		@Override
		public boolean hasNext() {
			return (this.count+1) < this.size;
		}
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
