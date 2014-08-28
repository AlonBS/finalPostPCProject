package com.dna.radius.dbhandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.businessmode.OwnerData;
import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.BusinessManager;
import com.dna.radius.datastructures.BusinessMarker;
import com.dna.radius.datastructures.BusinessMarker.BuisnessType;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.example.mapsample.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 * @author dror
 *
 */
public class DBHandler {



	private static LoadDealCommentsTask loadCommentsTask = null;
	private static LoadCloseBusinessesToMapTask loadBusinessesAndMapTask = null;
	private static LoadTopBusinessesRunnable loadTopBusinesses = null;


	public DBHandler(Context context) {
	}

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
	public void loadBusinessListAndMapMarkersAsync(LatLng mapCenter,GoogleMap gMap, BusinessManager bManager,double radius,Context context){
		loadBusinessesAndMapTask = new LoadCloseBusinessesToMapTask(context, gMap, bManager,radius);
		loadBusinessesAndMapTask.execute();

	}

	public void stopLoadBusinessListAndMapMarkersAsync(){
		loadBusinessesAndMapTask.stopTask();
	}

	/**
	 * loads the following data asynchroniously:
	 * 	- deal string.
	 *  - number of likes/dislikes.
	 *  - phone number
	 *  - address
	 *  
	 * updates the relevant views after the data was retrieved.
	 */
	public void loadDealInfoAndBusinessInfoAsync(long businessID ,TextView dealTextView, TextView detailsTextView,Context context){
		//LoadDealStringTask loadTask = new LoadDealStringTask(textView, businessID, context);
		//loadTask.execute();asd
		LoadDealInfoRunnable loadTask = new LoadDealInfoRunnable(dealTextView, detailsTextView, businessID, context);
		new Thread(loadTask){}.start();
	}

	/**
	 * receives a businessID and returns a corresponding BussinessMarker object
	 * with all the relevant information (excluding the business Bitmap, which will
	 * be loaded via loadImageAsync function).
	 */ //TODO - implement this function
	public BusinessMarker getBusinessInfo(long businessID){
		return new BusinessMarker("MCdonalds", BuisnessType.RESTURANT, new LatLng(31.781099, 35.217668),0,new Random().nextInt(99999),new Random().nextInt(99999));
	}


	/**
	 * if the business has a bitmap on parse server, loads it asynchronously and
	 * updates the relevant accordingly.
	 * 
	 * otherwise - does nothing at all :(
	 * 
	 */
	public void loadBusinessImageViewAsync(long businessID ,ImageView imageView, Context context){
		LoadDealBitmapTask loadTask = new LoadDealBitmapTask(imageView, businessID,context);
		context.getClass();
		loadTask.execute();

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
			//TODO - remove from the like list at the parse DB
		}

		//TODO - ALON add a like to the parse

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
	public static void loadOwnerDataSync(OwnerData owner, Context context){
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
		owner.businessID = 0;
		owner.address = "Jaffa street 61, Jerusalem";
		owner.currentDeal = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BAG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!";
		owner.dealHistory = new ArrayList<DealHistoryObject>();
		owner.image = BitmapFactory.decodeResource(context.getResources(), R.drawable.burger);
		owner.numberOfLikes = 23131;
		owner.numberOfDislikes = 524;
		owner.phoneNumber = "0508259193";
		owner.rating = 4;
		owner.hasImage = true; // TODO should be true only if the business has a an image
		
	}
	

	/**
	 * updates the user's like list, dislike list and favorites list
	 */
	public static void loadUesrDataSync(ClientData user){
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
		
		//TODO These strings should be returned from parse
		String favouritesStr = "";
		String likesStr = "";
		String dislikesStr = "";; 
		List<String> favoritesList = Arrays.asList(favouritesStr.split("\\s*,\\s*"));
		List<String> likesList = Arrays.asList(likesStr.split("\\s*,\\s*"));
		List<String> dislikesList = Arrays.asList(dislikesStr.split("\\s*,\\s*"));

		//TODO These values should be returned from parse
		double homeLatitude = 31.78507 ; 
		double homeLongitude = 35.214328;
		user.setHome(new LatLng(homeLatitude, homeLongitude), false);


	}




	/**
	 * receives a commentArrayAdapter and comments list. updates both of the
	 * parameter asynchronously, using parse.
	 */
	public static void loadCommentsListAsync(ArrayList<Comment> comments,CommentsArrayAdapter adapter){
		loadCommentsTask = new LoadDealCommentsTask(comments, adapter);
		loadCommentsTask.execute();
	}



	/**
	 * adds the user comment for the given business deal.
	 * this method should check if the user already commented on this deal before.
	 * if he did - the new comment should replace the previous one.
	 */
	public static void addComment(long businessID, Comment comment){
		//TODO - alon, dbhandling
	}

	/**
	 * return true if the user have a business, and able to switch to business mode.
	 * return false otherwise. 
	 * notice that method shuold be static.
	 * @return
	 */
	public static boolean doesUserHaveBusinessMode(){
		return true;
		//TODO - ALON implement this function.
	}

	/***
	 * loads the top businesses data into a given TopBusinessesHorizontalView.
	 * @param view
	 */
	public void LoadTopBusinessesAsync(TopBusinessesHorizontalView view, Context context){
		loadTopBusinesses = new LoadTopBusinessesRunnable(view, context);
		new Thread(loadTopBusinesses){}.start();
	}
	
	
	public static void changeBusinessImage(Bitmap bmap){
		//TODO - ALON
	}
	
	public static void changeBusinessAddress(String address){
		//TODO - ALON
	}
	public static void changeBusinessPhone(String phoneNumber){
		//TODO - ALON
	}
	public static void changeBusinessName(String name){
		//TODO - ALON
	}
	public static void changeBusinessLocation(LatLng newLocation){
		//TODO - ALON
	}
	
	
	public static void setImage(int businessID, Bitmap image) {
		// TODO ALON
		
	}
	
	public static void setDeal(int businessID, String deal) {
		// TODO ALON
		
	}

}
