package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.Taxi", locator = "com.cabit.server.TaxiLocator")
public interface TaxiProxy extends ValueProxy {

	String getDriver();
	
	String getStatusLine();
	
	void setDriver(String driver);

	GpsLocationProxy getGpsLocation();

	void setGpsLocation(GpsLocationProxy gpsLocation);

}
