package com.cabit.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import org.apache.geronimo.mail.util.SessionUtil;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.dev.ModuleTabPanel.Session;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class DataStore {

	// TODO log all the action in to the logger  with the method log.info(str)
	private static final Logger log = Logger.getLogger(DataStore.class.getName());
	
	
	private HashMap<String,GpsLocation> map;
	private List<Order> orders;
	
	/*	public DataStore() {
		
	
		map = new HashMap<String, GpsLocation>();
		orders = new LinkedList<Order>();
		
		// TODO delete this 
		GpsLocation l = new GpsLocation();
		l.setLatitude((int) (32*1e6));
		l.setLongitude((int) (35*1e6));
		l.setTitle("itzikyacobi@gmail.com");
		map.put(l.getTitle(), l);
		
	}
	
	public Order createOrder(String user,String cab,GpsLocation from,GpsLocation to ){
		Order order = new Order();
		orders.add( order);
		order.setUser(user);
		order.setCab(cab);
		order.setFrom(from);
		order.setTo(to);
		return order;
	}
	
	public Order findOrder(String user){
		for (Order ord : orders) {
			if(ord.getUser() == user || ord.getCab() == user){
				return ord;
			}
		}
		return null;
	}
	
	public GpsLocation updateMyLocation(GpsLocation location) {
		String cabName = Utils.getUserEmail();
		location.setTitle(cabName);
		map.put(cabName, location);
		return location;
	}
	
	public GpsLocation findCabLocation(String cab) {
		return map.get(cab);
	}

	public void deleteMyLocation(String cab) {
		map.remove(cab);
	}

	public List<GpsLocation> findAllCabLocation() {
		List<GpsLocation> list = new LinkedList<GpsLocation>();
		for (GpsLocation location : map.values()) {
			list.add(location); 
		} 
		return list;
	}
	
	// Can return null in case there are no cabs!
	public GpsLocation findClosestCab(GpsLocation myLocation ) {
		
		GpsLocation bestCab = null;
		double min = 0; 
		for (GpsLocation location : map.values()) {
			double tmp = location.distanceTo(myLocation);
			if(bestCab == null || tmp <=min){
				min = tmp;
				bestCab = location;
			}
		} 
		return bestCab;
	}
*/	
	

}
