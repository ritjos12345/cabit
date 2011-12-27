package com.cabit.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "com.cabit.server.CabitService", locator = "com.cabit.server.CabitServiceLocator")
public interface CabitRequest extends RequestContext   {

	Request<String> orderCab(String cabName, LocationProxy from, LocationProxy to ) ;
	Request<String> orderCab( LocationProxy from, LocationProxy to ) ;
	Request<Void> confirmOrder() ;
	Request<Void> cancelOrder() ;
	Request<Void> updateMyLocation(LocationProxy location) ;
	Request<Void> deleteMyLocation() ;
	Request<List<LocationProxy>> getAllCabs() ;
	

}
