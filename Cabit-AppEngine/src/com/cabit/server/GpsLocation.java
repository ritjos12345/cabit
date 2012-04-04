package com.cabit.server;

import java.awt.image.TileObserver;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.ServletContext;

@SuppressWarnings("serial")
@Entity
public class GpsLocation implements Serializable{
	
	private long latitude;
	private long longitude;
	
	public GpsLocation() {
		
	}
	

	public long getLatitude() {
		return latitude;
	}


	public void setLatitude(long latitude) {
		this.latitude = latitude;
	}


	public long getLongitude() {
		return longitude;
	}


	public void setLongitude(long longitude) {
		this.longitude = longitude;
	}


	@Override
	public String toString() {	
	    return "GpsLocation (" + longitude +","+ latitude + ")";
	}

	public double distanceTo(GpsLocation otherLocation) {
		return Math.sqrt(Math.pow(longitude - otherLocation.getLongitude(), 2) + 
				Math.pow(latitude - otherLocation.getLatitude(), 2) );
	}
}
