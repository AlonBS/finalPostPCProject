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

import com.dna.radius.datastructures.Comment;
import com.example.mapsample.R;

public class CommentsArrayAdapter extends ArrayAdapter<Comment>{
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	private Context context;
	
	
	
	public CommentsArrayAdapter(Context context, int resource, ArrayList<Comment> list) {
		super(context, resource,list);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
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
		 String commentStr = c.getCommentStr();
		 String userNameStr = c.getUserName();		 
		 String dateStr = dateFormat.format(c.getDate().getTime());
		 
		 TextView rowTextView = (TextView)rowView.findViewById(R.id.comment_text_view);
		 
		 String userNameHtml = " <font color=#000000><b> " + userNameStr + ": </font></b>";
		 String commentHtml = "<font color=#000000>" + commentStr + "</font>   ";
		 String dateHtml = "<font color=#0000DD><b>" + dateStr + "<b></font>";
		 
		 rowTextView.setText(Html.fromHtml(userNameHtml + commentHtml + dateHtml));
		 
		 return rowView;
	}

	
}	
