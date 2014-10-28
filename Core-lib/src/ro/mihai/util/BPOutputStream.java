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
		itemStream.writeInt(items.size());
		for(T s: items) {
			s.persist(itemStream, lazy);
			lazy.flush();
		}
		itemStream.flush();
		
		stream.writeInt(1);
		stream.writeInt(itemStream.size());
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
