package com.dna.radius.infrastructure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.dna.radius.R;

public class GeneralSettingsFragment extends Fragment{


	private EditText userNameEditText;
	private EditText newPasswordEditText;
	private EditText newPasswordConformationEditText;
	private EditText emailEditText;
	private String currentUserName;
	private String currentEmail;

	public GeneralSettingsFragment(String currentUserName, String currentEmail){
		this.currentUserName = currentUserName;
		this.currentEmail = currentEmail;
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.general_settings_fragment,container, false);

		userNameEditText = (EditText)view.findViewById(R.id.user_name_edit_text);
		newPasswordEditText = (EditText)view.findViewById(R.id.password_edit_text);
		newPasswordConformationEditText = (EditText)view.findViewById(R.id.password_confirmation_edit_text);
		emailEditText = (EditText)view.findViewById(R.id.email_address_edit_text);

		userNameEditText.setHint(currentUserName);
		emailEditText.setHint(currentEmail);

		return view;
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




