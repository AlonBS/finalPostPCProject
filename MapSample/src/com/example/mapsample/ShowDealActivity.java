package com.example.mapsample;

import java.util.ArrayList;

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

import com.example.datastructures.BusinessMarker.BuisnessType;
import com.example.datastructures.Comment;
import com.example.dbhandling.DBHandler;

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
	private boolean isFavourite;
	private DBHandler dbHandle;
	
	/**The curreny Deal comments List*/
	public ArrayList<Comment> commentsList;
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHandle.close();
	}
	
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
					 ((CommentsFragment)fragmentToSwitch).businessID = businessID;
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
		dbHandle = new DBHandler(this);
		dbHandle.loadDealAsync(businessID, dealTextView);
		
		final ImageView favouritesBtn = (ImageView)findViewById(R.id.favourites_flag);
		isFavourite = dbHandle.isInFavourites(businessID);
		if(isFavourite){
			Bitmap favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_important);
			favouritesBtn.setImageBitmap(favouriteBmap);
		}
		favouritesBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isFavourite = !isFavourite;
				Bitmap favouriteBmap;
				if(isFavourite){
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_important);
					dbHandle.addToFavourites(businessID);
					}else{
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_not_important);
					dbHandle.removeFromFavourites(businessID);
				}
				favouritesBtn.setImageBitmap(favouriteBmap);
				
			}
		});
		
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		DealPresentorFragment dealPresantorFragment = new DealPresentorFragment();
		fragmentTransaction.add(R.id.deal_or_comments_fragment, dealPresantorFragment);
		fragmentTransaction.commit();
		
		
		
}

	
	
}
