package com.cabit.server;

import java.util.List;

import javax.persistence.Entity;

@Entity
public class TaxiStatus {

	
	
	private List<Order> orders;
	private int connectionStatus;
	
	public TaxiStatus(){
		
	}
	
	public List<Order> getOrders() {
		return orders;
	}
	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	public int getConnectionStatus() {
		return connectionStatus;
	}
	public void setConnectionStatus(int connectionStatus) {
		this.connectionStatus = connectionStatus;
	}
	
	
	
}
