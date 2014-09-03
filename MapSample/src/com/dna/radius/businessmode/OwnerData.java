package com.dna.radius.businessmode;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.dbhandling.DBHandler;
import com.google.android.gms.maps.model.LatLng;

/***
 * an owner data object represents all the data which the business owner needs for
 * his business.
 * @author dror
 *
 */
public class OwnerData {
	public String businessID;
	public String name; //busienss name
	public boolean hasImage; //does the business has an image?
	public Bitmap image; // the image of the business (if exists)
	public String currentDeal; //the current deal string
	public String address;
	public String phoneNumber;
	public int numberOfLikes;
	public int numberOfDislikes;
	public int rating; //rating - out of 5
	public ArrayList<DealHistoryObject> dealHistory;
	LatLng location;
	public boolean hasDeal;
	
	//TODO DROR the context param should be removed
	public OwnerData(String businessID, Context context){
		this.businessID = businessID;
	//	DBHandler.loadOwnerDataSync(this, context);
	}
	
	public void changeDeal(String currentDeal) {
		this.currentDeal = currentDeal;
//		DBHandler.setDeal(businessID,currentDeal,0,0);
	}
	
	public void changeBusinessImage(Bitmap bmap){
		this.image = bmap;
//		DBHandler.setImage(businessID, bmap);
	}
	
	public void changeBusinessAddress(String address){
		this.address = address;
	//	DBHandler.setBusinessAddress(businessID,address);
	}
	public void changeBusinessPhone(String phoneNumber){
		this.phoneNumber = phoneNumber;
//		DBHandler.setBusinessPhone(businessID,phoneNumber);
	}
	public void changeBusinessName(String name){
		this.name = name;
	//	DBHandler.setBusinessName(businessID,name);
		
	}
	public void changeBusinessLocation(LatLng newLocation){
		this.location = newLocation;
	//	DBHandler.setBusinessLocation(businessID,newLocation);
	}
}
