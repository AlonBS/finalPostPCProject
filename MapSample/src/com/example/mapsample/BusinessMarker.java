package com.example.mapsample;

import java.util.Random;

import com.google.android.gms.maps.model.LatLng;

//TODO look at anywalls
public class BusinessMarker {
	enum BuisnessType{
		RESTURANT(R.drawable.resturant_icon),
		PUB(R.drawable.bar_icon),
		HOTEL(R.drawable.hotel_icon),
		COFFEE(R.drawable.coffee_icon),
		SHOPPING(R.drawable.shopping_icon);
		
		private int iconID;
		private BuisnessType(int iconID){
			this.iconID = iconID;
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
	
	
	public BusinessMarker(String name,BuisnessType type,LatLng pos,String city){
		
		this.name = name;
		this.type = type;
		this.numOfStars = new Random().nextInt(6);
		this.rating = new Random().nextInt(100);
		this.pos = pos;
		this.city = city;
		this.iconID = type.iconID;
		this.businessId = new Random().nextInt(100000);
		//this.businessId = ""+new Random().nextInt(100000);
	}
	
}
