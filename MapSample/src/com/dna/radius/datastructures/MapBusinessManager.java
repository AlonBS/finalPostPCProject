package com.dna.radius.datastructures;

import java.util.HashMap;
import java.util.Set;

import android.util.Log;

import com.dna.radius.clientmode.ClientData;
import com.google.android.gms.maps.model.Marker;

/***
 * this objects holds all the relevant information which the map needs about the loaded businesses.
 * it is used instead of holding this information in separate data structures.
 * @author dror
 *
 */
public class MapBusinessManager {
	
	/**maps from map markers to businesses and from businesses to markers*/
	private HashMap <Marker, ExternalBusiness> markerToBusiness = new HashMap <Marker, ExternalBusiness>();
	private HashMap <ExternalBusiness, Marker> BusinessToMarker = new HashMap <ExternalBusiness, Marker>();
    
	/**relevant client data object*/
	private ClientData clientData;
	
	/**c-tor*/
    public MapBusinessManager(ClientData clientData){
    	this.clientData = clientData;
    }
    
	
    /**returns list of all the businesses which were download from parse*/
	public Set<ExternalBusiness> getAllBusinesses(){
		return BusinessToMarker.keySet();
	}
	
	/**returns list of all the map markers*/
	public Set<Marker> getAllMarkers(){
		return markerToBusiness.keySet();
	}
	
	/**given a BusinessMarker object, returns the relevand */
	public Marker getMarker(ExternalBusiness b){
		return BusinessToMarker.get(b);
	}
	public ExternalBusiness getBusiness(Marker m){
		return markerToBusiness.get(m);
	}
	public void addBusiness(ExternalBusiness b,Marker m){
		BusinessToMarker.put(b,m);
		markerToBusiness.put(m,b);
	}
	
	
	/** this function returns true if a business has a certein propert, 
	 * according to the propery enum list*/
	public enum Property{FAVORITES_PROP,TOP_BUSINESS_PROP,TOP_DEALS_PROP,ALL;}
	
	/**returns true if a business has a certain property.*/
	public boolean hasProperty(ExternalBusiness buisness,Property p){
		if(p==Property.FAVORITES_PROP){
			boolean retVal = clientData.isInFavourites(buisness.businessId); //TODO - improve!
			return retVal;
		}else if(p==Property.TOP_DEALS_PROP){
			return isInTopDeals(buisness.businessId); 
		}else if(p==Property.TOP_BUSINESS_PROP){
			return buisness.numOfStars>=4;
		}
		else if(p==Property.ALL){
			return true;
		}else{
			Log.e("BusinessManager", "ERROR Illegal property!!");
			
			return true;
		}
		
		
	}
	
	private boolean isInTopDeals(String businessId) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean hasProperty(Marker marker,Property p){
		ExternalBusiness buisness = markerToBusiness.get(marker);
		return hasProperty(buisness, p);		
	}	
}
