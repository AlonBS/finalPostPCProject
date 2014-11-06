package com.dna.radius.infrastructure;

import java.io.Serializable;

import com.dna.radius.R;

public class SupportedTypes {
	
	public static enum BusinessType implements Serializable{
		
		RESTAURANT(R.drawable.resturant_icon,R.drawable.resturant_icon_owner,"Restaurant"),
		PUB(R.drawable.bar_icon,R.drawable.bar_icon_owner,"Pub"),
		ACCOMMODATION(R.drawable.hotel_icon,R.drawable.hotel_icon_owner,"Accommodation"),
		COFFEE(R.drawable.coffee_icon,R.drawable.coffee_icon_owner,"Coffee"),
		GROCERIES(R.drawable.shopping_icon,R.drawable.shopping_icon_owner, "Groceries");
		
		private int iconDrawableID,ownerIconDrawableID;
		private String stringRep;
		
		private BusinessType(int iconID,int ownerIconDrawableID, String rep){
			this.iconDrawableID = iconID;
			this.ownerIconDrawableID = ownerIconDrawableID;
			this.stringRep = rep;
		}
		
		public boolean equals(BusinessType other) {
			return this.stringRep.equals(other.stringRep);
		}
		
		public String getStringRep(){ return stringRep;	}
		
		public int getIconID() {
			return iconDrawableID;
		}
		
		public int getOwnerIconID() {
			return ownerIconDrawableID;
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
