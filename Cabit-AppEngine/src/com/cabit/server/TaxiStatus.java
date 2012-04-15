package com.cabit.server;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

@Entity
public class TaxiStatus {
	
	@Override
	public String toString() {
		return "TaxiStatus [orders=" + orders + ", connectionStatus="
				+ connectionStatus + "]";
	}

	private List<Order> orders;
	private Integer connectionStatus;	
	/* 
	 *	connection frequency s.t. f0 > f1 > f2
	 *  f0 - client arrived nearby,
	 *  f1 - regular update rate
	 *  f2 - Taxi is busy, update less frequently
	*/
	
	public TaxiStatus(){
		orders = new LinkedList<Order>();		
		connectionStatus = 1;
	}
	
	
	public void addOrder(Order orders) {
		this.orders.add(orders);
	}
	public void removeOrder(Order ord){
		this.orders.remove(ord);
	}
	
	public void removeAllOrders() {
		orders.clear();
		orders = new LinkedList<Order>();
	}
	
	//remove order by orderID from the list of pending orders
	public void removeOrder(Integer ordId){
		for (Order ord: orders){
			if (ord != null && ord.getId().equals(ordId)){
				orders.remove(ord);
			}
		}
	}
	
	public Integer getConnectionStatus() {
		return connectionStatus;
	}
	public void setConnectionStatus( Integer freq) {
		this.connectionStatus = freq;
	}

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}
	
	
	
	
	
}
