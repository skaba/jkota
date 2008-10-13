package com.googlecode.jkota;

public enum Unit {

	BYTE(1L),
	KB(1024L),
	MB(1024L*1024L),
	GB(1024L*1024L*1024L);

	private long divider;

	private Unit(long divider) {
		this.divider = divider;
	}

	public final long getDivider() {
		return divider;
	}	
}
