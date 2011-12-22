package com.cabit.server;

import java.util.Date;
import java.util.logging.Logger;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.ServletContext;

@Entity
public class Location {
	private Date date;	
	private String userEmail;
	private int latitude;
	private int longitude;
	

	public Location() {
		
	}

	
	
	public Date getDate() {
		return date;
	}



	public void setDate(Date date) {
		this.date = date;
	}



	public String getUserEmail() {
		return userEmail;
	}



	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
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
	    return "Location [user=" + userEmail + ", is in (" + longitude +","+ latitude + ")]";
	  }

}
