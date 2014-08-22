package com.example.dbhandling;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class ChangeDealUpdateViewRunnable implements Runnable {
	private final WeakReference<TextView> textViewRef;
	private long businessID;
	private String newDealStr;
	//private int data = 0;



	public ChangeDealUpdateViewRunnable(TextView textView,long businessID, String newStr) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		this.textViewRef = new WeakReference<TextView>(textView);
		this.businessID = businessID;
		//this.context = context;
		this.newDealStr = newStr;
		if(newStr==null){
			Log.d("EditDeal	ChangeViewTask", "error changing a deal, new deal str is null");
		}
	}



	@Override
	public void run() {
		//TODO - write a code for updating a business deal, and saving the old deal on the history list.
		if (textViewRef != null && newDealStr != null) {
			final TextView textView = textViewRef.get();
			if (textView != null) {
				textView.post(new Runnable() {
					public void run() {
						textView.setText(newDealStr);
					}
				});
			}

		}
	}


}
