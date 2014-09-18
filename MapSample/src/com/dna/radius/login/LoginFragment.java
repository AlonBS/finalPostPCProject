package com.dna.radius.login;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessOpeningScreenActivity;
import com.dna.radius.clientmode.ClientOpeningScreenActivity;
import com.dna.radius.dbhandling.ParseClassesNames;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	/* This fragment's view*/
	private View v;

	private EditText userNameEditText, userPasswordEditText;

	private Button logInButton, signUpButton; 

	private CheckBox keepLoggedInCheckBox;


	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		v = inflater.inflate(R.layout.login_fragment, container, false);

		initViews();

		setLoginOnClickListener();
		
		setSignUpOnClickListener();
		
		setOnCheckedChangeListener();

		return v;
	}


	private void initViews() {

		userNameEditText = (EditText) v.findViewById(R.id.login_user_name_editText);
		userPasswordEditText = (EditText) v.findViewById(R.id.login_password_editText);
		signUpButton = (Button) v.findViewById(R.id.signUp_button);
		logInButton = (Button) v.findViewById(R.id.login_button);
		keepLoggedInCheckBox = (CheckBox) v.findViewById(R.id.keep_logged_in_checkbox);
	}
	
	
	private void setLoginOnClickListener() {
		
		logInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				logInButton.setEnabled(false);
				String userName = userNameEditText.getText().toString();
				String userPassword = userPasswordEditText.getText().toString();

				// Send data to Parse.com for verification
				ParseUser.logInInBackground(userName, userPassword,
						new LogInCallback() {
					public void done(ParseUser user, ParseException e) {

						if (user != null) {

							//User is Verified - Start relevant screen
							int lastMode = user.getInt(ParseClassesNames.LAST_MODE);

							FragmentActivity mainActivity = getActivity();
							
							if (lastMode == IntroFragment.CUSTOMER_MODE) {

								Intent intent = new Intent(mainActivity.getApplicationContext(), ClientOpeningScreenActivity.class);
								startActivity(intent);
								mainActivity.finish();

							}
							else{

								Intent intent = new Intent(mainActivity.getApplicationContext(), BusinessOpeningScreenActivity.class);
								startActivity(intent);
								mainActivity.finish();
							}


						} else {
							
							showErrorMessage(e);
							// Invalid userName or Password were entered
							
							logInButton.setEnabled(true);
						}
					}
				});
			}
		});
		
	}

	
	private void setSignUpOnClickListener() {

		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

				MainActivity.fragmentTransaction.replace(R.id.loaded_fragment, new IntroFragment());

				MainActivity.fragmentTransaction.addToBackStack(null);

				MainActivity.fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				
				//MainActivity.fragmentTransaction.setCustomAnimations(arg0, arg1, arg2, arg3) TODO - add

				MainActivity.fragmentTransaction.commit();
			}
		});
	}
	

	private void setOnCheckedChangeListener() {

		keepLoggedInCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean(MainActivity.KEEP_LOGGED, isChecked);
				editor.commit();
			}
		});   
	}
	
	void showErrorMessage(ParseException e) {
		
		int error_code = e.getCode();
		String msg; 
		
		switch (error_code) {
		
			case ParseException.CONNECTION_FAILED:
				msg = getResources().getString(R.string.connection_failed);
				break;
				
			case ParseException.USERNAME_MISSING:
				msg = getResources().getString(R.string.username_missing);
				break;
				
			case ParseException.PASSWORD_MISSING:
				msg = getResources().getString(R.string.password_missing);
				break;
				
			case ParseException.VALIDATION_ERROR:
				msg = getResources().getString(R.string.validation_failed);
				break;
				
			default:
				msg = getResources().getString(R.string.unknown_error);
		}
		
		
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_LONG).show();
	}
}


