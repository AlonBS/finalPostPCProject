package com.example.mapsample;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

//TODO look at anywalls
public class BusinessMarker implements Serializable{
	/**
	 * 
	 */
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
	public BuisnessType type;
	public LatLng pos;
	public String city;
	public long businessId;
	
	public BusinessMarker(String name,BuisnessType type,LatLng pos,String city,long id){
		
		this.name = name;
		this.type = type;
		this.numOfStars = new Random().nextInt(6);
		this.rating = new Random().nextInt(5);
		this.pos = pos;
		this.city = city;
		this.iconID = type.iconID;
		this.businessId  = id;
		//this.businessId = new Random().nextInt(99999);
		//this.businessId = ""+new Random().nextInt(100000);
	}
	
}
