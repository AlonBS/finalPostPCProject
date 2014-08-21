package com.example.dbhandling;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.datastructures.BusinessMarker.BuisnessType;


		/**
		 * A task which loads a business Bitmap from parse and update the imageview whenever
		 * the loading process ends. if the business doesn't have a bitmap, the returned bitmap
		 * will be a default bitmap. 
		 * inputs: imageView weak reference and businessID.
		 * @author dror
		 *
		 */
		public class LoadDealBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
		    private final WeakReference<ImageView> imageViewRef;
		    private long businessID;
		    private BuisnessType businessType;
		    private Context context;
		    //private int data = 0;
		    
		    public LoadDealBitmapTask(ImageView imageView,long businessID, BuisnessType businessType,Context context) {
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
		        boolean ImageExists = true; //TODO this value should be loaded from parse
		        if(!ImageExists){
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
