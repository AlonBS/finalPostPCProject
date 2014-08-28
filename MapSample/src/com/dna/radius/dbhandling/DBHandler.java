package com.dna.radius.dbhandling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.BusinessManager;
import com.dna.radius.datastructures.BusinessMarker;
import com.dna.radius.datastructures.BusinessMarker.BuisnessType;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 * @author dror
 *
 */
public class DBHandler {

	//TODO - ALON. the user_id and userName should be loaded from the DB.
	public static long user_id = 123123;
	public static String userName = "dror the king";

	/**these objects are used to access the local DB. we may not need it in the future.*/
	private LocalDBHelper localDBHelper;
	SQLiteDatabase localDB;

	/**holds the context of the activity which created the dbHandler.*/
	Context context;


	private LoadDealCommentsTask loadCommentsTask;
	private LoadCloseBusinessesToMapTask loadBusinessesAndMapTask;
	private LoadTopBusinessesRunnable loadTopBusinesses = null;




	/***
	 * this method inits all the relevant data for the user.
	 * currently it just loads the lists of use likes and dislikes.
	 * in the future we can load the favorites list from here as well.
	 * 
	 * this method should be called once, after the first dbHandler object was created.
	 * right now it is called from the DBHandler constructor.
	 * 
	 */
	public static void initUserData(){

		//TODO ALON - temporary values, should be loaded differently.
		user_id = 123123;
		userName = "dror the king";

	}


	public DBHandler(){}//TODO - context!!!

	static int numOfObjects = 0;
	public DBHandler(Context context) {
		if(numOfObjects++==0){
			initUserData();
		}
		// TODO Auto-generated constructor stub
		localDBHelper = new LocalDBHelper(context);
		localDB = localDBHelper.getWritableDatabase();
		this.context = context;
	}

	/**
	 * closes the DBHandler.
	 * don't forget to call this method whenever the activity which created
	 * the dbHandler object gets destroyed.
	 */
	public void close(){
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
		localDBHelper.close();
		localDB.close();
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
	public static void addToFavourites(int userID,int businessID){
		//TODO - alon, implement
	}

	/**
	 * remove a business id from the user favorites table.
	 * @param id
	 */
	public static void removeFromFavourites(int userID,int businessID){
		//TODO - alon, implement

	}




	/**
	 * loads all the Business Marker objects which represents businesses
	 * which are close to the current map center.
	 * A business is considered close to the center if it's distance from it
	 * is less than radius.
	 * 
	 */
	public void loadBusinessListAndMapMarkersAsync(LatLng mapCenter,GoogleMap gMap, BusinessManager bManager,double radius){

		loadBusinessesAndMapTask = new LoadCloseBusinessesToMapTask(context, gMap, bManager,radius);
		loadBusinessesAndMapTask.execute();

	}

	public void stopLoadBusinessListAndMapMarkersAsync(){
		loadBusinessesAndMapTask.stopTask();
	}

	/**
	 * loads a deal's Bitmap asynchronously. updates the relevant textView whenever the task was ended.
	 */
	public void loadDealAsync(long businessID ,TextView dealTextView, TextView detailsTextView){
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
		return new BusinessMarker("MCdonalds", BuisnessType.RESTURANT, new LatLng(31.781099, 35.217668), "Jerusalem",0,new Random().nextInt(99999),new Random().nextInt(99999));
	}


	/**a flag for loadBusinessImageViewAsync function*/
	static public final int ALTERNATIVE_IMAGE_FALSE = -1;

	/**
	 * if the business has a bitmap on parse server, loads it asynchronously and
	 * updates the relevant accordingly.
	 * 
	 * if business doesn't have an image and the alternative imageImage isn't ALTERNATIVE_IMAGE_FALSE - load
	 * the bitmap with the alternative image id.
	 * 
	 * otherwise - does nothing at all :(
	 * 
	 */
	public void loadBusinessImageViewAsync(long businessID ,ImageView imageView, int alternativeImageId){
		LoadDealBitmapTask loadTask = new LoadDealBitmapTask(imageView, businessID,context,alternativeImageId);
		context.getClass();
		loadTask.execute();

	}

	/**
	 * loads a deal's Bitmap asynchronously. updates the relevant ImageView whenever the task was ended.
	 */
	public void ChangeDealAndLoadToTextView(long businessID ,TextView textView,String newDealStr){
		//TODO - write code for changing the business deal.
		ChangeDealUpdateViewRunnable loadTask = new ChangeDealUpdateViewRunnable(textView,businessID,newDealStr);
		new Thread(loadTask){}.start();

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
	 * updates the user's like list, dislike list and favorites list
	 */
	public static void loadUesrDataSync(ClientData user){
		//TODO - alon - implement

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
	public void loadCommentsListAsync(ArrayList<Comment> comments,CommentsArrayAdapter adapter){
		loadCommentsTask = new LoadDealCommentsTask(comments, adapter);
		loadCommentsTask.execute();
	}



	/**
	 * adds the user comment for the given business deal.
	 * this method should check if the user already commented on this deal before.
	 * if he did - the new comment should replace the previous one.
	 */
	public void addComment(long businessID, Comment comment){
		boolean didUserCommentedBefore = true; //TODO - temporary value
		if(didUserCommentedBefore){
			Toast.makeText(context, "you already commented on this deal. your new comment will replace the old one", Toast.LENGTH_LONG).show();				
		}else{
			Toast.makeText(context, "Thank you for your comment!", Toast.LENGTH_LONG).show();				
		}

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
	 * loads the top businesses data into a give horiontal view.
	 * @param view
	 */
	public void LoadTopBusinessesAsync(TopBusinessesHorizontalView view){
		loadTopBusinesses = new LoadTopBusinessesRunnable(view, context);
		new Thread(loadTopBusinesses){}.start();
	}
}
