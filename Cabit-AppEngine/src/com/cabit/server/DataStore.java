package com.cabit.server;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.geronimo.mail.util.SessionUtil;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.dev.ModuleTabPanel.Session;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class DataStore {

	// TODO log all the action in to the logger  with the method log.info(str)
	private static final Logger log = Logger.getLogger(DataStore.class.getName());
	
	//map Driver to a Taxi
	private Map<String, Taxi> drivers;
	
	//map ID to Order
	private Map<Integer, Order> orders;

	//map order-ID to driver
	private Map<Integer, String> currentJobs;
	
	//hold reference to the session
	private HttpSession session;
	
	//the next order ID
	private Integer NextOrderID;
	
	//defines the length of nearby cabs list
	private final int nearbyCabs = 3;
	
	
	private void getOrders(){
		orders = (Map<Integer, Order>) session.getAttribute("orders");
	}
	private void updateOrders(Map<Integer, Order> ords){
		session.setAttribute("orders", ords);
	}
	
	private void getDrivers(){
		drivers = (Map<String, Taxi>) session.getAttribute("drivers");
	}
	private void updateDrivers(Map<String, Taxi> drvs){
		session.setAttribute("drivers", drvs);
	}
	
	public Map<Integer, String> getJobs(){
		return (Map<Integer, String>) session.getAttribute("currentJobs");
	}
	public void updateJobs(Map<Integer, String> jbs){
		session.setAttribute("currentJobs", jbs);
	}

	
	public DataStore() {		
		
		//init data structures
		orders = Collections.synchronizedMap(new HashMap<Integer, Order>());
		drivers = Collections.synchronizedMap(new HashMap<String, Taxi>());
		currentJobs = Collections.synchronizedMap(new HashMap<Integer, String>());
		NextOrderID = new Integer(0);
		
		//init session var for continues access
		session = RequestFactoryServlet.getThreadLocalRequest().getSession();
				
		//insert the DS to the current session 
		session.setAttribute("orders", orders);
		session.setAttribute("drivers", drivers);
		session.setAttribute("currentJobs", currentJobs);
		
	}
	
	//update Taxi's GpsLocation
	public TaxiStatus updateTaxiGpsLocation(GpsLocation loc) {  //return GpsLocation??
		String cabName = Utils.getUserEmail();			
		
		drivers = (Map<String, Taxi>)session.getAttribute("drivers");
		
		Taxi driver = null;
		
		driver = drivers.get(cabName);
		
		if (driver != null){
			driver.setGpsLocation(loc);
		}
		
		/*taxi wasn't found, add new available driver*/
		else{
			driver = new Taxi();
			driver.setDriver(cabName);
			driver.setGpsLocation(loc);
			
			//update the session's list of drivers
			drivers.put(cabName, driver);
		}
		
		
	
		//update the session
		session.setAttribute("drivers", drivers);
		
		return driver.getStatus();
	}
	
	
	//return true if order represented by it's id is available
	public boolean takeOrder(Integer orderId){
		String cabName = Utils.getUserEmail();
		
		getOrders();
		Order ord = orders.get(orderId);
		getDrivers();
		Taxi taxi = drivers.get(cabName);
		
		//if order is still available
		if (ord != null){			
			//remove references to the order from other taxis
			ord.removeDrivers(taxi);
			
			//remove the order from the hash map and update session
			orders.remove(ord);
			updateOrders(orders);
			
			//change to regular update frequency
			taxi.updateFreqRate(1);
			
			//update current taxi as busy ??????????????????????????????????????
			
			//clear driver's potential orders
			taxi.RemoveAllOrders();
			
			drivers.put(cabName, taxi);
			updateDrivers(drivers);
			
			//update the current jobs structure
			this.currentJobs = getJobs();
			currentJobs.put(orderId, taxi.getDriver());
			updateJobs(currentJobs);
			
			return true;			
		}
		else{
			//remove the order from the driver's list of pending orders
			taxi.removeOrder(orderId);
			
			//return to regular updating frequency
			taxi.updateFreqRate(1);
			
			drivers.put(cabName, taxi);
			updateDrivers(drivers);
			
			return false;
		}
	}
	
	//remove order represented by it's id from the driver's pending orders
	public void rejectOrder(Integer orderId){
		String cabName = Utils.getUserEmail();
		
		//find the driver's object
		getDrivers();
		Taxi taxi = drivers.get(cabName);
		
		//remove the reference
		taxi.removeOrder(orderId);
		
		//return to regular updating frequency
		if (taxi.getStatus().getOrders().size() == 0){
			taxi.updateFreqRate(1);
		}
		
		//update the drivers map
		drivers.put(cabName, taxi);
		
		//update the session
		updateDrivers(drivers);
		
		//add the driver to the orders black list
		getOrders();
		Order ord = orders.get(orderId);
		ord.addToBlackList(taxi);
		orders.put(orderId, ord);
		updateOrders(orders);
		
		
	}
	
	//create order, update the orders data structure and return it's order-id
	public Order createOrder(GpsAddress from,GpsAddress to ){
		String user = Utils.getUserEmail();
		
		Order order = new Order();
		order.setUser(user);
		order.setId(NextOrderID);
		
		//increment the next order id, cyclic
		NextOrderID++;
		if (NextOrderID < 0) NextOrderID = 0;
		
		order.setFrom(from);
		order.setTo(to);
				
		//update the session's orders map
		getOrders();
		orders.put(order.getId(), order);
		updateOrders(orders);
		
		return order;
	}
	
	
	//find potential drivers for an order
	public void newPotentialDrivers(Order ord){
		
		//create list of all potential drivers
		List<Taxi> cabs = this.allCabs();
		cabs.removeAll(ord.getBlackList());
		
		//find closest cabs among the list
		List<Taxi> foundCabs = this.findClosestCabs(ord.getFrom().getLocation(), cabs);
		
		//inform those cabs of a new job
		for (Taxi cab : foundCabs){
			//update the taxi
			cab.addOrder(ord);
			
			cab.updateFreqRate(2);
			
			//update the hash map
			drivers.put(cab.getDriver(), cab);
			
		}
		
		//update the order's list of potential drivers
		ord.addDrivers(foundCabs);
		orders.put(ord.getId(), ord);
		
		//update the session
		updateDrivers(drivers);
		updateOrders(orders);
	}
	
	
	
	//return order of a given user - by order-ID
	public Order findOrder(Integer id){
		this.getOrders();
		return orders.get(id);
	}
	
	//return Taxi object of a given driver
	public Taxi findTaxi(String driver){
		this.getDrivers();
		return drivers.get(driver);
	}
	
	
	//return list of all cabs
	public List<Taxi> allCabs(){
		
		//iterate the drivers data structure
		this.getDrivers();
		
		return new LinkedList<Taxi>(drivers.values());
	}
	
	
	// Can return empty list in case there are no cabs! - naive!!!
	public List<Taxi> findClosestCabs(GpsLocation loc, List<Taxi> cabs) {
		
		//used to hold the current min distances
		List<cabDist> cabsDist = new ArrayList<cabDist>(this.nearbyCabs);
		
		cabDist maxDist = null;
		
		for (Taxi cab : cabs) { 
			double temp = cab.getGpsLocation().distanceTo(loc);
			
			//insert the first cab
			if( maxDist == null){
				cabDist c = new cabDist(cab, temp);
				cabsDist.add(c);
				maxDist = c;				
			}
			
			//check if current cab is closer then the max distanced cab
			else{
				for (int i = 0; i < cabsDist.size(); i++){
					if (cabsDist.get(i)== null){
						cabDist c = new cabDist(cab, temp);
						cabsDist.add(c);
						
						//update the max distance of a cab
						if (temp > maxDist.getDist()){
							maxDist = c;
							break;
						}
					}
					
					else if (cabsDist.get(i).getDist() < maxDist.getDist()){
						cabsDist.remove(maxDist);
						cabDist c = new cabDist(cab, temp);
						cabsDist.add(c);
						maxDist = c;
						break;
					}
				}
			}
		}
		
		//convert to list of cabs
		cabs = new LinkedList<Taxi>();
		for (cabDist cab: cabsDist){
			if (cab != null){
				cabs.add(cab.getCab());
			}
		}
		
		return cabs;
	}
	
	
	//this class used to measure distance between cabs
	class cabDist{
		private Taxi cab;
		private double dist;
		
		public cabDist(Taxi cab, double dist){
			this.cab = cab;
			this.dist = dist;
		}
		
		public void setDist(double dist){
			this.dist = dist;
		}	
		public double getDist(){
			return this.dist;
		}
		private Taxi getCab(){
			return this.cab;
		}
	}
	
	
	//update the update-frequency of a given list of taxis and the desired rate
	public void changeFreq(List<Taxi> cabs, Integer freq){
		//update taxis and the relative hash map
		for (Taxi cab: cabs){
			cab.updateFreqRate(freq);
			drivers.put(cab.getDriver(), cab);
		}
		
		//update the data structure
		updateDrivers(drivers);
	}
	
	
	
	/*
	
	//return order of a given user - by username
	public Order findOrder(String user){
		//update the orders map from the session
		orders = (Map<Integer, Order>) session.getAttribute("orders");
		
		//iterate the map
		Iterator it = orders.entrySet().iterator();
		
		/*
		while (it.hasNext())
			Map.Entry e = (Map.Entry)it.next();
			if(ord.getUser() == user){
				return ord;
			}
		}
		return null;
	}
	
	
	
	public GpsLocation findCabGpsLocation(String cab) {
		return map.get(cab);
	}

	public void deleteMyGpsLocation(String cab) {
		map.remove(cab);
	}

	public List<GpsLocation> findAllCabGpsLocation() {
		List<GpsLocation> list = new LinkedList<GpsLocation>();
		for (GpsLocation GpsLocation : map.values()) {
			list.add(GpsLocation); 
		} 
		return list;
	}*/
	
}
