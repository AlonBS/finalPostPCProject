package com.example.datastructures;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.example.datastructures.BusinessMarker.BuisnessType;
import com.example.dbhandling.DBHandler;
import com.google.android.gms.maps.model.Marker;

public class BusinessManager {
	
	/**maps from map markers to businesses and from businesses to markers*/
	private HashMap <Marker, BusinessMarker> markerToBusiness = new HashMap <Marker, BusinessMarker>();
	private HashMap <BusinessMarker, Marker> BusinessToMarker = new HashMap <BusinessMarker, Marker>();
    
    public BusinessManager(Context context){
    	this.context = context;
    }
    
    private Context context;
	
    /**returns list of all the businesses which were download from parse*/
	public Set<BusinessMarker> getAllBusinesses(){
		return BusinessToMarker.keySet();
	}
	
	/**returns list of all the map markers*/
	public Set<Marker> getAllMarkers(){
		return markerToBusiness.keySet();
	}
	
	public Marker getMarker(BusinessMarker b){
		return BusinessToMarker.get(b);
	}
	public BusinessMarker getBusiness(Marker m){
		return markerToBusiness.get(m);
	}
	public void addBusiness(BusinessMarker b,Marker m){
		BusinessToMarker.put(b,m);
		markerToBusiness.put(m,b);
	}
	
	
	/** this function returns true if a business has a certein propert, 
	 * according to the propery enum list*/
	public enum Property{FAVORITES_PROP,TOP_BUSINESS_PROP,TOP_DEALS_PROP,ALL;}
	public boolean hasProperty(BusinessMarker buisness,Property p){
		if(p==Property.FAVORITES_PROP){
			DBHandler dbHandler = new DBHandler(context);
			boolean retVal = dbHandler.isInFavourites(buisness.businessId); //TODO - improve!
			dbHandler.close();
			return retVal;
		}else if(p==Property.TOP_DEALS_PROP){
			return isInTopDeals(buisness.businessId); 
		}else if(p==Property.TOP_BUSINESS_PROP){
			return buisness.rating>=4;
		}
		else if(p==Property.ALL){
			return true;
		}else{
			Log.e("BusinessManager", "ERROR Illegal property!!");
			
			return true;
		}
		
		
	}
	

	private boolean isInTopBuisnesses(long businessId) {
		// TODO Auto-generated method stub
		return false;
	}
	private boolean isInTopDeals(long businessId) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean hasProperty(Marker marker,Property p){
		BusinessMarker buisness = markerToBusiness.get(marker);
		return hasProperty(buisness, p);		
	}	
}
