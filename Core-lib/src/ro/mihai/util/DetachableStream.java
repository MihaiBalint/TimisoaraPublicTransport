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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class DetachableStream extends BPInputStream implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient DataInputStream in;

	protected DataInputStream ensureStream() throws IOException {
		if (null==in) {
			in = openInputStream();
			if(position>=mark && position<(mark+markReadLimit)) {
				// restore position mark
				skipOver(in, mark);
				in.mark(markReadLimit);
				if(position>mark)
					skipOver(in, position-mark);
			} else {
				// mark position does not exist or has been invalidated
				skipOver(in, position);
			}
		}
		return in;
	}
	
	protected abstract DataInputStream openInputStream() throws IOException;
	
	public static class FromFile extends DetachableStream {
		private static final long serialVersionUID = 1L;
		private String fileName;
		public FromFile(String fileName) {
			this.fileName = fileName;
		}
		
		@Override
		public DataInputStream openInputStream() throws IOException {
			return new DataInputStream(new BufferedInputStream(new FileInputStream(new File(fileName))));
		}
	}
	
	public static class FromBytes extends DetachableStream {
		private static final long serialVersionUID = 1L;
		private byte[] bytes;
		public FromBytes(byte[] bytes) {
			this.bytes = bytes;
		}
		
		@Override
		public DataInputStream openInputStream() throws IOException {
			return new DataInputStream(new ByteArrayInputStream(bytes));
		}
	}
	
}
