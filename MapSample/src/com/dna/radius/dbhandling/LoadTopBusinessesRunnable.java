package com.dna.radius.dbhandling;

import java.lang.ref.WeakReference;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.dna.radius.businessmode.TopBusinessesHorizontalView;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.datastructures.ExternalBusiness.BuisnessType;
import com.dna.radius.R;
import com.google.android.gms.maps.model.LatLng;

public class LoadTopBusinessesRunnable implements Runnable{
	private WeakReference<TopBusinessesHorizontalView> businessesHorizontalViewRef;
	private boolean stopFlag = false;
	private Context context;
	public LoadTopBusinessesRunnable(TopBusinessesHorizontalView view,Context context){
		this.businessesHorizontalViewRef = new WeakReference<TopBusinessesHorizontalView>(view);
		this.context = context;
	}

	public void stopTask(){
		stopFlag = true;
	}
	@Override
	public void run() {
		while(!stopFlag){
			//TODO - add here a code which gets the next top businesses item from the server.
			//it should load two things in each iteration: 
			//	1. a business marker object from the top businesses list.
			// 	2. an image view which is corresonding to the top businesses list.
			final ExternalBusiness bm = new ExternalBusiness("MCdonalds", BuisnessType.RESTAURANT, new LatLng(31.781099, 35.217668),"",new Random().nextInt(99999),new Random().nextInt(99999), Integer.toString(new Random().nextInt(99999)));
			final Bitmap businessBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.burger);
			final TopBusinessesHorizontalView horizontalView = businessesHorizontalViewRef.get();
			if (!stopFlag && horizontalView != null && bm != null) {
				try {
					Thread.sleep(1000); //TODO - just for testing, remove me!!!
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				horizontalView.post(new Runnable() {
					public void run() {
						boolean retVal = horizontalView.addBusiness(bm,businessBitmap);
						if(!retVal){
							stopFlag = true;
						}
					}
				});

			}else{
				break;
			}

		}

	}

}
