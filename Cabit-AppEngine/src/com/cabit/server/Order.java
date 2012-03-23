package com.cabit.server;

public class Order {

	private String user;
	private Location from;
	private Location to;
	private String Cab;
	
	
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Location getFrom() {
		return from;
	}
	public void setFrom(Location from) {
		this.from = from;
	}
	public Location getTo() {
		return to;
	}
	public void setTo(Location to) {
		this.to = to;
	}
	public String getCab() {
		return Cab;
	}
	public void setCab(String cab) {
		Cab = cab;
	} 
	
	
}
