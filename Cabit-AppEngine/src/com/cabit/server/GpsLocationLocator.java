package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class GpsLocationLocator extends Locator<GpsLocation, Void> {

	@Override
	public GpsLocation create(Class<? extends GpsLocation> clazz) {
		return new GpsLocation();
	}

	@Override
	public GpsLocation find(Class<? extends GpsLocation> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<GpsLocation> getDomainType() {
		return GpsLocation.class;
	}

	@Override
	public Void getId(GpsLocation domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(GpsLocation domainObject) {
		return null;
	}

}
