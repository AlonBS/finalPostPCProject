package com.example.mapsample;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.mapsample.BusinessMarker.BuisnessType;

public class ShowDealActivity extends Activity{
	
	public final static String BUSINESS_NAME_PARAM = "BusinessName";
	public final static String BUSINESS_ID_PARAM = "BusinessID";
	public final static String BUSINESS_TYPE_PARAM = "BusinessType";
	public final static String DEAL_RATING_PARAM = "DealRating";
	public final static String USER_MODE_PARAM = "IsInUserMode";	
	
	private ImageView switchFragmentsButton;
	private enum CurrentFragmentType{DEAL_FRAGMENT,COMMENTS_FRAGMENT};
	private CurrentFragmentType currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;
	public boolean isInUserMode;
	public String dealDetails;
	public long businessID;
	public BuisnessType bType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_show_deal);
		
		Intent intent = getIntent();
		String businessName = intent.getStringExtra(BUSINESS_NAME_PARAM);
		businessID = intent.getLongExtra(BUSINESS_ID_PARAM,0);
		bType = (BuisnessType)intent.getSerializableExtra(BUSINESS_TYPE_PARAM);
		int rating = intent.getIntExtra(DEAL_RATING_PARAM, 0);
		isInUserMode = intent.getBooleanExtra(USER_MODE_PARAM, true);
		
		TextView businessNameTV = (TextView)findViewById(R.id.businessTitle);
		businessNameTV.setText(businessName);
		
		RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		ratingBar.setRating(rating);
		/**overrides rating bar's on touch method so it won't change anything*/
		ratingBar.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });
		
		switchFragmentsButton = (ImageView)findViewById(R.id.switchFragmentButton);
		Bitmap commentsIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_chat);
		switchFragmentsButton.setImageBitmap(commentsIcon);
		
		switchFragmentsButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				Fragment fragmentToSwitch;
				Bitmap newIcon;
				// TODO Auto-generated method stub
				if (currentFragmentType == CurrentFragmentType.DEAL_FRAGMENT){
					currentFragmentType = CurrentFragmentType.COMMENTS_FRAGMENT;
					 fragmentToSwitch = new CommentsFragment();
					 newIcon= BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_back);
				}else{
					currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;
					fragmentToSwitch = new DealPresentorFragment();
					newIcon= BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_chat);
				}
				fragmentTransaction.replace(R.id.deal_or_comments_fragment, fragmentToSwitch);
				fragmentTransaction.commit();
				
				switchFragmentsButton.setImageBitmap(newIcon);
			}
		});
		
		TextView dealTextView = (TextView)findViewById(R.id.dealTextView);
		DBHandler.loadDealAsync(businessID, dealTextView,this);
		
		//switchFragmentsButton.setVisibility(View.GONE);
		//switchFragmentsButton.setImageBitmap(commentsIcon);
		/*TextView tvDebug = (TextView)findViewById(R.id.debugString);
		if(mode)
			tvDebug.setText(" user mode");
		else
			tvDebug.setText(" business mode");*/
		
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		DealPresentorFragment dealPresantorFragment = new DealPresentorFragment();
		fragmentTransaction.add(R.id.deal_or_comments_fragment, dealPresantorFragment);
		//CommentsFragment commentsFragment = new CommentsFragment();
		//fragmentTransaction.add(R.id.show_deal_fragment, commentsFragment);
		fragmentTransaction.commit();
		
		
		
}

}
