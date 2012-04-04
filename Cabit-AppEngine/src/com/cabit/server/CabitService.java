package com.cabit.server;

import java.util.LinkedList;
import java.util.List;

import com.cabit.annotation.ServiceMethod;


public class CabitService {

	// Taxi RPC functions
	private int i, b=0 ;
	@ServiceMethod
	public TaxiStatus UpdateLocation(GpsLocation loc){
		// TODO Auto-generated method stub
		if(b==2)
		{
			return new TaxiStatus();
		}
		else
		{
			b++;
			return null;
		}
	}
	
	@ServiceMethod
	public boolean UpdateOrder(int orderId,boolean except){
		// TODO Auto-generated method stub
		return true;
	}
	
	
	// User RPC functions
	@ServiceMethod
	public void IAmNear(GpsLocation loc){
		// TODO Auto-generated method stub
	}
	
	@ServiceMethod
	public int CreateOrder(GpsAddress from, GpsAddress to){  // return the order id
		// TODO Auto-generated method stub
		return 1;
	}
	
	@ServiceMethod
	public int GetOrderStatus(int orderId){ // TODO fix this  should return List<Taxi,enum status>
		// TODO Auto-generated method stub
		return 1;
	}
	
	@ServiceMethod
	public Taxi GetTaxi(String driver){
		// TODO Auto-generated method stub
		return null;
	}
	
	@ServiceMethod
	public List<Taxi> GetAllTaxi(){
		i=i+1 % 40;
		LinkedList<Taxi> l  = new LinkedList<Taxi>();
		GpsLocation g = new GpsLocation();
		g.setLatitude((int) (  i*1e6)); // 33.5
		g.setLongitude((int) (34.5*1e6));
		Taxi t= new Taxi();
		t.setDriver("udi2");
		t.setGpsLocation(g);
		l.add(t);
		return l;
	}
	
	
	
}
