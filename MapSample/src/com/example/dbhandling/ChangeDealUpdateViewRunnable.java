package com.example.dbhandling;

import java.lang.ref.WeakReference;

import android.util.Log;
import android.widget.TextView;

/***
 * This task receives a businessID and a string which represents a deal.
 * it performs the following actions:
 * 1. On Parse servers - it Updates the business deal through parse.
 * 	  if the business didn't have a deal before - simply creates a new deal.
 * 2. On Parse servers - it saves the current business deal at the business history list.
 * 3. it changes the given textView to contain the new deal str instead of the 
 * 	  old one. 
 * @author dror
 *
 */
public class ChangeDealUpdateViewRunnable implements Runnable {
	private final WeakReference<TextView> textViewRef;
	private long businessID;
	private String newDealStr;



	public ChangeDealUpdateViewRunnable(TextView textView,long businessID, String newStr) {
		this.textViewRef = new WeakReference<TextView>(textView);
		this.businessID = businessID;
		this.newDealStr = newStr;
		if(newStr==null){
			Log.d("EditDeal	ChangeViewTask", "error changing a deal, new deal str is null");
		}
	}



	@Override
	public void run() {
		if (textViewRef != null && newDealStr != null) {
			final TextView textView = textViewRef.get();
			if (textView != null) {
				textView.post(new Runnable() {
					public void run() {
						
						//TODO - ALON: write here a code for updating a business deal, and saving the old deal on the history list.
						textView.setText(newDealStr);
					}
				});
			}

		}
	}

	
	
	
	
}
