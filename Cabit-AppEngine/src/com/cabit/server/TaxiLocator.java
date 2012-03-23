package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class TaxiLocator extends Locator<Taxi, Void> {

	@Override
	public Taxi create(Class<? extends Taxi> clazz) {
		return new Taxi();
	}

	@Override
	public Taxi find(Class<? extends Taxi> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<Taxi> getDomainType() {
		return Taxi.class;
	}

	@Override
	public Void getId(Taxi domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(Taxi domainObject) {
		return null;
	}

}
