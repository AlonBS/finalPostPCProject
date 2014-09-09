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
	private final static int RESULT_LOAD_IMAGE_GALLERY = 1;
	private final static int RESULT_LOAD_IMAGE_CAMERA = 2;

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
		
			/**handles the comments segment*/
			//commentsList = new ArrayList<>();
			ListView commentsListView = (ListView)v.findViewById(R.id.comments_list_view);
			CommentsArrayAdapter commentsAdapter = new CommentsArrayAdapter(parentActivity,android.R.layout.simple_list_item_1 , BusinessData.currentDeal.getComments());
			commentsListView.setAdapter(commentsAdapter);
			DBHandler.loadCommentsListAsync(commentsAdapter,BusinessData.currentDeal.getId());
		}
		
		else {
			
			dealOnDisplayTextView.setText(R.string.tap_to_enter_deal);
			dealOnDisplayLikesTextView.setText("0");
			dealOnDisplayDislikesTextView.setText("0");
		}
	}
	
	
	private void displayImageIfNeeded() {
		
		ProgressBar loadImageProgressBar = (ProgressBar)v.findViewById(R.id.load_image_progress_bar);
		
		if (BusinessData.hasImage()) { //TODO
			
			if (BusinessData.imageFullyLoaded()) {
				imageOnDisplayImageView.setImageBitmap(BusinessData.businessImage);
				imageOnDisplayImageView.setVisibility(View.VISIBLE);
				loadImageProgressBar.setVisibility(View.GONE);
			}else {
				BusinessData.loadImage(imageOnDisplayImageView, loadImageProgressBar);
			}

		}else {
			imageOnDisplayImageView.setVisibility(View.VISIBLE);
			imageOnDisplayImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.set_business_image));
			loadImageProgressBar.setVisibility(View.GONE);
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
		
		/*handles the image of the business*/
		imageOnDisplayImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				new AlertDialog.Builder(parentActivity)
				.setTitle("Choose an image for your business")
				.setMessage("please choose an image source")
				.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
							startActivityForResult(takePictureIntent, RESULT_LOAD_IMAGE_CAMERA);
						}
					}
				}).setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						Intent i = new Intent(
								Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
						startActivityForResult(i, RESULT_LOAD_IMAGE_GALLERY);
					}
				}).show();
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
		DBHandler.close(); //TODO removes
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
		if (requestCode == RESULT_LOAD_IMAGE_GALLERY   && resultCode == FragmentActivity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getActivity().getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			newBmap = BitmapFactory.decodeFile(picturePath);

		}
		else if (requestCode == RESULT_LOAD_IMAGE_CAMERA  && resultCode == FragmentActivity.RESULT_OK) {
			Bundle extras = data.getExtras();
			newBmap = (Bitmap) extras.get("data");
		}else{
			return;
		}

		if (newBmap != null) {
			
			Bitmap processedImage = BusinessChooseImageFragment.processImage(newBmap);
			BusinessData.setImage(processedImage);
			imageOnDisplayImageView.setImageBitmap(processedImage);
			
		}else {
			Log.e("BusinessDashboardFragment", "ERROR!! The RETURNED BITMAP IS NULL");
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
