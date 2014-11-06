package com.dna.radius.map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessData;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.infrastructure.MyApp;
import com.dna.radius.infrastructure.MyApp.TrackerName;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class ShowDealActivity extends BaseActivity{
	//needed parameters for the activity

	public static final String EXTERNAL_BUSINESS_KEY = "externBusiness";
	

	//used for switching between fragments.
	public enum ShowDealFragmentType{DEAL_FRAGMENT,COMMENTS_FRAGMENT};

	//	public String businessID, dealID,phoneStr,addressStr,dealStr;
	//	public int numOfLikes,numOfDislikes;
	//	public SupportedTypes.BusinessType bType;
	private boolean isFavourite;


	private ExternalBusiness pressedExternal;

	public static final String EMPTY_DEAL = "---";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_deal_activity);

		
		Tracker tracker = ((MyApp) getApplication()).getTracker(MyApp.TrackerName.APP_TRACKER);
		tracker.enableExceptionReporting(true);
		tracker.setScreenName("Show deal activity");
		tracker.send(new HitBuilders.AppViewBuilder().build());
		
		
		Intent intent = getIntent();

		pressedExternal = (ExternalBusiness)intent.getSerializableExtra(EXTERNAL_BUSINESS_KEY);
		final String externBusId = pressedExternal.getExternBusinessId();

		//sets the views
		TextView businessNameTV = (TextView)findViewById(R.id.businessTitle);
		TextView detailsTV = (TextView)findViewById(R.id.businessDetailsTv);
		
		RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		
		if ( (!pressedExternal.getExternBusinessAddress().isEmpty()) && 
				(!pressedExternal.getExternBusinessPhone().isEmpty()) )  {
			detailsTV.setText(pressedExternal.getExternBusinessAddress() + ", " + pressedExternal.getExternBusinessPhone());
		}else if(!pressedExternal.getExternBusinessAddress().isEmpty()){
			detailsTV.setText(pressedExternal.getExternBusinessAddress());
			
		}else if(!pressedExternal.getExternBusinessPhone().isEmpty()){
			detailsTV.setText(pressedExternal.getExternBusinessAddress());
		}else{
			detailsTV.setVisibility(View.GONE);
		}

		businessNameTV.setText(pressedExternal.getExtenBusinessName());
		ratingBar.setRating((float)pressedExternal.getExternBusinessRating()); 


	

		//handles the favourites button
		final ImageView favouritesBtn = (ImageView)findViewById(R.id.favourites_flag);
		if(BaseActivity.isInBusinessMode){
			isFavourite = BusinessData.isInFavourites(externBusId);
		}else{
			isFavourite = ClientData.isInFavorites(externBusId);

		}
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
					if(BaseActivity.isInBusinessMode){
						BusinessData	.addToFavourites(externBusId);
					}else{
						ClientData.addToFavorites(externBusId);
					}
				}else{
					favouriteBmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_not_important);
					if(BaseActivity.isInBusinessMode){
						BusinessData.removeFromFavorites(externBusId);
					}else{
						ClientData.removeFromFavorites(externBusId);
					}
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

		ImageView cancelButton = (ImageView)findViewById(R.id.close_window);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		
		setScreenSize();

	}


	private void setScreenSize() {

		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.75);
		int screenHeight = getWindow().getAttributes().height;
		getWindow().setLayout(screenWidth, screenHeight);
	}

	
	
	/**
	 * this function is called whenever the comment button / the back arrow button is pressed.
	 * in this case - a new fragment should be loaded to the screen.
	 */
	public void switchToFragment(ShowDealFragmentType newFragmentType){

		String newFragmentName;
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment fragmentToSwitch;

		if (newFragmentType == ShowDealFragmentType.DEAL_FRAGMENT){
			fragmentToSwitch = new LikeAndDislikeFragment();
			newFragmentName = "show deal Fragment";
		}else{
			fragmentToSwitch = new CommentsFragment();
			newFragmentName = "show comment Fragment";
		}

		fragmentTransaction.replace(R.id.deal_or_comments_fragment, fragmentToSwitch);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
		
		Tracker t = ((MyApp) getApplication()).getTracker(
				TrackerName.APP_TRACKER);
		// Build and send an Event.
		t.send(new HitBuilders.EventBuilder()
		.setCategory("deal activity fragment switch")
		.setAction("fragment switch, to: " + newFragmentName)
		.setLabel("fragment switch to: " + newFragmentName)
		.build());

	}


	public ExternalBusiness getExternalBusiness() { return pressedExternal; }

}
