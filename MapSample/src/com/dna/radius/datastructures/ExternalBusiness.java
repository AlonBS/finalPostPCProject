package com.dna.radius.datastructures;

import java.io.Serializable;
import java.util.Random;

import com.dna.radius.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * represents a BusinessMarker.
 * contains all the relevant information which the MapFragment needs for
 * showing businesses over the map, and to call the ShowDealActivity for a certein business.
 *
 */
public class ExternalBusiness implements Serializable{

	private static final long serialVersionUID = 1L;

	public static enum BuisnessType implements Serializable{
		RESTAURANT(R.drawable.resturant_icon,1,"Restaurant"),
		PUB(R.drawable.bar_icon,2,"Pub"),
		ACCOMMODATION(R.drawable.hotel_icon,3,"Accommodation"),
		COFFEE(R.drawable.coffee_icon,4,"Coffee"),
		GROCERIES(R.drawable.shopping_icon,5, "Groceries");
		
		private int iconID;
		private int parseID;
		private String stringRep;
		
		private BuisnessType(int iconID, int parseID, String rep){
			this.iconID = iconID;
			this.parseID = parseID;
			this.stringRep = rep;
		}
		
		public int getParseID(){ return parseID; }
		
		public String getStringRep(){ return stringRep;	}

	}
	
	
	
	public String name;
	public int iconID;
	public int numOfStars;
	public int numOfLikes;
	public int numOfDislikes;
	public BuisnessType type;
	public LatLng pos;
	public String businessId;
	public String currentDealID;
	public String phoneStr;
	public String addressStr;
	public String currentDealStr;
	
	public ExternalBusiness(String name,BuisnessType type,LatLng pos,String id,int numOfLikes,
							int numOfDislikes,String currentDealID,String phoneStr,String addressStr,String currentDealStr){
		this.name = name;
		this.type = type;
		this.numOfStars = new Random().nextInt(5);
		this.pos = pos;
		this.iconID = type.iconID;
		this.businessId  = id;
		this.numOfDislikes = numOfDislikes;
		this.numOfLikes = numOfLikes;
		this.currentDealID = currentDealID;
		this.currentDealStr = currentDealStr;
		this.addressStr = addressStr;
		this.phoneStr = phoneStr;
	}
	
}
