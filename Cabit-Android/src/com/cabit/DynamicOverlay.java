package com.cabit;

import java.util.HashMap;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;

public class DynamicOverlay<K> extends ItemizedOverlay<OverlayItem>{
	
	HashMap<K, OverlayItem> hashMap;
	Context context;
	
	public DynamicOverlay(Drawable defaultMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		this.hashMap = new HashMap<K, OverlayItem>();
		this.context = context;
		populate();
	}

	
	public void UpdateItem(K key, Location location ,String title,String snippet){
		UpdateItem(key, (int)(location.getLatitude() * 1e6),(int)(location.getLongitude() * 1e6),title,snippet);
	}
	
	public void UpdateItem(K key, int latitudeE6,int longitudeE6 ,String title,String snippet){
		UpdateItem(key, new GeoPoint (latitudeE6, longitudeE6),title,snippet);
	}
	
	public void UpdateItem(K key, GeoPoint point,String title,String snippet){
		UpdateItem(key, new OverlayItem(point, title, snippet));
	}
	
	public void UpdateItem(K key, OverlayItem item){
		hashMap.put(key, item);
		
	}

	public void RefreshItems() {
		populate();
	}
	
	@Override
	protected OverlayItem createItem(int i) {
	  return (OverlayItem) hashMap.values().toArray()[i];
	}

	@Override
	public int size() {
	  return hashMap.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = createItem(index);
	  return  OnItemSelected(item ,context );
	}
	
	public boolean OnItemSelected(OverlayItem item, Context context ){
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		dialog.show();
		return true;
	}



	

	
	
}

