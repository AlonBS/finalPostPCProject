package com.dna.radius.businessmode;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.dbhandling.DBHandler;
import com.google.android.gms.maps.model.LatLng;

public class OwnerData {
	public String name;
	public boolean hasImage;
	public String currentDeal;
	public Bitmap image;
	public String address;
	public String phoneNumber;
	public int numberOfLikes;
	public int numberOfDislikes;
	public int rating;
	public ArrayList<Comment> commentsList;
	public int businessID;
	public ArrayList<DealHistoryObject> dealHistory;
	LatLng location;
	//TODO the context param should be removed
	public OwnerData(int businessID, Context context){
		this.businessID = businessID;
		DBHandler.loadOwnerDataSync(this, context);
	}
	
	public void changeName(String name) {
		this.name = name;
	}

	public void changeDeal(String currentDeal) {
		this.currentDeal = currentDeal;
		DBHandler.setDeal(businessID,currentDeal);
	}
	
	public void changeBusinessImage(Bitmap bmap){
		this.image = bmap;
	}
	
	public void changeBusinessAddress(String address){
		this.address = address;
	}
	public void changeBusinessPhone(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}
	public void changeBusinessName(String name){
		this.name = name;
		
	}
	public void changeBusinessLocation(LatLng newLocation){
		this.location = newLocation;
	}
}
