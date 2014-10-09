package com.dna.radius.mapsample;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dna.radius.R;
import com.dna.radius.clientmode.ClientData;
import com.dna.radius.datastructures.ExternalBusiness;
import com.dna.radius.dbhandling.DBHandler;
import com.dna.radius.infrastructure.BaseActivity;
import com.dna.radius.mapsample.ShowDealActivity.ShowDealFragmentType;

/***
 * this fragment allows the user to like or dislike a certain deal, if he is
 * in a client mode.
 * it is also contains an imageView for the business.
 *
 */
public class LikeAndDislikeFragment extends Fragment{

	private View view; //This fragment's view

	private ClientChoice newChoice, oldChoice;

	private View dislikeBtn;
	private View likeBtn;
	private ShowDealActivity parentActivity;
	private TextView likesTextView;
	private TextView dislikesTextView;
	private ImageView likeImageView;
	private ImageView dislikeImageView;

	private ExternalBusiness pressedExtern;

	private int newNumOfLikes, newNumOfDislikes;

	public enum ClientChoice{LIKE,DISLIKE,DONT_CARE};



	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.like_and_dislike_fragment,container, false);
		parentActivity = (ShowDealActivity)getActivity();
		pressedExtern = parentActivity.getExternalBusiness();

		loadImage();

		boolean hasDeal = loadLikesDislikes();

		if (!hasDeal){
			TextView dealTV = (TextView)view.findViewById(R.id.dealTextView);
			dealTV.setText(getResources().getString(R.string.no_deal_currently));
			return view;
		}
		
		setNewNumbersCounters();

		
		likeImageView = (ImageView)view.findViewById(R.id.like_image_view);
		dislikeImageView = (ImageView)view.findViewById(R.id.dislike_image_view);

		//handles the like and dislikes buttons.
		if(!BaseActivity.isInBusinessMode){
			oldChoice = ClientData.getClientChoiceOnDeal(pressedExtern.getExternBusinessDeal().getId());
			newChoice = oldChoice;
		}

		
		ImageView commentsFragmentBtn = (ImageView)view.findViewById(R.id.commentsFragmentButton);
		commentsFragmentBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				parentActivity.switchToFragment(ShowDealFragmentType.COMMENTS_FRAGMENT);
			}
		});
		
		setLikeBtnOnClickListener();
		setDislikeBtnOnClickListener();
		
		setDeal();
		
		setDislikeAndLikeBG();
		
		return view;
	}


	private void loadImage() {

		//loads the business image		
		ImageView imageView = (ImageView) view.findViewById(R.id.business_image_view);
		DBHandler.loadBusinessImageViewAsync(pressedExtern.getExternBusinessId(), imageView);
	}

	private boolean loadLikesDislikes() {

		likesTextView = (TextView)view.findViewById(R.id.like_text_view);
		dislikesTextView = (TextView)view.findViewById(R.id.dislike_text_view);

		//updates the likes and dislikes text views
		if (pressedExtern.getExternBusinessDeal() != null) {

			likesTextView.setText(Integer.toString(pressedExtern.getExternBusinessDeal().getNumOfLikes()));
			dislikesTextView.setText(Integer.toString(pressedExtern.getExternBusinessDeal().getNumOfDislikes()));
		}
		else {

			likesTextView.setText(ShowDealActivity.EMPTY_DEAL);
			dislikesTextView.setText(ShowDealActivity.EMPTY_DEAL);
			return false;
		}

		return true;
	}
	
	
	private void setNewNumbersCounters() {

		newNumOfLikes = pressedExtern.getExternBusinessDeal().getNumOfLikes();
		newNumOfDislikes = pressedExtern.getExternBusinessDeal().getNumOfDislikes();
		
		ClientChoice c = ClientData.getClientChoiceOnDeal(pressedExtern.getExternBusinessDeal().getId());
		
		if (c == ClientChoice.LIKE && newNumOfLikes == 0) {
			++newNumOfLikes;
			likesTextView.setText(Integer.toString(newNumOfLikes));
			
		}
		if (c == ClientChoice.DISLIKE && newNumOfDislikes == 0){
			++newNumOfDislikes;
			dislikesTextView.setText(Integer.toString(newNumOfDislikes));
		}
		
		
	}

	
	private void setLikeBtnOnClickListener() {

		likeBtn = (View)view.findViewById(R.id.show_deal_like_btn);
		
		likeBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (BaseActivity.isInBusinessMode) {
					parentActivity.createAlertDialog(getString(R.string.no_like_in_bus_mode));
					return;
				}

				if (newChoice == ClientChoice.LIKE) {
					newChoice = ClientChoice.DONT_CARE;
					likesTextView.setText(Integer.toString(--newNumOfLikes));

				} else {

					if (newChoice == ClientChoice.DISLIKE) {
						dislikesTextView.setText(Integer.toString(--newNumOfDislikes));
						
					}

					newChoice = ClientChoice.LIKE;
					likesTextView.setText(Integer.toString(++newNumOfLikes));
				}

				setDislikeAndLikeBG();
			}
		});

	}

	
	private void setDislikeBtnOnClickListener() {

		dislikeBtn = view.findViewById(R.id.show_deal_dislike_btn);
		dislikeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(BaseActivity.isInBusinessMode){
					parentActivity.createAlertDialog("it's impossible to like/dislike a deal on business mode");
					return;
				}
				
				if (newChoice == ClientChoice.DISLIKE) {
					
					newChoice = ClientChoice.DONT_CARE;
					dislikesTextView.setText(Integer.toString(--newNumOfDislikes));
					
				} else {
					
					if (newChoice == ClientChoice.LIKE) {
						
						likesTextView.setText(Integer.toString(--newNumOfLikes));
					}

					newChoice = ClientChoice.DISLIKE;
					dislikesTextView.setText(Integer.toString(++newNumOfDislikes));
				}
				
				setDislikeAndLikeBG();
			}
		});
	}
	
	private void setDeal() {
		
		TextView dealTV = (TextView)view.findViewById(R.id.dealTextView);
		TextView dateTV = (TextView)view.findViewById(R.id.deal_date_text_view);
		if (pressedExtern.getExternBusinessDeal() != null) {
			Date dealDate = pressedExtern.getExternBusinessDeal().getDealDate();
			String dateStr = new SimpleDateFormat(BaseActivity.DATE_FORMAT).format(dealDate);
			dateTV.setText(dateStr);
			dealTV.setText(pressedExtern.getExternBusinessDeal().getDealContent());
		}
		else {
			dealTV.setText(getResources().getString(R.string.no_deal_currently));
		}
	}



	/**
	 * this function changes the backroung of the dislike and like buttons
	 * according to the user preferenced. 
	 */
	private void setDislikeAndLikeBG(){

		if(BaseActivity.isInBusinessMode) {
			return;
		}

		if (newChoice == ClientChoice.LIKE) {

			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_blue));
			likesTextView.setTextColor(getResources().getColor(R.color.green_likes));
			
