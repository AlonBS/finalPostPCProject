package com.dna.radius.mapsample;

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
import com.dna.radius.infrastructure.BaseActivity;

/***
 * represents an array adapter view for the comments list.
 * @author dror
 *
 */
public class CommentsArrayAdapter extends ArrayAdapter<Comment>{
	
	
	
	public CommentsArrayAdapter(Context context, int resource, ArrayList<Comment> list) {
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
		  rowView=inflater.inflate(R.layout.comment_row, parent, false);
		 
		 }
		 
		 Comment c =  super.getItem(position);
		 String authorName = c.getAuthorName();		
		 String commentContent = c.getCommentContent();
		 String dateStr = new SimpleDateFormat(BaseActivity.DATE_FORMAT).format(c.getCommentDate()); 
		 
		 
		 TextView rowTextView = (TextView)rowView.findViewById(R.id.comment_text_view);
		 
		 String userNameHtml = " <font color=#000000><b> " + authorName + ": </font></b>";
		 String commentHtml = "<font color=#000000>" + commentContent + "</font>   ";
		 String dateHtml = "<font color=#0000DD><b>" + dateStr + "<b></font>";
		 
		 rowTextView.setText(Html.fromHtml(userNameHtml + commentHtml + dateHtml));
		 
		 return rowView;
	}

	
}	
