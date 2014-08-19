package com.example.dbhandling;

import java.util.ArrayList;
import java.util.Random;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datastructures.BusinessMarker;
import com.example.datastructures.BusinessMarker.BuisnessType;
import com.example.datastructures.Comment;
import com.example.mapsample.CommentsArrayAdapter;
import com.example.mapsample.MapWindowFragment;
import com.google.android.gms.maps.model.LatLng;


/**
 * The DBHandler is used for accessing Parse server, as well as local DB (using sqlite).
 * @author dror
 *
 */
public class DBHandler {
	
	public static long user_id = 123123;
	public static String userName = "dror the king";
	
	private LocalDBHelper localDBHelper;
	SQLiteDatabase localDB;
	Context context;
	private LoadDealCommentsTask loadCommentsTask;
	
	static private ArrayList<Long> dislikeBusinesses = new ArrayList<Long>();
	static private ArrayList<Long> likeBusinesses = new ArrayList<Long>();
	
	static int debug_initUserDataCounter = 0;
	public static void initUserData(){
			debug_initUserDataCounter++;
			updateLikeAndDislikeListsAsync();
			
			//TODO temporary values, should be loaded differently.
			user_id = 123123;
			userName = "dror the king";
			
	}
	
	public DBHandler(Context context) {
		// TODO Auto-generated constructor stub
		localDBHelper = new LocalDBHelper(context);
		localDB = localDBHelper.getWritableDatabase();
		this.context = context;
	}
		
		/**
		 * closes the DBHandler.
		 */
		public void close(){
			if(loadCommentsTask!=null){
				loadCommentsTask.stopTask();
			}
			localDBHelper.close();
			localDB.close();
		}
	
		/**
		 * set a new home location for the user (using sqlite).
		 * @param lat
		 * @param lng
		 */
		public void setHome(double lat, double lng){
			String countQuery = "SELECT  * FROM " + LocalDBHelper.HOME_TABLE;
			Cursor cursor = localDB.rawQuery(countQuery, null);
			int numOfRows = cursor.getCount();
			cursor.close();
			 
			//if there is already another home location, it should be deleted
			if(numOfRows>0){
				localDB.execSQL("delete from "+ LocalDBHelper.HOME_TABLE);
			}
			
			ContentValues contentValue = new ContentValues();
			contentValue.put(LocalDBHelper.LAT_COL, lat);
			contentValue.put(LocalDBHelper.LONG_COL,lng);
			localDB.insert(LocalDBHelper.HOME_TABLE, null, contentValue);
		}
		
		/**
		 * returns the user home location (using sqlite). 
		 * if he doesn't have a home location, returns the default location.
		 * @return
		 */
		public LatLng getHome(){
			Cursor cursor = localDB.rawQuery("select * from " + LocalDBHelper.HOME_TABLE,null);
			if (cursor .moveToFirst()) {
				double lat = cursor.getDouble(cursor.getColumnIndex(LocalDBHelper.LAT_COL));
				double lng = cursor.getDouble(cursor.getColumnIndex(LocalDBHelper.LONG_COL));
				cursor.close();
				return new LatLng(lat,lng);	
			}
			else{
				Log.d("DBHandler", "no home location is set. returns default location");
				return MapWindowFragment.DEFAULT_LOCATION;
			}
		}
		
		/**
		 * adds a business id to the user favorites table (using sqlite).
		 * @param id
		 */
		public void addToFavourites(long id){
			if(this.isInFavourites(id)){
				Log.d("DBHandler", "business is already in favourites");
			}
			ContentValues addedBusiness = new ContentValues();
			addedBusiness.put(LocalDBHelper.FAVOURITES_COL, id);
			localDB.insert(LocalDBHelper.FAVOURITES_TABLE, null, addedBusiness);
			Log.d("DBHandler", "business " + Long.toString(id) + " was inserted to favourites");
		}
		
		/**
		 * remove a business id from the user favorites table (in sqlite).
		 * @param id
		 */
		public void removeFromFavourites(long id){
			Cursor cursor =  localDB.query(LocalDBHelper.FAVOURITES_TABLE, new String[] {LocalDBHelper.ID_COL,LocalDBHelper.FAVOURITES_COL},
					LocalDBHelper.FAVOURITES_COL + "=?", new String[] { Long.toString(id)},
					null, null, LocalDBHelper.ID_COL + " desc");
			if (cursor.moveToFirst()) {
				String whereClause = LocalDBHelper.ID_COL+"=?";
				String[]whereArgs = new String[] {String.valueOf(cursor.getInt(0))};
				localDB.delete(LocalDBHelper.FAVOURITES_TABLE, whereClause , whereArgs);
				Log.d("DBHandler", "business " + Long.toString(id) + " was removed from favourites");
			}else{
				Log.d("DBHandler", "business " + Long.toString(id) + "isn't exists in favourites");
			}
			cursor.close();
		}
		
