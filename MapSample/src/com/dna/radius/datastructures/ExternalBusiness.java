package com.dna.radius.datastructures;

import java.util.Random;

import com.dna.radius.infrastructure.SupportedTypes;
import com.google.android.gms.maps.model.LatLng;

/**
 * represents a BusinessMarker.
 * contains all the relevant information which the MapFragment needs for
 * showing businesses over the map, and to call the ShowDealActivity for a certein business.
 *
 */
public class ExternalBusiness {


	
	public String name;
	public int iconID;
	public int numOfStars;
	public int numOfLikes;
	public int numOfDislikes;
	public SupportedTypes.BusinessType type;
	public LatLng pos;
	public String businessId;
	public String currentDealID;
	public String phoneStr;
	public String addressStr;
	public String currentDealStr;
	
	public ExternalBusiness(String name, SupportedTypes.BusinessType type,LatLng pos,String id,int numOfLikes,
							int numOfDislikes,String currentDealID,String phoneStr,String addressStr,String currentDealStr){
		this.name = name;
		this.type = type;
		this.numOfStars = new Random().nextInt(5);
		this.pos = pos;
		this.iconID = type.getIconID();
		this.businessId  = id;
		this.numOfDislikes = numOfDislikes;
		this.numOfLikes = numOfLikes;
		this.currentDealID = currentDealID;
		this.currentDealStr = currentDealStr;
		this.addressStr = addressStr;
		this.phoneStr = phoneStr;
	}
	
}
