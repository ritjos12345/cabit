package com.cabit;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class orderMenu extends Activity{
	EditText editTextSearch;
	Button buttonCommit,buttonCancel; 
	Spinner spinnerDrivers;
	Context  mContext;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order);
        
        buttonCommit = (Button)findViewById(R.id.button_commit);
        buttonCancel = (Button)findViewById(R.id.button_cancel);
        editTextSearch = (EditText) findViewById(R.id.edit_text_search);
        spinnerDrivers = (Spinner) findViewById(R.id.spinner_opt);
        
        // at the beginning, don't show the spinner
        spinnerDrivers.setVisibility(View.GONE);
        
        buttonCommit.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						
						// TODO Auto-generated method stub
						
						// if no search val wa added
						if (true==editTextSearch.getText().toString().equals("")){
							Toast.makeText(orderMenu.this,"Enter a dest to search!",Toast.LENGTH_LONG).show();
							return;
						}
						
						// connect to the server using editTextSearch.getText().toString() as the search val
						
						Geocoder g = new Geocoder(getBaseContext());
					    String locationName = editTextSearch.getText().toString(); 
					    try {
					    	int maxAddress = 5;
							final List<Address> arr =g.getFromLocationName(locationName , maxAddress);
							final String[]  ans = new String [arr.size()];
							
							for (int i = 0; i < arr.size(); i++) {
								System.out.println("found:"+i);
								//itemizedoverlay.UpdateItem(""+i,new OverlayItem(new GeoPoint((int)(arr.get(i).getLatitude() * 1e6),(int)(arr.get(i).getLongitude() * 1e6)), String.valueOf(i), "I'm in Mexico City!"));
								String str="";
								int j = 0;
								while(arr.get(i).getAddressLine(j) != null){
									str+= arr.get(i).getAddressLine(j) + " , ";
									j++;
								}
								
								 
								ans[i] = str;
							}
							
							
						
						
							// showing the ans from the server
							//final String [] ans = new String[] {"Red","Blue","White","Yellow","Black", "Green","Purple","Orange","Grey"};
							ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, ans);
							System.out.println("a1");
							adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							System.out.println("a2");
							spinnerDrivers.setAdapter(adapter);
							System.out.println("a3");
							
							
							
							spinnerDrivers.setOnItemSelectedListener(new OnItemSelectedListener() {
								 public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {               
								        //String selectedItem = ans.get(position);
								        System.out.println("SELECTED: " );
								        
								        Intent resultData = new Intent();
								        resultData.putExtra("latitude", arr.get(position).getLatitude());
								        resultData.putExtra("longitude", arr.get(position).getLocality());
								        
								        orderMenu.this.setResult(Activity.RESULT_OK, resultData);
								        orderMenu.this.finish();
								        
								 }
								 public void onNothingSelected(AdapterView<?> arg0) {
									 System.out.println("NOT SELECTED ");
								 }
							});
							System.out.println("a4");
							// fake click in order to invoke the spinner menu
							spinnerDrivers.performClick();
							
							Intent resultData = new Intent();
							resultData.putExtra("address", ans[0]);
					        resultData.putExtra("latitude", (int)(arr.get(0).getLatitude()*1e6));
					        resultData.putExtra("longitude",(int)(arr.get(0).getLongitude()*1e6));
					        
					        orderMenu.this.setResult(Activity.RESULT_OK, resultData);
					        orderMenu.this.finish();
					        
					    } catch (IOException e) {
							// TODO Auto-generated catch block
					    	System.out.println("a5 "+e.getMessage());
							e.printStackTrace();
						}
					    
						
				        
				        
						System.out.println("a6");
					}
				});
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				orderMenu.this.setResult(Activity.RESULT_CANCELED);
				finish();
			}
        });
    }

}
