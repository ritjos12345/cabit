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
	private static DataStore instance;
	
	public static DataStore GetInstance(){
		if (instance ==null){
			instance = new DataStore();
		}
		return instance;
	}
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
		
		/*GpsLocation g =new GpsLocation();
		g.setLatitude(33);
		g.setLatitude(34);
		updateTaxiGpsLocation(g,"udi taxis inc" );*/
	}
	
	
	private void getOrders(){
		orders = (Map<Integer, Order>) session.getAttribute("orders");
	}
	private void updateOrders(Map<Integer, Order> ords){
		session.setAttribute("orders", ords);
		//this.orders = ords;
	}
	
	private void getDrivers(){
		drivers = (Map<String, Taxi>) session.getAttribute("drivers");
	}
	private void updateDrivers(Map<String, Taxi> drvs){
		session.setAttribute("drivers", drvs);
		//this.drivers = drvs;
	}
	
	public Map<Integer, String> getJobs(){
		return (Map<Integer, String>) session.getAttribute("currentJobs");
		//return this.currentJobs;
	}
	public void updateJobs(Map<Integer, String> jbs){
		session.setAttribute("currentJobs", jbs);
		//this.currentJobs = jbs;
	}
	
	
	
	//update Taxi's GpsLocation
	public TaxiStatus updateTaxiGpsLocation(GpsLocation loc, String cabName) {  //return GpsLocation??
		
		/////////////////////
		System.out.println("updateGpsLocation @ DataStore, " + cabName);
		/////////////////////	
		
		this.getDrivers();
		
		Taxi driver = null;
		
		driver = drivers.get(cabName);
		
		if (driver != null && loc != null){
			
			//////////////////////////////////////////////////
			System.out.println("update existing driver, " + cabName);
			//////////////////////////////////////////////////
			
			driver.setGpsLocation(loc);
		}
		
		/*taxi wasn't found, add new available driver*/
		else{
			
			////////////////////////////////////////////////
			System.out.println("create new driver: " + cabName);
			///////////////////////////////////////////////
			
			driver = new Taxi();
			driver.setDriver(cabName);
			
			if (loc != null){
				driver.setGpsLocation(loc);
			}
			
			//update the session's list of drivers
			drivers.put(cabName, driver);
		}
		
		
	
		//update the session
		this.updateDrivers(drivers);
		
		return driver.getStatus();
	}
	
	
	//return true if order represented by it's id is available
	public boolean takeOrder(Integer orderId, String cabName){
		
		getOrders();
		Order ord = orders.get(orderId);
		getDrivers();
		Taxi taxi = drivers.get(cabName);
		
		//if order is still available
		if (ord != null){
			
			////////////////////////////////////////////////
			System.out.println("driver assigned another job, " + cabName);
			///////////////////////////////////////////////
			
			
			//remove references to the order from other taxis
			ord.removeDrivers(taxi);
			
			//remove the order from the hash map and update session
			orders.remove(orderId);
			updateOrders(orders);
			
			//change to regular update frequency
			taxi.updateFreqRate(1);
			
			//update current taxi as busy ??
			
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
			
			////////////////////////////////////////////////
			System.out.println("driver failed to take a job, " + cabName);
			///////////////////////////////////////////////
			
			
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
	public void rejectOrder(Integer orderId, String cabName){
		
		////////////////////////////////////////////////
		System.out.println("driver rejected a job offer, " + cabName);
		///////////////////////////////////////////////
		
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
		
		//if order is still pending
		if (ord != null){
			ord.addToBlackList(taxi);
			ord.removeDriver(taxi);
			orders.put(orderId, ord);
			updateOrders(orders);
			
			//if order is still pending but with no potential drivers
			if (ord.numOfPotentionDrivers() == 0){
				this.newPotentialDrivers(ord);
			}
		}		
		
	}
	
	//create order, update the orders data structure and return it's order-id
	public Order createOrder(GpsAddress from,GpsAddress to, String user){
		
		////////////////////////////////////////////////
		System.out.println("new order is created, " + user);
		///////////////////////////////////////////////
		
		System.out.println("x0");
		Order order = new Order();
		order.setUser(user);
		order.setId(NextOrderID);
		
		//increment the next order id, cyclic
		NextOrderID++;
		if (NextOrderID < 0) NextOrderID = 0;
		
		order.setFrom(from);
		order.setTo(to);
			
		System.out.println("x1");
		//update the session's orders map
		getOrders();
		System.out.println("x2");
		orders.put(order.getId(), order);
		System.out.println("x3");
		updateOrders(orders);
		System.out.println("x4");
		return order;
	}
	
	
	//find potential drivers for an order
	public void newPotentialDrivers(Order ord){
		
		System.out.println("21");
		//create list of all potential drivers
		List<Taxi> cabs = this.allCabs();
		cabs.removeAll(ord.getBlackList());
		System.out.println("22");
		//find closest cabs among the list
		List<Taxi> foundCabs = this.findClosestCabs(ord.getFrom().getLocation(), cabs);
		System.out.println("23");
		//inform those cabs of a new job
		for (Taxi cab : foundCabs){
			//update the taxi
			System.out.println("24");
			cab.addOrder(ord);
			System.out.println("25");
			cab.updateFreqRate(2);
			System.out.println("26");
			//update the hash map
			drivers.put(cab.getDriver(), cab);
			System.out.println("27");
			
		}
		System.out.println("28");
		
		//update the order's list of potential drivers
		ord.addDrivers(foundCabs);
		System.out.println("29");
		orders.put(ord.getId(), ord);
		System.out.println("210");
		
		//update the session
		updateDrivers(drivers);
		System.out.println("211");
		updateOrders(orders);
		System.out.println("212");
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
		System.out.println("22 1");
		//used to hold the current min distances
		List<cabDist> cabsDist = new ArrayList<cabDist>(this.nearbyCabs);
		System.out.println("22 2");
		cabDist maxDist = null;
		
		for (Taxi cab : cabs) {
			System.out.println("22 3");
			System.out.println(cab);
			System.out.println(cab.getGpsLocation());
			System.out.println(loc);
			
			double temp = cab.getGpsLocation().distanceTo(loc);
			System.out.println("22 4");
			//insert the first cab
			if( maxDist == null){
				System.out.println("22 5");
				cabDist c = new cabDist(cab, temp);
				cabsDist.add(c);
				maxDist = c;
				System.out.println("22 6");
			}
			
			//check if num of taxis < |nearByTaxi|
			else if (cabsDist.size() < this.nearbyCabs){
				System.out.println("22 7");
				cabDist c = new cabDist(cab, temp);
				cabsDist.add(c);
				System.out.println("22 8");
				if (maxDist.getDist() < c.getDist()){
					maxDist = c;
				}
			}
			
			
			//check if current cab is closer then the max distanced cab
			else if  (temp < maxDist.getDist()){
				System.out.println("22 9");
				cabDist c = new cabDist(cab, temp);
				cabsDist.add(c);
				
				//remove the maxDist cab
				cabsDist.remove(maxDist);
				maxDist = c;
				System.out.println("22 10");
				//make sure maxDist is updated
				for (int i = 0; i < cabsDist.size(); i++){
					System.out.println("22 11");
					if (cabsDist.get(i).getDist() > maxDist.getDist()){
						maxDist = cabsDist.get(i);
					}
				}
			}
		}
		System.out.println("22 12");
		//convert to list of cabs
		cabs = new LinkedList<Taxi>();
		for (cabDist cab: cabsDist){
			System.out.println("22 13");
			if (cab != null){
				cabs.add(cab.getCab());
			}
		}
		System.out.println("22 15");
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
	
	
	//update the driver's statusLine
	public boolean updateStatusLine(String newStatusLine, String cabName){
		//find the cab
		this.getDrivers();
		Taxi cab = drivers.get(cabName);
		
		if (cab != null){
		
			drivers.remove(cabName);
			
			//update the cab's status line
			cab.setStatusLine(newStatusLine);
			
			drivers.put(cabName, cab);
			
			//update the drivers
			this.updateDrivers(drivers);
			
			return true;
		}
		else{
			return false;
		}
	}
	
	
	//private boolean driverWithClient(String cabName){  
}
