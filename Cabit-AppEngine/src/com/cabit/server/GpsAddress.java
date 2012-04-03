package com.cabit.server;

import javax.persistence.Entity;


@Entity
public class GpsAddress {
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
