package com.dna.radius.businessmode;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.DealHistoryManager;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

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
	
	

	//static boolean hasADealOnDisplay;
	static Deal currentDeal; //the current deal string
	//public int numberOfLikes;
	//public int numberOfDislikes;
	
	
	
	static boolean hasImage; //does the business has an image?
	static Bitmap businessImage = null; // the image of the business (if exists)
	
	static DealHistoryManager dealsHistory;
	
	static final float DEFAULT_RATING = 3;
	
	
	private static final String SEPERATOR = "###";
	
	
	
	
	
	public static String getUserName(){ return currentUser.getUsername(); }
	
	
	
	public static void setUserName(String newUserName){
		//TODO alon - is this enough? + saveEventually() get stuck.
		currentUser.setUsername(newUserName);
		currentUser.saveInBackground();
	}
	
	public static String getEmail(){ return currentUser.getEmail(); }
	
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
	
	
	public static boolean hasADealOnDisplay() { return currentDeal != null; }
	
	public static boolean hasImage() { return hasImage; }
	
	public static boolean imageFullyLoaded() { return businessImage != null; }
	
	

//	public static Bitmap getImage(){
//		return businessImage;
//	}
//	
//	public static void setImage (Bitmap newImage) {
//		businessImage = newImage;
//		//TODO save image on parse
//	}
	
	public static String getName() { return businessName; }
	
	public static void setName(String newName) {
		
		businessName = newName;
		businessInfo.put(ParseClassesNames.BUSINESS_NAME, businessName);
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}
	
	public static SupportedTypes.BusinessType getType() { return businessType; }
	
	public static void setType (BusinessType newType) {
		
		businessType = newType;
		businessInfo.put(ParseClassesNames.BUSINESS_TYPE, businessType.getStringRep());
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}
	
	
	public String getAddress(){ return businessAddress; }
	
	public static void setAddress(String newAddress) {
		
		businessAddress = newAddress;
		businessInfo.put(ParseClassesNames.BUSINESS_ADDRESS, businessAddress);
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}
	
	
	public static String getPhoneNumber() { return businessPhoneNumber; }
	public static String getBusinessAddress() { return businessAddress; }
	
	public static void setPhoneNumber(String newPhoneNumber) {
		
		businessPhoneNumber = newPhoneNumber;
		businessInfo.put(ParseClassesNames.BUSINESS_PHONE, businessPhoneNumber);
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}
	
	
	public static LatLng getLocation() { return businessLocation; }
	
	public static void setLocation (LatLng newLocation) {
		
		businessLocation = newLocation;
		
		JSONObject coordinates = new JSONObject();
		try {
			coordinates.put(ParseClassesNames.BUSINESS_LOCATION_LAT , businessLocation.latitude);
			coordinates.put(ParseClassesNames.BUSINESS_LOCATION_LONG , businessLocation.longitude);

		} catch (JSONException e) {

			Log.e("Business - location change", e.getMessage());
		}
		businessInfo.put(ParseClassesNames.BUSINESS_LOCATION, coordinates);
		businessInfo.saveInBackground();
	}
	
	
	
	
	
	
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
		
		loadRawData();

		loadLocation();
				
		loadCurrentDeal();
		
		loadDealsHistory();
	}
	
	private static void loadRawData() {
		
		businessName = businessInfo.getString(ParseClassesNames.BUSINESS_NAME);
		businessRating = businessInfo.getDouble(ParseClassesNames.BUSINESS_RATING); // range should be: [0, 5]
		businessType = SupportedTypes.BusinessType.stringToType(businessInfo.getString(ParseClassesNames.BUSINESS_TYPE));
		businessAddress = businessInfo.getString(ParseClassesNames.BUSINESS_ADDRESS);
		businessPhoneNumber = businessInfo.getString(ParseClassesNames.BUSINESS_PHONE);
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
	
	
	static void loadImage(final ImageView im, final ProgressBar pb) {
		
		
		ParseFile file = businessInfo.getParseFile(ParseClassesNames.BUSINESS_IMAGE);
		
		if (file == null) {
			
			hasImage = false;
			return;
		}
		
		file.getDataInBackground(
				new GetDataCallback() {

					public void done(byte[] data, ParseException e) {

						if (e == null) {

							pb.setVisibility(View.GONE);
							businessImage = BitmapFactory.decodeByteArray(data, 0 ,data.length);
							
							if (im == null) return;
							
							im.setImageBitmap(businessImage);
							hasImage = true;
						}
						else {
							Log.e("Business - Load image", e.getMessage());
						}
					}
				});
	}
	
	
	static void setImage(Bitmap newImage) {
		
		//TODO Remove old picture reference
		
		businessImage = newImage;   
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    byte[] data = stream.toByteArray();

	    if (data != null){
	    	
	        final ParseFile file = new ParseFile(data);
	        
	        file.saveInBackground(new SaveCallback() {

				@Override
				public void done(ParseException e) {
					
					if (e==null) {
						
						businessInfo.put(ParseClassesNames.BUSINESS_IMAGE, file);
						businessInfo.saveInBackground(); //TODO SHOULD BE SAVE EVENTUALLY
						
					}
				}
			});
	    }
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
					new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT)
						.parse(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)));

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
					//TODO - alon, try to add a deal, log out, log back in, and then open deal history - and youll get an error
					new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT).parse(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE))));
				
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

	
	public static void deleteCurrentDeal(){
		//TODO alon - this method should delete the current deal and move it to history
	}
	
	
	public static void createNewDeal(String content) {
		
		// get old deal (as JSON object)
		JSONObject oldDealJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
		
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
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE, new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT).format(date));
			newDealJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS, new JSONArray());
			
		} catch (JSONException e) {
			
			Log.e("Business - new deal create", e.getMessage());
		}
		businessInfo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, newDealJO);
		
		
		// check if history update is required
		if ( hasADealOnDisplay() ) {
			
			// history update - locally
			dealsHistory.incTotalNumOfLikes(currentDeal.getNumOfLikes());
			dealsHistory.incTotalNumOfDisLikes(currentDeal.getNumOfDislikes());

			dealsHistory.addDeal(currentDeal);
			
			// history update - on Parse.com
			JSONObject oldDealsJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
			try {
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES, dealsHistory.getNumOfLikes());
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES, dealsHistory.getNumOfDislikes());
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS, dealsHistory.getTotalNumOfDeals());
				
				oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_DEALS,
						oldDealsJO.getJSONArray(ParseClassesNames.BUSINESS_HISTORY_DEALS).put(oldDealJO));
				
			} catch (JSONException e) {
				Log.e("Business - new deal create", e.getMessage());
			}
			
			businessInfo.put(ParseClassesNames.BUSINESS_HISTORY, oldDealsJO);
		}
		
		dealsHistory.incTotalNumOfDeals();
		currentDeal = newDeal;
		
		//TODO should be saveEventually()
		currentUser.saveInBackground(null);
		businessInfo.saveInBackground(null);
	}
	
	
	
	public static void addCommentToCurrentDeal(Comment newComment) {
		
		currentDeal.addComment(newComment);
		
		JSONObject newCommentJO = new JSONObject();
		
		try {
			newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_AUTHOR, newComment.getAuthorName());
			newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_CONTENT, newComment.getCommentContent());
			newCommentJO.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_DATE, newComment.getCommentDate());
			
			
			JSONObject currentDealJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
			currentDealJO.getJSONArray(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS).put(newCommentJO);
			businessInfo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, currentDealJO);
						
			// TODO should be eventually
			businessInfo.saveInBackground();			
			
			
		} catch (JSONException e) {
			Log.e("Business - add comment to deal", e.getMessage());
		}
	}
	
}
