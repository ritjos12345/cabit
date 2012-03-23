package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.GpsLocation", locator = "com.cabit.server.GpsLocationLocator")
public interface GpsLocationProxy extends ValueProxy {

	long getLatitude();

	void setLatitude(long latitude);

	long getLongitude();

	void setLongitude(long longitude);

}
