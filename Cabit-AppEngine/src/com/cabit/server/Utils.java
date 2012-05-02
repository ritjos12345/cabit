package com.cabit.server;

import java.util.List;

import javax.servlet.ServletContext;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.geolocation.client.Geolocation;
import com.google.web.bindery.requestfactory.server.RequestFactoryServlet;

public class Utils {

	/*public static List<Location>  getAddressLocation(String Address){
		return null;
		
		Geocoder g = new Geocoder(mapView.getContext());
	    String locationName = "herzel"; 
	    try {
			List<Address> arr =g.getFromLocationName(locationName , 10);
			for (int i = 0; i < arr.size(); i++) {
				//itemizedoverlay.UpdateItem(""+i,new OverlayItem(new GeoPoint((int)(arr.get(i).getLatitude() * 1e6),(int)(arr.get(i).getLongitude() * 1e6)), String.valueOf(i), "I'm in Mexico City!"));
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	public static ServletContext getServletContext (){
		return  RequestFactoryServlet.getThreadLocalRequest().getSession().getServletContext();
	}
	
	public static String getUserEmail() {
	 UserService userService = UserServiceFactory.getUserService();
     User user = userService.getCurrentUser();    
     user.getEmail();
     return user.getEmail();
   }

	// TODO recive the target name..
	public static void sendC2DMUpdate(String target, String message) {
        SendMessage.sendMessage(getServletContext(), target, message);
		
	}
}
