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
package ro.mihai.tpt.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import android.content.Context;
import ro.mihai.util.DetachableStream;

public abstract class AndroidDetachableStream extends DetachableStream {
	private static final long serialVersionUID = 1L;

	private transient Context ctx;
	
	public AndroidDetachableStream(Context ctx) {
		setContext(ctx);
	}
	
	public Context getContext() {
		if(null==ctx)
			throw new IllegalStateException("Context must be re-initialized after serialization.");
		return ctx;
	}
	
	public void setContext(Context ctx) {
		if(null==ctx)
			throw new IllegalArgumentException("Context cannot be null.");
		this.ctx = ctx;
	}
	
	public static class FromFile extends AndroidDetachableStream {
		private static final long serialVersionUID = 1L;
		private String fileName;
		public FromFile(Context ctx, String fileName) {
			super(ctx);
			this.fileName = fileName;
		}
		
		@Override
		public DataInputStream openInputStream() throws IOException {
			return new DataInputStream(new BufferedInputStream(this.getContext().openFileInput(fileName)));
		}
	}

	public static class FromRawResource extends AndroidDetachableStream {
		private static final long serialVersionUID = 1L;
		private int resourceId;
		public FromRawResource(Context ctx, int resourceId) {
			super(ctx);
			this.resourceId = resourceId;
		}
		
		@Override
		public DataInputStream openInputStream() throws IOException {
			return new DataInputStream(new BufferedInputStream(this.getContext().getResources().openRawResource(resourceId)));
		}
	}	
}
