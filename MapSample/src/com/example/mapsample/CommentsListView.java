package com.example.mapsample;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CommentsListView extends ArrayAdapter<String>{
	
	
	public CommentsListView(Context context, int resource, ArrayList<String> list) {
		super(context, resource,list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View row = convertView;
		LayoutInflater inflater=LayoutInflater.from(super.getContext());
		row = inflater.inflate(R.layout.comment_row, parent, false);

		return row;
	}


}
