package com.example.mapsample;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.datastructures.BusinessMarker;

public class TopBusinessesHorizontalView extends HorizontalScrollView{
	private Context context;
	private final int NUMBER_OF_TOP_BUSINESSES_MAX = 10;
	private ArrayList<BusinessMarker> topBusinessesList = new ArrayList<>();
	private LinearLayout hostLayout = null;
	
	public TopBusinessesHorizontalView(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	public TopBusinessesHorizontalView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.context = context;
    }
	public TopBusinessesHorizontalView(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle); 	
	    this.context = context;
	}
	public void addBusiness(BusinessMarker bm){
		if(hostLayout == null){
			hostLayout = (LinearLayout)this.findViewById(R.id.top_businesses_linear_layout);
		}
		if(topBusinessesList.size()>NUMBER_OF_TOP_BUSINESSES_MAX){
			Log.e("TopBusinessesHorizontalView", "to many businesses were added into top list");
			return;
		}
		LinearLayout newLayout = (LinearLayout) inflate(context, R.layout.top_businesses_col, hostLayout);
		topBusinessesList.add(bm);
		TextView businessNameTv = (TextView)newLayout.findViewById(R.id.top_business_name);
		TextView businessLikesTv = (TextView)newLayout.findViewById(R.id.top_business_num_of_likes);
		TextView businessDislikesTv = (TextView)newLayout.findViewById(R.id.top_business_num_of_dislikes);
		businessNameTv.setText(bm.name);
		businessLikesTv.setText(Long.toString(bm.numOfLikes) + getResources().getString(R.string.likes));
		businessDislikesTv.setText(Long.toString(bm.numOfDislikes) + getResources().getString(R.string.dislikes));
	}
	

}
