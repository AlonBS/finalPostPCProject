package com.dna.radius.login;




import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseUser;

public class MainActivity extends FragmentActivity {

	private final static String APP_ID = "x3jwFIHknyHP4pajQ7P9ottjCnwlnZIPl3JQLNzZ";
	private final static String CLIENT_KEY = "EXlH8MQcMa8l50sAcrL0jSbQlOhGW6MdJAu4hHAA";

	public static final String KEEP_LOGGED = "keepLoggedIn";

	public static final String SP_NAME = "com.dna.radius.SHARED_PREFERENCES";
	public static final String SHOW_WELCOME = "showWelcomeScreen";
	
	
	


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
		boolean isChecked = settings1.getBoolean(KEEP_LOGGED, false);

		checkKeepLoggedIn(isChecked); //TODO change to isChecked

	}


	/*
	 * Sets Parse.com (connecting information to Parse.com cloud).
	 */
	private void setParse() {

		Parse.initialize(getApplicationContext(), APP_ID, CLIENT_KEY);
		Log.d("MainActivity","Parse was initialized");
		//ParseUser.enableAutomaticUser(); TODO should remove?

	}


	private void checkKeepLoggedIn(boolean isChecked) { 


		// call Owner / Client opening screen if 'keep me logged in' is checked
		if (isChecked) {

			ParseUser currentUser = ParseUser.getCurrentUser();

			// not anonymous user - we log in
			if (!ParseAnonymousUtils.isLinked(currentUser)) {

				// User is Verified - Start relevant screen
				if (currentUser != null) { 

					// we lode 'client screen' if last was logged on in this screen, and business otherwise
					int lastMode = currentUser.getInt(ParseClassesNames.LAST_MODE);
					if (lastMode == IntroFragment.CUSTOMER_MODE) {

						Intent intent = new Intent(getApplicationContext(), ClientOpeningScreenActivity.class);
						intent.putExtra(SHOW_WELCOME, false);
						startActivity(intent);
						finish();
					}
					else{

						Intent intent = new Intent(getApplicationContext(), BusinessOpeningScreenActivity.class);
						intent.putExtra(SHOW_WELCOME, false);
						startActivity(intent);
						finish();
					}
				}
			}

			else {

				SharedPreferences settings = getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(KEEP_LOGGED, false);
				editor.commit();

			}
		}

		openLoginScreen();

	}


	private void openLoginScreen() {

		fragmentTransaction = getSupportFragmentManager().beginTransaction();

		fragmentTransaction.replace(R.id.loaded_fragment,  new LoginFragment());

		fragmentTransaction.commit();

	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

}