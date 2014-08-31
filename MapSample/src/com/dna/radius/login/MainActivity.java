package com.dna.radius.login;




import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private final static String APP_ID = "x3jwFIHknyHP4pajQ7P9ottjCnwlnZIPl3JQLNzZ";
	private final static String CLIENT_KEY = "EXlH8MQcMa8l50sAcrL0jSbQlOhGW6MdJAu4hHAA";

	static final String SP_NAME = "com.dna.radius.SHARED_PREFERENCES";


	//static FragmentManager fragmentManager;
	static FragmentTransaction fragmentTransaction;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_activity);

		// initializse Parse settings
		setParse();

		// if 'keep me logged in' was previously set - we log in if possible
		SharedPreferences settings1 = getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		boolean isChecked = settings1.getBoolean("isChecked", false);
		
		checkKeepLoggedIn(isChecked);

	}


	/*
	 * Sets a parse (connecting information to parse.com cloud).
	 */
	private void setParse() {

		Parse.initialize(getApplicationContext(), APP_ID, CLIENT_KEY);
		ParseUser.enableAutomaticUser();

	}



	private void checkKeepLoggedIn(boolean isChecked) { 


		// call Owner / Client opening screen if 'keep me logged in' is checked
		if (isChecked) {

			ParseUser currentUser = ParseUser.getCurrentUser();

			// not anonymous user - we log in
			if (!ParseAnonymousUtils.isLinked(currentUser)) {

				if (currentUser != null) {

					//User is Verified - Start relevant screen
					int lastMode = currentUser.getInt(ParseClassesNames.LAST_MODE);
					
					if (lastMode == IntroFragment.CUSTOMER_MODE) {

						// TODO REMOVE
						//Toast.makeText(getApplicationContext(), "Customer MOde", Toast.LENGTH_LONG).show();
							
						Intent intent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);
						startActivity(intent);
						finish();

					}
					else{

						// TODO REMOVE
						//Toast.makeText(getApplicationContext(), "Business MOde", Toast.LENGTH_LONG).show();
						
						Intent intent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
						startActivity(intent);
						finish();
					}
				}
			}
			
			else {
				
				SharedPreferences settings = getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("isChecked", false);
				editor.commit();
				
			}
		}

		//TODO remove 
		Toast.makeText(getApplicationContext(), "No keep logged in", Toast.LENGTH_LONG).show();
		openLoginScreen();

	}

	private void openLoginScreen() {

		fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.replace(R.id.loaded_fragment,  new LoginFragment());

		fragmentTransaction.commit();

	}
	
	public static String getSPName() { return SP_NAME; }
}