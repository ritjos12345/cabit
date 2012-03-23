package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class TaxiStatusLocator extends Locator<TaxiStatus, Void> {

	@Override
	public TaxiStatus create(Class<? extends TaxiStatus> clazz) {
		return new TaxiStatus();
	}

	@Override
	public TaxiStatus find(Class<? extends TaxiStatus> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<TaxiStatus> getDomainType() {
		return TaxiStatus.class;
	}

	@Override
	public Void getId(TaxiStatus domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(TaxiStatus domainObject) {
		return null;
	}

}
