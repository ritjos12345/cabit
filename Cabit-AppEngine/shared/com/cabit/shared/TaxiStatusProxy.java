package com.cabit.shared;

import java.util.List;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.TaxiStatus", locator = "com.cabit.server.TaxiStatusLocator")
public interface TaxiStatusProxy extends ValueProxy {

	List<OrderProxy> getOrders();

	void setOrders(List<OrderProxy> orders);

	int getConnectionStatus();

	void setConnectionStatus(int connectionStatus);

}
