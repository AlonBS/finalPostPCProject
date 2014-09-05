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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.ExternalBusiness.BuisnessType;
import com.dna.radius.dbhandling.DBHandler;

public class ShowDealActivity extends FragmentActivity{
	//needed parameters for the activity
	public final static String BUSINESS_NAME_PARAM = "BusinessName";
	public final static String BUSINESS_ID_PARAM = "BusinessID";
	public final static String DEAL_ID_PARAM = "BusinessID";
	public final static String BUSINESS_TYPE_PARAM = "BusinessType";
	public final static String DEAL_RATING_PARAM = "DealRating";
	public final static String USER_MODE_PARAM = "IsInUserMode";	
	public final static String NUM_OF_LIKES_PARAM = "likesParam";
	public final static String NUM_OF_DISLIKES_PARAM = "dislikesParam";	
	public final static String CURRENT_DEAL_STR_PARAM = "currentDealParam";	
	public final static String PHONE_STR_PARAM = "phone";
	public final static String ADDRESS_STR_PARAM = "address";	


	//a button which allows switching between the like fragment and the comments fragment.
	private ImageView switchFragmentsButton;
	//used for switching between fragments.
	private enum CurrentFragmentType{DEAL_FRAGMENT,COMMENTS_FRAGMENT};
	private CurrentFragmentType currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;

	public boolean isInUserMode;
	public String businessID, dealID,phoneStr,addressStr,dealStr;
	public int numOfLikes,numOfDislikes;
	public BuisnessType bType;
	private boolean isFavourite;

	/**The curreny Deal comments List*/
	public ArrayList<Comment> commentsList;

	ClientData clientData;
	private String businessName;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_deal_activity);


		Intent intent = getIntent();
		//loads the relevant data
		businessName = intent.getStringExtra(BUSINESS_NAME_PARAM);
		businessID = intent.getStringExtra(BUSINESS_ID_PARAM);
		dealID = intent.getStringExtra(DEAL_ID_PARAM);
		bType = (BuisnessType)intent.getSerializableExtra(BUSINESS_TYPE_PARAM);
		int rating = intent.getIntExtra(DEAL_RATING_PARAM, 0);
		isInUserMode = intent.getBooleanExtra(USER_MODE_PARAM, true);
		numOfLikes = intent.getIntExtra(NUM_OF_LIKES_PARAM,0);
		numOfDislikes = intent.getIntExtra(NUM_OF_DISLIKES_PARAM,0);
		phoneStr = intent.getStringExtra(PHONE_STR_PARAM);
		addressStr = intent.getStringExtra(ADDRESS_STR_PARAM);
		dealStr = intent.getStringExtra(CURRENT_DEAL_STR_PARAM);

		//sets the views
		TextView businessNameTV = (TextView)findViewById(R.id.businessTitle);
		businessNameTV.setText(businessName);
		TextView dealTextView = (TextView)findViewById(R.id.dealTextView);
		dealTextView.setText(dealStr);
		TextView detailsTV = (TextView)findViewById(R.id.businessDetails);
		detailsTV.setText(addressStr + "    " + phoneStr);
		RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		ratingBar.setRating(rating);

		Log.d("ShowDealActivity", "businessId:" + businessID + ", dealID:" + dealID); //TODO remove


		/**overrides rating bar's on touch method so it won't do anything*/
		ratingBar.setOnClickListener(new OnClickListener() {@Override
			public void onClick(View arg0) {return;}});

		//handles the switch fragment button
		switchFragmentsButton = (ImageView)findViewById(R.id.switchFragmentButton);
		Bitmap commentsIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_chat);
		switchFragmentsButton.setImageBitmap(commentsIcon);
		//until the Extra data will be loaded fully - this button is unusable.
		switchFragmentsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onFragmentSwitchBtnClick();
			}
		});

		//handles the favourites button
		final ImageView favouritesBtn = (ImageView)findViewById(R.id.favourites_flag);
		isFavourite = ClientData.isInFavourites(businessID);
		if(isFavourite){
			Bitmap favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_important);
			favouritesBtn.setImageBitmap(favouriteBmap);
		}
		favouritesBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isFavourite = !isFavourite;
				Bitmap favouriteBmap;
				if(isFavourite){
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_important);
					ClientData.addToFavourites(businessID);
				}else{
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_not_important);
					ClientData.removeFromFavorites(businessID);
				}
				favouritesBtn.setImageBitmap(favouriteBmap);
			}
		});
		
		//starts the deal fragment
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		LikeAndDislikeFragment dealPresantorFragment = new LikeAndDislikeFragment();
		fragmentTransaction.add(R.id.deal_or_comments_fragment, dealPresantorFragment);
		fragmentTransaction.commit();

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		DBHandler.close();
	}

	/**
	 * this function is called whenever the comment button / the back arrow button is pressed.
	 * in this case - a new fragment should be loaded to the screen.
	 */
	private void onFragmentSwitchBtnClick(){
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment fragmentToSwitch;
		Bitmap newIcon;
		if (currentFragmentType == CurrentFragmentType.DEAL_FRAGMENT){
			currentFragmentType = CurrentFragmentType.COMMENTS_FRAGMENT;
			fragmentToSwitch = new CommentsFragment(dealID);
			newIcon= BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_back);
		}else{
			currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;
			fragmentToSwitch = new LikeAndDislikeFragment();
			newIcon= BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_chat);
		}
		fragmentTransaction.replace(R.id.deal_or_comments_fragment, fragmentToSwitch);
		fragmentTransaction.commit();

		switchFragmentsButton.setImageBitmap(newIcon);
	}

}
