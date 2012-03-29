package com.cabit.server;

import javax.persistence.Entity;

@Entity
public class Address extends GpsLocation{
	String title;
	
	public Address() {
		super();
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Override
	public String toString() {
		return "Address [title=" + title + ", gpsLocation=" + super.toString() + "]";
	}
	
	
}
