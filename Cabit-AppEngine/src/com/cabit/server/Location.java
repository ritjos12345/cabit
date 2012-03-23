package com.cabit.server;

import java.awt.image.TileObserver;
import java.util.Date;
import java.util.logging.Logger;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.ServletContext;

@Entity
public class Location {
	
	private String title;
	private int latitude;
	private int longitude;
	
	public Location() {
		
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getLatitude() {
		return latitude;
	}

	public void setLatitude(int latitude) {
		this.latitude = latitude;
	}

	public int getLongitude() {
		return longitude;
	}

	public void setLongitude(int longitude) {
		this.longitude = longitude;
	}

	@Override
	  public String toString() {
		String  t = "";
		if(title !=null)
			t= title;
	    return "Location [title=" +t + ", is in (" + longitude +","+ latitude + ")]";
	  }

	public double distanceTo(Location myLocation) {
		return Math.sqrt(Math.pow(longitude - myLocation.getLongitude(), 2) + 
				Math.pow(latitude - myLocation.getLatitude(), 2) );
		
	}
}
