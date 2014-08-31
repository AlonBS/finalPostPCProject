package com.dna.radius.businessmode;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dna.radius.R;
/**
 * represents the history fragment for the business owner.
 * should contain a list of deal with number of likes and dislikes for each.
 * also, it should allow to choose a deal from the history list and set it instead of the current one.
 * @author dror
 *
 */
public class BusinessHistoryFragment extends Fragment{
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.business_history_fragment,container, false);	
		return view;
	}
}
