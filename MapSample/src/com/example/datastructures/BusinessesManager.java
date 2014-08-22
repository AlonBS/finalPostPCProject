package com.example.datastructures;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import com.example.datastructures.BusinessMarker.BuisnessType;
import com.example.dbhandling.DBHandler;
import com.google.android.gms.maps.model.Marker;

public class BusinessesManager {
	
	/**maps from map markers to businesses and from businesses to markers*/
	private HashMap <Marker, BusinessMarker> markerToBusiness = new HashMap <Marker, BusinessMarker>();
	private HashMap <BusinessMarker, Marker> BusinessToMarker = new HashMap <BusinessMarker, Marker>();
    private static List<Long> favourite;
    
    public BusinessesManager(Context context){
    	this.context = context;
    }
    
    private Context context;
	
	public Set<BusinessMarker> getAllBusinesses(){
		return BusinessToMarker.keySet();
	}
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
	
	/*
	public enum Property{
		RESTURANT_PROP(true,BuisnessType.RESTURANT),
		PUB_PROP(true,BuisnessType.PUB),
		HOTEL_PROP(true,BuisnessType.HOTEL),
		COFFEE_PROP(true,BuisnessType.COFFEE),
		SHOPPING_PROP(true,BuisnessType.SHOPPING),
		FAVORITES_PROP(false,null),
		TOP_BUSINESS_PROP(false,null),
		TOP_DEALS_PROP(false,null);
		
		public  boolean isTypePropery;
		public BuisnessType matching_type;
		private Property(boolean isTypePropery, BuisnessType matching_type){
			this.isTypePropery = isTypePropery;
			this.matching_type = matching_type;
		}
		
		
	}
	
	public boolean hasProperty(BusinessMarker buisness,Property p){
		if(p.isTypePropery){
			return buisness.type==p.matching_type;
		}
		else if(p==Property.FAVORITES_PROP){
			DBHandler dbHandler = new DBHandler(context);
			return dbHandler.isInFavourites(buisness.businessId); //TODO - improve!
		}
		else{
			//TODO handle top deals/top buisnesses
			return true;
		}
		
		
	}
	

	public boolean hasProperty(Marker marker,Property p){
		BusinessMarker buisness = markerToBusiness.get(marker);
		return hasProperty(buisness, p);		
	}
*/
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
			return isInTopBuisnesses(buisness.businessId);
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