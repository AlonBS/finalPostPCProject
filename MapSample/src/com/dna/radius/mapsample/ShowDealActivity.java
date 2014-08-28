package com.dna.radius.mapsample;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.BusinessMarker.BuisnessType;
import com.dna.radius.dbhandling.DBHandler;
import com.example.mapsample.R;

public class ShowDealActivity extends FragmentActivity{
	
	public final static String BUSINESS_NAME_PARAM = "BusinessName";
	public final static String BUSINESS_ID_PARAM = "BusinessID";
	public final static String BUSINESS_TYPE_PARAM = "BusinessType";
	public final static String DEAL_RATING_PARAM = "DealRating";
	public final static String USER_MODE_PARAM = "IsInUserMode";	
	public final static String NUM_OF_LIKES_PARAM = "likesParam";
	public final static String NUM_OF_DISLIKES_PARAM = "dislikesParam";	
	
	private ImageView switchFragmentsButton;
	private enum CurrentFragmentType{DEAL_FRAGMENT,COMMENTS_FRAGMENT};
	private CurrentFragmentType currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;
	public boolean isInUserMode;
	public String dealDetails;
	public int businessID;
	public int numOfLikes,numOfDislikes;
	public BuisnessType bType;
	private boolean isFavourite;
	private DBHandler dbHandle;
	
	/**The curreny Deal comments List*/
	public ArrayList<Comment> commentsList;
	
	ClientData clientData;
	
	
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
		
		clientData = ClientData.getInstance();
		
		Intent intent = getIntent();
		String businessName = intent.getStringExtra(BUSINESS_NAME_PARAM);
		businessID = (int)intent.getIntExtra(BUSINESS_ID_PARAM,0);//TODO!
		bType = (BuisnessType)intent.getSerializableExtra(BUSINESS_TYPE_PARAM);
		int rating = intent.getIntExtra(DEAL_RATING_PARAM, 0);
		isInUserMode = intent.getBooleanExtra(USER_MODE_PARAM, true);
		numOfLikes = intent.getIntExtra(NUM_OF_LIKES_PARAM,0);
		numOfDislikes = intent.getIntExtra(NUM_OF_DISLIKES_PARAM,0);
		
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
				FragmentManager fragmentManager = getSupportFragmentManager();
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
		TextView detailsTV = (TextView)findViewById(R.id.businessDetails);
		dbHandle = new DBHandler(this);
		dbHandle.loadDealAsync(businessID, dealTextView,detailsTV);
		
		final ImageView favouritesBtn = (ImageView)findViewById(R.id.favourites_flag);
		isFavourite = clientData.isInFavourites(businessID);
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
					clientData.addToFavorites(businessID);
					}else{
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_not_important);
					clientData.removeFromFavorites(businessID);
				}
				favouritesBtn.setImageBitmap(favouriteBmap);
				
			}
		});
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		DealPresentorFragment dealPresantorFragment = new DealPresentorFragment();
		fragmentTransaction.add(R.id.deal_or_comments_fragment, dealPresantorFragment);
		fragmentTransaction.commit();
		
		
		
}


	
	
}
