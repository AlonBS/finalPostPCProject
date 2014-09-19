package com.dna.radius.businessmode;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.DealHistoryManager;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;
import com.dna.radius.mapsample.MapWindowFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FunctionCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseUser;
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

	static Deal currentDeal;

	static boolean hasImage;
	static Bitmap businessImage = null; // the image of the business (if exists)


	private static ArrayList<String> favourites = new ArrayList<String>();
	static DealHistoryManager dealsHistory;
	static List<ExternalBusiness> topBusinesses;

	static final float DEFAULT_RATING = 0f;

	/***************************************************************************/


	public static String getUserName() { return currentUser.getUsername(); }


	public static String getBusinessID(){
		return businessInfo.getObjectId();
	}

	public static void setUserName(String newUserName){
		//TODO alon - is this enough? + saveEventually() get stuck.
		currentUser.setUsername(newUserName);
		//currentUser.saveInBackground(); 
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


	public static String getAddress(){ return businessAddress; }

	public static void setAddress(String newAddress) {

		businessAddress = newAddress;
		businessInfo.put(ParseClassesNames.BUSINESS_ADDRESS, businessAddress);
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}


	public static String getPhoneNumber() { return businessPhoneNumber; }

	public static void setPhoneNumber(String newPhoneNumber) {

		businessPhoneNumber = newPhoneNumber;
		businessInfo.put(ParseClassesNames.BUSINESS_PHONE, businessPhoneNumber);
		businessInfo.saveInBackground(); //TODO should be saveEvantually()
	}


	public static LatLng getLocation() { return businessLocation; }

	public static void setLocation (LatLng newLocation) {

		businessLocation = newLocation;

		ParseGeoPoint gp = new ParseGeoPoint(newLocation.latitude, newLocation.longitude);
		businessInfo.put(ParseClassesNames.BUSINESS_LOCATION, gp);
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

		loadTopBusiness();

		loadPreferrings();

	}


	private static void loadRawData() {

		businessName = businessInfo.getString(ParseClassesNames.BUSINESS_NAME);
		businessType = SupportedTypes.BusinessType.stringToType(businessInfo.getString(ParseClassesNames.BUSINESS_TYPE));
		businessAddress = businessInfo.getString(ParseClassesNames.BUSINESS_ADDRESS);
		businessPhoneNumber = businessInfo.getString(ParseClassesNames.BUSINESS_PHONE);
		loadRating();
	}

	private static void loadRating() {

		businessRating = businessInfo.getDouble(ParseClassesNames.BUSINESS_RATING); // range should be: [0, 5]
	}


	private static void loadLocation() {

		ParseGeoPoint gp = businessInfo.getParseGeoPoint(ParseClassesNames.BUSINESS_LOCATION);
		businessLocation = new LatLng(gp.getLatitude(), gp.getLongitude());
	}


	private static void loadCurrentDeal() {

		try {
			List<ParseObject> objectsToFetch = new ArrayList<ParseObject>();
			objectsToFetch.add(businessInfo);
			ParseObject.fetchAll(objectsToFetch);

		} catch (ParseException e1) {

			Log.e("Business - loadCurrentDeal", "fetch failed:" + e1.getMessage());
		}


		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

		if ( jo.isNull(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID) )
			return;

		try { 

			ArrayList<Comment> currentDealComments = new ArrayList<Comment>();
			JSONArray ja = jo.getJSONArray(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS);

			for (int i = 0 ; i < ja.length() && i < 50 ; ++i) { // TODO we support 50 comments

				JSONObject commentJO = ja.getJSONObject(i);

				Comment c = new Comment(
						commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_AUTHOR),
						commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_CONTENT),
						new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(commentJO.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_COMMENTS_DATE)));

				currentDealComments.add(c);
			}


			currentDeal = new Deal(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
					jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
					jo.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
					jo.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
					new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT)
			.parse(jo.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)),
			currentDealComments);

		} catch (JSONException e) {

			Log.e("Business - load current deal", e.getMessage());

		} catch (java.text.ParseException e) {

			Log.e("Business - load current deal", e.getMessage());
		}
	}


	private static void loadDealsHistory() {

		int totalLikes = 0, totalDislikes = 0, totalDeals = 0;
		ArrayList<Deal> oldDeals = new ArrayList<Deal>();

		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
		try {

			totalLikes = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES);
			totalDislikes = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES);
			totalDeals = jo.getInt(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS);

			JSONArray ja = jo.getJSONArray(ParseClassesNames.BUSINESS_HISTORY_DEALS);
			int len = ja.length();

			for (int i = 0 ; i < len ; ++i) {
				
				try {
					
					JSONObject temp = ja.getJSONObject(i);
					oldDeals.add( new Deal(
							temp.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
							temp.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
							temp.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
							temp.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
							new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT).parse(temp.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE)),
							null));
					
				}
				catch (JSONException e) {
					Log.e("Business - load old deal", e.getMessage());
				}

			}
		}
		catch (JSONException e) {
			
			Log.e("Business - history create", e.getMessage());
		}

		catch (java.text.ParseException e) {

			Log.e("Business - load old deals", e.getMessage());
		}
		finally {
			
			dealsHistory = new DealHistoryManager(totalLikes, totalDislikes, totalDeals, oldDeals);
		}
	}

	// This should be called after(!) registration and load
	private static void loadTopBusiness() {

		if (businessLocation != null) {

			topBusinesses = DBHandler.LoadTopBusinessesSync(
					new ParseGeoPoint(businessLocation.latitude, businessLocation.longitude),
					MapWindowFragment.LOAD_RADIUS) ;

		}
		else {
			//TODO - show message to user
			Log.e("Business - loadTopBusiness", "Business Location was not defined when trying to extract top business");
		}
	}


	public static boolean hasImage() {

		if (businessInfo == null) return false;

		ParseFile file = businessInfo.getParseFile(ParseClassesNames.BUSINESS_IMAGE);

		if (file == null) return false;

		return true;
	}


	static void loadImage(final Context context, final ImageView imageView) {

		if (businessInfo == null){
			Log.e("Business - loadImage", "businessInfo was null");
			return;
		}

		ParseFile file = businessInfo.getParseFile(ParseClassesNames.BUSINESS_IMAGE);

		if (file == null) {
			hasImage = false;
			return;
		}

		hasImage = true;

		final WeakReference<ImageView> imageViewWR = new WeakReference<ImageView>(imageView);

		file.getDataInBackground( new GetDataCallback() {

			public void done(byte[] data, ParseException e) {

				if (e == null) {

					businessImage = BitmapFactory.decodeByteArray(data, 0 ,data.length);

					if (imageViewWR == null ||imageViewWR.get() == null) return;

					if(businessImage!=null){
						Log.d("BusinessData", "bitmap was return. size: " + businessImage.getHeight() + "," + businessImage.getWidth());
						imageViewWR.get().setImageBitmap(businessImage);
					}
					else{
						imageViewWR.get().setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.error_loading_image_version_2));
						Toast.makeText(context, context.getResources().getString(R.string.image_wasnt_loaded), Toast.LENGTH_LONG).show();
					}
				}
				else {
					Log.e("DB - loadImageBusinessIdentified", e.getMessage());
				}
			}
		});		
	}


	static void setImage(Bitmap bMap, byte[] imageData) {

		//TODO Remove old picture reference

		if (imageData == null) return;

		businessImage = bMap;
		hasImage = true;
		//
		//		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//		businessImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		//		byte[] data = stream.toByteArray();


		final ParseFile file = new ParseFile(imageData);
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



	public static void createNewDeal(String content) {
		
		int serialNumber;
		if (dealsHistory == null) {
			Log.e("Business - create new Deal", "dealsHistory was null");
			serialNumber = 0;
		}
		else {
			serialNumber = dealsHistory.getTotalNumOfDeals();
		}

		// get old deal (as JSON object)
		JSONObject oldDealJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);

		String id = businessInfo.getObjectId() + BusinessOpeningScreenActivity.SEPERATOR + Integer.toString(serialNumber);
		Date date = new Date();

		// update locally
		Deal newDeal = new Deal(id, content, 0, 0, date, new ArrayList<Comment>());

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
		businessInfo.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException arg0) {
				updateRating();
			}
		});
	}


	private static void updateRating() {

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("businessId", businessInfo.getObjectId());

		ParseCloud.callFunctionInBackground("calculateRating", params, new FunctionCallback<Double>() {

			public void done(Double newRating, ParseException e) {
				if (e == null) {
					businessRating = newRating;
					Log.d("New Rating was:", Double.toString(newRating));
					return;
				}

				Log.e("Business - updateRating", e.getMessage());
			}
		});
	}



	/**
	 * This does NOT move old deal to history
	 */
	public static void deleteCurrentDeal(){

		dealsHistory.incTotalNumOfLikes(currentDeal.getNumOfLikes());
		dealsHistory.incTotalNumOfDisLikes(currentDeal.getNumOfDislikes());

		// history update - on Parse.com
		JSONObject oldDealsJO = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
		try {
			oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_LIKES, dealsHistory.getNumOfLikes());
			oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_DISLIKES, dealsHistory.getNumOfDislikes());
			oldDealsJO.put(ParseClassesNames.BUSINESS_HISTORY_TOTAL_NUM_OF_DEALS, dealsHistory.getTotalNumOfDeals());

		} catch (JSONException e) {
			Log.e("Business - deal delete", e.getMessage());
		}

		businessInfo.put(ParseClassesNames.BUSINESS_HISTORY, oldDealsJO);
		businessInfo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, new JSONObject());

		dealsHistory.incTotalNumOfDeals();
		currentDeal = null;

		//TODO should be saveEventually()
		businessInfo.saveInBackground(null);
	}



	public static void bringDealFromHistory(Deal deal) {

		deletedDealFromHistory(deal);
		createNewDeal(deal.getDealContent());
	}


	/***
	 * receives a business id and a deal, deletes the deal from the business's history list.
	 */
	public static void deletedDealFromHistory(Deal deal) {

		if (!dealsHistory.hasDeal(deal)) return;

		dealsHistory.deleteOldDeal(deal);

		try {

			JSONArray ja = new JSONArray();
			for (Deal d : dealsHistory.getOldDeals()) {

				JSONObject jo = new JSONObject();
				jo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID, d.getId());
				jo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT, d.getDealContent());
				jo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES, d.getNumOfLikes());
				jo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES, d.getNumOfDislikes());
				jo.put(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE, new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT).format(d.getDealDate()));
				//TODO currently - we don't support old deals comments.

				ja.put(jo);
			}


			JSONObject historyJo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_HISTORY);
			historyJo.put(ParseClassesNames.BUSINESS_HISTORY_DEALS, ja);
			businessInfo.put(ParseClassesNames.BUSINESS_HISTORY, historyJo);
			businessInfo.saveInBackground();

		}
		catch (JSONException e1) {

			Log.e("deletedDealFromHistory", e1.getMessage());
		}
	}



	private static void loadPreferrings() {


		JSONObject jo = businessInfo.getJSONObject(ParseClassesNames.BUSINESS_PREFERRING);

		try {
			loadFavorites(jo.getJSONArray(ParseClassesNames.BUSINESS_PREFERRING_FAVORITES));

		} catch (JSONException e) {
			Log.e("Business - getting Array of preferences", e.getMessage());
		}
	}


	private static void loadFavorites(JSONArray ar) {

		int length = ar.length();
		for (int i = 0 ; i < length ; ++i) {

			try {
				favourites.add(ar.getJSONObject(i).getString(ParseClassesNames.BUSINESS_PREFERRING_FAVORITES_ID));
			} catch (JSONException e) {
				Log.e("Business - Add Favorites", e.getMessage());
			}
		}
	}


	/**
	 * 
	 * @param businessId
	 */
	public static void addToFavourites(String businessId) {

		addToStorage(favourites, businessId,
				ParseClassesNames.BUSINESS_PREFERRING,
				ParseClassesNames.BUSINESS_PREFERRING_FAVORITES,
				ParseClassesNames.BUSINESS_PREFERRING_FAVORITES_ID,
				"Business - Add to Favorites");
	}


	private static void addToStorage(ArrayList<String> ds, String itemId,
			String n1, String n2, String n3, String errMsg){

		if (!ds.contains(itemId)) {

			ds.add(itemId);
			JSONObject newItem = new JSONObject();

			try {

				newItem.put(n3, itemId);
				businessInfo.getJSONObject(n1).getJSONArray(n2).put(newItem);

			} catch (JSONException e) {

				Log.e(errMsg, e.getMessage());
			}

			businessInfo.saveEventually(); //TODO should be saveEventaully?
		}
		else {

			Log.e(errMsg, "Item was added twice");
		}
	}


	/**
	 * receives a business id and check if it's in the user favorites list.
	 */
	public static boolean isInFavourites(String businessId){

		return favourites.contains(businessId);
	}



	public static void removeFromFavorites(String businessId) {

		removeFromStorage(favourites, businessId,
				ParseClassesNames.BUSINESS_PREFERRING,
				ParseClassesNames.BUSINESS_PREFERRING_FAVORITES,
				ParseClassesNames.BUSINESS_PREFERRING_FAVORITES_ID,
				"Business -Remove from Favorites");
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

				businessInfo.getJSONObject(n1).put(n2, newArr);
				businessInfo.saveEventually();

			} catch (JSONException e) {
				Log.e(errMsg, e.getMessage());
			}

		}else{

			Log.e(errMsg,"Item wasn't in db to remove");
		}
	}




	static void refreshDB() {

		loadCurrentDeal();

		loadRating();

		topBusinesses = DBHandler.LoadTopBusinessesSync(
				new ParseGeoPoint(businessLocation.latitude,
						businessLocation.longitude),
						MapWindowFragment.LOAD_RADIUS);

	}

}
