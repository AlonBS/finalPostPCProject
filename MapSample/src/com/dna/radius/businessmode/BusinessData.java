package com.dna.radius.businessmode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.dna.radius.R;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.DealHistoryManager;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.SupportedTypes;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/***
 * an owner data object represents all the data which the business owner needs for
 * his business.
 * @author dror
 *
 */
public class BusinessData {
	
	static ParseUser currentUser;

	static ParseObject businessInfo;
		
	static String businessName;
	static double businessRating; // range should be: [0, 5]
	static SupportedTypes.BusinessType businessType;
	static String businessAddress;
	static String businessPhoneNumber;
	static LatLng businessLocation;
	
	

	static boolean hasADealOnDisplay;
	static Deal currentDeal; //the current deal string
	//public int numberOfLikes;
	//public int numberOfDislikes;
	
	
	
	static boolean hasImageOnDisplay; //does the business has an image?
	public Bitmap image; // the image of the business (if exists)
	
	static DealHistoryManager dealsHistory;
	
	static final float DEFAULT_RATING = 3;
	
	private static final String SEPERATOR = "###";
	
	static final String DATE_FORMAT = "dd-MM-yyyy";
	
	/** loads the Client data from the parse DB*/
	public static void loadBusinessInfo(){

		currentUser = ParseUser.getCurrentUser();
	

		try {

			if (currentUser != null) {

				currentUser.fetchIfNeeded();

				if (currentUser.isDataAvailable()) {

					businessInfo = currentUser.getParseObject(ParseClassesNames.BUSINESS_INFO);

					if (businessInfo != null) { //This means registration has finished, and we can load data from Parse

						businessInfo.fetchIfNeeded();

						if (businessInfo.isDataAvailable()) {

							loadExtraInfo();
						}
						
					}

					else {
						
						//set default values for first time
						businessName = "";
						businessRating = 0;

					}
				}

			}

		} catch (ParseException e) {

			Log.e("Business - fetch info", e.getMessage());
		}
	}
	
	
	private static void loadExtraInfo() {
		
		businessName = businessInfo.getString(ParseClassesNames.BUSINESS_INFO);
		businessRating = businessInfo.getDouble(ParseClassesNames.BUSINESS_RATING); // range should be: [0, 5]
		businessType = SupportedTypes.BusinessType.stringToType(businessInfo.getString(ParseClassesNames.BUSINESS_TYPE));
		businessAddress = businessInfo.getString(ParseClassesNames.BUSINESS_ADDRESS);
		businessPhoneNumber = businessInfo.getString(ParseClassesNames.BUSINESS_PHONE);

		loadLocation();
		
		loadCurrentDeal();
		
		loadDealsHistory();
	}
	
	
	private static void loadLocation() {
		
		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_LOCATION);
		try {
			businessLocation = new LatLng(jo.getDouble(ParseClassesNames.BUSINESS_LOCATION_LAT),
					jo.getDouble(ParseClassesNames.BUSINESS_LOCATION_LONG));

		} catch (JSONException e) {

			Log.e("Business -load location", e.getMessage());
		}
		
		
		
	}
	
	
	
	public static String getUserName(){
		return currentUser.getUsername();
	}
	
	public static String getEmail(){
		return currentUser.getEmail();
	}
	
	public static void setUserName(String newUserName){
		//TODO alon - is this enough? + saveEventually() get stuck.
		currentUser.setUsername(newUserName);
		currentUser.saveInBackground();
	}
	
	public static void setEmail(String newEmail){
		//TODO alon - is this enough? + saveEventually() get stuck.
		currentUser.setEmail(newEmail);
		currentUser.saveInBackground();
	}
	
	public static void setPassword(String newPassword){
		//TODO alon - is this enough? + saveEventually() get stuck.
		currentUser.setPassword(newPassword); 
		currentUser.saveInBackground();
	}
	
	
	
//	
//	//TODO - remove me!!!
//	//******************
//	try {
//		Thread.sleep(1000);
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
//	//*******************


