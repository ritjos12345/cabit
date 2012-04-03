package com.cabit.server;

import javax.persistence.Entity;

@Entity
public class Taxi {
	private String driver;
	private GpsLocation gpsLocation;
	
	public String getDriver() {
		return driver;
	}
	public void setDriver(String driver) {
		this.driver = driver;
	}
	public GpsLocation getGpsLocation() {
		return gpsLocation;
	}
	public void setGpsLocation(GpsLocation gpsLocation) {
		this.gpsLocation = gpsLocation;
	}
	@Override
	public String toString() {
		return "Taxi [driver=" + driver + ", gpsLocation=" + gpsLocation + "]";
	}
	
}
