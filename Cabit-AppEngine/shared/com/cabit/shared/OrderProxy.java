package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.Order", locator = "com.cabit.server.OrderLocator")
public interface OrderProxy extends ValueProxy {

	Integer getId();

	String getUser();

	void setUser(String user);

	GpsAddressProxy getFrom();

	void setFrom(GpsAddressProxy from);

	GpsAddressProxy getTo();

	void setTo(GpsAddressProxy to);

}
