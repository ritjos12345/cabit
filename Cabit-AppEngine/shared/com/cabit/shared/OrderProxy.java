package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.Order", locator = "com.cabit.server.OrderLocator")
public interface OrderProxy extends ValueProxy {

	int getId();

	String getUser();

	void setUser(String user);

	AddressProxy getFrom();

	void setFrom(AddressProxy from);

	AddressProxy getTo();

	void setTo(AddressProxy to);

}
