package com.dna.radius.businessmode;

import java.util.ArrayList;

import com.dna.radius.R;
import com.dna.radius.datastructures.ExternalBusiness.BuisnessType;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.dna.radius.login.MainActivity;
import com.dna.radius.mapsample.LocationFinderFragment;
import com.dna.radius.mapsample.WaitingFragment;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

public class BusinessWelcomeActivity extends FragmentActivity {


	private Button finishBtn;

	private String businessName;
	
	private int businessType;
	
	private LatLng businessLocation;

	private boolean locationSelected = false;
	
	private static final int RESULT_LOAD_IMAGE = 1;
	
	private Fragment currentFragment = null;
	
	/**
	 * This integer and constants were meant to tell which fragment should be 
	 * loaded whenever the next button is pressed.*/
	private int numberOfTimesNextWasPressed = 0;
	private final int FIND_LOCATION_FRAGEMENT_TURN = 0;
	private final int GET_IMAGE_FRAGEMENT_TURN = 1;
	private final int FINISH_FILLING_DETAILS= 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_welcome_dialog_activity);

		setScreenSize();
		
		setNextBtn();
		
		final FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		BusinessWelcomeFillDetailsFragment businessWelcomeFragment = new BusinessWelcomeFillDetailsFragment();
		fragmentTransaction.add(R.id.business_welcome_main_fragment_layout, businessWelcomeFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		numberOfTimesNextWasPressed--;
	}
	
	private void setNextBtn(){

		ImageView nextBtn = (ImageView)findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				if(numberOfTimesNextWasPressed==FIND_LOCATION_FRAGEMENT_TURN){
					
					//tests if it's possible to move to the next fragment
					final FragmentManager fragmentManager = getSupportFragmentManager();
					BusinessWelcomeFillDetailsFragment currentFragment = (BusinessWelcomeFillDetailsFragment)fragmentManager.findFragmentById(R.id.business_welcome_main_fragment_layout);
					if(!currentFragment.didUserFillAllData()){
						return;
					}
					
					numberOfTimesNextWasPressed++;
					
					//retrives the business name and type
					businessName = currentFragment.getBusinessName();
					businessType = currentFragment.getBusinessType();
					
					//moves to the next fragment
					FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
					LocationFinderFragment findLocationFragment = new LocationFinderFragment();
					fragmentTransaction.replace(R.id.business_welcome_main_fragment_layout, findLocationFragment);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.commit();
				}else if(numberOfTimesNextWasPressed==FIND_LOCATION_FRAGEMENT_TURN){
					
				}else if(numberOfTimesNextWasPressed==FINISH_FILLING_DETAILS){
					
				}else{
					Log.e("BusinessWelcomeActivity", "error with the next button. numberOfTimesNextWasPressed:" + numberOfTimesNextWasPressed);
				}
			}
		});

		
	}
	
	private void setScreenSize() {

		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.8);
		getWindow().setLayout(screenWidth, screenHeight);
	}



	private void setChooseLocationListener() {
		ImageView mapImageView = null; //TODO delete
		mapImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO  DROR  - how to open map in here!? 
				businessLocation = new LatLng(32,23); // should be taken from map

				if (/*legal coordinates were taken from map*/ true) {

					locationSelected = true;

				}
				else {
					
					locationSelected = false;
				}
			}
		});

	}

	private void setChoosePictureListener() {

		ImageView choosePicImageView = null; //TODO delete
		choosePicImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);

			}
		});

	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
			
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();

			ImageView imageView = (ImageView) findViewById(R.id.buisness_image_view);
			imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
			

		}
	}


	private void setFinishBtnListener() {

		finishBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (finishedFillingAllData()) {

					//save to parse user new location and close
					finishRegistration(); //TODO data recived from map
					finish(); // activity


				}

			}
		});

	}

	private boolean finishedFillingAllData() {

		
		if (!locationSelected) {

			//TODO maybe different message?
			Toast.makeText(getApplicationContext(), "PUT LOCATIN MAFAFACKA!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;

	}






	// todo LATLING ?
	private void finishRegistration() { 

		ParseObject newClient = new ParseObject(ParseClassesNames.BUSINESS_CLASS);
		
		newClient.put(ParseClassesNames.BUSINESS_NAME, businessName);
		newClient.put(ParseClassesNames.BUSINESS_TYPE, businessType);
		
		//TODO add picture to parse - using parseFile
		//newClient.put(ParseClassesNames.BUSINESS_PICTURE, businessLocation);

		ArrayList<Double> coordinates = new ArrayList<Double>();
		coordinates.add(businessLocation.latitude);
		coordinates.add(businessLocation.longitude);
		newClient.put(ParseClassesNames.BUSINESS_LOCATION, coordinates);

		// add a pointer in user to business. i.e. user->businessData
		ParseUser currentUser = ParseUser.getCurrentUser();
		currentUser.put(ParseClassesNames.BUSINESS_INFO, newClient);

		newClient.saveInBackground();
		currentUser.saveInBackground();
	}

	
	//@Override
	//public void onBackPressed() {
	//}
}
