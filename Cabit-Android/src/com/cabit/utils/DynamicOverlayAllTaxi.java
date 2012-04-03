package com.cabit.utils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.TaxiProxy;
import com.google.android.maps.MapView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;

public class DynamicOverlayAllTaxi extends DynamicOverlay<String>{

	private static final String TAG = "DynamicOverlayAllTaxi";
	protected MapView mapView;
	protected Timer timer ;
	public DynamicOverlayAllTaxi(Drawable defaultMarker,MapView map ) {
		super(defaultMarker, map.getContext());
		
		mapView = map; 
		timer = new Timer();
	}
	
	public void Start(int FPS ){
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				UpdateTaxi();
			}
		}, 0, 1000 * FPS);
	}
	
	public void Stop(){
		timer.cancel();
	}
	
	protected void UpdateTaxi() {
		// Use an AsyncTask to avoid blocking the UI thread
		new AsyncTask<Void, Void, List<TaxiProxy>>() {
			private List<TaxiProxy> result;

			@Override
			protected List<TaxiProxy> doInBackground(Void... params) {
				MyRequestFactory requestFactory = Util.getRequestFactory( mContext, MyRequestFactory.class);
				final CabitRequest request = requestFactory.cabitRequest();
				Log.i(TAG, "Sending request to server: GetAllTaxi"); 
				request.GetAllTaxi().fire(new Receiver<List<TaxiProxy>>() {
					@Override
					public void onSuccess(List<TaxiProxy> arg0) {
						result = arg0;
					}

					@Override
					public void onFailure(ServerFailure error) {
						result = null;
					}
				});
				return result;
			}

			@Override
			protected void onPostExecute(List<TaxiProxy> result) {
				System.out.println("Answer from the server..");
				if (result != null) {
					for (TaxiProxy taxiProxy : result) {
						System.out.println("Update Taxi: " + taxiProxy.getDriver());
						UpdateItem(taxiProxy.getDriver(),
								(int) taxiProxy.getGpsLocation().getLatitude(),
								(int) taxiProxy.getGpsLocation().getLongitude(),
								taxiProxy.getDriver(), "coool");
					}
					RefreshItems();
					mapView.invalidate();
					
				} else {
				
					System.out.println("No answer from the server..");
				}
			}
		}.execute();	
	}

}
