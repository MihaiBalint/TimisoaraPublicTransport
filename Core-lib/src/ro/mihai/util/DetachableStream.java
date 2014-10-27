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
