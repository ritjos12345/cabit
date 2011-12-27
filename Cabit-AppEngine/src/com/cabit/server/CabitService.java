package com.cabit.server;

import java.util.List;

import com.cabit.annotation.ServiceMethod;

/***
 * 
 * @author Udi
 *	This class is the business logic (the class that will be accessible from RPC )
 * 
 */
public class CabitService {	
	static DataStore db = new DataStore();
	
	@ServiceMethod
	public String orderCab(String cabName, Location from, Location to ) {
		Order ord = db.createOrder(Utils.getUserEmail(),cabName,from,to);
		sendOrderToCab(ord);
		return ord.getCab();
	}
	
	@ServiceMethod
	public String orderCab( Location from, Location to ) {
		return orderCab(db.findClosestCab(from).getTitle(),from,to);
	}
	
	@ServiceMethod
	public void confirmOrder( ) {
		Order ord = db.findOrder(Utils.getUserEmail());
		confirmClientOrder(ord);
	}

	@ServiceMethod
	public void cancelOrder() {
		db.findOrder(Utils.getUserEmail());
		// TODO send c2dm
	}
	
	@ServiceMethod
	public void updateMyLocation(Location location) {
		db.updateMyLocation(location);
	}

	@ServiceMethod
	public void deleteMyLocation() {
		db.deleteMyLocation(Utils.getUserEmail());
	}

	@ServiceMethod
	public List<Location> getAllCabs() {
		return db.findAllCabLocation();
	}
	/*
	@ServiceMethod
	public List<Location> addressToLocation(String address) {
		// TODO
		return null;
	}
	*/
	
	private void sendOrderToCab( Order order ){
		String message= "NEW_ORDER";
		message+= "," + order.getUser();
		message+= "," + order.getFrom();
		message+= "," + order.getTo();
		Utils.sendC2DMUpdate(order.getCab(), message);
	}
	
	private void cancelOrderToCab(String cabName,Order order){
		String message= "CANCEL_ORDER";
		message+= "," + order.getUser();
		
		Utils.sendC2DMUpdate(cabName, message);
	}
	
	private void confirmClientOrder(Order order){
		String message= "NEW_ORDER";
		message+= "," + order.getCab();
		message+= "," + order.getFrom();
		message+= "," + order.getTo();
		Utils.sendC2DMUpdate(order.getUser(), message);
	}
	
	private void cancelClientOrder(String cabName,Order order){
		String message= "CANCEL_ORDER";
		message+= "," + order.getCab();
		Utils.sendC2DMUpdate(cabName, message);
	}
}
