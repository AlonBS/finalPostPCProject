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

	private EditText businessNameEditText;

	private Spinner businessTypeSpinner;

	private Button finishBtn;

	private String businessName;
	private int businessType;
	private LatLng businessLocation;

	boolean typeSelected = false, locationSelected = false;
	
	private static final int RESULT_LOAD_IMAGE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.business_welcome_dialog_activity);

		setScreenSize();

		initViews();

		setBusinessTypeSpinner();

		ImageView nextBtn = (ImageView)findViewById(R.id.next_btn);
		nextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout oldLayout = (LinearLayout)findViewById(R.id.business_welcome_details_layout);
				oldLayout.setVisibility(View.GONE);
				
				final FragmentManager fragmentManager = getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				LocationFinderFragment findLocationFragment = new LocationFinderFragment();
				fragmentTransaction.add(R.id.business_welcome_main_fragment_layout, findLocationFragment);
				fragmentTransaction.commit();
			}
		});

		//setFinishBtnListener();

	}

	private void setScreenSize() {

		// This will set this dialog-themed activity to take 80% of the screen
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int screenWidth = (int) (metrics.widthPixels * 0.9);
		int screenHeight = (int) (metrics.heightPixels * 0.8);
		getWindow().setLayout(screenWidth, screenHeight);
	}

	private void initViews() {

		businessNameEditText = (EditText) findViewById(R.id.business_name_textView);
		businessTypeSpinner = (Spinner) findViewById(R.id.business_type_spinner);
		finishBtn = (Button) findViewById(R.id.finish_btn);

	}

	private void setBusinessTypeSpinner() {

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(),
				R.array.business_type_spinner, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		businessTypeSpinner.setAdapter(adapter);

		businessTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view, 
					int pos, long id) {
				
				String typeStr = parent.getItemAtPosition(pos).toString();
				
				if (typeStr.compareToIgnoreCase(BuisnessType.RESTAURANT.getStringRep()) == 0){
					
					businessType = BuisnessType.RESTAURANT.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.PUB.getStringRep()) == 0){
					
					businessType = BuisnessType.PUB.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.COFFEE.getStringRep()) == 0){
					
					businessType = BuisnessType.COFFEE.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.GROCERIES.getStringRep()) == 0){
					
					businessType = BuisnessType.GROCERIES.getParseID();
				}
				else if (typeStr.compareToIgnoreCase(BuisnessType.ACCOMMODATION.getStringRep()) == 0){
					
					businessType = BuisnessType.ACCOMMODATION.getParseID();
				}
			}

			public void onNothingSelected(AdapterView<?> parent) {
				//TODO REMOVE
				Toast.makeText(getApplicationContext(), "PLEASE MAKE A SELECTION MADAFACKA", Toast.LENGTH_SHORT).show();
				
				typeSelected = false;
			}

		});

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

		businessName = businessNameEditText.getText().toString();

		if (businessName.isEmpty() || illegalBusinessName(businessName)) {

			//TODO maybe different message?
			Toast.makeText(getApplicationContext(), "PUT BUSINESS!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!typeSelected) {

			//TODO maybe different message?
			Toast.makeText(getApplicationContext(), "PUT TYPE!", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (!locationSelected) {

			//TODO maybe different message?
			Toast.makeText(getApplicationContext(), "PUT LOCATIN MAFAFACKA!", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;

	}

	private boolean illegalBusinessName(String bn) {

		//TODO business name filter? alpha-numeric only ? 
		return false;

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
