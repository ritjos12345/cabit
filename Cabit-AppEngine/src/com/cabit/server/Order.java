package com.cabit.server;

import javax.persistence.Entity;

@Entity
public class Order {

	private int id;
	private String user;
	private GpsAddress from;
	private GpsAddress to;
	
	public Order(){
		
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
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

	@Override
	public String toString() {
		return "Order [id=" + id + ", user=" + user + ", from=" + from
				+ ", to=" + to + "]";
	}
	
}
