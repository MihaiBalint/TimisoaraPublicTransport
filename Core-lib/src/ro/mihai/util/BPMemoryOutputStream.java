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
