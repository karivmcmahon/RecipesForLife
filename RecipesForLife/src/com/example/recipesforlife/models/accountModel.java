package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class accountModel extends baseDataSource{
	long id;
	public accountModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public void insertAccount(List<String> accountInfo) 
	{ 
			open();
			//Get datetime
			Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
            Date today = cal.getTime();
            String lastUpdated = dateToString(today);
		 	
            //User values
		    ContentValues values = new ContentValues();
		    values.put("name", accountInfo.get(0)); // inserting a string
		  //  values.put("secondName", accountInfo.get(1)); // inserting a string
		    values.put("updateTime", lastUpdated); // inserting a string
		    values.put("country", accountInfo.get(2)); // inserting a string
		    values.put("bio", accountInfo.get(3)); // inserting a string
		    values.put("city", accountInfo.get(4)); // inserting a string
		    values.put("cookingInterest", accountInfo.get(5)); // inserting a string
		    
		    try
		    {	
		    	id = database.insert("Users", null, values);
			} 
		    catch (SQLException e) 
		    {
		        Log.v("db","Exception 1 " +  e.toString());
		    } 
		   
		    //Account values
		    ContentValues accountValues = new ContentValues();
		    accountValues.put("id", (int)id);
		    accountValues.put("email", accountInfo.get(6));
		    accountValues.put("updateTime", lastUpdated);
		    accountValues.put("password", accountInfo.get(7));
		    try
		    {
		    	database.insert("Account", null, accountValues);	    
		    } 
		    catch (SQLException e) 
		    {
		    	Log.v("db","Exception 2 " +  e.toString());
		    } 
		    JSONObject student1 = new JSONObject();
			try {
				student1.put("name", accountInfo.get(0)); // inserting a string
				  //  values.put("secondName", accountInfo.get(1)); // inserting a string
				    student1.put("updateTime", lastUpdated); // inserting a string
				    student1.put("country", accountInfo.get(2)); // inserting a string
				    student1.put("bio", accountInfo.get(3)); // inserting a string
				    student1.put("city", accountInfo.get(4)); // inserting a string
				    student1.put("cookingInterest", accountInfo.get(5)); // in
				    student1.put("id", (int)id);
				    student1.put("email", accountInfo.get(6));
				    student1.put("updateTime", lastUpdated);
				    student1.put("password", accountInfo.get(7));
				    JSONArray jsonArray = new JSONArray();
					jsonArray.put(student1);
				    Log.v("JSON create ", "JSON create " + jsonArray);

			} catch (JSONException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

			
		    close();
	} 
	
	private String dateToString(Date date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	

	

}