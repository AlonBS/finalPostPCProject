package com.dna.radius.clientmode;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.DBHandler.DealLikeStatus;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/***
 * represents a data object for handling all the relevant data which is needed by
 * the client. this data is relevant for both regular clients and business owner.
 * therefore I choose to implement this class as a public singleton.
 * The ClientData should be loaded only once for each log in, with a proper client id (currently it is loaded
 * through the business owner opening screen and the client opening screen).
 * afterwards, it can be used anywhere in the application using the getInstance() function.
 *
 */
public class ClientData{

	static ParseUser currentUser;

	static ParseObject clientInfo;

	static LatLng homeLocation;

	/**lists which holds all the deals which the user liked or disliked*/
	private static ArrayList<String> favourites = new ArrayList<String>();
	private static ArrayList<String> likes = new ArrayList<String>();
	private static ArrayList<String> dislikes = new ArrayList<String>();

	//TODO ? add private class?
	private static final LatLng JAFFA_STREET = new LatLng(31.78507,35.214328);


	public static String getUserName(){
		return currentUser.getUsername();
	}
	
	public static void setUserName(String newUserName){
		//TODO ALON - is it enough? + saveEventually() get stuck
		currentUser.setUsername(newUserName);
		currentUser.saveInBackground(); 
	}
	
	public static String getEmail(){
		return currentUser.getEmail();
	}
	
	public static void setEmail(String newEmail){
		//TODO ALON - is it enough? + saveEventually() get stuck
		currentUser.setEmail(newEmail); 
		currentUser.saveInBackground(); 
	}
	
	public static void setPassword(String newPassword){
		//TODO ALON - is it enough? + saveEventually() get stuck
		currentUser.setPassword(newPassword); 
		currentUser.saveInBackground(); 
	}

