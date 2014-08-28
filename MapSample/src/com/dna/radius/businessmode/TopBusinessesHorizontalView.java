package com.dna.radius.businessmode;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dna.radius.datastructures.BusinessMarker;
import com.dna.radius.mapsample.ShowDealActivity;
import com.example.mapsample.R;

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
	

	public boolean addBusiness(final BusinessMarker bm, Bitmap image){
		if(hostLayout == null){
			hostLayout = (LinearLayout)this.findViewById(R.id.top_businesses_linear_layout);
		}
		if(topBusinessesList.size()>NUMBER_OF_TOP_BUSINESSES_MAX){
			Log.e("TopBusinessesHorizontalView", "to many businesses were added into top list");
			return false;
		}
		
		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout newLayout = (RelativeLayout) inflater.inflate(R.layout.top_businesses_col,hostLayout,false);
			hostLayout.addView(newLayout);
		topBusinessesList.add(bm);
		TextView businessNameTv = (TextView)newLayout.findViewById(R.id.top_business_name);
		TextView businessLikesTv = (TextView)newLayout.findViewById(R.id.top_business_num_of_likes);
		TextView businessDislikesTv = (TextView)newLayout.findViewById(R.id.top_business_num_of_dislikes);
		if(businessNameTv!=null){
			businessNameTv.setText(bm.name);
		}
		if(businessLikesTv!=null){
			businessLikesTv.setText(Long.toString(bm.numOfLikes));
		}
		if(businessLikesTv!=null){
			businessDislikesTv.setText(Long.toString(bm.numOfDislikes));
		}
		ImageView imageView = (ImageView)newLayout.findViewById(R.id.business_image_top_businesses);
		if(imageView!=null){
			imageView.setImageBitmap(image);
		}
		
		newLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(context, ShowDealActivity.class);
				myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, bm.name); //Optional parameters
				myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, bm.businessId); //Optional parameters
				myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, bm.type); //Optional parameters
				myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, bm.numOfStars); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, bm.numOfDislikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, bm.numOfLikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, false);
				//myIntent.putExtra(ShowDealActivity.BUSINESS_MARKER_PARAM, bMarker);
				//myIntent.putExtra(ShowDealActivity.BUSINESS_MARKER_PARAM, bMarker);
				context.startActivity(myIntent);
			}
		});
		
		return true;
	}
	

}
