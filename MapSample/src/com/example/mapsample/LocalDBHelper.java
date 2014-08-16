package com.example.mapsample;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class LocalDBHelper extends SQLiteOpenHelper{
	public LocalDBHelper(Context context) {
		super(context, DB_NAME, null, 1);	
	}

	public static final String DB_NAME = "DEALS_APP_DB";
	
	public static final String ID_COL = "ID";	
	public static final String FAVOURITES_TABLE = "FAVOURITES";
	public static final String FAVOURITES_COL = "BUSINESS_ID";
	
	public static final String HOME_TABLE = "USER_HOME";
	public static final String LONG_COL =  "LONG_COL";
	public static final String LAT_COL = "LAT_COL";	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
	    db.execSQL("create table " + FAVOURITES_TABLE + " ( " +
	    	      "  " + ID_COL + " integer primary key autoincrement, " +
	    	      "  " + FAVOURITES_COL + " long);");


	    db.execSQL("create table " + HOME_TABLE + " ( " +
	    	      "  " + ID_COL + " integer primary key autoincrement, " +
	    	      "  " + LAT_COL + " double, " +
	    	      "  " + LONG_COL + " double);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
