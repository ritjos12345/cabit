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
	
	public DataStore() {
		map = new HashMap<String, Location>();
		Location l = new Location();
		l.setDate(null);
		l.setLatitude(19240000);
		l.setLongitude(-99120000);
		l.setUserEmail("www.udi@gmail.com");
		update(l);
	}
	public Location update(Location location) {
		
		map.put(location.getUserEmail(), location);
		return location;
	}
	
	public Location find(String userEmail) {
		
		return map.get(userEmail);
	}

	public void delete(String userEmail) {
		map.remove(userEmail);
	}

	public List<Location> findAll() {
		List<Location> list = new LinkedList<Location>();
		for (Location location : map.values()) {
			list.add(location); 
		} 
		return list;
	}
	
	
	
	// TODO move to other class
	public static ServletContext getServletContext (){
		return  RequestFactoryServlet.getThreadLocalRequest().getSession().getServletContext();
	}
	
	public static String getUserEmail() {
	 UserService userService = UserServiceFactory.getUserService();
     User user = userService.getCurrentUser();
     return user.getEmail();
   }

	public static void sendC2DMUpdate(String string) {
		// TODO Auto-generated method stub
		
	}

}
