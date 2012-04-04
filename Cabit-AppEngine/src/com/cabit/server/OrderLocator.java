package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class OrderLocator extends Locator<Order, Void> {

	@Override
	public Order create(Class<? extends Order> clazz) {
		return new Order();
	}

	@Override
	public Order find(Class<? extends Order> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<Order> getDomainType() {
		return Order.class;
	}

	@Override
	public Void getId(Order domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(Order domainObject) {
		return null;
	}

}
