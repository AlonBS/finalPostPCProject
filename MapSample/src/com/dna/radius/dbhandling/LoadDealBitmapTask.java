package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.dna.radius.datastructures.BusinessMarker.BuisnessType;
import com.example.mapsample.R;


		/**
		 * A task which loads a business Bitmap from parse and update the imageview whenever
		 * the loading process ends. if the business doesn't have a bitmap, the returned bitmap
		 * will be a default bitmap. 
		 * inputs: imageView weak reference and businessID.
		 * @author dror
		 * 
		 * TODO - perhaps this Runnable should be a runnable. i tried it before (implementation is in
		 * testing folder and it didnt work).
		 *
		 */
		public class LoadDealBitmapTask extends AsyncTask<Integer, Void, Bitmap> {
		    private final WeakReference<ImageView> imageViewRef;
		    private long businessID;
		    private Context context;
			private int alternativeImageId;
		    
		    public LoadDealBitmapTask(ImageView imageView,long businessID, Context context, int alternativeImageId) {
		        // Use a WeakReference to ensure the ImageView can be garbage collected
		    	this.imageViewRef = new WeakReference<ImageView>(imageView);
		    	this.businessID = businessID;
		    	this.context = context;
		    	this.alternativeImageId = alternativeImageId;
		    }
		    // Decode image in background.
		    @Override
		    protected Bitmap doInBackground(Integer... params) {
		        //data = params[0];
		        boolean ImageExists = false; //TODO this value should be loaded from parse
		        if(!ImageExists){
		        	if(alternativeImageId!=DBHandler.ALTERNATIVE_IMAGE_FALSE){
		        		return BitmapFactory.decodeResource(context.getResources(), alternativeImageId);
		        	}else{
		        		return null;
		        	}
		        }else{
		        	Bitmap retBitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.burger); 
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
