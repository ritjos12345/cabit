package com.cabit.server;

import javax.persistence.Entity;

@Entity
public class Order {

	private int id;
	private String user;
	private Address from;
	private Address to;
	
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
	public Address getFrom() {
		return from;
	}
	public void setFrom(Address from) {
		this.from = from;
	}
	public Address getTo() {
		return to;
	}
	public void setTo(Address to) {
		this.to = to;
	}
	
}
