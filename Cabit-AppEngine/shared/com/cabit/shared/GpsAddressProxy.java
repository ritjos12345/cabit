package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.GpsAddress", locator = "com.cabit.server.GpsAddressLocator")
public interface GpsAddressProxy extends ValueProxy {

	String getTitle();

	void setTitle(String title);

	GpsLocationProxy getLocation();

	void setLocation(GpsLocationProxy location);

}
