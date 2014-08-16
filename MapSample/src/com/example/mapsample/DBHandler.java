package com.example.mapsample;

import java.lang.ref.WeakReference;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mapsample.BusinessMarker.BuisnessType;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;

public class DBHandler {
	
	private LocalDBHelper localDBHelper;
	SQLiteDatabase localDB;

	public DBHandler(Context context) {
		// TODO Auto-generated constructor stub
		localDBHelper = new LocalDBHelper(context);
		localDB = localDBHelper.getWritableDatabase();
	}
		
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
		
		LatLng getHome(){
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
		public void addToFavourites(long id){
			
		}
		public void removeFromFavourites(long id){
			
		}
	
		public void updateBusinessMarkerListAndMapAsync(){
			
			
		}
		
		public void loadDealAsync(long businessID ,TextView textView, Context context){
			LoadDealTask loadTask = new LoadDealTask(textView, businessID, context);
			loadTask.execute();
		}
		public void loadBusinessImageViewAsync(long businessID, BuisnessType buisnessType ,ImageView imageView, Context context){
			LoadBitmapTask loadTask = new LoadBitmapTask(imageView, businessID,buisnessType,context);
			loadTask.execute();
		}
		
		
		
		/**
		 * A task which loads a business Bitmap from parse and update the imageview whenever
		 * the loading process ends. if the business doesn't have a bitmap, the returned bitmap
		 * will be a default bitmap. 
		 * inputs: imageView weak reference and businessID.
		 * @author dror
		 *
		 */
		private static class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
		    private final WeakReference<ImageView> imageViewRef;
		    private long businessID;
		    private BuisnessType businessType;
		    private Context context;
		    //private int data = 0;
		    
		    public LoadBitmapTask(ImageView imageView,long businessID, BuisnessType businessType,Context context) {
		        // Use a WeakReference to ensure the ImageView can be garbage collected
		    	this.imageViewRef = new WeakReference<ImageView>(imageView);
		    	this.businessID = businessID;
		    	this.businessType = businessType;
		    	this.context = context;
		    }
		    // Decode image in background.
		    @Override
		    protected Bitmap doInBackground(Integer... params) {
		        //data = params[0];
		        boolean ImageExists = false; //TODO this value should be loaded from parse
		        if(ImageExists){
		        	return null; //should load the bitmap from parse and return it
		        }else{
		        	Bitmap retBitmap = BitmapFactory.decodeResource(context.getResources(), businessType.getDefaultImageID()); 
		        	Log.d("DBHandler","finished loading bitmap from parse");
		        	return retBitmap;
		        }

		        //return decodeSampledBitmapFromResource(getResources(), data, 100, 100));
		    }

		    // Once complete, see if ImageView is still around and set bitmap.
		    @Override
		    protected void onPostExecute(Bitmap bitmap) {
		        if (imageViewRef != null && bitmap != null) {
		            final ImageView imageView = imageViewRef.get();
		            if (imageView != null) {
		                imageView.setImageBitmap(bitmap);
		            }
		        }
		    }
		}
	
		/**
		 * A task which loads a business Bitmap from parse and update the imageview whenever
		 * the loading process ends. if the business doesn't have a bitmap, the returned bitmap
		 * will be a default bitmap. 
		 * inputs: imageView weak reference and businessID.
		 * @author dror
		 *
		 */
		private static class LoadDealTask extends AsyncTask<Integer, Void, String> {
		    private final WeakReference<TextView> textViewRef;
		    private long businessID;
		    private Context context;
		    //private int data = 0;
		    
		    public LoadDealTask(TextView imageView,long businessID, Context context) {
		        // Use a WeakReference to ensure the ImageView can be garbage collected
		    	this.textViewRef = new WeakReference<TextView>(imageView);
		    	this.businessID = businessID;
		    	this.context = context;
		    }
		    // Decode image in background.
		    @Override
		    protected String doInBackground(Integer... params) {
		        //data = params[0];
		        	String retString = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BUG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!"; 
		        	Log.d("DBHandler","finished loading deal from parse");
		        	return retString;
		        //return decodeSampledBitmapFromResource(getResources(), data, 100, 100));
		    }

		    // Once complete, see if ImageView is still around and set bitmap.
		    @Override
		    protected void onPostExecute(String dealStr) {
		        if (textViewRef != null && dealStr != null) {
		            final TextView textView = textViewRef.get();
		            if (textView != null) {
		            	textView.setText(dealStr);
		            }
		        }
		    }
		}
}
