package ro.mihai.tpt.model;

import java.io.Serializable;

public enum DataVersion implements Serializable {
	Version1, 
	Version2,
	Version3,
	Version4,
	Version5,
	Version6;
	
	public boolean lessThan(DataVersion other) {
		return this.ordinal() < other.ordinal();
	}
	
	public boolean atLeast(DataVersion other) {
		return this.ordinal() >= other.ordinal();
	}
}
