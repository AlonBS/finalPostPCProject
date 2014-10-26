package com.dna.radius.clientmode;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dna.radius.datastructures.Comment;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.map.LikeAndDislikeFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;

/***
 * represents a data object for handling all the relevant data which is needed by
 * the client. this data is relevant for both regular clients and business owner.
 * therefore I choose to implement this class as a public singleton.
 * The ClientData should be loaded only once for each log in, with a proper client id (currently it is loaded
 * through the business owner opening screen and the client opening screen).
 * afterwards, it can be used anywhere in the application using the getInstance() function.
 */
public class ClientData{

	/** Currently logged-on user */
	static ParseUser currentUser;

	/** Logged-on user information */
	static ParseObject clientInfo;

	/** Default Home Location (set by user) */
	static LatLng homeLocation;

	/** User favorites, likes and dislikes lists */
	private static ArrayList<String> favorites = new ArrayList<String>();
	private static ArrayList<String> likes = new ArrayList<String>();
	private static ArrayList<String> dislikes = new ArrayList<String>();

	private static final LatLng JAFFA_STREET = new LatLng(31.78507,35.214328);


	/************************************Setters And Getters *****************************************/
	
	/**
	 * Returns this client's name
	 */
	static String getUserName() { 
		return currentUser.getUsername();
	}

	
	/**
	 * Sets this client's name.
	 */
	static void setUserName(String newUserName) {
		
		currentUser.setUsername(newUserName);
	}
	
	
	/**
	 * Returns this client's email address
	 */
	static String getEmail(){
		return currentUser.getEmail();
	}
	
	
	/**
	 * Sets this client's email address
	 */
	static void setEmail(String newEmail) {
		
		currentUser.setEmail(newEmail); 
	}

	
	/**
	 * Sets this client's password
	 */
	static void setPassword(String newPassword) {
		
		currentUser.setPassword(newPassword); 
	}
	
	
	static void syncChanges() {
		
		currentUser.saveInBackground();
	}
	
	
	/**
	 * Returns this client's home location.
	 */
	public static LatLng getHome(){ return homeLocation; }

	
	/**
	 * Sets this client's home location.
	 */
	public static void setHome(LatLng latlng) {
		
		homeLocation = latlng;
		ParseGeoPoint location = new ParseGeoPoint(homeLocation.latitude, homeLocation.longitude);
		clientInfo.put(ParseClassesNames.CLIENT_LOCATION, location);
		clientInfo.saveEventually();
	}
	
	/***************************************************************************************************/
	

	/**
	 * Loads this client's data from the parse.com DB
	 */
	static void loadClientInfo(){

		currentUser = ParseUser.getCurrentUser();

		try {

			if (currentUser != null) {

				currentUser.fetchIfNeeded();

				if (currentUser.isDataAvailable()) {

					clientInfo = currentUser.getParseObject(ParseClassesNames.CLIENT_INFO);

					if (clientInfo != null) { //This means registration has finished, and we can load data from Parse

						clientInfo.fetchIfNeeded();

						if (clientInfo.isDataAvailable()) {

							loadExtraInfo();
						}
					}

					else {

						homeLocation = JAFFA_STREET;
					}
				}

			}

		} catch (ParseException e) {

			Log.e("Client - fetch info", e.getMessage());
		}
	}
	
	
	/**
	 * Loads extra info, such as location and user preferrings
	 */
	private static void loadExtraInfo() {
		
		loadLocation();

		loadPreferrings();
	}


	private static void loadLocation()   {
		
		ParseGeoPoint gp = clientInfo.getParseGeoPoint(ParseClassesNames.CLIENT_LOCATION);
		homeLocation = new LatLng(gp.getLatitude(), gp.getLongitude());
	}


	private static void loadPreferrings() {

		JSONObject jo = clientInfo.getJSONObject(ParseClassesNames.CLIENT_PREFERRING);

		try {
			loadFavorites(jo.getJSONArray(ParseClassesNames.CLIENT_PREFERRING_FAVORITES));
			loadLikes(jo.getJSONArray(ParseClassesNames.CLIENT_PREFERRING_LIKES));
			loadDislikes(jo.getJSONArray(ParseClassesNames.CLIENT_PREFERRING_DISLIKES));

		} catch (JSONException e) {
			Log.e("Client - getting Array of preferences", e.getMessage());
		}
	}


	private static void loadFavorites(JSONArray ar) {

		int length = ar.length();
		
		for (int i = 0 ; i < length ; ++i) {
			
			try {
				favorites.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_PREFERRING_FAVORITES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Favorites", e.getMessage());
			}
		}
	}


	private static void loadLikes(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {
			try {
				likes.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_PREFERRING_LIKES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Likes", e.getMessage());
			}
		}
	}


	private static void loadDislikes(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {
			try {
				dislikes.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_PREFERRING_DISLIKES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Dislikes", e.getMessage());
			}
		}
	}

	
	/**
	 * Adds a business (using its' id) to this client's favorites list)
	 */
	public static void addToFavorites(String businessId) {

		addToStorage(favorites, businessId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_FAVORITES,
				ParseClassesNames.CLIENT_PREFERRING_FAVORITES_ID,
				"Add to Favorites");
	}


	/**
	 * Adds a business (using its' id) to this client's liked list)
	 */
	public static void addToLikes(String dealId){
		
		boolean removalNeeded = false;

		addToStorage(likes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_LIKES,
				ParseClassesNames.CLIENT_PREFERRING_LIKES_ID,
				"Add to Likes");
		
		
		if (isInDislikes(dealId)) {
			removeFromDislikes(dealId);
			removalNeeded = true;
		}
		
		DBHandler.addLikeExternally(dealId, removalNeeded);
	}