		/**
		 * receives a business id and check if it's in the user favorites list using sqlite.
		 */
		public boolean isInFavourites(long businessId){
			Cursor cursor =  localDB.query(LocalDBHelper.FAVOURITES_TABLE, new String[] {LocalDBHelper.ID_COL,LocalDBHelper.FAVOURITES_COL},
					LocalDBHelper.FAVOURITES_COL + "=?", new String[] { Long.toString(businessId)},
					null, null, LocalDBHelper.ID_COL + " desc");
			boolean retVal = cursor.getCount() == 1;
			cursor.close();
			String flag = retVal? " is in ":" is not in ";
			Log.d("DBHandler", "business " + Long.toString(businessId) + flag + "favourites");
			return retVal;
		}
		
		
		
		/**
		 * loads all the Business Marker objects which represents businesses
		 * which are close to the current map center.
		 * A business is considered close to the center if it's distance from it
		 * is less than RADIUS km (constant value).
		 * 
		 */
		public void updateBusinessMarkerListAndMapAsync(LatLng mapCenter){
			final double RADIUS = 5;
			
		}
		
		/**
		 * loads a deal's Bitmap asynchronously. updates the relevant textView whenever the task was ended.
		 */
		public void loadDealAsync(long businessID ,TextView textView){
			LoadDealStringTask loadTask = new LoadDealStringTask(textView, businessID, context);
			loadTask.execute();
		}
		
		/**
		 * receives a businessID and returns a corresponding BussinessMarker object
		 * with all the relevant information (excluding the business Bitmap, which will
		 * be loaded via loadImageAsync function).
		 */ //TODO - implement this function
		public BusinessMarker getBusinessInfo(long businessID){
			return new BusinessMarker("MCdonalds", BuisnessType.RESTURANT, new LatLng(31.781099, 35.217668), "Jerusalem",0,new Random().nextInt(99999),new Random().nextInt(99999));
		}
		
		
		/**
		 * loads a deal's Bitmap asynchronously. updates the relevant ImageView whenever the task was ended.
		 */
		public void loadBusinessImageViewAsync(long businessID, BuisnessType buisnessType ,ImageView imageView){
			LoadDealBitmapTask loadTask = new LoadDealBitmapTask(imageView, businessID,buisnessType,context);
			loadTask.execute();
		
		}
		
		/**
		 * updates the parse servers that the user like the 
		 * current business deal.
		 * this method should do 2 things:
		 * 	add the dealID from the user likes table
		 * 	add a like to the current business deal Table.
		 */
		
		public void addLikeToDeal(long businessId){
			if(dislikeBusinesses.contains(businessId)){
				dislikeBusinesses.remove(businessId);
				//TODO - also remove from the dislike list at the parse DB
			}
			likeBusinesses.add(businessId);
		}
		
		
		
		public enum DealLikeStatus{LIKE,DISLIKE,DONT_CARE};
		/**
		 * return LIKE/DISLIKE/DONT_CARE according to the user preferences regarding
		 * to the current deal.
		 * notice that there is no need to access to the parse method this time, since
		 * the entire like/dislike data was already brougt from the parse servers
		 * using updateLikeAndDislikeListsAsync().
		 * @param businessId
		 * @return
		 */
		public DealLikeStatus getDealLikeStatus(long businessId){
			if(likeBusinesses.contains(businessId)){
				return DealLikeStatus.LIKE;
			}else if(dislikeBusinesses.contains(businessId)){
				return DealLikeStatus.DISLIKE;
			}else{
				return DealLikeStatus.DONT_CARE;
			}
		}
		

		/**
		 * updates the parse servers that the user doesnt like the 
		 * current business deal.
		 * this method shuold do 2 things:
		 * 	add the dealID from the user dislikes table
		 * 	add a dislike to the current business deal Table.
		 */
		public void addDislikeToDeal(long businessId){
			if(likeBusinesses.contains(businessId)){
				likeBusinesses.remove(businessId);
				//TODO - also remove from the like list at the parse DB
			}
			dislikeBusinesses.add(businessId);
		}
		
		/**
		 * updates the parse servers that the user doesn't like/dislike the 
		 * current business deal anymore.
		 * this method shuold do 2 things:
		 * 	erase the dealID from the user likes table
		 * 	delete a like from the current business deal Table.
		 */
		public void setDontCareToDeal(long businessId){
			if(likeBusinesses.contains(businessId)){
				//TODO remove user id from the likes list from parse DB
				likeBusinesses.remove(businessId);
			}
			else if(dislikeBusinesses.contains(businessId)){
				//TODO remove user id from the dislikes list from parse DB
				dislikeBusinesses.remove(businessId);
			}else{
				Log.d("DBHandler", "Business " + Long.toString(businessId) + " error: the user didnt like it nor dislike it");
			}
		}
		
		/**
		 * updates the dislikeBusinesses and likeBusinesses lists.
		 * this method will be called right before the first DBHandler is created;
		 */
		public static void updateLikeAndDislikeListsAsync(){
			
		}
		
	
				
		/**
		 * receives a commentArrayAdapter and comments list. updates both of the
		 * parameter asynchronously, using parse.
		 */
		public void getCommentsListAsync(ArrayList<Comment> comments,CommentsArrayAdapter adapter){
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
		
		
}
