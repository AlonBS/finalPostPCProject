package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.widget.TextView;

/***
 * Implementation of a runnable object which updates the deal TextView,
 * and the address&phone TextView within the ShowDealActivity.
 * The 3 strings are downloaded from parse (phone,address and deal).
 * 
 * @author dror
 *
 */
public class LoadDealInfoRunnable implements Runnable{
	private final WeakReference<TextView> dealTextViewRef;
	private final WeakReference<TextView> addressAndPhoneViewRef;
	private long businessID;
	private Context context;

	/**
	 * c-tor for LoadDealInfoRunnable class.
	 * receives two TextViews. if one of them is null - doesn't updates it.
	 * 
	 */
	public LoadDealInfoRunnable(TextView dealTextView,TextView addressAndPhoneView, long businessID,Context context) {
		// Use a WeakReference to ensure the ImageView can be garbage collected
		this.dealTextViewRef = dealTextView!=null? new WeakReference<TextView>(dealTextView):null;
		this.addressAndPhoneViewRef = addressAndPhoneView!=null?new WeakReference<TextView>(addressAndPhoneView):null;
		this.businessID = businessID;
		this.context = context;
	}

	@Override
	public void run() {



		if (dealTextViewRef != null) {
			//TODO - ALON, this values should be loaded from parse using the business id.
			String dealStr = "ONLY TODAY AND DURING THE REST OF THE YEAR!!! BUY A COOOOL SHIRT AND GET A PLASTIC BUG TO PUT IT IN FOR 10 AGOROT ONLY!!! wow!!"; 
			final TextView nameTv = dealTextViewRef.get();
			if (nameTv != null) {
				nameTv.setText(dealStr);
			}
		}

		if (addressAndPhoneViewRef != null) {
			//TODO - ALON, this values should be loaded from parse using the business id.
			String phoneStr = "050-8512391";
			String addressStr = "Jaffa St. Jerusalem";
			final TextView detailsTv = addressAndPhoneViewRef.get();
			if (detailsTv != null) {
				detailsTv.setText(addressStr + "     " + phoneStr);
			}
		}

	}
}
