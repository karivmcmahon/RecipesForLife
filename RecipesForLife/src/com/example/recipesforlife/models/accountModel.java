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






import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.userBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Class handles database details relating to the users account
 * @author Kari
 *
 */
public class accountModel extends baseDataSource
{
	long id;
	String str;
	ContentValues values;
	ContentValues accountValues;
	String lastUpdated;
	Context context;
	syncModel sync;
	
	public accountModel(Context context) {
		super(context);
		// TODO Auto-generated constructor st
		this.context = context;
		sync = new syncModel(context);
	}

	/**
	 * Code to insert sign up information into sqlite database
	 * @param accountInfo
	 */
	public void insertAccount(List<String> accountInfo) 
	{
			open();
			Calendar cal = Calendar.getInstance(); // creates calendar
            cal.setTime(new Date()); // sets calendar time/date
            Date today = cal.getTime();
            lastUpdated = dateToString(today);	
            database.beginTransaction();
            try
            {
            	insertUserData(accountInfo);
            	database.setTransactionSuccessful();
            	database.endTransaction(); 
            	Log.v("suc", "suc");
            }catch(SQLException e)
            {
            	database.endTransaction();
            	Log.v("Trans fail", "Trans fail");
            }
		    close();
	} 
	
	public ArrayList<accountBean> selectAccount(String email, String password)
	{
		open();
		ArrayList<accountBean> accountList = new ArrayList<accountBean>();
	        Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=? AND password=?", new String[] { email, password  });
	        if (cursor != null && cursor.getCount() > 0) {
	            for (int i = 0; i < cursor.getCount(); i++) {
	                cursor.moveToPosition(i);
	                accountList.add(sync.cursorToAccount(cursor));
	            }
	        }
	        cursor.close();
	        close();
	        return accountList;
	}
	
	public ArrayList<userBean> selectUser(int id)
	{
		open();
		 ArrayList<userBean> userList = new ArrayList<userBean>();
	        Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE id=?", new String[] { Integer.toString(id) });
	        if (cursor != null && cursor.getCount() > 0) {
	            for (int i = 0; i < cursor.getCount(); i++) {
	                cursor.moveToPosition(i);
	                userList.add(sync.cursorToUser(cursor));
	            }
	        }
	        cursor.close();
	        close();
	        return userList;
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
	 * Checks if email is already in use
	 * @param email
	 * @return true if in use and false if not
	 */
	public boolean checkEmail(String email )
	{
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=?", new String[] { email });
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
							Toast.makeText(context, 
					        	    "Error with sync", Toast.LENGTH_LONG).show();
							Log.v("Error", "Error with executing connection");
							e.printStackTrace();
						} 
					catch (IOException e) 
					{
						Toast.makeText(context, 
				        	    "Error with sync", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
				} 
				catch (UnsupportedEncodingException e) 
				{	
					Toast.makeText(context, 
			        	    "Error with sync", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
					
			} 
			catch (JSONException e) 
			{
				Toast.makeText(context, 
		        	    "Error with sync", Toast.LENGTH_LONG).show();
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
    	id = database.insertOrThrow("Users", null, values);
    	insertAccountData(accountInfo, id);
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
	    database.insertOrThrow("Account", null, accountValues);
	}
	

	

}