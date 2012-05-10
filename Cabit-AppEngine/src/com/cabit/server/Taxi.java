package com.cabit.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
public class Taxi implements Serializable{
	private String driver;
	private GpsLocation gpsLocation;
	private TaxiStatus status;
	private String statusLine;
	
	
	public Taxi(){
		status = new TaxiStatus();
		statusLine = new String();
	}
	
	public String getStatusLine() {
		return statusLine;
	}

	public void setStatusLine(String statusLine) {
		this.statusLine = statusLine;
	}

	//add another order to the taxi's current status
	public void newOrder(Order ord){
		status.addOrder(ord);
	}
	public void addOrder(Order ord){
		status.addOrder(ord);
	}
	public void removeOrder(Integer orderId){
		status.removeOrder(orderId);
	}
	public void removeOrder(Order ord){
		status.removeOrder(ord);
	}
	public void RemoveAllOrders(){
		status.removeAllOrders();
	}
	public TaxiStatus getStatus(){
		return this.status;
	}
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
	public void updateFreqRate(Integer freq){
		this.status.setConnectionStatus(freq);
	}


	@Override
	public String toString() {
		return "Taxi [driver=" + driver + ", gpsLocation=" + gpsLocation
				+ ", status=" + status + "]";
	}
	
	
}