//			Log.e("HERE", "HERE1");
//			
//			if (likesTextView.getText().toString().compareTo("0") == 0 ) {
//				String s1 = likesTextView.getText().toString();
//				likesTextView.setText("1");
//				Log.e("HERE2", s1);
//			}
//			else 
//			{
//				String s2 = likesTextView.getText().toString();
//				Log.e("HERE3", s2);
//			}

		}else{
			
			likeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_good_black));
			likesTextView.setTextColor(Color.BLACK);
		}

		if (newChoice == ClientChoice.DISLIKE) {
			
			dislikesTextView.setTextColor(getResources().getColor(R.color.red_dislikes));
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_blue));
			
//			if (dislikesTextView.getText().toString().compareTo("0") == 0 ) {
//				dislikesTextView.setText("1");
//			}

		} else {

			dislikesTextView.setTextColor(Color.BLACK);
			dislikeImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_action_bad_black));
		}
	}

			

	private void saveUserActions() {

		if (BaseActivity.isInBusinessMode || oldChoice == newChoice) return;

		String dealId = pressedExtern.getExternBusinessDeal().getId();

		switch (newChoice) { 
			case LIKE:
				ClientData.addToLikes(dealId);
				break;

			case DISLIKE:
				ClientData.addToDislikes(dealId);
				break;

			case DONT_CARE:
				if (oldChoice == ClientChoice.LIKE)
					ClientData.removeFromLikes(dealId);
				else 
					ClientData.removeFromDislikes(dealId);	
				break;

			default:
				Log.e("Like Dislike fragment", "ClientChoice wasn't recognized");
		}
		
		MapManager.updateExternalBusinessLikesAndDislikes(pressedExtern.getExternBusinessId(), newNumOfLikes, newNumOfDislikes);
	}


	@Override
	public void onDestroy() {

		saveUserActions();
		super.onDestroy();
	}
}
