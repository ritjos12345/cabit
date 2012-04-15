
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

import com.cabit.R;
import com.cabit.R.drawable;
import com.cabit.R.id;
import com.cabit.R.layout;
import com.cabit.client.MyRequestFactory;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.cabit.shared.TaxiProxy;
import com.cabit.utils.DynamicOverlay;
import com.cabit.utils.DynamicOverlayMyLocation;
import com.cabit.utils.DynamicOverlayOneTaxi;
import com.cabit.utils.Util;
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
	
	private static final String TAG = "TrackTaxiActivity";
	private Context mContext = this;
	
	
	protected MapView mapView; 
	protected DynamicOverlayOneTaxi oneTaxiOverlay ;
	protected DynamicOverlayMyLocation myLocationOverlay ;
	protected String myTaxiName;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracktaxi);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        Bundle extras = getIntent().getExtras();
		if(extras !=null) {
			myTaxiName = extras.getString("taxi");
		}
		
        oneTaxiOverlay = new DynamicOverlayOneTaxi(this.getResources().getDrawable(R.drawable.taxi), mapView , myTaxiName);
		mapView.getOverlays().add(oneTaxiOverlay);
		oneTaxiOverlay.Start(10);
		
		myLocationOverlay =new DynamicOverlayMyLocation(this.getResources().getDrawable(R.drawable.dot), mapView, false);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.start();
		
    }
    
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
	    
}
