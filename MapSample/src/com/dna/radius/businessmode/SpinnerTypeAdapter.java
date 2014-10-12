package com.dna.radius.businessmode;

import java.util.ArrayList;

import com.dna.radius.R;
import com.dna.radius.infrastructure.SupportedTypes.BusinessType;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

/***** Adapter class extends with ArrayAdapter ******/
public class SpinnerTypeAdapter extends ArrayAdapter<BusinessType>{

	private Activity activity;
	private ArrayList<BusinessType> data;
	public Resources res;
	LayoutInflater inflater;
	int textColor = Color.WHITE;
	BusinessType hintType = null;

	public SpinnerTypeAdapter(
			Activity activitySpinner, 
			int textViewResourceId,   
			ArrayList<BusinessType> data,
			Resources resLocal, BusinessType hintType
			) 
	{
		super(activitySpinner, textViewResourceId, data);
		this.activity = activitySpinner;
		this.data = data;

		data.add(0,null);
		this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}


	public SpinnerTypeAdapter(Activity activitySpinner, int textViewResourceId,   
			ArrayList<BusinessType> data, Resources resLocal, BusinessType hintType, int textColor) 
	{
		super(activitySpinner, textViewResourceId, data);
		this.activity = activitySpinner;
		this.data = data;
		data.add(0,null);
		this.inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.textColor = textColor;
		this.hintType = hintType;
	}



	@Override
	public View getDropDownView(int position, View convertView,ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustomView(position, convertView, parent);
	}

	// This funtion called for each row ( Called data.size() times )
	public View getCustomView(int position, View convertView, ViewGroup parent) {

		/********** Inflate spinner_rows.xml file for each row ( Defined below ) ************/
		View row = inflater.inflate(R.layout.spinner_types_layout, parent, false);

		/***** Get each Model object from Arraylist ********/
		BusinessType currentType = (BusinessType) data.get(position);

		TextView label = (TextView)row.findViewById(R.id.spinner_item_text_view);

		ImageView icon = (ImageView)row.findViewById(R.id.spinner_item_image_view);

		if (position == 0) {

			// Default selected Spinner item 
			label.setTextColor(new EditText(getContext()).getHintTextColors());
			//label.setText(activity.getResources().getString(R.string.business_type));
			
		} else {
			
			//makes the current type to be colored like a hint
			if (hintType == null || !currentType.equals(hintType)) {
				label.setTextColor(textColor);
			} else {
				label.setTextColor(new EditText(getContext()).getHintTextColors());
			}
			
			// Set values for spinner each row 
			label.setText(currentType.getStringRep());
			icon.setImageResource(currentType.getIconID());
		}   

		return row;
	}
}