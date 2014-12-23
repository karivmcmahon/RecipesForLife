package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class accountModel extends baseDataSource{
	public accountModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void insertAccount(List<String> accountInfo) 
	{ 
			open();
			Log.v("NAME2", "NAME2 " + accountInfo.get(0));
			Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
            Date today = cal.getTime();
            String lastUpdated = dateToString(today);
		 	Log.v("NAME2", "NAME2 " + accountInfo.get(0));
		    ContentValues values = new ContentValues();
		   // values.put("id", 7);
		    values.put("firstName", accountInfo.get(0)); // inserting a string
		    values.put("secondName", accountInfo.get(1)); // inserting a string
		    values.put("updateTime", lastUpdated); // inserting a string
		    values.put("country", accountInfo.get(2)); // inserting a string
		    values.put("bio", accountInfo.get(3)); // inserting a string
		    values.put("city", accountInfo.get(4)); // inserting a string
		    values.put("cookingInterest", accountInfo.get(5)); // inserting a string
		    Log.v("NAME2", "NAME2 " + accountInfo.get(0));
		    try
		    {
		    	
		    database.insert("Users", null, values);
		    
	} catch (SQLException e) {
        Log.v("dv", e.toString());
    } 
		   // database.close(); // Closing database connection 
		    close();
	} 
	
	private String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}

	

}
