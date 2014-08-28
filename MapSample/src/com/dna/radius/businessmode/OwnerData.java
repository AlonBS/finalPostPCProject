package com.dna.radius.businessmode;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.dbhandling.DBHandler;
import com.google.android.gms.maps.model.LatLng;

public class OwnerData {
	private String name;
	private String currentDeal;
	private Bitmap image;
	private String address;
	private String phoneNumber;
	private int numberOfLikes;
	private int numberOfDislikes;
	private int rating;
	private ArrayList<Comment> commentsList;
	private int businessID;
	private ArrayList<DealHistoryObject> dealHistory;
	LatLng location;
	//TODO the context param should be removed
	public OwnerData(int businessID, Context context){
		this.businessID = businessID;
		DBHandler.loadOwnerDataSync(this, context);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setDeal(String currentDeal) {
		this.currentDeal = currentDeal;
	}
	
	public void changeDeal(String currentDeal) {
		this.currentDeal = currentDeal;
		DBHandler.setDeal(businessID,currentDeal);
	}
	
	public String getCurrentDeal() {
		return currentDeal;
	}
	
	public void changeImage(Bitmap image) {
		this.image = image;
		DBHandler.setImage(businessID,image);
	}
	
	public void setImage(Bitmap image) {
		this.image = image;
	}
	
	public Bitmap getImage() {
		return image;
	}
	
	public void setRating(int rating) {
		this.rating = rating;
	}
	
	public int getRating() {
		return rating;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setNumberOfDislikes(int numberOfDislikes) {
		this.numberOfDislikes = numberOfDislikes;
	}
	
	public int getNumberOfDislikes() {
		return numberOfDislikes;
	}
	
	public void setNumberOfLikes(int numberOfLikes) {
		this.numberOfLikes = numberOfLikes;
	}
	
	public int getNumberOfLikes() {
		return numberOfLikes;
	}
	
	public void setBusinessID(int businessID) {
		this.businessID = businessID;
	}
	
	public int getBusinessID() {
		return businessID;
	}
	
	public void setDealHistory(ArrayList<DealHistoryObject> dealHistory) {
		this.dealHistory = dealHistory;
	}
	
	public ArrayList<DealHistoryObject> getDealHistory() {
		return dealHistory;
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
