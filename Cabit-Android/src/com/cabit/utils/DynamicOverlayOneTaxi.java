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

public class DynamicOverlayOneTaxi extends DynamicOverlay<String>{

	private static final String TAG = "DynamicOverlayAllTaxi";
	protected MapView mapView;
	protected Timer timer ;
	protected String myTaxiName;
	
	public DynamicOverlayOneTaxi(Drawable defaultMarker,MapView map, String taxiName ) {
		super(defaultMarker, map.getContext());
		
		mapView = map; 
		timer = new Timer();
		myTaxiName = taxiName;
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
	
    private void UpdateTaxi() {
		// Use an AsyncTask to avoid blocking the UI thread
		new AsyncTask<Void, Void, TaxiProxy>() {
			private TaxiProxy result;

			@Override
			protected TaxiProxy doInBackground(Void... params) {
				MyRequestFactory requestFactory = Util.getRequestFactory( mContext, MyRequestFactory.class);
				final CabitRequest request = requestFactory.cabitRequest();
				Log.i(TAG, "Sending getTaxi request to server"); 
				request.GetTaxi(myTaxiName).fire(new Receiver<TaxiProxy>() {
					@Override
					public void onSuccess(TaxiProxy arg0) {
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
			protected void onPostExecute(TaxiProxy result) {
				if (result != null) {
						UpdateItem(result.getDriver(),
								(int) result.getGpsLocation().getLatitude(),
								(int) result.getGpsLocation().getLongitude(),
								result.getDriver(), "coool");
					RefreshItems();
					mapView.invalidate();
				} else {
					System.out.println("no RPC answer from the server..");
				}
			}
		}.execute();	
	}

}
