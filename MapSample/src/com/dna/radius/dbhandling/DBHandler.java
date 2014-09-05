package com.dna.radius.dbhandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import testing_stuff.LoadDealInfoRunnable;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.MapBusinessManager;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.dna.radius.mapsample.MapWindowFragment;
import com.dna.radius.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 * @author dror
 *
 */
public class DBHandler {

	private static LoadDealCommentsTask loadCommentsTask = null;
	private static LoadCloseBusinessesToMapTask loadBusinessesAndMapTask = null;
	private static LoadTopBusinessesRunnable loadTopBusinesses = null;

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
		if(loadBusinessesAndMapTask!=null){
			loadBusinessesAndMapTask.stopTask();
			loadBusinessesAndMapTask = null;
		}
		if(loadTopBusinesses!=null){
			loadTopBusinesses.stopTask();
			loadTopBusinesses = null;
		}
	}

	/**
	 * set a new home location for the user.
	 */
	public static void setHome(int id, double lat, double lng){
		//TODO - ALON - implement
	}


	/**
	 * adds a business id to the user favorites table.
	 * @param id
	 */
	public static void updateFavourites(int userId, ArrayList<Integer> favouritesList){
		//TODO - alon, implement
	}



	/**
	 * loads all the Business Marker objects which represents businesses
	 * which are close to the current map center.
	 * A business is considered close to the center if it's distance from it
	 * is less than radius.
	 * 
	 */
	public static void loadBusinessListAndMapMarkersAsync(LatLng mapCenter,GoogleMap gMap, MapBusinessManager bManager,double radius,MapWindowFragment fragment){
		loadBusinessesAndMapTask = new LoadCloseBusinessesToMapTask(fragment, gMap, bManager,radius);
		loadBusinessesAndMapTask.execute();

	}

	public static void stopLoadBusinessListAndMapMsarkersAsync(){
		if(loadBusinessesAndMapTask!=null){
			loadBusinessesAndMapTask.stopTask();
		}

	}

	/**
	 * if the business has a bitmap on parse server, loads it asynchronously and
	 * updates the relevant accordingly.
	 * 
	 * otherwise - does nothing at all :(
	 * 
	 */
	public static void loadBusinessImageViewAsync(String businessID ,ImageView imageView, Context context){
		LoadDealBitmapTask loadTask = new LoadDealBitmapTask(imageView, businessID,context);
		context.getClass();
		loadTask.execute();
	}

//	/***this object is used whenever a business marker is pressed on the map,
//	 * or whenever one of the businesses in the top businesses list is pressed.
//	 * in this case - more information is needed regarding to the business, such as:
//	 * phone number, address and deal text. This object holds these data items.
//	 */
//	public static class ExternalBusinessExtraInfo{
//		public String address;
//		public String phone;
//		public String dealStr;
//
//		public ExternalBusinessExtraInfo(String address,String phone,String dealStr ){
//			this.address = address;
//			this.phone = phone;
//			this.dealStr = dealStr;
//		}
//	}

