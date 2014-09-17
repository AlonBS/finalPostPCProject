package com.dna.radius.businessmode;



import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.businessmode.DealHistoryDialogFragment.DealHistoryDialogCommunicator;
import com.dna.radius.businessmode.DealHistoryDialogFragment.DealHistoryDialogResult;
import com.dna.radius.datastructures.Deal;
/**
 * represents the history fragment for the business owner.
 * should contain a list of deal with number of likes and dislikes for each.
 * also, it should allow to choose a deal from the history list and set it instead of the current one.
 * @author dror
 *
 */
public class BusinessHistoryFragment extends Fragment  implements DealHistoryDialogCommunicator{

	private View view;

	private  DealHistoryArrayAdapter adapter;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.business_history_fragment,container, false);	

		//load the comments list


		loadTotalLikesAndDislikes();

		if(BusinessData.dealsHistory==null){
			return view;
		}

		ListView dealHistoryListView = (ListView)view.findViewById(R.id.deal_history_list_view);
		//registerForContextMenu(dealHistoryListView); TODO (alon to dror) - why needed?
		adapter = new DealHistoryArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, BusinessData.dealsHistory.getOldDeals());
		dealHistoryListView.setAdapter(adapter);

		dealHistoryListView.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(final AdapterView<?> adapterView, View v, int position,long arg3) {
				final Deal dealObject = (Deal)adapterView.getItemAtPosition(position);
				displayHistoryDialog(dealObject);
			}
		});

//		dealHistoryListView.setOnItemClickListener(new OnItemClickListener() {
//
//			//TODO mant strings to put into string file DROR
//			@Override
//			public void onItemClick(final AdapterView<?> adapterView, View v, int position,long arg3) {
//
//				final Deal dealObject = (Deal)adapterView.getItemAtPosition(position);
//
//				final TextView chosenDeal = new TextView(getActivity());
//				chosenDeal.setText("You chose the following deal: \n\n" + dealObject.getDealContent() + "\n\n what do you want to do now?");
//
//				new AlertDialog.Builder(getActivity())
//				.setView(chosenDeal)
//				.setPositiveButton(getString(R.string.display_deal_from_history), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//
//						BusinessData.bringDealFromHistory(dealObject);
//						adapter.notifyDataSetChanged();
//
//					}
//				}).setNegativeButton(getString(R.string.delete_deal_from_history), new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int whichButton) {
//
//						BusinessData.deletedDealFromHistory(dealObject);
//						adapter.notifyDataSetChanged();
//
//					}
//				}).show();
//			}
//		});


		return view;
	}
	
	private void displayHistoryDialog(Deal dealObj){
		DealHistoryDialogFragment dialog = new DealHistoryDialogFragment(dealObj,this);
		dialog.show( getActivity().getSupportFragmentManager(),"");
	}


	private void loadTotalLikesAndDislikes() {

		TextView totalLikesTv = (TextView)view.findViewById(R.id.history_fragment_total_likes_tv);
		TextView totalDislikesTv = (TextView)view.findViewById(R.id.history_fragment_total_dislikes_tv);

		String totalLikes = "0", totalDislikes = "0";
		if (BusinessData.dealsHistory != null) {

			totalLikes = Integer.toString(BusinessData.dealsHistory.getNumOfLikes());
			totalDislikes = Integer.toString(BusinessData.dealsHistory.getNumOfDislikes());
		}

		totalLikesTv.setText(" " + totalLikes);
		totalDislikesTv.setText(" " + totalDislikes);

	}


	@Override
	public void onDealHistoryDialogResult(DealHistoryDialogResult result,Deal dealObject) {
		if(result==DealHistoryDialogResult.SET_AS_CURRENT){
			BusinessData.bringDealFromHistory(dealObject);
			adapter.notifyDataSetChanged();

		}else if(result==DealHistoryDialogResult.DELETE_DEAL_OBJECT){
			BusinessData.deletedDealFromHistory(dealObject);
			adapter.notifyDataSetChanged();
		}else if(result==DealHistoryDialogResult.USER_CANCELED){
			
		}else{
			Log.e("BusinessHistoryFragment", "received unknown value from deal history dialog");
		}
	}
}
