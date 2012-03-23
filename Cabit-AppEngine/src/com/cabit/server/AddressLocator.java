package com.cabit.server;

import com.google.web.bindery.requestfactory.shared.Locator;


public class AddressLocator extends Locator<Address, Void> {

	@Override
	public Address create(Class<? extends Address> clazz) {
		return new Address();
	}

	@Override
	public Address find(Class<? extends Address> clazz, Void id) {
		return create(clazz);
	}

	@Override
	public Class<Address> getDomainType() {
		return Address.class;
	}

	@Override
	public Void getId(Address domainObject) {
		return null;
	}

	@Override
	public Class<Void> getIdType() {
		return Void.class;
	}

	@Override
	public Object getVersion(Address domainObject) {
		return null;
	}

}
