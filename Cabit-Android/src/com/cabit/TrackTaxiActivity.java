/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.cabit;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Main activity a menu item to invoke the accounts activity.
 */
public class TrackTaxiActivity extends MapActivity {
 	/** Called when the activity is first created. */
	protected MapView mapView; 
	protected DynamicOverlay<String> taxiOverlay ;
	protected Context mContext = this;
	
	protected static final String TAG = "TrackTaxiActivity";
	private String myTaxiName;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracktaxi);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        taxiOverlay = new DynamicOverlay<String>(this.getResources()
				.getDrawable(R.drawable.taxi), mapView.getContext());
		mapView.getOverlays().add(taxiOverlay);
		
		Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			myTaxiName = extras.getString("taxi");
		}
		
		
		Timer timer = new Timer();
		int FPS = 12;
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				UpdateTaxi();

			}
		}, 0, 1000 * FPS);
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
						taxiOverlay.UpdateItem(result.getDriver(),
								(int) result.getGpsLocation().getLatitude(),
								(int) result.getGpsLocation().getLongitude(),
								result.getDriver(), "coool");
					taxiOverlay.RefreshItems();
					mapView.invalidate();
				} else {
					//AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
					// dialog.setTitle("Error");
					// dialog.setMessage("recive null pointer from the server..");
					// dialog.show();
					System.out.println("no RPC answer from the server..");
				}
			}
		}.execute();
	}
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
	    
}
