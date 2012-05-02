
package com.cabit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.cabit.utils.DynamicOverlayRoad;
import com.cabit.utils.Road;
import com.cabit.utils.RoadProvider;
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
	
	protected double fromLat ;//= 49.85;
	protected double fromLon ;//= 24.016667;
	protected double toLat ;//= 50.45;
	protected double toLon;// = 30.523333;
	
	LinearLayout linearLayout;    
    private Road mRoad;
    
	/** Called when the activity is first created. */
    
    @Override
	public void onPause(){
		super.onPause();
		if(oneTaxiOverlay != null){
			oneTaxiOverlay.Stop();
		}
		if(myLocationOverlay != null){
			myLocationOverlay.stop();		
		}		
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(oneTaxiOverlay != null){
			oneTaxiOverlay.Start(15);
		}
		if(myLocationOverlay != null){
			myLocationOverlay.start();		
		}
	}
	
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
			fromLat = extras.getDouble("fromLat");
			fromLon = extras.getDouble("fromLon");
			toLat = extras.getDouble("toLat");
			toLon = extras.getDouble("toLon");
		}
		
        oneTaxiOverlay = new DynamicOverlayOneTaxi(this.getResources().getDrawable(R.drawable.taxi), mapView , myTaxiName);
		mapView.getOverlays().add(oneTaxiOverlay);
		
		myLocationOverlay =new DynamicOverlayMyLocation(this.getResources().getDrawable(R.drawable.dot), mapView, false);
		mapView.getOverlays().add(myLocationOverlay);
		
		new Thread() {
            @Override
            public void run() {
                    String url = RoadProvider
                                    .getUrl(fromLat, fromLon, toLat, toLon);
                    InputStream is = getConnection(url);
                    mRoad = RoadProvider.getRoute(is);
                    
                     
                    mHandler.sendEmptyMessage(0);
            }
		}.start();
		
		
    }
    
    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }
    
    // this func estimates the price from start point to end
    public double wayPrice(int hours, int minutes){
    	
    	// find the current time
    	Calendar c = Calendar.getInstance();
    	int hour = c.get(Calendar.HOUR_OF_DAY);
    	int minute = c.get(Calendar.MINUTE);
    	
    	int rate;
    	// between 5:30-21:00 it's rate 1, else it's rate 2
        if ((hour<=21)&&((hour>=6)||((hour==5)&&(minute>=30)))){
        	rate = 1;
        }
        else{
        	rate = 2;
        }
        
    	
    	// starting mone price + price for ordering a cab 
    	double sumPrice = 8.5 + 3;
    	long sumSeconds = 60*(minutes + hours*60);
    	// the first 151 seconds are "on the house"
    	sumSeconds-=151;
    	
    	// calculating the sum of pulses to the dest
    	if (1==rate){
    		sumPrice += (sumSeconds/11)*0.3;
    	}
    	else{
    		sumPrice += (sumSeconds/9)*0.3;
    	}
    	
    	return sumPrice;	
    }
    
	Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
                /*TextView textView = (TextView) findViewById(R.id.description);
                textView.setText(mRoad.mName + " " + mRoad.mDescription);*/
        	
        	
        		
        	
        		
        		if(mRoad!=null){
        			if(mRoad.mDescription!=null){
		        		Log.i("mRoad.mDescription", "mRoad.mDescription........" + mRoad.mDescription);
		        		String ms = mRoad.mDescription;
		                try{
		                	int ih = 0;
		                	int im = 0;
		                	String tmpMs = ms.substring(ms.indexOf("(about")+7);
		            		if(tmpMs.contains("hours")){
		            	     String h = tmpMs.substring(0,tmpMs.indexOf(" hours") );
		            	     tmpMs = tmpMs.substring(tmpMs.indexOf("hours")+6);
		            	     ih = Integer.valueOf(h);
		            		}
		            		if(tmpMs.contains("mins")){
		             		String m = tmpMs.substring(0,tmpMs.indexOf(" mins") );
		             		im = Integer.valueOf(m);
		            		}
		            		
			        		ms += " Estimated price: " + wayPrice(ih,im) +" Shekel !";
		                }catch (Exception e) {
		                		Log.i("mRoad.mDescription", "error "+ e.getMessage());	
						}
		                Toast.makeText(TrackTaxiActivity.this,   ms ,Toast.LENGTH_LONG).show();
		                Log.i("mRoad.mDescription", "show" );
        			}
	        		DynamicOverlayRoad roadOverlay = new DynamicOverlayRoad(mRoad, mapView);
	        		
	                mapView.getOverlays().add(roadOverlay);
	                mapView.invalidate();
                
        		}
                
                
                
        };
	};
	
	private InputStream getConnection(String url) {
	        InputStream is = null;
	        try {
	                URLConnection conn = new URL(url).openConnection();
	                is = conn.getInputStream();
	        } catch (MalformedURLException e) {
	                e.printStackTrace();
	        } catch (IOException e) {
	                e.printStackTrace();
	        }
	        return is;
	}
}
