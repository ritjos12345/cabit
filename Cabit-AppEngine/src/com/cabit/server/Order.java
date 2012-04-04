package com.cabit.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;

@SuppressWarnings("serial")
@Entity
public class Order implements Serializable{

	private Integer id;
	private String user;
	private GpsAddress from;
	private GpsAddress to;
	private List<Taxi> potentialDrivers;
	private List<Taxi> driversBlackList;
	
	
	
	public Order(){
		this.potentialDrivers = new LinkedList<Taxi>();
		this.driversBlackList = new LinkedList<Taxi>();
	}
	
	//return if driver is at order's black list
	public boolean blackListed(Taxi driver){
		return driversBlackList.contains(driver);
	}
	
	//return the order's black list of drivers
	public List<Taxi> getBlackList(){
		return this.driversBlackList;
	}
	
	//add driver to the order's black list
	public void addToBlackList(Taxi driver){
		driversBlackList.add(driver);
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public GpsAddress getFrom() {
		return from;
	}
	public void setFrom(GpsAddress from) {
		this.from = from;
	}
	public GpsAddress getTo() {
		return to;
	}
	public void setTo(GpsAddress to) {
		this.to = to;
	}
	
	//add drivers to the list of optional drivers
	public void addDrivers(List<Taxi> newDrivers){
		this.potentialDrivers.removeAll(newDrivers);		//behavior ???
		this.potentialDrivers.addAll(newDrivers);
	}
	
	//remove the list of  potential drivers after updating them, skip a given taxi
	public void removeDrivers(Taxi accepted){
		for (Taxi taxi: potentialDrivers){
			//remove order from every taxi that did not respond
			if (!taxi.equals(accepted)){
				if (taxi != null){
					taxi.removeOrder(this);
				}
			}
		}
	}
}
