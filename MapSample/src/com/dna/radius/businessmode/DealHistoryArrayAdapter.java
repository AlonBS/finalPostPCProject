package com.dna.radius.businessmode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.datastructures.Deal;

/***
 * represents an array adapter view for the comments list.
 *
 */
public class DealHistoryArrayAdapter extends ArrayAdapter<Deal>{
	
	
	public DealHistoryArrayAdapter(Context context, int resource, ArrayList<Deal> list) {
		super(context, resource, list);
	}
	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		 View rowView = convertView;
		
		 if(rowView==null){
		  LayoutInflater inflater=LayoutInflater.from(super.getContext());
		  rowView=inflater.inflate(R.layout.deal_history_row, parent, false);
		 
		 }
		 
		 //sets the deal string into the proper text view
		 Deal deal =  super.getItem(position);
		 TextView dealTextView = (TextView)rowView.findViewById(R.id.deal_hist_text_view);
		 String dealStr = deal.getDealContent();
		 dealTextView.setText(dealStr);
		 
		//sets the deal data (date, num of likes, num of dislikes) into the proper text view
		 TextView dataTextView = (TextView)rowView.findViewById(R.id.deal_hist_date_text_view);
		 String dateStr = new SimpleDateFormat(BusinessOpeningScreenActivity.DATE_FORMAT).format(deal.getDealDate().getTime());
		 dataTextView.setText(dateStr);
		 
		 TextView likesTextView = (TextView)rowView.findViewById(R.id.deal_hist_likes_tv);
		 likesTextView.setText(Integer.toString(deal.getNumOfLikes()));
		
		 TextView dislikesTextView = (TextView)rowView.findViewById(R.id.deal_hist_dislikes_tv);
		 dislikesTextView.setText(Integer.toString(deal.getNumOfDislikes()));

		 return rowView;
	}
	
	
}	
