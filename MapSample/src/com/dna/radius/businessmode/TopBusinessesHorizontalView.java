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

import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.mapsample.ShowDealActivity;
import com.dna.radius.R;

/***
 * a costum view which extends an horizontal scroll view.
 * is used for showing the top deals list.
 *
 */
public class TopBusinessesHorizontalView extends HorizontalScrollView{
	private Context context;
	/**maximum number of business which will be shown in the view*/
	private final int NUMBER_OF_TOP_BUSINESSES_MAX = 10;
	/**the list of all the businesses*/
	private ArrayList<ExternalBusiness> topBusinessesList = new ArrayList<>();
	private LinearLayout hostLayout = null;

	/**C-tor*/
	public TopBusinessesHorizontalView(Context context) {
		super(context);
		this.context = context;
	}
	/**C-tor*/
	public TopBusinessesHorizontalView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
	}
	/**C-tor*/
	public TopBusinessesHorizontalView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle); 	
		this.context = context;
	}

	/**Add a business into the top list*/
	public boolean addBusiness(final ExternalBusiness bm, Bitmap image){
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

		//whenever a business is pressed - opens a ShowDealActivity.
		newLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(context, ShowDealActivity.class);
				myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, bm.name); //Optional parameters
				myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, bm.businessId); //Optional parameters
				myIntent.putExtra(ShowDealActivity.DEAL_ID_PARAM, bm.currentDealID); 
				myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, bm.type); //Optional parameters
				myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, bm.numOfStars); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, bm.numOfDislikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, bm.numOfLikes); //Optional parameters
				myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, false);
				context.startActivity(myIntent);
			}
		});

		return true;
	}


}
