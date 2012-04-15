package com.cabit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;

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
import com.cabit.utils.Util;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class MainActivity extends MapActivity {

	private static final String TAG = "MainActivity";
	private Context mContext = this;
	
	protected MapView mapView; 
	protected DynamicOverlayAllTaxi taxiOverlay ;
	protected DynamicOverlayMyLocation myLocationOverlay;
	protected Button buttonOrderEntry;

	public void onPause(){
		super.onPause();
		taxiOverlay.Stop();
		myLocationOverlay.stop();
		
	}
	
	public void onCreate(Bundle savedInstanceState) 
    {
		super.onCreate(savedInstanceState);
		
        setContentView(R.layout.main);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		
		taxiOverlay = new DynamicOverlayAllTaxi(this.getResources().getDrawable(R.drawable.taxi), mapView);
		mapView.getOverlays().add(taxiOverlay);
		taxiOverlay.Start(15);

		myLocationOverlay =new DynamicOverlayMyLocation(this.getResources().getDrawable(R.drawable.dot), mapView, false);
		mapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.start();
		
		buttonOrderEntry = (Button) findViewById(R.id.button_commit);
		buttonOrderEntry.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// Intent intent = new Intent(CabitActivity.this,
				// orderMenu.class);
				
				// TODO udi fix this

				finish();
				Intent intent = new Intent(mContext, OrderCabMenu.class);
				startActivityForResult(intent, 1);
				
	/*			Intent intent = new Intent(mContext,
				TrackTaxiActivity.class);
				
				Bundle b = new Bundle();
				b.putString("taxi", "udi");
				intent.putExtras(b);

				startActivityForResult(intent, 1);
*/						
			}
		});

	
		
    }
	
	
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
/*	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {

				final int latitude = data.getExtras().getInt("latitude");
				final int longitude = data.getExtras().getInt("longitude");
				final String address = data.getExtras().getString("address");

				LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
				final Location loc = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				final int myLatitude = 33;
				final int myLongitude = 33;
				// TODO fix this
				
				 * if(loc != null){ myLatitude = (int) (loc.getLatitude()*1e6);
				 * myLongitude= (int) (loc.getLongitude()*1e6); }
				 
				new AlertDialog.Builder(this)
						.setMessage(
								"Do you want to order a cab to \n" + address
										+ " ?")
						.setTitle("Order a cab")
						.setCancelable(false)
						.setPositiveButton("yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										new AsyncTask<Void, Void, String>() {
											private String result;

											@Override
											protected String doInBackground(
													Void... params) {
												MyRequestFactory requestFactory = Util
														.getRequestFactory(
																mContext,
																MyRequestFactory.class);
												final CabitRequest request = requestFactory
														.cabitRequest();
												Log.i(TAG,
														"Sending request to server");

												
												 * LocationProxy from =
												 * request.create
												 * (LocationProxy.class);
												 * LocationProxy to =
												 * request.create
												 * (LocationProxy.class);
												 * 
												 * to.setLatitude(latitude);
												 * to.setLongitude(longitude);
												 * to.setTitle(address);
												 * 
												 * from.setLatitude(myLatitude);
												 * from
												 * .setLongitude(myLongitude);
												 * 
												 * request.orderCab(from,
												 * to).fire(new
												 * Receiver<String>(){
												 * 
												 * @Override public void
												 * onSuccess(String arg0) {
												 * System.out.println("1");
												 * result = arg0; }
												 * 
												 * @Override public void
												 * onFailure(ServerFailure
												 * error) {
												 * System.out.println("2:"
												 * +error.getClass().getName()
												 * +" , "
												 * +error.getExceptionType(
												 * )+", " + error.getMessage() +
												 * " , "
												 * +error.getStackTraceString
												 * ()); result = null; }
												 * 
												 * });
												 
												return result;
											}

											@Override
											protected void onPostExecute(
													String result) {
												System.out.println("3");
												if (result != null) {
													new AlertDialog.Builder(
															mContext)
															.setTitle(
																	"Order was created")
															.setMessage(
																	"Waiting for :"
																			+ result)
															.show();
												} else {
													new AlertDialog.Builder(
															mContext)
															.setTitle(
																	"Order Error")
															.setMessage(
																	"find cab to order.")
															.show();
												}

											}

										}.execute();
									}
								})

						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

									}
								}).show();

			}
			
			
		}
	}
*/


}
