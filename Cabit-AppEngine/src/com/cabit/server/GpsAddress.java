package com.cabit.server;

import java.io.Serializable;

import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
public class GpsAddress implements Serializable{
	String title;
	GpsLocation location;
	
	public GpsAddress() {
		super();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public GpsLocation getLocation() {
		return location;
	}

	public void setLocation(GpsLocation location) {
		this.location = location;
	}
	@Override
	public String toString() {
		return "Address [title=" + title + ", location=" + location + "]";
	}
	
	
}