//	//owner.name = "Mcdonalds";
//	//owner.businessID = "SAD";
//	owner.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.burger);
//	BusinessData.numberOfLikes = 23131;
//	owner.numberOfDislikes = 524;
//	owner.phoneNumber = "0508259193";
//	//owner.rating = 4;
//	owner.hasImage = true;
//	owner.hasDeal = true;// TODO should be true only if the business has a an image
//	owner.address = "Jaffa street 61, Jerusalem";
//	owner.currentDeal = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BAG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!";
//	owner.dealHistory = new ArrayList<Deal>();
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Eat a megaburger and kill 3 animals for free",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"buy a pack of Supergoal and get a sticker with Kfir Partiely signature. you dont wanna miss that!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"buy a triangle toaster and get another triangle toaster!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy seasonal tickets for Maccabi Petah Tikva and get Free entrance to the first Toto Cup match against Hapoel Raanana",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"New at superfarm! a supporting sports bra which gives you extra support during your period",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy a nokia Phone and get free games! (snake) ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"only 99.99$ for a full Kosher cellular package ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy a set of Tfilin and get a free tour at Kivrey Zadikim ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Eat a megaburger and kill 3 animals for free",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"buy a pack of Supergoal and get a sticker with Kfir Partiely signature. you dont wanna miss that!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"buy a triangle toaster and get another triangle toaster!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy seasonal tickets for Maccabi Petah Tikva and get Free entrance to the first Toto Cup match against Hapoel Raanana",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"New at superfarm! a supporting sports bra which gives you extra support during your period",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy a nokia Phone and get free games! (snake) ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"only 99.99$ for a full Kosher cellular package ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
//	owner.dealHistory.add(new Deal(new Random().nextInt(99999),"Buy a set of Tfilin and get a free tour at Kivrey Zadikim ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));

	
	
	private static void loadCurrentDeal() {
		
		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
		try { //String STRING, int , int, date
			currentDeal = new Deal(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
					jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
					jo.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
					jo.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
					new SimpleDateFormat(DATE_FORMAT).parse(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)));
			
			hasADealOnDisplay = true;
			

		} catch (JSONException e) {

			Log.e("Business -load current deal", e.getMessage());
			
		} catch (java.text.ParseException e) {
			
			Log.e("Business -load current deal", e.getMessage());
		}
	}
	
	
	private static void loadDealsHistory() {
		
		int totalLikes, totalDislikes, totalDeals;
		ArrayList<Deal> oldDeals = new ArrayList<Deal>();
		
		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
		try {
			
			totalLikes = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES);
			totalDislikes = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES);
			totalDeals = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS);
			
			JSONArray ja = jo.getJSONArray(ParseClassesNames.BUSINESS_HISTORY_DEALS);
			int len = ja.length();
			
			for (int i = 0 ; i < len ; ++i) {
				
				JSONObject temp = ja.getJSONObject(i);
				
				oldDeals.add( new Deal(temp.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
					temp.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
					temp.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
					temp.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
					new SimpleDateFormat(DATE_FORMAT).parse(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE))));
				
			}
			
			dealsHistory = new DealHistoryManager(totalLikes, totalDislikes, totalDeals, oldDeals);
			
		}
		catch (JSONException e) {
		
			Log.e("Business - history create", e.getMessage());
		}
		
		catch (java.text.ParseException e) {
			
			Log.e("Business - load old deals", e.getMessage());
		}
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void createNewDeal(String content) {
		
		String id = businessInfo.getObjectId() + SEPERATOR + Integer.toString(dealsHistory.getTotalNumOfDeals());
		Date date = new Date();
		
		// update locally
		Deal newDeal = new Deal(id, content, 0, 0, date);
		
		//update on Parse.Com
		JSONObject newDealJO = new JSONObject();
		try {
			
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID, id);
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT, content);
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES, 0);
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES, 0);
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE, date);
			
		} catch (JSONException e) {
			
			Log.e("Business - new deal create", e.getMessage());
		}
		businessInfo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, newDealJO);
		
		
		// check if history update is required
		if (hasADealOnDisplay) {
			
			// history update - locally
			dealsHistory.incTotalNumOfLikes(currentDeal.getNumOfLikes());
			dealsHistory.incTotalNumOfDisLikes(currentDeal.getNumOfDislikes());
			dealsHistory.incTotalNumOfDeals();

			dealsHistory.addOldDeal(currentDeal);
			currentDeal = newDeal;
			
			
			// history update - on Parse.com
			JSONObject oldDealsJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
			try {
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES, dealsHistory.getNumOfLikes());
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES, dealsHistory.getNumOfDislikes());
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS, dealsHistory.getTotalNumOfDeals());
				
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_DEALS,
						oldDealsJO.getJSONArray(ParseClassesNames.BUSINESS_HISTORY_DEALS).put(newDealJO));
				
			} catch (JSONException e) {
				Log.e("Business - new deal create", e.getMessage());
			}
			
			businessInfo.put(ParseClassesNames.BUSINESS_HISTORY, oldDealsJO);
			
			//TODO should be saveEventually()
			currentUser.saveInBackground(null);
			businessInfo.saveInBackground(null);
		}
		
		
		
		hasADealOnDisplay = true;
		
		
//		DBHandler.setDeal(businessID,currentDeal,0,0);
	}
	
	
	
	
	public void changeBusinessImage(Bitmap bmap){
	//	this.image = bmap;
//		DBHandler.setImage(businessID, bmap);
	}
	
	public void changeBusinessAddress(String address){
	//	this.address = address;
	//	DBHandler.setBusinessAddress(businessID,address);
	}
	public void changeBusinessPhone(String phoneNumber){
		//this.phoneNumber = phoneNumber;
//		DBHandler.setBusinessPhone(businessID,phoneNumber);
	}
	
	public void changeBusinessName(String name){
		//this.name = name;
	//	DBHandler.setBusinessName(businessID,name);
		
	}
	public void changeBusinessLocation(LatLng newLocation){
		this.businessLocation = newLocation;
	//	DBHandler.setBusinessLocation(businessID,newLocation);
	}
	
	
	public static boolean hasADealOnDisplay() { return hasADealOnDisplay; }
	
	public static boolean hasImageOnDisplay() { return hasImageOnDisplay; }
}
