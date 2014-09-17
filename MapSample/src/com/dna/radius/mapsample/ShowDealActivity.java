package com.dna.radius.mapsample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

public class ShowDealActivity extends FragmentActivity{
	//needed parameters for the activity

	public static final String EXTERNAL_BUSINESS_KEY = "externBusiness";


	//a button which allows switching between the like fragment and the comments fragment.
	private ImageView switchFragmentsButton;
	//used for switching between fragments.
	private enum CurrentFragmentType{DEAL_FRAGMENT,COMMENTS_FRAGMENT};
	private CurrentFragmentType currentFragmentType = CurrentFragmentType.DEAL_FRAGMENT;

	//	public String businessID, dealID,phoneStr,addressStr,dealStr;
	//	public int numOfLikes,numOfDislikes;
	//	public SupportedTypes.BusinessType bType;
	private boolean isFavourite;


	private ExternalBusiness pressedExternal;

	public static final String EMPTY_DEAL = "---";

	private static final String WHITE_SPACES = "    ";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.show_deal_activity);

		Intent intent = getIntent();

		pressedExternal = (ExternalBusiness)intent.getSerializableExtra(EXTERNAL_BUSINESS_KEY);
		final String externBusId = pressedExternal.getExternBusinessId();

		//sets the views
		TextView businessNameTV = (TextView)findViewById(R.id.businessTitle);
		TextView detailsTV = (TextView)findViewById(R.id.businessDetails);
		RatingBar ratingBar = (RatingBar)findViewById(R.id.businessRatingBar);
		if ( (!pressedExternal.getExternBusinessAddress().isEmpty()) && 
				(!pressedExternal.getExternBusinessPhone().isEmpty()) )  {
			detailsTV.setText(pressedExternal.getExternBusinessAddress() + WHITE_SPACES + pressedExternal.getExternBusinessPhone());
		}else if(!pressedExternal.getExternBusinessAddress().isEmpty()){
			detailsTV.setText(pressedExternal.getExternBusinessAddress());
			
		}else if(!pressedExternal.getExternBusinessPhone().isEmpty()){
			detailsTV.setText(pressedExternal.getExternBusinessAddress());
		}else{
			detailsTV.setVisibility(View.GONE);
		}

		businessNameTV.setText(pressedExternal.getExtenBusinessName());
		ratingBar.setRating((float)pressedExternal.getExternBusinessRating()); 


		switchFragmentsButton = (ImageView)findViewById(R.id.switchFragmentButton);
		if(pressedExternal.getExternBusinessDeal() != null){

			//handles the switch fragment button
			Bitmap commentsIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_chat);
			switchFragmentsButton.setImageBitmap(commentsIcon);
			//until the Extra data will be loaded fully - this button is unusable.
			switchFragmentsButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					onFragmentSwitchBtnClick();
				}
			});
		}else{
			switchFragmentsButton.setVisibility(View.GONE);
		}


		//handles the favourites button
		final ImageView favouritesBtn = (ImageView)findViewById(R.id.favourites_flag);
		if(BaseActivity.isInBusinessMode){
			isFavourite = BusinessData.isInFavourites(externBusId);
		}else{
			isFavourite = ClientData.isInFavourites(externBusId);

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
						ClientData.addToFavourites(externBusId);
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
	private void onFragmentSwitchBtnClick(){

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		Fragment fragmentToSwitch;
		Bitmap newIcon;

		if (currentFragmentType == CurrentFragmentType.DEAL_FRAGMENT){
			currentFragmentType = CurrentFragmentType.COMMENTS_FRAGMENT;
			fragmentToSwitch = new CommentsFragment();
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


	public ExternalBusiness getExternalBusiness() { return pressedExternal; }

}