//	/**
//	 * this function is called whenever the ShowDealActivity is turned on.
//	 * in this case, more data is needed, such as - address, phone and deal string.
//	 * this function loads it from parse.
//	 * @param BusinessID
//	 */
//	public static ExternalBusinessExtraInfo getExtraInfoOnExternalBusiness(String BusinessID){
//		//******************************88
//		//TODO - erase the sleeping operation!! its for testing only
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}	
//		//******************************88
//		//TODO - ALON this data should be loaded from parse
//		String dealStr = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BUG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!"; 
//		String phoneStr = "050-8512391";
//		String addressStr = "Jaffa St. Jerusalem";
//
//		return new ExternalBusinessExtraInfo(addressStr, phoneStr, dealStr);
//
//
//	}

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

	/**
	 * receives an owner data objects, load all it's fields from parse except for the comments list,
	 * which will be loaded saperatly.
	 * loads the following fields:
	 * @param user
	 */
	public static void loadOwnerDataSync(BusinessData owner, Context context){
		//TODO  ALON - all these values shuold be received from parse

		//TODO - remove me!!!
		//******************
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*******************


		owner.name = "Mcdonalds";
		owner.businessID = "SAD";
		owner.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.burger);
		owner.numberOfLikes = 23131;
		owner.numberOfDislikes = 524;
		owner.phoneNumber = "0508259193";
		owner.rating = 4;
		owner.hasImage = true;
		owner.hasDeal = true;// TODO should be true only if the business has a an image
		owner.address = "Jaffa street 61, Jerusalem";
		owner.currentDeal = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BAG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!";
		owner.dealHistory = new ArrayList<DealHistoryObject>();
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Eat a megaburger and kill 3 animals for free",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"buy a pack of Supergoal and get a sticker with Kfir Partiely signature. you dont wanna miss that!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"buy a triangle toaster and get another triangle toaster!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy seasonal tickets for Maccabi Petah Tikva and get Free entrance to the first Toto Cup match against Hapoel Raanana",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"New at superfarm! a supporting sports bra which gives you extra support during your period",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy a nokia Phone and get free games! (snake) ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"only 99.99$ for a full Kosher cellular package ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy a set of Tfilin and get a free tour at Kivrey Zadikim ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Eat a megaburger and kill 3 animals for free",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"buy a pack of Supergoal and get a sticker with Kfir Partiely signature. you dont wanna miss that!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"buy a triangle toaster and get another triangle toaster!",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy seasonal tickets for Maccabi Petah Tikva and get Free entrance to the first Toto Cup match against Hapoel Raanana",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"New at superfarm! a supporting sports bra which gives you extra support during your period",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy a nokia Phone and get free games! (snake) ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"only 99.99$ for a full Kosher cellular package ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));
		owner.dealHistory.add(new DealHistoryObject(new Random().nextInt(99999),"Buy a set of Tfilin and get a free tour at Kivrey Zadikim ",new Date(),new Random().nextInt(9999),new Random().nextInt(9999)));





	}


	/**
	 * updates the user's like list, dislike list and favorites list
	 */
	public static void loadClientInfoSync(ParseObject base, ClientData instance){
		//TODO - alon - implement

		//TODO - remove me!!!
		//******************
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//*******************
		
		//instance.setHome(base.getLocation, updateServers);
		
		//base.getJSONObject(arg0)
		
		

		//TODO These strings should be returned from parse
		String favouritesStr = "";
		String likesStr = "";
		String dislikesStr = "";
		List<String> favoritesList = Arrays.asList(favouritesStr.split("\\s*,\\s*"));
		List<String> likesList = Arrays.asList(likesStr.split("\\s*,\\s*"));
		List<String> dislikesList = Arrays.asList(dislikesStr.split("\\s*,\\s*"));

		//TODO These values should be returned from parse
		double homeLatitude = 31.78507 ; 
		double homeLongitude = 35.214328;
		//instance.setHome(new LatLng(homeLatitude, homeLongitude), false);

	}




	/**
	 * receives a commentArrayAdapter and comments list. updates both of the
	 * parameter asynchronously, using parse.
	 */
	public static void loadCommentsListAsync(ArrayList<Comment> comments,CommentsArrayAdapter adapter){
		//TODO ALON - add your implementation to the LoadDealCommentsTask.
		loadCommentsTask = new LoadDealCommentsTask(comments, adapter);
		loadCommentsTask.execute();
	}


	/**
	 * adds the user comment for the given business deal.
	 * this method should check if the user already commented on this deal before.
	 * if he did - the new comment should replace the previous one.
	 */
	public static void addComment(String dealID, Comment comment){
		//TODO - alon, dbhandling
	}


	/***
	 * loads the top businesses data into a given TopBusinessesHorizontalView.
	 * @param view
	 */
	public static void LoadTopBusinessesAsync(TopBusinessesHorizontalView view, Context context){
		loadTopBusinesses = new LoadTopBusinessesRunnable(view, context);
		new Thread(loadTopBusinesses){}.start();
	}


	public static void setImage(String businessID, Bitmap image) {
		// TODO ALON

	}

	/**
	 * changes the business current deal according to the given parameters.
	 * set the numbers of likes correspondly. 
	 */
	public static void setDeal(String businessID, String deal, int numOfLikes, int numOfDislikes) {
		// TODO ALON

	}
	public static void setBusinessName(String businessID, String name) {
		// TODO ALON

	}

	public static void setBusinessPhone(String businessID, String phone) {
		// TODO ALON

	}

	public static void setBusinessAddress(String businessID, String address) {
		// TODO ALON

	}

	public static void setBusinessLocation(String businessID, LatLng location) {
		// TODO ALON
		double latitude = location.latitude;
		double longitude = location.longitude;

	}

	/***
	 * receives a business id and a deal, deletes the deal from the business's history list.
	 */
	public static void deletedDealFromHistory(String businessID,DealHistoryObject deal){
		
	}
	
	/***
	 * receives a business id and a deal, adds the deal to the business's history list.
	 */
	public static void addDealToHistory(String businessID,String deal,int numOfLikes,int numOfDislikes){

	}

}
