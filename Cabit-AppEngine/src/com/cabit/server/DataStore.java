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
	
	
	private HashMap<String,Location> map;
	private List<Order> orders;
	
	public DataStore() {
		map = new HashMap<String, Location>();
		orders = new LinkedList<Order>();
		
		// TODO delete this 
		Location l = new Location();
		l.setLatitude((int) (33*1e6));
		l.setLongitude((int) (34*1e6));
		l.setTitle("www.udi@gmail.com");
		map.put(l.getTitle(), l);
		
		
		// TODO delete this 
		Location x = new Location();
		x.setLatitude((int) (33.5*1e6));
		x.setLongitude((int) (34.5*1e6));
		x.setTitle("itzikyacobi@gmail.com");
		map.put(x.getTitle(), x);
		
		
	}
	
	public Order createOrder(String user,String cab,Location from,Location to ){
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
	
	public Location updateMyLocation(Location location) {
		String cabName = Utils.getUserEmail();
		location.setTitle(cabName);
		map.put(cabName, location);
		return location;
	}
	
	public Location findCabLocation(String cab) {
		return map.get(cab);
	}

	public void deleteMyLocation(String cab) {
		map.remove(cab);
	}

	public List<Location> findAllCabLocation() {
		List<Location> list = new LinkedList<Location>();
		for (Location location : map.values()) {
			list.add(location); 
		} 
		return list;
	}
	
	// Can return null in case there are no cabs!
	public Location findClosestCab(Location myLocation ) {
		
		Location bestCab = null;
		double min = 0; 
		for (Location location : map.values()) {
			double tmp = location.distanceTo(myLocation);
			if(bestCab == null || tmp <=min){
				min = tmp;
				bestCab = location;
			}
		} 
		return bestCab;
	}
	
	

}