	/** loads the Client data from the parse DB*/
	public static void loadClientInfo(){

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
	
	
	private static void loadExtraInfo() {
		
		loadLocation();

		loadPreferrings();
	}


	private static void loadLocation()   {


		JSONObject jo = clientInfo.getJSONObject(ParseClassesNames.CLIENT_LOCATION);
		try {
			homeLocation = new LatLng(jo.getDouble(ParseClassesNames.CLIENT_LOCATION_LAT),
					jo.getDouble(ParseClassesNames.CLIENT_LOCATION_LONG));

		} catch (JSONException e) {

			Log.e("Client -load location", e.getMessage());
		}


	}


	private static void loadPreferrings() {


		JSONObject jo = clientInfo.getJSONObject(ParseClassesNames.CLIENT_PREFERRING);

		try {
			loadFavorites(jo.getJSONArray(ParseClassesNames.CLIENT_FAVORITES));
			loadLikes(jo.getJSONArray(ParseClassesNames.CLIENT_LIKES));
			loadDislikes(jo.getJSONArray(ParseClassesNames.CLIENT_DISLIKES));

		} catch (JSONException e) {
			Log.e("Client - getting Array of preferences", e.getMessage());
		}
	}


	private static void loadFavorites(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {
			try {
				favourites.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_FAVORITES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Favorites", e.getMessage());
			}
		}
	}


	private static void loadLikes(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {
			try {
				likes.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_LIKES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Likes", e.getMessage());
			}
		}
	}


	private static void loadDislikes(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {
			try {
				dislikes.add(ar.getJSONObject(i).getString(ParseClassesNames.CLIENT_DISLIKES_ID));
			} catch (JSONException e) {
				Log.e("Client - Add Dislikes", e.getMessage());
			}
		}
	}


	/***
	 * gets the user's home location according to the given LatLng.
	 * if the updateServers parameter is true, updates the parse servers as well.
	 */
	public static LatLng getHome(){ return homeLocation; }

	
	/***
	 * sets the user's home location according to the given LatLng.
	 * if the updateServers parameter is true, updates the parse servers as well.
	 */
	public static void setHome(LatLng latlng) {
		
		homeLocation = latlng;
		JSONObject coordinates = new JSONObject();
		try {
			coordinates.put(ParseClassesNames.CLIENT_LOCATION_LAT ,ClientData.homeLocation.latitude);
			coordinates.put(ParseClassesNames.CLIENT_LOCATION_LONG ,ClientData.homeLocation.longitude);
			
		} catch (JSONException e) {
			
			Log.e("Client - setHome()", e.getMessage());
			
		}
		clientInfo.put(ParseClassesNames.CLIENT_LOCATION, coordinates);
		
		// TODO - maybe concentrate more than one call
		clientInfo.saveInBackground(); //TODO SHOULD BE saveEvantually()
	}



	/**
	 * returns the user's favourites list
	 * @return
	 */
	public static void addToFavourites(String businessId){

		addToStorage(favourites, businessId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_FAVORITES,
				ParseClassesNames.CLIENT_FAVORITES_ID,
				"Add to Favorites");
	}


	/**
	 * returns the user's likes list
	 * @return
	 */
	public static void addToLikes(String dealId){
		
		boolean removalNeeded = false;

		addToStorage(likes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_LIKES,
				ParseClassesNames.CLIENT_LIKES_ID,
				"Add to Likes");
		
		
		
		if (isInDislikes(dealId)) {
			removeFromDislikes(dealId);
			removalNeeded = true;
		}
		
		DBHandler.addLikeExternally(dealId, removalNeeded);
	}


	/**
	 * returns the user's likes list
	 * @return
	 */
	public static void addToDislikes(String dealId){

		addToStorage(dislikes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_DISLIKES,
				ParseClassesNames.CLIENT_DISLIKES_ID,
				"Add to dislikes");
		
		if (isInLikes(dealId)) {
			removeFromLikes(dealId);
		}
	}



	/**
	 * 
	 * @param ds
	 * @param itemId
	 * @param n1 SEE REMOVE FROM STORAE TODO
	 * @param n2
	 * @param n3
	 * @param errMsg
	 */
	private static void addToStorage(ArrayList<String> ds, String itemId,
			String n1, String n2, String n3, String errMsg){

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


	public static void removeFromFavorites(String businessId) {

		removeFromStorage(favourites, businessId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_FAVORITES,
				ParseClassesNames.CLIENT_FAVORITES_ID,
				"Remove from Favorites");
	}



	public static void removeFromLikes(String dealId){

		removeFromStorage(likes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_LIKES,
				ParseClassesNames.CLIENT_LIKES_ID,
				"Remove from Likes");
	}


	public static void removeFromDislikes(String dealId){

		removeFromStorage(dislikes, dealId,
				ParseClassesNames.CLIENT_PREFERRING,
				ParseClassesNames.CLIENT_DISLIKES,
				ParseClassesNames.CLIENT_DISLIKES_ID,
				"Remove from Dislikes");
	}


	/**
	 * 
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
	 * receives a business id and check if it's in the user favorites list.
	 */
	public static boolean isInFavourites(String businessId){

		return favourites.contains(businessId);
	}
	
	
	/**
	 * receives a business id and check if it's in the user favorites list.
	 */
	public static boolean isInLikes(String dealId){

		return likes.contains(dealId);
	}
	
	
	/**
	 * receives a business id and check if it's in the user favorites list.
	 */
	public static boolean isInDislikes(String dealId){

		return dislikes.contains(dealId);
	}


	/**
	 * return LIKE/DISLIKE/DONT_CARE according to the user preferences regarding
	 * to the current business deal.
	 * @return
	 */
	public static DealLikeStatus getDealLikeStatus(String businessId){

		if (likes.contains(businessId))
			return DealLikeStatus.LIKE;

		else if(dislikes.contains(businessId))
			return DealLikeStatus.DISLIKE;

		return DealLikeStatus.DONT_CARE;
	}

//	public static void setDontCareToDeal(String dealId){
//		
//		if (isInLikes(dealId)) {
//			removeFromLikes(dealId);
//		}
//		else {
//			removeFromDislikes(dealId);
//		}
//	}
}
