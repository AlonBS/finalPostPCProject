package com.dna.radius.businessmode;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.dna.radius.R;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.mapsample.CommentsArrayAdapter;
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
		BusinessOpeningScreenActivity parentActivity = (BusinessOpeningScreenActivity)getActivity();
		//load the comments list
		ListView dealHistoryListView = (ListView)view.findViewById(R.id.deal_history_list_view);
		registerForContextMenu(dealHistoryListView);
		ArrayList<DealHistoryObject> dealHistoryList = parentActivity.ownerData.dealHistory;
		DealHistoryArrayAdapter adapter = new DealHistoryArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, dealHistoryList);
		dealHistoryListView.setAdapter(adapter);

		return view;
	}
}
