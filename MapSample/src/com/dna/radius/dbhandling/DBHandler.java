package com.dna.radius.dbhandling;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.dna.radius.mapsample.MapBusinessManager;
import com.dna.radius.mapsample.MapWindowFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 * @author dror
 *
 */
public class DBHandler {

	private static LoadDealCommentsTask loadCommentsTask = null;
	//	private static LoadCloseBusinessesToMapTask loadBusinessesAndMapTask = null;
	//	private static LoadTopBusinessesRunnable loadTopBusinesses = null; TODO REMOVE

	/**
	 * closes the DBHandler.
	 * don't forget to call this method whenever the activity which created
	 * the dbHandler object gets destroyed.
	 */
	public static void close(){
		if(loadCommentsTask!=null){
			loadCommentsTask.stopTask();
			loadCommentsTask = null;
		}
		//		if(loadBusinessesAndMapTask!=null){
		//			loadBusinessesAndMapTask.stopTask();
		//			loadBusinessesAndMapTask = null;
		//		}
		//		if(loadTopBusinesses!=null){
		//			loadTopBusinesses.stopTask();
		//			loadTopBusinesses = null;
		//		}
	}
	//
	//	/**
	//	 * set a new home location for the user.
	//	 */
	//	public static void setHome(int id, double lat, double lng){
	//		//TODO - ALON - implement
	//	}
	//

	//	/**
	//	 * adds a business id to the user favorites table.
	//	 * @param id
	//	 */
	//	public static void updateFavourites(int userId, ArrayList<Integer> favouritesList){
	//		//TODO - alon, implement
	//	}
	//


