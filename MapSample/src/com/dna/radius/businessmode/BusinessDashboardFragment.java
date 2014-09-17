package com.dna.radius.businessmode;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.businessmode.AddNewDealDialogFragment.AddDealDialogResult;
import com.dna.radius.businessmode.AddNewDealDialogFragment.AddNewDealCommunicator;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.mapsample.CommentsArrayAdapter;
import com.dna.radius.mapsample.ShowDealActivity;

/**
 * represents the first screen which the business owner sees when he is logging in.
 * contains data about his deal, an image, comments and top deals segment.
 *
 */
public class BusinessDashboardFragment extends Fragment implements AddNewDealCommunicator{

	private BusinessOpeningScreenActivity  parentActivity = null;

	private View v; // This fragment's view

	//private ArrayList<Comment> commentsList;

	private TextView dealOnDisplayTextView;
	private TextView dealOnDisplayLikesTextView;
	private TextView dealOnDisplayDislikesTextView;
	private ImageView imageOnDisplayImageView;
	private ImageView removeDealOnDisplayImageView;
	private	TopBusinessesHorizontalView topBusinessesHorizontalScrollView;

	/**this variable is used for loading an image from the gallery*/
	private final static int RESULT_LOAD_IMAGE = 1;
	//TODO dolphin
	//	private final static int RESULT_LOAD_IMAGE_GALLERY = 1;
	//	private final static int RESULT_LOAD_IMAGE_CAMERA = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.business_dashboard_fragment,container, false);	
		parentActivity = (BusinessOpeningScreenActivity)getActivity();

		initViews();

		displayDealIfNeeded();

		displayImageIfNeeded();

		displaySurroundingTopBusiness();

		setChangeImageOnClickListener();

		setChangeDealOnClickListener();

		setRemoveDealOnClickListener();

		return v;
	}


	private void initViews() {

		dealOnDisplayTextView = (TextView) v.findViewById(R.id.deal_tv);
		imageOnDisplayImageView = (ImageView) v.findViewById(R.id.buisness_image_view);
		dealOnDisplayLikesTextView = (TextView) v.findViewById(R.id.num_of_likes_tv);
		dealOnDisplayDislikesTextView = (TextView) v.findViewById(R.id.num_of_dislikes_tv);
		removeDealOnDisplayImageView = (ImageView)v.findViewById(R.id.remove_deal_image_view);
		topBusinessesHorizontalScrollView = (TopBusinessesHorizontalView)v.findViewById(R.id.top_businesses_list_view);

	}

	private void displayDealIfNeeded() {

		if (BusinessData.hasADealOnDisplay()) {

			dealOnDisplayTextView.setText(BusinessData.currentDeal.getDealContent());
			dealOnDisplayLikesTextView.setText(Integer.toString(BusinessData.currentDeal.getNumOfLikes()));
			dealOnDisplayDislikesTextView.setText(Integer.toString(BusinessData.currentDeal.getNumOfDislikes()));

			// add comments on display
			ListView commentsListView = (ListView)v.findViewById(R.id.comments_list_view);
			CommentsArrayAdapter commentsAdapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1 , BusinessData.currentDeal.getComments());
			commentsListView.setAdapter(commentsAdapter);
		}

		else {

			dealOnDisplayTextView.setText(R.string.tap_to_enter_deal);
			dealOnDisplayLikesTextView.setText(ShowDealActivity.EMPTY_DEAL);
			dealOnDisplayDislikesTextView.setText(ShowDealActivity.EMPTY_DEAL);
		}
	}


	private void displayImageIfNeeded() {


		if (BusinessData.hasImage()) { //TODO

			if (BusinessData.imageFullyLoaded()) {
				imageOnDisplayImageView.setImageBitmap(BusinessData.businessImage);
			}else {
				BusinessData.loadImage(getActivity(),imageOnDisplayImageView);
			}

		}else {
			imageOnDisplayImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.add_image_icon_transparent));
		}
	}


	private void displaySurroundingTopBusiness() {

		if (BusinessData.topBusinesses != null) {

			for(ExternalBusiness b : BusinessData.topBusinesses){
				topBusinessesHorizontalScrollView.addBusiness(b);
			}
		}
	}


	private void setChangeImageOnClickListener() {

		imageOnDisplayImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(getActivity(), BusinessChooseImageDialogActivity.class);
				startActivityForResult(intent, RESULT_LOAD_IMAGE );


			}

		});
	}


	private void setChangeDealOnClickListener() {

		/***
		 * allows adding a new deal instead of the old one
		 */

		dealOnDisplayTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				displayAddNewDealDialog();
			}
		});

	}


	private void setRemoveDealOnClickListener() {


		removeDealOnDisplayImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				deleteDisplayedDeal();
			}
		});
	}





	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	private void displayAddNewDealDialog(){
		AddNewDealDialogFragment dialog;
		if(BusinessData.hasADealOnDisplay()){
			dialog = new AddNewDealDialogFragment(BusinessData.currentDeal.getDealContent(),this);
		}else{
			dialog = new AddNewDealDialogFragment(this);
		}
		dialog.show( getActivity().getSupportFragmentManager(),"");
	}


	private void deleteDisplayedDeal() {

		if (BusinessData.hasADealOnDisplay()) {

			new AlertDialog.Builder(parentActivity)
			.setTitle("Delete Current Deal")
			.setMessage("Are you sure you want to delete your current deal?")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {

					dealOnDisplayTextView.setText(R.string.tap_to_enter_deal);
					dealOnDisplayLikesTextView.setText("0");
					dealOnDisplayDislikesTextView.setText("0");

					BusinessData.deleteCurrentDeal();
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int whichButton) {/* Do nothing */ }

			}).show();
		}
	}



	/**
	 * currently used for handling an image which was picked from the gallery by the user, or
	 * an image which was took by the camera.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Bitmap newBmap = null;
		/**receives an image from the gallery, and change the image of the business*/

		if (requestCode == RESULT_LOAD_IMAGE  && resultCode == FragmentActivity.RESULT_OK) {

			byte[] byteArray = data.getByteArrayExtra("data");
			newBmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

			if (newBmap != null) {
				BusinessData.setImage(newBmap, byteArray);
				imageOnDisplayImageView.setImageBitmap(newBmap);

			}else {
				Log.e("BusinessDashboardFragment", "ERROR!! The RETURNED BITMAP IS NULL");
			}

		}else{
			Log.e("BusinessDashboardFragment", "ERROR!! return from unknown activity");
			return;
		}

	}


	@Override
	public void onAddNewDealDialogResult(AddDealDialogResult result,String newDealStr) {
		if(result==AddDealDialogResult.USER_APPROVED){
			BusinessData.createNewDeal(newDealStr);
			dealOnDisplayTextView.setText(newDealStr);
		}
	}


}
