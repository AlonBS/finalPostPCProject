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
import com.dna.radius.infrastructure.SupportedTypes;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;

public class BusinessFillDetailsFragment extends Fragment{

	private EditText businessNameEditText, businessAddressEditText, businessPhoneEditText;

	private Spinner businessTypeSpinner;

	private SupportedTypes.BusinessType businessType;

	/**these parameters are used for changing the default hints whenever the business is already
	 * exists, and the user wants to change his settings*/
	private String businessNameHint;
	private BusinessType typeHint;
	private String businessPhoneHint;
	private String businessAddressHint;

	private ArrayAdapter<BusinessType> adapter;

	private boolean changeCurrentSettingsMode = false;


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.business_fill_details_fragment,container, false);	

		businessNameEditText = (EditText) view.findViewById(R.id.business_name_textView);
		businessTypeSpinner = (Spinner) view.findViewById(R.id.business_type_spinner);
		businessPhoneEditText = (EditText) view.findViewById(R.id.business_phone_textView);
		businessAddressEditText = (EditText) view.findViewById(R.id.business_address_textView);
		setBusinessTypeSpinner();

		if(changeCurrentSettingsMode){
			businessNameEditText.setHint(businessNameHint);

			businessType = typeHint;
			int spinnerPosition = adapter.getPosition(typeHint);
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
		}

		return view;
	}


	private void setBusinessTypeSpinner() {

		ArrayList<SupportedTypes.BusinessType> businessTypes = new ArrayList<SupportedTypes.BusinessType>();
		for(BusinessType b : BusinessType.values()){
			businessTypes.add(b);
		}
		adapter = new SpinnerTypeAdapter(getActivity(), R.layout.spinner_types_layout, businessTypes,getResources());
		businessTypeSpinner.setAdapter(adapter);
		businessTypeSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> data, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				String typeStr = data.getItemAtPosition(pos).toString();
				businessType = SupportedTypes.BusinessType.stringToType(typeStr);

				if (businessType == null) {
					Log.e("BusinessWelcomeFillDerailsFragment", "error choosing a spinner option");
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

		/**
		 * this function is called from the settings activity and is used to set a hint for the fragment.
		 * the phoneNumber and address parameters can be null or empty strings. in this case, the hint won't change for these fields.
		 */
		public void setHint(String businessNameHint,SupportedTypes.BusinessType typeHint,  String businessPhoneHint, String businessAddressHint){
			changeCurrentSettingsMode = true;
			this.businessNameHint = businessNameHint;
			this.typeHint = typeHint;

			this.businessPhoneHint = businessPhoneHint;
			this.businessAddressHint = businessAddressHint;

		}



	}
