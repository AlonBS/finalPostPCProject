package com.dna.radius.businessmode;


import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dna.radius.R;
import com.dna.radius.datastructures.Deal;
import com.dna.radius.dbhandling.DBHandler;
/**
 * represents the history fragment for the business owner.
 * should contain a list of deal with number of likes and dislikes for each.
 * also, it should allow to choose a deal from the history list and set it instead of the current one.
 * @author dror
 *
 */
public class BusinessHistoryFragment extends Fragment{

	private DealHistoryArrayAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.business_history_fragment,container, false);	
		final BusinessOpeningScreenActivity parentActivity = (BusinessOpeningScreenActivity)getActivity();
		//load the comments list
		ListView dealHistoryListView = (ListView)view.findViewById(R.id.deal_history_list_view);
		registerForContextMenu(dealHistoryListView);
		ArrayList<Deal> dealHistoryList = parentActivity.ownerData.dealHistory;
		adapter = new DealHistoryArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, dealHistoryList);
		dealHistoryListView.setAdapter(adapter);
		
		dealHistoryListView.setOnItemClickListener(new OnItemClickListener() {
			//TODO mant strings to put into string file DROR
			@Override
			public void onItemClick(AdapterView<?> adapter, View v, int position,long arg3) {
				final Deal dealObject = (Deal)adapter.getItemAtPosition(position);
				Log.d("BusinessHistoryFragment", "pressed on the following deal: " + dealObject.getContent());
				final TextView chosenDeal = new TextView(getActivity());
				//chosenDeal.setBackgroundColor(Color.BLACK);
				//chosenDeal.setTextColor(Color.WHITE);
				chosenDeal.setText("You chose the following deal: \n\n" + dealObject.getContent() + "\n\n what do you want to do now?");
				
				new AlertDialog.Builder(getActivity())
				//.setTitle("You chose the following deal: \n" + dealObject.getDealStr())
				.setView(chosenDeal)
				//.setMessage("what would you like to do next?")
				.setPositiveButton("delete it from history", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
						
						//DBHandler.deletedDealFromHistory(parentActivity.ownerData.businessID,dealObject);
						
						//TODO 
						//BusinessData.deleteDealFromHistory();
					}
				}).setNegativeButton("set as current deal", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						
					}
				}).show();
			
			}
		});

		TextView totalLikesTv = (TextView)view.findViewById(R.id.history_fragment_total_likes_tv);
		TextView totalDislikesTv = (TextView)view.findViewById(R.id.history_fragment_total_dislikes_tv);
		totalLikesTv.setText(totalLikesTv.getText()+ " " + adapter.getTotalLikes());
		totalDislikesTv.setText(totalDislikesTv.getText()+ " " + adapter.getTotalDislikes());
		return view;
	}

}
