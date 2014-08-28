package com.dna.radius.datastructures;

import java.io.Serializable;
import java.util.Random;

import com.example.mapsample.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * represents a BusinessMarker.
 * contains all the relevant information which the MapFragment needs for
 * showing businesses over the map, and to call a certain deal activity.
 * @author dror
 *
 */
public class BusinessMarker implements Serializable{

	private static final long serialVersionUID = 1L;

	public static enum BuisnessType implements Serializable{
		RESTURANT(R.drawable.resturant_icon,R.drawable.burger),
		PUB(R.drawable.bar_icon,R.drawable.burger),
		HOTEL(R.drawable.hotel_icon,R.drawable.burger),
		COFFEE(R.drawable.coffee_icon,R.drawable.burger),
		SHOPPING(R.drawable.shopping_icon,R.drawable.burger);
		
		private int defaultImageID;
		private int iconID;
		private BuisnessType(int iconID, int defaultImageID){
			this.iconID = iconID;
			this.defaultImageID = defaultImageID;
		}
		
		public int getDefaultImageID(){
			return defaultImageID;
		}
		

	}
	public String name;
	public int iconID;
	public int rating;
	public int numOfStars;
	public int numOfLikes;
	public int numOfDislikes;
	public BuisnessType type;
	public LatLng pos;
	public String city;
	public long businessId;

	
	public BusinessMarker(String name,BuisnessType type,LatLng pos,String city,long id,int numOfLikes,int numOfDislikes){
		
		this.name = name;
		this.type = type;
		this.numOfStars = new Random().nextInt(5);
		this.rating = new Random().nextInt(5);
		this.pos = pos;
		this.city = city;
		this.iconID = type.iconID;
		this.businessId  = id;
		this.numOfDislikes = numOfDislikes;
		this.numOfLikes = numOfLikes;
	}
	
}
