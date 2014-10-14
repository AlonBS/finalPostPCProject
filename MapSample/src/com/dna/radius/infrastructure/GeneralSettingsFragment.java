package com.dna.radius.infrastructure;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessSettingsActivity;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.login.MainActivity;
import com.dna.radius.map.CommentsFragment;

public class GeneralSettingsFragment extends Fragment{

	private View v;


	private EditText userNameEditText;
	private EditText newPasswordEditText;
	private EditText newPasswordConformationEditText;
	private EditText emailEditText;
	private Button deleteAccountButton;

	public static String USER_NAME_PARAM = "uesrNameParameter";
	public static String EMAIL_PARAM = "emailParameter";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.general_settings_fragment, container, false);

		initViews();

		//TODO NEEDED? (current working without it)
		//setOnTouchListeners();

		setDeleteAccountOnClickListener();

		displayHints();

		return v;
	}

	
	private void initViews() {

		userNameEditText = (EditText)v.findViewById(R.id.user_name_edit_text);
		newPasswordEditText = (EditText)v.findViewById(R.id.password_edit_text);
		newPasswordConformationEditText = (EditText)v.findViewById(R.id.password_confirmation_edit_text);
		emailEditText = (EditText)v.findViewById(R.id.email_address_edit_text);
		deleteAccountButton = (Button)v.findViewById(R.id.delete_acount_button);
	}

	
	//TODO DELETE METHOD
	private void setOnTouchListeners() {

		//TODO DELETE
		userNameEditText.setOnTouchListener(new BusinessSettingsActivity.EditTextOnTouchListenerWithinTabhost());
		newPasswordEditText.setOnTouchListener(new BusinessSettingsActivity.EditTextOnTouchListenerWithinTabhost());
		newPasswordConformationEditText.setOnTouchListener(new BusinessSettingsActivity.EditTextOnTouchListenerWithinTabhost());
		emailEditText.setOnTouchListener(new BusinessSettingsActivity.EditTextOnTouchListenerWithinTabhost());
	}


	private void setDeleteAccountOnClickListener() {

		deleteAccountButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(getActivity())
				.setTitle(getResources().getString(R.string.delete_account))
				.setMessage(getResources().getString(R.string.delete_account_instructions))
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						/*DO NOTHING */
					}
				})
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {

						DBHandler.deleteUserAccount();

						// remove 'keep logged in' 
						SharedPreferences settings = getActivity().getSharedPreferences(MainActivity.SP_NAME, Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(MainActivity.KEEP_LOGGED, false);
						editor.commit();

						// Reset comments-to-time map
						CommentsFragment.restartCommentsHistory();

						// back to 'log in' screen
						Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
						startActivity(intent);

						getActivity().finish();
					}

				}).show();
			}
		});
	}
	
	
	private void displayHints() {
		
		Bundle b = getArguments();
		
		if (b == null) {
			Log.e("General Settings", "Unable to fetch bundle");
			return;
		}
		
		String currentUserName = b.getString(USER_NAME_PARAM);
		if (currentUserName == null) {
			Log.e("General Settings", "Unable to fetch user name from bundle");
			return;
		}
		
		String currentEmail = b.getString(EMAIL_PARAM);
		if (currentEmail == null) {
			Log.e("General Settings", "Unable to fetch user email address from bundle");
			return;
		}
		
		userNameEditText.setHint(currentUserName);
		emailEditText.setHint(currentEmail);
	}


	public String getUserName(){
		return userNameEditText.getText().toString();

	}
	public String getEmail(){
		return emailEditText.getText().toString();
	}

	public String getPassword(){
		return newPasswordEditText.getText().toString();
	}

	public String getConformationPassword(){
		return newPasswordConformationEditText.getText().toString();
	}
}	




