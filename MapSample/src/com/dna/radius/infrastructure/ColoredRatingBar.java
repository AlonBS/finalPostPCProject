package com.dna.radius.infrastructure;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RatingBar;

public class ColoredRatingBar extends RatingBar {

	public ColoredRatingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setRating(float rating) {
		super.setRating(rating);
		setColor();
		
	}

	public void setColor(){
		int color = getRatingBarColor(getRating());
		LayerDrawable stars = (LayerDrawable) getProgressDrawable();
		stars.getDrawable(2).setColorFilter(color,PorterDuff.Mode.SRC_ATOP);
	}

	/**the red and blur values are constant. the cahnge will be with the green values only*/
	public static int getRatingBarColor(float numOfStars){
		int maxGreen = 0xcc;
		float multFactor = numOfStars==0? 0 : numOfStars / 5;
		int greenNumRep = (int) (00 + (maxGreen * multFactor));
		String greenHexRep = Integer.toHexString(greenNumRep);
		if(greenHexRep.length() == 1){
			greenHexRep = "0" + greenHexRep;
		}
		if(greenHexRep.length() > 2){
			greenHexRep = greenHexRep.substring(greenHexRep.length()-2,greenHexRep.length()-1);
		}
		
		String redHexRep = "FF";
		String blueHexRep = "21";

		String colorHexRep = "#FF" + redHexRep + greenHexRep + blueHexRep;
		try{
			int retColor = android.graphics.Color.parseColor(colorHexRep);
			Log.d("ColoredRatingBar", "given color was: " + colorHexRep);
			return retColor;
		}catch(Exception e){
			e.printStackTrace();
			Log.e("ColoredRatingBar", "couldn't decide on color. given color was: " + colorHexRep);
			return Color.RED;
		}
		
		//final float gap = 0xcc;
//		String redHexRep = Integer.toHexString(currentRed);
//		if(redHexRep.length() == 1){
//			redHexRep = "0" + redHexRep;
//		}
//		if(redHexRep.length() > 2){
//			redHexRep = redHexRep.substring(redHexRep.length()-2,redHexRep.length()-1);
//		}
//		

	}

}
