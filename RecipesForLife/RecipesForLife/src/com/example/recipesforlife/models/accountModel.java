package com.example.recipesforlife.models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;








import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
	String str;
	ContentValues values;
	ContentValues accountValues;
	String lastUpdated;
	
	public accountModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Code to insert sign up information into sqlite database
	 * @param accountInfo
	 */
	public void insertAccount(List<String> accountInfo) 
	{
			open();
			//Get datetime
			Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
            Date today = cal.getTime();
            lastUpdated = dateToString(today);	 	
            insertUserData(accountInfo);
		    close();
	} 
	
	/**
	 * Converts date into string
	 * @param date
	 * @return
	 */
	private String dateToString(Date date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	/**
	 * Check if details provided are an account
	 * @param email
	 * @param password
	 * @return true if an account or false if not
	 */
	public boolean logIn(String email, String password) {
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=? AND password=?", new String[] { email, password });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			close();
			return true;
		}
		else
		{
			cursor.close();
			close();
			return false;
		} 	
	}
	
	/**
	 * Create a json with information entered in app to send to SQL on the server database
	 * @param accountInfo
	 */
	public void createAndSendJSON(List<String> accountInfo)
	{
		 JSONObject account = new JSONObject();
			try 
			{
					account.put("name", accountInfo.get(0)); // inserting a string 
				    account.put("country", accountInfo.get(2)); // inserting a string
				    account.put("bio", accountInfo.get(3)); // inserting a string
				    account.put("city", accountInfo.get(4)); // inserting a string
				    account.put("cookingInterest", accountInfo.get(5)); // in
				    account.put("id", (int)id);
				    account.put("email", accountInfo.get(6));
				    account.put("updateTime", lastUpdated);
				    account.put("password", accountInfo.get(7));
				    JSONArray jsonArray = new JSONArray();
					jsonArray.put(account);
				
					HttpResponse response = null;
			        HttpClient myClient = new DefaultHttpClient();
			        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm1.aspx");      	   	
					try 
					{
						myConnection.setEntity(new ByteArrayEntity(
								jsonArray.toString().getBytes("UTF8")));
						try 
						{
							response = myClient.execute(myConnection);
							str = EntityUtils.toString(response.getEntity(), "UTF-8");
							
						} 
						catch (ClientProtocolException e) 
						{							
							e.printStackTrace();
						} 
					catch (IOException e) 
					{
						e.printStackTrace();
					}
				} 
				catch (UnsupportedEncodingException e) 
				{						
					e.printStackTrace();
				}
					
			} 
			catch (JSONException e) 
			{
			    e.printStackTrace();
			}
	}
	
	/**
	 * Insert user info into the user sqlite table
	 * @param accountInfo
	 */
	public void insertUserData(List<String> accountInfo)
	{
		 //User values
	    values = new ContentValues();
	    values.put("name", accountInfo.get(0)); 
	    values.put("updateTime", lastUpdated); 
	    values.put("country", accountInfo.get(2)); 
	    values.put("bio", accountInfo.get(3)); 
	    values.put("city", accountInfo.get(4)); 
	    values.put("cookingInterest", accountInfo.get(5));     
	    try
	    {	
	    	id = database.insert("Users", null, values);
	    	insertAccountData(accountInfo, id);
		} 
	    catch (SQLException e) 
	    {
	        Log.v("db","Exception 1 " +  e.toString());
	    } 
	}
	
	/**
	 * Insert account info into account sqlite table
	 * @param accountInfo
	 * @param id
	 */
	public void insertAccountData(List<String> accountInfo, long id)
	{
		//Account values
	    accountValues = new ContentValues();
	    accountValues.put("id", (int)id);
	    accountValues.put("email", accountInfo.get(6));
	    accountValues.put("updateTime", lastUpdated);
	    accountValues.put("password", accountInfo.get(7));
	    try
	    {
	    	database.insert("Account", null, accountValues);
	    	createAndSendJSON(accountInfo);
	    } 
	    catch (SQLException e) 
	    {
	    	Log.v("db","Exception 2 " +  e.toString());
	    } 
	}
	

	

}