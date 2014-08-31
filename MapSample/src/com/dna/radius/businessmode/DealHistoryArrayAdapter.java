package com.dna.radius.businessmode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.datastructures.Comment;
import com.dna.radius.datastructures.DealHistoryObject;

/***
 * represents an array adapter view for the comments list.
 * @author dror
 *
 */
public class DealHistoryArrayAdapter extends ArrayAdapter<DealHistoryObject>{
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	
	
	public DealHistoryArrayAdapter(Context context, int resource, ArrayList<DealHistoryObject> list) {
		super(context, resource,list);
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
		 DealHistoryObject deal =  super.getItem(position);
		 TextView dealTextView = (TextView)rowView.findViewById(R.id.deal_hist_text_view);
		 String dealStr = deal.getDealStr();
		 dealTextView.setText(dealStr);
		 
		//sets the deal data (date, num of likes, num of dislikes) into the proper text view
		 TextView dataTextView = (TextView)rowView.findViewById(R.id.deal_hist_data_text_view);
		 String dateStr = dateFormat.format(deal.getDate().getTime());
		 String dateHtml = " <font color=#000000><b> " + dateStr + ",  </font></b> </t>";
		 String numOfLikes = Integer.toString(deal.getNumOfLikes());	
		 String numOfLikesHtml = " <font color=#008800><b> " + numOfLikes + " likes,  </font></b></t>";
		 String numOfDislikes = Integer.toString(deal.getNumOfDislikes());
		 String numOfDislikesHtml = " <font color=#FF0000><b> " + numOfDislikes + " dislikes </font></b></t>";
		 dataTextView.setText(Html.fromHtml(dateHtml + numOfLikesHtml + numOfDislikesHtml));
		 
		 return rowView;
	}

	
}	
