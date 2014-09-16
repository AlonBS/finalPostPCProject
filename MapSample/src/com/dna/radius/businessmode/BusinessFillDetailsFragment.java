package com.dna.radius.businessmode;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.businessmode.BusinessSettingsActivity.EditTextOnTouchListenerWithinTabhost;
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;

public class BusinessFillDetailsFragment extends Fragment{

	private EditText businessNameEditText, businessAddressEditText, businessPhoneEditText;
	private TextView businessNameTextView, businessPhoneTextView,  businessAddressTextView,  businessTypeTextView;

	private Spinner businessTypeSpinner;

	private SupportedTypes.BusinessType businessType = null;

	private ArrayAdapter<BusinessType> spinnerAdapter;


	/***this parameters represents a boolean flag which is true if the fragment is inflated
	inside of the settings scope. in this case - the edit texts should ve filled with
	the current user details.*/
	public static String IS_IN_SETTINGS_MODE_PARAM = "isInSettingsModeParam";
	public static String BUSINESS_NAME_HINT_PARAM = "businessNameParam";
	public static String BUSINESS_TYPE_HINT_PARAM = "businessTypeParam";
	public static String BUSINESS_PHONE_HINT_PARAM = "businessPhoneParam";
	public static String BUSINESS_ADDRESS_HINT_PARAM = "businessAddressParam";
	
	//default color is white
	public static String TEXT_COLOR_PARAM = "textColorParam";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.business_fill_details_fragment,container, false);	
		
		//retrives the relevant views
		businessNameEditText = (EditText) view.findViewById(R.id.business_name_edit_text);
		businessPhoneEditText = (EditText) view.findViewById(R.id.business_phone_edit_text);
		businessAddressEditText = (EditText) view.findViewById(R.id.business_address_edit_text);
		businessTypeSpinner = (Spinner) view.findViewById(R.id.business_type_spinner);
		
		businessNameTextView = (TextView) view.findViewById(R.id.business_name_text_view);
		businessPhoneTextView = (TextView) view.findViewById(R.id.business_phone_text_view);
		businessAddressTextView = (TextView) view.findViewById(R.id.business_address_text_view);
		businessTypeTextView = (TextView) view.findViewById(R.id.business_type_text_view);
		
		
		businessAddressEditText.setOnTouchListener(new EditTextOnTouchListenerWithinTabhost());
		businessNameEditText.setOnTouchListener(new EditTextOnTouchListenerWithinTabhost());
		businessPhoneEditText.setOnTouchListener(new EditTextOnTouchListenerWithinTabhost());
		
		
		if(getArguments().containsKey(TEXT_COLOR_PARAM)){
			int color = getArguments().getInt(TEXT_COLOR_PARAM);
			businessNameEditText.setTextColor(color);
			businessPhoneEditText.setTextColor(color);
			businessAddressEditText.setTextColor(color);
			
			businessNameTextView.setTextColor(color);
			businessPhoneTextView.setTextColor(color);
			businessAddressTextView.setTextColor(color);
			businessTypeTextView.setTextColor(color);
		}
		boolean changeCurrentSettingsMode = getArguments().getBoolean(IS_IN_SETTINGS_MODE_PARAM);
		if(changeCurrentSettingsMode){
			//receives the input arguments
			String businessNameHint = getArguments().getString(BUSINESS_NAME_HINT_PARAM);
			BusinessType typeHint = (BusinessType)getArguments().getSerializable(BUSINESS_TYPE_HINT_PARAM);
			String businessAddressHint = getArguments().getString(BUSINESS_ADDRESS_HINT_PARAM);
			String businessPhoneHint = getArguments().getString(BUSINESS_PHONE_HINT_PARAM);
			
			//sets the hints accordingly
			businessNameEditText.setHint(businessNameHint);
			businessType = typeHint;
			setBusinessTypeSpinner(businessType);
			
			int spinnerPosition = spinnerAdapter.getPosition(typeHint);
			businessTypeSpinner.setSelection(spinnerPosition);

			if(businessPhoneHint!=null && !businessPhoneHint.equals("")){
				businessPhoneEditText.setHint(businessPhoneHint);
			}

			if(businessAddressHint!=null && !businessAddressHint.equals("")){
				businessAddressEditText.setHint(businessAddressHint);
			}

			//since we are on the settings menu, no message is requires
			TextView welcomeMessage = (TextView)view.findViewById(R.id.welcome_business_textView);
			welcomeMessage.setVisibility(View.GONE);
		}else{
			setBusinessTypeSpinner(null);
		}
		
		
		return view;
	}


	private void setBusinessTypeSpinner(BusinessType hintType) {


		ArrayList<SupportedTypes.BusinessType> businessTypes = new ArrayList<SupportedTypes.BusinessType>();
		for(BusinessType b : BusinessType.values()){
			businessTypes.add(b);
		}
		
		if(getArguments().containsKey(TEXT_COLOR_PARAM)){
			int color = getArguments().getInt(TEXT_COLOR_PARAM);
			spinnerAdapter = new SpinnerTypeAdapter(getActivity(), R.layout.spinner_types_layout, businessTypes,getResources(),hintType,color);
		}else{
			spinnerAdapter = new SpinnerTypeAdapter(getActivity(), R.layout.spinner_types_layout, businessTypes,getResources(),hintType);
		}
		businessTypeSpinner.setAdapter(spinnerAdapter);
		businessTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> data, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				businessType = (BusinessType)data.getItemAtPosition(pos);

				if (businessType == null) {
					Log.d("BusinessWelcomeFillDerailsFragment", "chosen spinner option is null");
				}else{
					Log.d("BusinessWelcomeFillDerailsFragment", "chosen spinner option is " + businessType.getStringRep());
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});


	}

	/**
	 * return true iff the user filled all the relevant data
	 * @return
	 */
	public boolean neededInfoGiven() {

		String businessName = businessNameEditText.getText().toString();

		if (businessName.isEmpty() || illegalBusinessName(businessName)) {
			Toast.makeText(getActivity(), getResources().getString(R.string.business_name_forgot_to_fill), Toast.LENGTH_SHORT).show();
			return false;
		}
		if (businessType == null) {
			Toast.makeText(getActivity(), getResources().getString(R.string.business_type_forgot_to_fill), Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;

	}

	private boolean illegalBusinessName(String bn) {

		//TODO business name filter? alpha-numeric only ? 
		return false;

	}

	public String getBusinessName(){
		return businessNameEditText.getText().toString();
	}

	public SupportedTypes.BusinessType getBusinessType(){
		return businessType;
	}

	public String getBusinessPhoneNumber(){

		return businessPhoneEditText.getText().toString();
	}

	public String getBusinessAddress(){

		return businessAddressEditText.getText().toString();
	}




}