	/**
	 * Adds a business (using its' id) to this client's disliked list)
	 */
	public static void addToDislikes(String dealId){

		boolean removalNeeded = false;
		
		addToStorage(dislikes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_DISLIKES,
				ParseClassesNames.CLIENT_PREFERRING_DISLIKES_ID,
				"Add to dislikes");
		
		if (isInLikes(dealId)) {
			removeFromLikes(dealId);
			removalNeeded = true;
		}
		
		DBHandler.addDislikeExternally(dealId, removalNeeded);
	}


	/**
	 * Adds data to a storage. This method should be used in order to add
	 * onto favorites, likes and dislikes lists - as they all share same mechanism.
	 * @param ds - The data structure to which the data should be added. (Namely, favorites, likes and dislikes lists)
	 * @param itemId - The item to add
	 * @param n1 - Base to preferences column (i.e - CLIENT_PREFERRING)
	 * @param n2 - Name of JSONArray to remove from (i.e. 'likes' etc.)
	 * @param n3 - Name of field inside array (i.e. itemID)
	 * @param errMsg - An error message to log incase something went wrong
	 */
	private static void addToStorage(
			ArrayList<String> ds, String itemId,
			String n1, String n2, String n3, String errMsg) {

		if (!ds.contains(itemId)) {

			ds.add(itemId);
			JSONObject newItem = new JSONObject();

			try {

				newItem.put(n3, itemId);
				clientInfo.getJSONObject(n1).getJSONArray(n2).put(newItem);

			} catch (JSONException e) {

				Log.e(errMsg, e.getMessage());
			}

			clientInfo.saveEventually();
		}
		else {

			Log.e(errMsg, "Item was added twice");
		}
	}


	/**
	 * Removes a business (using its' id) from this clients favorites list
	 */
	public static void removeFromFavorites(String businessId) {

		removeFromStorage(favorites, businessId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_FAVORITES,
				ParseClassesNames.CLIENT_PREFERRING_FAVORITES_ID,
				"Remove from Favorites");
	}


	/**
	 * Removes a business (using its' id) from this clients likes list
	 */
	public static void removeFromLikes(String dealId){

		removeFromStorage(likes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_LIKES,
				ParseClassesNames.CLIENT_PREFERRING_LIKES_ID,
				"Remove from Likes");
		
		DBHandler.removelikeExternally(dealId);
	}

	
	/**
	 * Removes a business (using its' id) from this clients' dislikes list
	 */
	public static void removeFromDislikes(String dealId){

		removeFromStorage(dislikes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_PREFERRING_DISLIKES,
				ParseClassesNames.CLIENT_PREFERRING_DISLIKES_ID,
				"Remove from Dislikes");
		
		DBHandler.removeDislikeExternally(dealId);
	}


	/**
	 * Removes data from storage. This method should be used in order to add
	 * onto favorites, likes and dislikes lists - as they all share same mechanism.
	 * @param ds
	 * @param itemId
	 * @param n1 - base to preferences column (i.e - CLIENT_PREFERRING)
	 * @param n2 - name of JSONArray to remove from (i.e. 'likes' etc.)
	 * @param n3 - name of field inside array (i.e. itemID)
	 * @param errMsg
	 */
	private static void removeFromStorage(ArrayList<String> ds, String itemId,
			String n1, String n2, String n3, String errMsg) {

		if(ds.contains(itemId)){

			ds.remove(itemId);
			try {

				JSONArray newArr = new JSONArray();

				for (String f : ds){
					JSONObject temp = new JSONObject().put(n3, f);
					newArr.put(temp);
				}

				clientInfo.getJSONObject(n1).put(n2, newArr);
				clientInfo.saveEventually();

			} catch (JSONException e) {
				Log.e(errMsg, e.getMessage());
			}

		}else{

			Log.e(errMsg,"Item wasn't in db to remove");
		}
	}


	/**
	 * Returns true if this businessId is in this client's favorites lists. False - otherwise.
	 */
	public static boolean isInFavorites(String businessId){

		return favorites.contains(businessId);
	}
	
	
	/**
	 * Returns true if this businessId is in this client's likes lists. False - otherwise.
	 */
	public static boolean isInLikes(String dealId){

		return likes.contains(dealId);
	}
	
	
	/**
	 * Returns true if this businessId is in this client's dislikes lists. False - otherwise.
	 */
	public static boolean isInDislikes(String dealId){

		return dislikes.contains(dealId);
	}


	/**
	 * return LIKE/DISLIKE/DONT_CARE according to the user preferences regarding
	 * the current business deal.
	 */
	public static LikeAndDislikeFragment.ClientChoice getClientChoiceOnDeal(String dealId){

		if (isInLikes(dealId))
			return LikeAndDislikeFragment.ClientChoice.LIKE;

		else if(isInDislikes(dealId))
			return LikeAndDislikeFragment.ClientChoice.DISLIKE;

		return LikeAndDislikeFragment.ClientChoice.DONT_CARE;
	}

	/**
	 * Adds the comment 'commentContent' to the deal (using its id - dealId)
	 */
	public static void commentOnADeal(String dealId, String commentContent) {
		
		DBHandler.addCommentToDealExternally(
				dealId.split(BaseActivity.SEPERATOR)[0],
				new Comment(currentUser.getUsername(), commentContent, new Date()));
	}
}
