package com.example.mapsample;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dbhandling.DBHandler;

public class BusinessDashboardFragment extends Fragment{
		private DBHandler dbHandler;
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View view = inflater.inflate(R.layout.business_dashboard_fragment,container, false);	
			dbHandler = new DBHandler(getActivity());
			TextView dealTv = (TextView) view.findViewById(R.id.deal_tv);
			
			adsfasfd
			dbHandler.loadDealAsync(businessID, textView);
			return view;
		
		
		
		}
		
		@Override
		public void onDestroyView() {
		// TODO Auto-generated method stub
			super.onDestroyView();
			if(dbHandler!=null){
				dbHandler.close();
				dbHandler = null;
			}
		}
}
