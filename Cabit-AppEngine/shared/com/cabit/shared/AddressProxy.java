package com.cabit.shared;

import com.google.web.bindery.requestfactory.shared.ProxyForName;
import com.google.web.bindery.requestfactory.shared.ValueProxy;

@ProxyForName(value = "com.cabit.server.Address", locator = "com.cabit.server.AddressLocator")
public interface AddressProxy extends ValueProxy {

	String getTitle();

	void setTitle(String title);

}
