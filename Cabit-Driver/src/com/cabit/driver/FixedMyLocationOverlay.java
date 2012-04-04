package com.cabit.driver;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;

import com.cabit.client.MyRequestFactory;
import com.cabit.driver.R;
import com.cabit.shared.CabitRequest;
import com.cabit.shared.GpsLocationProxy;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Projection;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

/**
 * Fixes bug with some phone's location overlay class (ie Droid X).
 * Essentially, it attempts to use the default MyLocationOverlay class,
 * but if it fails, we override the drawMyLocation method to provide
 * an icon and accuracy circle to mimic showing user's location.  Right
 * now the icon is a static image.  If you want to have it animate, modify
 * the drawMyLocation method.
 */
public class FixedMyLocationOverlay extends MyLocationOverlay {
        
        private boolean bugged = false;
        
        private Drawable drawable;
        private Paint accuracyPaint;
        private Point center;
        private Point left;
        private int width;
        private int height;
        private Context context;
        
        public FixedMyLocationOverlay(Context context, MapView mapView) {
                super(context, mapView);
                this.context =context;
        }
        
        @Override
        protected void drawMyLocation(Canvas canvas, MapView mapView,
                        Location lastFix, GeoPoint myLocation, long when) {
                if(!bugged) {
                        try {
                                super.drawMyLocation(canvas, mapView, lastFix, myLocation, when);
                        } catch (Exception e) {
                                // we found a buggy phone, draw the location icons ourselves
                                bugged = true;
                        }
                }
                
                if(bugged) {
                        if(drawable == null) {
                                
                                accuracyPaint = new Paint();
                                accuracyPaint.setAntiAlias(true);
                                accuracyPaint.setStrokeWidth(2.0f);
                                
                                drawable = mapView.getContext().getResources().getDrawable(R.drawable.dot);
                                width = drawable.getIntrinsicWidth();
                                height = drawable.getIntrinsicHeight();
                                center = new Point();
                                left = new Point();
                        }
                        
                        Projection projection = mapView.getProjection();
                        double latitude = lastFix.getLatitude();
                        double longitude = lastFix.getLongitude();
                        float accuracy = lastFix.getAccuracy();
                        
                        float[] result = new float[1];
                        
                        Location.distanceBetween(latitude, longitude, latitude, longitude + 1, result);
                        float longitudeLineDistance = result[0];
                        
                        GeoPoint leftGeo = new GeoPoint((int)(latitude*1e6), (int)((longitude-accuracy/longitudeLineDistance)*1e6));
                        projection.toPixels(leftGeo, left);
                        projection.toPixels(myLocation, center);
                        int radius = center.x - left.x;
                        
                        accuracyPaint.setColor(0xff6666ff);
                        accuracyPaint.setStyle(Style.STROKE);
                        canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
                        
                        accuracyPaint.setColor(0x186666ff);
                        accuracyPaint.setStyle(Style.FILL);
                        canvas.drawCircle(center.x, center.y, radius, accuracyPaint);
                        
                        drawable.setBounds(center.x - width/2, center.y - height/2, center.x + width/2, center.y + height/2);
                        drawable.draw(canvas);
                }
        }
        @Override
        public synchronized void onLocationChanged(final Location location) {
        	super.onLocationChanged(location);
        	           
        	
        	System.out.println("1");
        	
        	new AsyncTask<Void, Void, GpsLocationProxy>() {
        		MyRequestFactory requestFactory = Util.getRequestFactory(context, MyRequestFactory.class);
        		CabitRequest request = requestFactory.cabitRequest();
    	        
    	        
        		GpsLocationProxy loc = request.create(GpsLocationProxy.class);
        		GpsLocationProxy res =null;
				@Override
				protected GpsLocationProxy doInBackground(Void... params) {
					System.out.println("2");
					request.UpdateLocation(loc).fire();
					
					System.out.println("6");
					return res;
				}
        		
        	}.execute();
        	System.out.println("10");
        	
        }
}