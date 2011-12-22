package com.cabit.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "com.cabit.server.CabitService", locator = "com.cabit.server.CabitServiceLocator")
public interface CabitRequest extends RequestContext   {

	Request<LocationProxy> createLocation();

	Request<LocationProxy> readLocation(String userEmail); 

	Request<LocationProxy> updateLocation(LocationProxy location);
	
	Request<Void> deleteLocation(String userEmail);

	Request<List<LocationProxy>> queryLocations();

}
