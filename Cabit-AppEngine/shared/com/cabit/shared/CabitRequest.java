package com.cabit.shared;

import java.util.List;

import com.cabit.server.GpsAddress;
import com.cabit.server.GpsLocation;
import com.cabit.server.Taxi;
import com.cabit.server.TaxiStatus;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.ServiceName;

@ServiceName(value = "com.cabit.server.CabitService", locator = "com.cabit.server.CabitServiceLocator")
public interface CabitRequest extends RequestContext {

	Request<TaxiStatusProxy> UpdateLocation(GpsLocationProxy loc);

	Request<Boolean> UpdateOrder(int orderId,boolean except);
	 
	Request<Void> IAmNear(GpsLocationProxy loc);  
	
	Request<Integer> CreateOrder(GpsAddressProxy from, GpsAddressProxy to);
	
	Request<String> GetOrderStatus(int orderId); 
	
	Request<TaxiProxy> GetTaxi(String driver);
	
	Request<List<TaxiProxy>> GetAllTaxi();
	
	/*
	Request<TaxiStatusProxy> createTaxiStatus();

	Request<TaxiStatusProxy> readTaxiStatus(Long id);

	Request<TaxiStatusProxy> updateTaxiStatus(TaxiStatusProxy taxistatus);

	Request<Void> deleteTaxiStatus(TaxiStatusProxy taxistatus);

	Request<List<TaxiStatusProxy>> queryTaxiStatuss();

	Request<TaxiProxy> createTaxi();

	Request<TaxiProxy> readTaxi(Long id);

	Request<TaxiProxy> updateTaxi(TaxiProxy taxi);

	Request<Void> deleteTaxi(TaxiProxy taxi);

	Request<List<TaxiProxy>> queryTaxis();

	Request<OrderProxy> createOrder();

	Request<OrderProxy> readOrder(Long id);

	Request<OrderProxy> updateOrder(OrderProxy order);

	Request<Void> deleteOrder(OrderProxy order);

	Request<List<OrderProxy>> queryOrders();

	Request<AddressProxy> createAddress();

	Request<AddressProxy> readAddress(Long id);

	Request<AddressProxy> updateAddress(AddressProxy address);

	Request<Void> deleteAddress(AddressProxy address);

	Request<List<AddressProxy>> queryAddresss();

	Request<GpsLocationProxy> createGpsLocation();

	Request<GpsLocationProxy> readGpsLocation(Long id);

	Request<GpsLocationProxy> updateGpsLocation(GpsLocationProxy gpslocation);

	Request<Void> deleteGpsLocation(GpsLocationProxy gpslocation);

	Request<List<GpsLocationProxy>> queryGpsLocations();
*/
}
