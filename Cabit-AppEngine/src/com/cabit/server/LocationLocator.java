package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class LocationLocator extends Locator<Location, Void> {

	@Override
	public Location create(Class<? extends Location> clazz) {
		return new Location();
	}

	@Override
	public Location find(Class<? extends Location> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<Location> getDomainType() {
		return Location.class;
	}

	@Override
	public Void getId(Location domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(Location domainObject) {
		return null;
	}

}
