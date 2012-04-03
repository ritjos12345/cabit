package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class GpsAddressLocator extends Locator<GpsAddress, Void> {

	@Override
	public GpsAddress create(Class<? extends GpsAddress> clazz) {
		return new GpsAddress();
	}

	@Override
	public GpsAddress find(Class<? extends GpsAddress> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<GpsAddress> getDomainType() {
		return GpsAddress.class;
	}

	@Override
	public Void getId(GpsAddress domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(GpsAddress domainObject) {
		return null;
	}

}
