package com.example.mapsample;

import com.example.dbhandling.DBHandler;
import com.example.dbhandling.DBHandler.DealLikeStatus;

import android.app.ActivityManager;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DealPresentorFragment extends Fragment{
	private Bitmap Image;
	private DBHandler dbHandle;
	private DealLikeStatus dealStatus;
	private ImageView dislikeBtn;
	private View likeBtn;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.deal_presantor_fragment,container, false);
		
		//DBHandler.loadBusinessBitmapAsync(businessID, buisnessType, imageView, context);
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);
		//imageView.setImageBitmap(Image);
		
		ShowDealActivity activityParent = (ShowDealActivity)getActivity();
		if(!activityParent.isInUserMode){
			LinearLayout buttonsLayout = (LinearLayout)view.findViewById(R.id.buttonsLayout);
			buttonsLayout.setVisibility(View.GONE);
		}
		
		dbHandle = new DBHandler(getActivity());
		final long businessID  = activityParent.businessID;
		dbHandle.loadBusinessImageViewAsync(businessID, activityParent.bType, imageView);
	
		final TextView likesText = (TextView)view.findViewById(R.id.like_counter);
		likesText.setText(Long.toString(activityParent.numOfLikes));
		final TextView dislikesText = (TextView)view.findViewById(R.id.dislike_counter);
		dislikesText.setText(Long.toString(activityParent.numOfDislikes));
		dealStatus = dbHandle.getDealLikeStatus(businessID);
		likeBtn = (ImageView)view.findViewById(R.id.sounds_cool_btn);
		likeBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(dealStatus==DealLikeStatus.LIKE){
					dealStatus = DealLikeStatus.DONT_CARE;
					
					String newStr = Long.toString(Long.parseLong(likesText.getText().toString())-1);
					likesText.setText(newStr);
					dbHandle.setDontCareToDeal(businessID);
				}else{
					if(dealStatus==DealLikeStatus.DISLIKE){
						String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())-1);
						dislikesText.setText(newStr);
					}
					
					dealStatus = DealLikeStatus.LIKE;
					dbHandle.addLikeToDeal(businessID);
					String oldText =likesText.getText().toString();
					String newStr = Long.toString(Long.parseLong(oldText)+1);
					likesText.setText(newStr);
				}
				setDislikeAndLikeBG();
			}
		});
		dislikeBtn = (ImageView)view.findViewById(R.id.no_thanks_btn);
		dislikeBtn.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				if(dealStatus==DealLikeStatus.DISLIKE){
					dealStatus = DealLikeStatus.DONT_CARE;
					dbHandle.setDontCareToDeal(businessID);
					String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())-1);
					dislikesText.setText(newStr);
				}else{
					if(dealStatus==DealLikeStatus.LIKE){
						String newStr = Long.toString(Long.parseLong(likesText.getText().toString())-1);
						likesText.setText(newStr);
					}
					dealStatus = DealLikeStatus.DISLIKE;
					dbHandle.addDislikeToDeal(businessID);
					String newStr = Long.toString(Long.parseLong(dislikesText.getText().toString())+1);
					dislikesText.setText(newStr);
				}
				setDislikeAndLikeBG();
				
			}
		});
		setDislikeAndLikeBG();
		return view;
	}
	
	public void setDislikeAndLikeBG(){
		if(dealStatus==DealLikeStatus.LIKE){
			likeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_on));
		}else{
			likeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_off));
		}
		if(dealStatus==DealLikeStatus.DISLIKE){
			dislikeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_on));
		}else{
			dislikeBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.like_buttons_background_off));
		}
		
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		dbHandle.close();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//TODO load images and data here
		//Image = BitmapFactory.decodeResource(getResources(), R.drawable.burger);
	}
}