	/**
	 * 
	 * @param dealId
	 * @param removalNeeded - true if one needs to decrement this dealId number of dislikes
	 * 							(This is done so the parseQuery wouldn't run twice)
	 */
	public static void addLikeExternally(String dealId, boolean deleteNeeded) {

		addLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES,
				deleteNeeded, ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES);
	}



	/**
	 * 
	 * @param dealId
	 * @param removalNeeded - true if one needs to decrement this dealId number of likes
	 * 							(This is done so the parseQuery wouldn't run twice)
	 */
	public static void addDislikeExternally(String dealId, boolean deleteNeeded) {

		addLikes_N_Dislikes(dealId, ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES,
				deleteNeeded, ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES);
	}

	/**
	 * 
	 * @param dealId
	 * @param n1 - Should be - ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES / _DISLIKES
	 * @param deleteNeeded - see above
	 * @param n2 - if deleteNeeded is true, should be in contra to n1 (i.e. likes vs. dislikes etc.)
	 */
	private static void addLikes_N_Dislikes(String dealId, final String n1, final boolean deleteNeeded, final String n2) {

		String businessId = dealId.split(BaseActivity.SEPERATOR)[1];
		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);

		query.getInBackground(businessId, new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject object, ParseException e) {

				if (e == null) {

					JSONObject businessCurrentDealJO = object.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
					try {

						businessCurrentDealJO.put(n1, businessCurrentDealJO.getInt(n1) + 1);

						if (deleteNeeded)
							businessCurrentDealJO.put(n2, businessCurrentDealJO.getInt(n2) -1 );

						object.put(ParseClassesNames.BUSINESS_CURRENT_DEAL, businessCurrentDealJO);
						object.saveInBackground();
					}

					catch (JSONException exc) {
						Log.e("External - add like to deal", exc.getMessage());
					}
				}
				else {
					Log.e("External Like", e.getMessage());
				}
			}
		});
	}




	//TODO 
	public static void getExternalBusinessAtRadius(LatLng location, double radius) {


		//TODO remove
		//		double top, buttom, left, right;
		//		top = location.latitude + radius;
		//		buttom = location.latitude - radius;
		//		right = location.longitude + radius;
		//		left = location.longitude - radius;


		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);

		//TODO remove
		//		query.whereWithinRadians(ParseClassesNames.BUSINESS_LOCATION, new ParseGeoPoint(location.latitude, location.longitude), radius);
		//
		//		query.whereLessThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
		//				ParseClassesNames.BUSINESS_LOCATION_LAT, top);
		//		query.whereGreaterThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
		//				ParseClassesNames.BUSINESS_LOCATION_LAT, buttom);
		//		query.whereLessThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
		//				ParseClassesNames.BUSINESS_LOCATION_LONG, right);
		//		query.whereGreaterThanOrEqualTo(ParseClassesNames.BUSINESS_LOCATION + "." +
		//				ParseClassesNames.BUSINESS_LOCATION_LONG, left);



		query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> objects, ParseException e) {
				if (e == null) {

					List<ExternalBusiness>result = new ArrayList<ExternalBusiness>();
					//					boolean result = new ArrayList<ExternalBusiness>();

					for (ParseObject o : objects) {



						ExternalBusiness newExtern;
						boolean isRelevant = MapBusinessManager.addExternalBusiness(null);
						if(!isRelevant){
							break;
						}

						Log.d("test", o.getObjectId());
					}

				} else {

				}
			}
		});


	}


















	//
	//
	//
	//	/**
	//	 * loads all the Business Marker objects which represents businesses
	//	 * which are close to the current map center.
	//	 * A business is considered close to the center if it's distance from it
	//	 * is less than radius.
	//	 * 
	//	 */
	//	public static void loadBusinessListAndMapMarkersAsync(LatLng mapCenter,GoogleMap gMap, MapBusinessManager bManager,double radius,MapWindowFragment fragment){
	//
	////		loadBusinessesAndMapTask = new LoadCloseBusinessesToMapTask(fragment, gMap, bManager,radius);
	//		//		loadBusinessesAndMapTask.execute();
	//
	//	}
	//
	//	public static void stopLoadBusinessListAndMapMsarkersAsync(){
	////		if(loadBusinessesAndMapTask!=null){
	////			loadBusinessesAndMapTask.stopTask();
	////		}
	//
	//	}

	/**
	 * if the business has a bitmap on parse server, loads it asynchronously and
	 * updates the relevant accordingly.
	 * 
	 * otherwise - does nothing at all :(
	 * 
	 */
	public static void loadBusinessImageViewAsync(String businessID ,ImageView imageView, Context context){
		//		LoadDealBitmapTask loadTask = new LoadDealBitmapTask(imageView, businessID,context);
		//		context.getClass();
		//		loadTask.execute();
		imageView.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.image_demo));
	}



	public enum DealLikeStatus{LIKE,DISLIKE,DONT_CARE};
	/**
	 * updates the parse servers that the user like the 
	 * current business deal.
	 * this method should do 2 things:
	 * 	add the dealID from the user likes table
	 * 	add a like to the current business deal Table.
	 */

	public static void setLikeToDeal(int userId, int businessId, DealLikeStatus oldStatus){
		if(oldStatus==DealLikeStatus.LIKE){
			return;
		}
		if(oldStatus==DealLikeStatus.DISLIKE){
			//TODO - remove from the dislike list at the parse DB
		}

		//TODO - ALON add a like to the parse
	}

	/**
	 * updates the parse servers that the user doesnt like the 
	 * current business deal.
	 * this method shuold do 2 things:
	 * 	add the dealID from the user dislikes table
	 * 	add a dislike to the current business deal Table.
	 */
	public static void setDislikeToDeal(int userID, int businessId, DealLikeStatus oldStatus){
		if(oldStatus==DealLikeStatus.DISLIKE){
			return;
		}
		if(oldStatus==DealLikeStatus.LIKE){
			//TODO - remove from the business like list at the parse DB
		}

		//TODO - ALON add a like option to the parse

	}

	/**
	 * updates the parse servers that the user doesn't like/dislike the 
	 * current business deal anymore.
	 * this method shuold do 2 things:
	 * 	erase the dealID from the user likes table
	 * 	delete a like from the current business deal Table.
	 */
	public static void setDontCareToDeal(int userID, int businessId, DealLikeStatus oldStatus){
		if(oldStatus==DealLikeStatus.LIKE){
			//TODO - remove from the like list at the parse DB
		}else if(oldStatus==DealLikeStatus.DISLIKE){
			//TODO - remove from the dislike list at the parse DB
		}else{
			Log.d("DBHandler", "Business " + Long.toString(businessId) + " error: the user didnt like it nor dislike it");
		}

	}



	//	/**
	//	 * updates the user's like list, dislike list and favorites list
	//	 */
	//	public static void loadClientInfoSync(ParseObject base, ClientData instance){
	//		//TODO - alon - implement
	//
	//		//TODO - remove me!!!
	//		//******************
	//		try {
	//			Thread.sleep(1000);
	//		} catch (InterruptedException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		}
	//		//*******************
	//		
	//		//instance.setHome(base.getLocation, updateServers);
	//		
	//		//base.getJSONObject(arg0)
	//		
	//		
	//
	//		//TODO These strings should be returned from parse
	//		String favouritesStr = "";
	//		String likesStr = "";
	//		String dislikesStr = "";
	//		List<String> favoritesList = Arrays.asList(favouritesStr.split("\\s*,\\s*"));
	//		List<String> likesList = Arrays.asList(likesStr.split("\\s*,\\s*"));
	//		List<String> dislikesList = Arrays.asList(dislikesStr.split("\\s*,\\s*"));
	//
	//		//TODO These values should be returned from parse
	//		double homeLatitude = 31.78507 ; 
	//		double homeLongitude = 35.214328;
	//		//instance.setHome(new LatLng(homeLatitude, homeLongitude), false);
	//
	//	}




	/**
	 * receives a commentArrayAdapter and comments list. updates both of the
	 * parameter asynchronously, using parse.
	 */
	public static void loadCommentsListAsync(CommentsArrayAdapter adapter){

		//TODO
		adapter.add(new Comment("yosi", "zevel", new Date()));

		//TODO ALON - add your implementation to the LoadDealCommentsTask.
		//		loadCommentsTask = new LoadDealCommentsTask(comments, adapter);
		//		loadCommentsTask.execute();
	}


	/**
	 * adds the user comment for the given business deal.
	 * this method should check if the user already commented on this deal before.
	 * if he did - the new comment should replace the previous one.
	 */
	//public static void addComment(String dealID, Comment comment){
	//TODO - alon, dbhandling
	//}


	//	/***
	//	 * loads the top businesses data into a given TopBusinessesHorizontalView.
	//	 * @param view
	//	 */
	//	public static void LoadTopBusinessesAsync(TopBusinessesHorizontalView view, Context context){
	//		loadTopBusinesses = new LoadTopBusinessesRunnable(view, context);
	//		new Thread(loadTopBusinesses){}.start();
	//	}

	public static List<ExternalBusiness> LoadTopBusinessesSync(ParseGeoPoint gp, double radius) {

		//TODO simulate asynchronous way

		final List<ExternalBusiness> result = new ArrayList<ExternalBusiness>();

		ParseQuery<ParseObject> query = ParseQuery.getQuery(ParseClassesNames.BUSINESS_CLASS);
		query.whereWithinRadians(ParseClassesNames.BUSINESS_LOCATION, gp, radius );

		try {

			if (query.count() < TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) {

				radius += 0.1; //TODO change ammount
				if (query.count() < TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) {
					radius += 0.1;
				}

			}
		} catch (ParseException e1) {
			Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
		}

		query.addDescendingOrder(ParseClassesNames.BUSINESS_RATING);

		try {
			List<ParseObject> objects = query.find();

			for (ParseObject o : objects) {

				try {
					JSONObject j = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
					Deal externBusinessDeal = new Deal(
							j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
							j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
							j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
							j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
							new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE))); //TODO getDAte()

					ExternalBusiness newExtern = new ExternalBusiness(
							o.getObjectId(),
							o.getString(ParseClassesNames.BUSINESS_NAME),
							SupportedTypes.BusinessType.stringToType(o.getString(ParseClassesNames.BUSINESS_TYPE)),
							o.getDouble(ParseClassesNames.BUSINESS_RATING),
							o.getParseGeoPoint(ParseClassesNames.BUSINESS_RATING),
							o.getString(ParseClassesNames.BUSINESS_ADDRESS),
							o.getString(ParseClassesNames.BUSINESS_PHONE),
							externBusinessDeal); 


					result.add(newExtern);
					if (result.size() >= TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) break;


				} catch (JSONException | java.text.ParseException e1) {

					Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
				} 
			}
		} catch (ParseException e) {
			Log.e("DBHandler - LoadTopBusinessesSync", e.getMessage());
		}

		//THIS is the correct version
		//		query.findInBackground(new FindCallback<ParseObject>() {
		//			public void done(List<ParseObject> objects, ParseException e) {
		//
		//				if (e == null) {
		//
		//					for (ParseObject o : objects) {
		//
		//						try {
		//							JSONObject j = o.getJSONObject(ParseClassesNames.BUSINESS_CURRENT_DEAL);
		//							Deal externBusinessDeal = new Deal(
		//									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_ID),
		//									j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_CONTENT),
		//									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_LIKES),
		//									j.getInt(ParseClassesNames.BUSINESS_CURRENT_DEAL_DISLIKES),
		//									new SimpleDateFormat(BaseActivity.DATE_FORMAT).parse(j.getString(ParseClassesNames.BUSINESS_CURRENT_DEAL_DATE))); //TODO getDAte()
		//
		//
		//							ExternalBusiness newExtern = new ExternalBusiness(
		//									o.getObjectId(),
		//									o.getString(ParseClassesNames.BUSINESS_NAME),
		//									SupportedTypes.BusinessType.stringToType(o.getString(ParseClassesNames.BUSINESS_TYPE)),
		//									o.getDouble(ParseClassesNames.BUSINESS_RATING),
		//									o.getParseGeoPoint(ParseClassesNames.BUSINESS_RATING),
		//									o.getString(ParseClassesNames.BUSINESS_ADDRESS),
		//									o.getString(ParseClassesNames.BUSINESS_PHONE),
		//									externBusinessDeal); 
		//							
		//							
		//							result.add(newExtern);
		//							if (result.size() >= TopBusinessesHorizontalView.MAX_TOP_BUSINESSES) break;
		//							
		//
		//						} catch (JSONException | java.text.ParseException e1) {
		//							
		//							Log.e("DBHandler - LoadTopBusinessesSync", e1.getMessage());
		//						} 
		//
		//
		//						//TODO not needed - but add simulation mechanism
		//						boolean isRelevant = MapBusinessManager.addExternalBusiness(null);
		//						if(!isRelevant){
		//							break;
		//						}
		//
		//						Log.d("test", o.getObjectId());
		//					}
		//
		//				} else {
		//					
		//					Log.e("DBHandler - LoadTopBusinessesSync", e.getMessage());
		//				}
		//			}
		//		});

		return result;
	}




	//	public static void setImage(String businessID, Bitmap image) {
	//		// TODO ALON
	//
	//	}

	//	/**
	//	 * changes the business current deal according to the given parameters.
	//	 * set the numbers of likes correspondly. 
	//	 */
	//	public static void setDeal(String businessID, String deal, int numOfLikes, int numOfDislikes) {
	//		// TODO ALON
	//
	//	}
	//	public static void setBusinessName(String businessID, String name) {
	//		// TODO ALON
	//
	//	}

	//	public static void setBusinessPhone(String businessID, String phone) {
	//		// TODO ALON
	//
	//	}
	//
	//	public static void setBusinessAddress(String businessID, String address) {
	//		// TODO ALON
	//
	//	}
	//
	//	public static void setBusinessLocation(String businessID, LatLng location) {
	//		// TODO ALON
	//		double latitude = location.latitude;
	//		double longitude = location.longitude;
	//
	//	}

	/***
	 * receives a business id and a deal, deletes the deal from the business's history list.
	 */
	public static void deletedDealFromHistory(String businessID,Deal deal){

	}

	/***
	 * receives a business id and a deal, adds the deal to the business's history list.
	 */
	public static void addDealToHistory(String businessID,String deal,int numOfLikes,int numOfDislikes){

	}

	//	static{
	//		setDBs_debug();
	//	}
	//
	//	//TODO - the markersDB object and the  loadDBs_debug are only for debug, delete them!
	//	public static List<ExternalBusiness> markersDB; //TODO: delete
	//	public static void setDBs_debug()
	//	{
	//		int id = 0;
	//		markersDB = new ArrayList<ExternalBusiness>();
	//		Random r = new Random();
	//		String currentDeal = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BUG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!";
	//		markersDB.add(new ExternalBusiness("MCdonalds", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.781099, 35.217668),Integer.toString(1),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Ivo", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.779949, 35.218948),Integer.toString(2),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Dolfin Yam", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.779968, 35.221209),Integer.toString(3),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Birman", SupportedTypes.BusinessType.PUB, new LatLng(31.781855, 35.218086),Integer.toString(4),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Bullinat", SupportedTypes.BusinessType.PUB, new LatLng(31.781984, 35.218221),Integer.toString(5),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Hamarush", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.781823, 35.219065),Integer.toString(6),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Adom", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.781334, 35.220703),Integer.toString(7),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Tel Aviv Bar", SupportedTypes.BusinessType.PUB, new LatLng(31.781455, 35.220525),Integer.toString(8),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Jabutinski Bar", SupportedTypes.BusinessType.PUB, new LatLng(31.779654, 35.221654),Integer.toString(9),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Reva Sheva", SupportedTypes.BusinessType.GROCERIES, new LatLng(31.779793, 35.219728),Integer.toString(10),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("The one with the shirts", SupportedTypes.BusinessType.GROCERIES, new LatLng(31.779293, 35.221624),Integer.toString(11),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Hamashbir Latsarchan", SupportedTypes.BusinessType.GROCERIES, new LatLng(31.781824, 35.219959),Integer.toString(12),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Hataklit", SupportedTypes.BusinessType.PUB, new LatLng(31.781905, 35.221372),Integer.toString(13),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Hatav Hashmini", SupportedTypes.BusinessType.GROCERIES, new LatLng(31.781191, 35.219621),Integer.toString(14),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//		markersDB.add(new ExternalBusiness("Katsefet", SupportedTypes.BusinessType.RESTAURANT, new LatLng(31.779921, 35.187777),Integer.toString(15),new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)),"052525621","Jaffa street, Jerusalem", currentDeal));id++;
	//	}

}
