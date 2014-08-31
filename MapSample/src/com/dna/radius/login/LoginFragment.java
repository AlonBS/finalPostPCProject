package com.dna.radius.login;

import com.dna.radius.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginFragment extends Fragment {

	/* This fragment's view*/
	private View v;


	private EditText userEmailEditText, userPasswordEditText;

	private Button logInButton, signUpButton; 

	private CheckBox keepLoggedInCheckBox;
	private boolean isChecked;





	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		v = inflater.inflate(R.layout.login_fragment, container, false);

		initViews();

		setOnClickListeners();

		return v;
	}





	private void initViews() {

		userEmailEditText = (EditText) v.findViewById(R.id.login_user_name_editText);
		userPasswordEditText = (EditText) v.findViewById(R.id.login_password_editText);
		signUpButton = (Button) v.findViewById(R.id.signUp_button);
		logInButton = (Button) v.findViewById(R.id.login_button);
		keepLoggedInCheckBox = (CheckBox) v.findViewById(R.id.keep_logged_in_checkbox);
		isChecked = false;

	}

	private void setOnClickListeners() {

		logInButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {


				String userEmail = userEmailEditText.getText().toString();
				String userPassword = userPasswordEditText.getText().toString();

				// Send data to Parse.com for verification
				ParseUser.logInInBackground(userEmail, userPassword,
						new LogInCallback() {
					public void done(ParseUser user, ParseException e) {

						if (user != null) {

							//User is Verified - Start relevant screen
							int lastMode = user.getInt(IntroFragment.MODE_KEY);

							if (lastMode == IntroFragment.CUSTOMER_MODE) {
								
								
								// TODO REMOVE
								Toast.makeText(getActivity().getApplicationContext(), "Customer MOde was last logged in", Toast.LENGTH_LONG).show();


								//Intent intent = new Intent(LoginSignupActivity.this, Welcome.class);
								//startActivity(intent);

								//TODO prompt Welcome Screen

							}
							else{
								
								// TODO REMOVE
								Toast.makeText(getActivity().getApplicationContext(), "business MOde was last logged in", Toast.LENGTH_LONG).show();


								

								//TODO Start owner screen - main by dror

							}



						} else {

							// Invalid Email or Password were entered
							Toast.makeText(getActivity().getApplicationContext(),
									getString(R.string.incorrect_info),
									Toast.LENGTH_LONG).show();
						}
					}
				});
			}
		});


		signUpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MainActivity.fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();

				MainActivity.fragmentTransaction.replace(R.id.loaded_fragment, new IntroFragment());

				MainActivity.fragmentTransaction.addToBackStack(null);

				MainActivity.fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

				MainActivity.fragmentTransaction.commit();
			}
		});

		keepLoggedInCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = settings.edit();
				editor.putBoolean("isChecked", isChecked);
				editor.commit();
			}
		});   
	}
}



/*
 * 
 * 
 * 	 * TODO Determine if user already exist (signed up + checked box)

		final Button customerBtn = (Button) findViewById(R.id.customer_mode_button);
		final Button businessBtn = (Button) findViewById(R.id.business_mode_button);

		customerBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent signUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
				signUpActivity.putExtra(MODE_KEY, CUSTOMER_MODE);

				startActivity(signUpActivity);
				finish();
			}
		});


		businessBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent signUpActivity = new Intent(getApplicationContext(), SignUpActivity.class);
				signUpActivity.putExtra(MODE_KEY, BUSINESS_MODE);

				startActivity(signUpActivity);
				finish();

			}
		});



	}




 */

