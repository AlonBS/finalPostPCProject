package com.example.dbhandling;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

/**
 * A task which loads a business Bitmap from parse and update the imageview whenever
 * the loading process ends. if the business doesn't have a bitmap, the returned bitmap
 * will be a default bitmap. 
 * inputs: imageView weak reference and businessID.
 * @author dror
 *
 */
public class LoadDealStringTask extends AsyncTask<Integer, Void, String> {
    private final WeakReference<TextView> textViewRef;
    private long businessID;
    private Context context;
    //private int data = 0;
    
    public LoadDealStringTask(TextView imageView,long businessID, Context context) {
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
