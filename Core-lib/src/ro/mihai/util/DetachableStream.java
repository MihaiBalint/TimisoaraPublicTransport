package ro.mihai.util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class DetachableStream implements Serializable {
	private static final long serialVersionUID = 1L;
	private transient DataInputStream in;
	private long position = 0, mark = 0;
	private int markReadLimit = 0;

	@Deprecated
	public DataInputStream getInputStream() throws IOException {
		return ensureStream();
	}

	private DataInputStream ensureStream() throws IOException {
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
	
	private static void skipOver(DataInputStream in, long p) throws IOException {
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
	
	protected abstract DataInputStream openInputStream() throws IOException;
	
	
	public synchronized int readInt() throws IOException {
		int result = ensureStream().readInt();
		position+=4;
		return result;
	}
	
	public synchronized String readString() throws IOException {
		int bc = ensureStream().readInt();
		byte[] b = new byte[bc];
		ensureStream().readFully(b);
		position+=bc+4;
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
}
