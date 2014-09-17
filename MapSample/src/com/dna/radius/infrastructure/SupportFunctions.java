package com.dna.radius.infrastructure;

public class SupportFunctions {
	
	public static int getRatingBarColor(int numOfStars){
		final int gap = 0xFF;
		
		int currentGreen = 00 + (gap / 6) * 0xFF;
		int currentRed = 0xFF - (gap / 6) * 0xFF;
		
		String redHexRep = Integer.toHexString(currentRed);
		if(redHexRep.length() == 1){
			redHexRep = "0" + redHexRep;
		}
		String greenHexRep = Integer.toHexString(currentGreen);
		if(greenHexRep.length() == 1){
			greenHexRep = "0" + greenHexRep;
		}
		
		String colorHexRep = "#FF" + redHexRep + greenHexRep + "00";
		int retColor = android.graphics.Color.parseColor(colorHexRep);
		return retColor;
	}
}
