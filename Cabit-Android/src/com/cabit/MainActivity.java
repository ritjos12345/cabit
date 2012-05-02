package com.cabit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.cabit.R;
import com.cabit.R.drawable;
import com.cabit.R.id;
import com.cabit.R.layout;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.TaxiProxy;
import com.cabit.utils.DynamicOverlay;
import com.cabit.utils.DynamicOverlayAllTaxi;
import com.cabit.utils.DynamicOverlayMyLocation;
import com.cabit.utils.DynamicOverlayRoad;
import com.cabit.utils.Road;
import com.cabit.utils.RoadProvider;
import com.cabit.utils.Util;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
 
public class MainActivity extends MapActivity {

	private static final String TAG = "MainActivity";
	private Context mContext = this;
	
	protected MapView mapView; 
	protected DynamicOverlayAllTaxi taxiOverlay ;
	protected DynamicOverlayMyLocation myLocationOverlay;
	protected Button buttonOrderEntry;


	@Override
	public void onPause(){
		super.onPause();
		if(taxiOverlay != null){
			taxiOverlay.Stop();
		}
		if(myLocationOverlay != null){
			myLocationOverlay.stop();		
		}		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(taxiOverlay != null){
			taxiOverlay.Start(15);
		}
		if(myLocationOverlay != null){
			myLocationOverlay.start();		
		}
	}
	
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);

        // create map
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		mapView.setTraffic(true);

		// add all taxi to map
		taxiOverlay = new DynamicOverlayAllTaxi(this.getResources().getDrawable(R.drawable.taxi), mapView);
		mapView.getOverlays().add(taxiOverlay);

		// add my location to map
		myLocationOverlay =new DynamicOverlayMyLocation(this.getResources().getDrawable(R.drawable.dot), mapView, false);
		mapView.getOverlays().add(myLocationOverlay);
    
		
		buttonOrderEntry = (Button) findViewById(R.id.button_commit);
		buttonOrderEntry.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
				Intent intent = new Intent(mContext, OrderCabMenu.class);
				startActivityForResult(intent, 1);
			}
		});		
    }
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	

}

