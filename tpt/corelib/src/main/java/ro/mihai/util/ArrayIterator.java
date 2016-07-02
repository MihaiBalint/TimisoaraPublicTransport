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

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {
	private E[][] multiArray;
	private int arrayIndex, index;
	
	public ArrayIterator(E[]... multiArray) {
		this.multiArray = multiArray;
		this.arrayIndex = 0;
		this.index = 0;
	}
	
	@Override
	public boolean hasNext() {
		if (this.arrayIndex >= this.multiArray.length)
			return false;
		if (this.index < this.multiArray[this.arrayIndex].length)
			return true;
		for(int ai=this.arrayIndex+1;ai<this.multiArray.length;ai++)
			if (this.multiArray[ai].length > 0)
				return true;
		return false;
	}
	
	@Override
	public E next() {
		if (this.arrayIndex >= this.multiArray.length)
			throw new NoSuchElementException();
		E value;
		if (this.index < this.multiArray[this.arrayIndex].length) {
			value = this.multiArray[this.arrayIndex][this.index];
			this.index++;
			return value;
		}
		this.index = 0;
		this.arrayIndex++;
		while(this.arrayIndex < this.multiArray.length) {
			if (this.multiArray[this.arrayIndex].length > 0) {
				value = this.multiArray[this.arrayIndex][this.index];
				this.index++;
				return value;
			}
			this.arrayIndex++;
		}
		throw new NoSuchElementException();
	}
	
	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
