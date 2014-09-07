package com.dna.radius.mapsample;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.util.Log;

import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/***
 * this objects holds all the relevant information which the map needs about the loaded businesses.
 * it is used instead of holding this information in separate data structures.
 * @author dror
 *
 */
public class MapBusinessManager {
	
	/**maps from map markers to businesses and from businesses to markers*/
	private static HashMap <Marker, ExternalBusiness> markerToBusiness = new HashMap <Marker, ExternalBusiness>();
	private static HashMap <ExternalBusiness, Marker> BusinessToMarker = new HashMap <ExternalBusiness, Marker>();
   
	private static WeakReference<GoogleMap> gMapRef = null;
	
	public static void init(GoogleMap gMap){
		gMapRef = new WeakReference<GoogleMap>(gMap);
		markerToBusiness = new HashMap <Marker, ExternalBusiness>();
		BusinessToMarker = new HashMap <ExternalBusiness, Marker>();
	}
	
	public static boolean addExternalBusiness(ArrayList<ExternalBusiness> businesses){
		if(gMapRef==null || gMapRef.get()==null){
			Log.d("MapBusinessManager.addExternalBusiness", "the map doesnt exist anymore");
			return false;
		}
		GoogleMap gMap = gMapRef.get();
		
		for(ExternalBusiness b : businesses){
			Marker m =  gMap.addMarker(new MarkerOptions()
				.position(b.getExternBuisnessLocation())
				.title(b.getExtenBusinessName())
				.icon(BitmapDescriptorFactory.fromResource(b.getExternBusinessType().getIconID())));
			BusinessToMarker.put(b,m);
			markerToBusiness.put(m,b);
		}
		return true;
	}
    
    public static void loadExternalBusinesses(){
    	
    	if (gMapRef == null || gMapRef.get() == null) {
			Log.d("MapBusinessManager.addExternalBusiness", "the map doesnt exist anymore");
		}
    	GoogleMap gMap = gMapRef.get();
    	LatLng location = gMap.getCameraPosition().target;
    	DBHandler.getExternalBusinessAtRadius(location, MapWindowFragment.LOAD_RADIUS);
    }
    
    
    /**returns list of all the businesses which were download from parse*/
	public static Set<ExternalBusiness> getAllBusinesses(){
		return BusinessToMarker.keySet();
	}
	
	/**returns list of all the map markers*/
	public static Set<Marker> getAllMarkers(){
		return markerToBusiness.keySet();
	}
	
	/**given a BusinessMarker object, returns the relevand */
	public static Marker getMarker(ExternalBusiness b){
		return BusinessToMarker.get(b);
	}
	
	public static ExternalBusiness getBusiness(Marker m){
		return markerToBusiness.get(m);
	}
	
	
//TODO 
//	public static void addBusiness(ExternalBusiness b,Marker m){
//		BusinessToMarker.put(b,m);
//		markerToBusiness.put(m,b);
//	}
	
	/** this function returns true if a business has a certein propert, 
	 * according to the propery enum list*/
	public enum Property{FAVORITES_PROP,TOP_BUSINESS_PROP,TOP_DEALS_PROP,ALL;}
	
	
	//TODO (alon - to dror) - unused?
	//public static boolean hasProperty(Marker marker, Property p){
	//	ExternalBusiness buisness = markerToBusiness.get(marker);
	//	return hasProperty(buisness, p);		
	//}	
	
	/**returns true if a business has a certain property.*/
	public static boolean hasProperty(ExternalBusiness extern ,Property p){
		
		if (p == Property.FAVORITES_PROP) {
			return ClientData.isInFavourites(extern.getExternBusinessId()); //TODO - improve!
			
		}else if (p == Property.TOP_DEALS_PROP) {
			
			return isInTopDeals(extern.getExternBusinessId());
			
		}else if(p == Property.TOP_BUSINESS_PROP) {
			return extern.getExternBusinessRating() >= 4; //TODO decide creteria
		}
		else if (p == Property.ALL) {
			return true;
			
		}else{
			Log.e("BusinessManager", "ERROR Illegal property!!");
			return true;
		}
	}

	
	
	private static boolean isInTopDeals(String businessId) {
		
		// TODO call DBhandler topBusiness finder
		// TODO Auto-generated method stub
		return false;
	}
	
}
