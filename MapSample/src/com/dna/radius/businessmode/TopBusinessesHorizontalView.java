package com.dna.radius.businessmode;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.mapsample.ShowDealActivity;

/***
 * a costum view which extends an horizontal scroll view.
 * is used for showing the top deals list.
 *
 */
public class TopBusinessesHorizontalView extends HorizontalScrollView{
	
	private Context context;
	/**maximum number of business which will be shown in the view*/
	
	private int topCounter = 0; 
	public static final int MAX_TOP_BUSINESSES = 10;
	
	
	/**the list of all the businesses*/
	
	//private SparseArray<ExternalBusiness> intToExterns; TODO remove
	//private ArrayList<ExternalBusiness> topBusinessesList = new ArrayList<>();
	
	private LinearLayout hostLayout = null;

	//TODO remove
//	/**C-tor*/
//	public TopBusinessesHorizontalView(Context context) {
//		super(context);
//		this.context = context;
//		
//		//intToExterns = new SparseArray<ExternalBusiness>();
//	}
	
	
	/**C-tor*/
	public TopBusinessesHorizontalView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.context = context;
		
		//intToExterns = new SparseArray<ExternalBusiness>(); TODO remove
	}
	
	//TODO remove
//	/**C-tor*/
//	public TopBusinessesHorizontalView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle); 	
//		this.context = context;
//	}
	

	/**Add a business into the top list*/
	public boolean addBusiness(final ExternalBusiness eb) {
		
		if(hostLayout == null){
			hostLayout = (LinearLayout)this.findViewById(R.id.top_businesses_linear_layout);
		}
		
		if (topCounter > MAX_TOP_BUSINESSES) {
			Log.e("TopBusinessesHorizontalView", "to many businesses were added into top list");
			return false;
		}

		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout newLayout = (LinearLayout) inflater.inflate(R.layout.top_businesses_col,hostLayout,false);
		
		//intToExterns.append(newLayout.hashCode(), eb); todo remove
		hostLayout.addView(newLayout);
		
		TextView businessNameTextView = (TextView) newLayout.findViewById(R.id.top_business_name);
		TextView businessTotalLikesTextView = (TextView) newLayout.findViewById(R.id.top_business_num_of_likes);
		TextView businessTotalDislikesTextView = (TextView) newLayout.findViewById(R.id.top_business_num_of_dislikes);
		//TextView businessTotalDealsTextView = (TextView) newLayout.findViewById(R.id.blabla); TODO support if needed
		RatingBar businessRatingBar = (RatingBar) newLayout.findViewById(R.id.top_business_rating_bar);
		
		if (businessNameTextView != null) {
			businessNameTextView.setText(eb.getExtenBusinessName());
		}
		if (businessTotalLikesTextView != null) {
			businessTotalLikesTextView.setText(Integer.toString(eb.getExternBusinessTotalLikes()));
			
		}
		if (businessTotalDislikesTextView != null) {
			businessTotalDislikesTextView.setText(Integer.toString(eb.getExternBusinessTotalDislikes()));
		}
//TODO support if needed
//		if (businessTotalDeals != null {
//			businessTotalDealsTextView.setText(Integer.toString(eb.getTotalNumberOfDeal()));
//			
//		}
		if (businessRatingBar != null) {
			businessRatingBar.setRating((float)eb.getExternBusinessRating());
		}

//TODO not nedded
//businessRatingBar.isIndicator()();
//		// overrides rating bar's on touch method so it won't change anything
//		businessRatingBar.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) { return true; }
//		});
		

		//whenever a business is pressed - opens a ShowDealActivity.
		newLayout.setOnClickListener(new TopDealsOnClickListener(context, eb));
		++topCounter;
		
		return true;
	}
	
	
	private static class TopDealsOnClickListener implements OnClickListener {

		private Context c;
		private ExternalBusiness topBusiness;
		
		public TopDealsOnClickListener(Context c, ExternalBusiness eb) {
			
			this.c = c;
			this.topBusiness = eb;
		}
		
		@Override
		public void onClick(View v) {
			
			
			Intent myIntent = new Intent(c, ShowDealActivity.class);
			myIntent.putExtra(ShowDealActivity.EXTERNAL_BUSINESS_KEY, topBusiness);
			c.startActivity(myIntent);
			
			
//			myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, eb.businessName); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, eb.externBusinessId); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.DEAL_ID_PARAM, eb.currentDealID); 
//			myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, eb.externBusinessType); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, eb.rating); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, eb.numOfDislikes); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, eb.numOfLikes); //Optional parameters
//			myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, false);
//			myIntent.putExtra(ShowDealActivity.PHONE_STR_PARAM, eb.phoneStr); 
//			myIntent.putExtra(ShowDealActivity.ADDRESS_STR_PARAM, eb.addressStr);
//			myIntent.putExtra(ShowDealActivity.CURRENT_DEAL_STR_PARAM, eb.currentDealStr);
			
			
		}
		
		
		
	}
}






//{
//	@Override
//	public void onClick(View v) {
//		
//		ExternalBusiness eb = intToExterns.get(v.hashCode());
//		
//		Intent myIntent = new Intent(context, ShowDealActivity.class);
//		myIntent.putExtra(ShowDealActivity.EXTERNAL_BUSINESS_KEY, eb);
//		context.startActivity(myIntent);
		
		
//		myIntent.putExtra(ShowDealActivity.BUSINESS_NAME_PARAM, eb.businessName); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.BUSINESS_ID_PARAM, eb.externBusinessId); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.DEAL_ID_PARAM, eb.currentDealID); 
//		myIntent.putExtra(ShowDealActivity.BUSINESS_TYPE_PARAM, eb.externBusinessType); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.DEAL_RATING_PARAM, eb.rating); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.NUM_OF_DISLIKES_PARAM, eb.numOfDislikes); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.NUM_OF_LIKES_PARAM, eb.numOfLikes); //Optional parameters
//		myIntent.putExtra(ShowDealActivity.USER_MODE_PARAM, false);
//		myIntent.putExtra(ShowDealActivity.PHONE_STR_PARAM, eb.phoneStr); 
//		myIntent.putExtra(ShowDealActivity.ADDRESS_STR_PARAM, eb.addressStr);
//		myIntent.putExtra(ShowDealActivity.CURRENT_DEAL_STR_PARAM, eb.currentDealStr);
		
//	}
//});
