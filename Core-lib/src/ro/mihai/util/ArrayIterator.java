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
