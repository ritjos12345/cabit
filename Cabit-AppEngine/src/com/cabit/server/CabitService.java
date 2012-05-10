package com.cabit.server;

import java.util.List;
import java.util.Map;

import com.cabit.annotation.ServiceMethod;


public class CabitService {
	
	private static DataStore db = DataStore.GetInstance();

	//// Taxi RPC functions ////
	
	//update Taxi's status line
	@ServiceMethod
	public boolean updadateStatusLine(String newStatusLine){
		boolean r = db.updateStatusLine(newStatusLine, Utils.getUserEmail());
		
		////////////////////////////////////////
		System.out.println("driver's status line was updated, " + Utils.getUserEmail());
		////////////////////////////////////////
		
		return r;
	}
	
	// update Taxi's location, return list of (pending) orders
	@ServiceMethod
	public TaxiStatus UpdateLocation(GpsLocation loc){
		System.out.println("UpdateLocation");
		return db.updateTaxiGpsLocation(loc, Utils.getUserEmail());
	}
	
	//return true if order accepted (= true) and order is available
	@ServiceMethod
	public boolean UpdateOrder(int orderId,boolean accepted){
		//if driver accepted to take an order
		if (accepted){
			return db.takeOrder(orderId, Utils.getUserEmail());
		}
		else{
			db.rejectOrder(orderId, Utils.getUserEmail());
			return false;
		}
	}
	
	
	
	
	
	
	//// User RPC functions ////
	
	

	//update nearby drivers update-frequency
	@ServiceMethod
	public void IAmNear(GpsLocation loc){
		System.out.println("IAmNear");
		
		List<Taxi> cabs = db.allCabs();
		
		//get list of closest taxis
		List<Taxi> potentialDrivers = db.findClosestCabs(loc, cabs);
		
		//update every taxi
		db.changeFreq(potentialDrivers, 2);
	}
	
	
	@ServiceMethod
	public int CreateOrder(GpsAddress from, GpsAddress to){  // return the order id
		System.out.println("CreateOrder");
		System.out.println("1");
		//create the order object
		Order ord = db.createOrder(from, to, Utils.getUserEmail());
		System.out.println("2");
		//inform potential cabs of the order
		db.newPotentialDrivers(ord);
		System.out.println("3");
		System.out.println("new order number : "+ord.getId());
		
		return ord.getId().intValue();
	}
	
	//if a driver took the order return the driver's name, o.w. return null 
	@ServiceMethod
	public String GetOrderStatus(int orderId){
		
		///////////////////////////////////////
		System.out.println("GetOrderStatus");
		///////////////////////////////////////
		
		//find the order
		Order ord = db.findOrder(new Integer(orderId));
		
		/* return the status:
		 * ord != null -> order is still pending
		 * ord == null -> order was assigned to a driver (find at currentJobs map) 
		 */
		if (ord != null){
			return null;
		}
		else{
			Map<Integer, String> CJ = db.getJobs();
			return CJ.get(new Integer(orderId));
		}
	}
	//@ServiceMethod
	public Taxi GetTaxi(String driver){		//GetTaxi?!?! F*** them
		
		///////////////////////////////////////
		System.out.println("GetTaxi: "+driver);
		///////////////////////////////////////
		
		return db.findTaxi(driver);
	}
	
	
	////// naive !!!
	//@ServiceMethod
	public List<Taxi> GetAllTaxi(){
		///////////////////////////////////////
		System.out.println("getAllTaxi");
		///////////////////////////////////////
		return db.allCabs();
	}
	
	
}
