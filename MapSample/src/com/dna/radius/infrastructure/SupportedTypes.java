package com.dna.radius.infrastructure;

import java.io.Serializable;

import com.dna.radius.R;

public class SupportedTypes {
	
	public static enum BusinessType implements Serializable{
		
		RESTAURANT(R.drawable.resturant_icon,"Restaurant"),
		PUB(R.drawable.bar_icon,"Pub"),
		ACCOMMODATION(R.drawable.hotel_icon,"Accommodation"),
		COFFEE(R.drawable.coffee_icon,"Coffee"),
		GROCERIES(R.drawable.shopping_icon, "Groceries");
		
		private int iconDrawableID;
		private String stringRep;
		
		private BusinessType(int iconID, String rep){
			this.iconDrawableID = iconID;
			this.stringRep = rep;
		}
		
		public boolean equals(BusinessType other) {
			return this.stringRep.equals(other.stringRep);
		}
		
		public String getStringRep(){ return stringRep;	}
		
		public int getIconID() {
			return iconDrawableID;
		}
		
		public static BusinessType stringToType(String str){
			if (str.compareToIgnoreCase(RESTAURANT.stringRep) == 0){
				return RESTAURANT;
			}
			else if (str.compareToIgnoreCase(PUB.stringRep) == 0){
				return PUB;
			}
			else if (str.compareToIgnoreCase(COFFEE.stringRep) == 0){
				return COFFEE;
			}
			else if (str.compareToIgnoreCase(GROCERIES.stringRep) == 0){
				return GROCERIES;
			}
			else if (str.compareToIgnoreCase(ACCOMMODATION.stringRep) == 0){
				return ACCOMMODATION;
			}
			return null;
		}

	}

}
