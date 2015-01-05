package com.example.recipesforlife.models;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import com.example.recipesforlife.controllers.userBean;
import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.views.MainActivity;
import com.example.recipesforlife.views.SignUpSignInActivity;

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

/**
 * This class syncs the sever and phone databases
 * @author Kari
 *
 */
public class syncModel extends baseDataSource
{
	Context context;
	String currentDate;
	public syncModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets	the users that have been added after last sync datetime
	 * @return the list of users
	 */
	public ArrayList<userBean> getUsers()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
		 ArrayList<userBean> userList = new ArrayList<userBean>();
	        Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE datetime(updateTime) > datetime(?)", new String[] { sharedpreferences.getString("Date", "DEFAULT")  });
	        if (cursor != null && cursor.getCount() > 0) {
	            for (int i = 0; i < cursor.getCount(); i++) {
	                cursor.moveToPosition(i);
	                userList.add(cursorToUser(cursor));
	            }
	        }
	     cursor.close();
	     close();
	     return userList;
	}
	
	/**
	 * Gets the accounts that have been added after last sync datetime
	 * @return the list of accounts
	 */
	public ArrayList<accountBean> getAccount()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
		 ArrayList<accountBean> accountList = new ArrayList<accountBean>();
	        Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE datetime(updateTime) > datetime(?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });
	        if (cursor != null && cursor.getCount() > 0) {
	            for (int i = 0; i < cursor.getCount(); i++) {
	                cursor.moveToPosition(i);
	                accountList.add(cursorToAccount(cursor));
	            }
	        }
	        cursor.close();
	        close();
	        return accountList;
	}
	
	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return accountBean
	 */
	private accountBean cursorToAccount(Cursor cursor) {
	        accountBean ab = new accountBean();
	        ab.setId(cursor.getInt(getIndex("id",cursor)));       
	        ab.setEmail(cursor.getString(getIndex("email", cursor)));
	        ab.setPassword(cursor.getString(getIndex("password", cursor)));
	        ab.setUpdateTime(cursor.getString(getIndex("updateTime",cursor)));
	        return ab;
	    }
	 
	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	 private userBean cursorToUser(Cursor cursor) {
	        userBean ub = new userBean();
	        ub.setId(cursor.getInt(getIndex("id",cursor)));       
	        ub.setName(cursor.getString(getIndex("name", cursor)));
	        ub.setBio(cursor.getString(getIndex("bio", cursor)));
	        ub.setCity(cursor.getString(getIndex("city", cursor)));
	        ub.setCountry(cursor.getString(getIndex("country", cursor)));
	        ub.setCookingInterest(cursor.getString(getIndex("cookingInterest", cursor)));
	        return ub;
	    }
	
	 /**
	  * Create a json with the recently added account info to send to server
	  * @throws JSONException
	  */
	public void getAndCreateAccountJSON() throws JSONException
	{
		ArrayList<userBean> userList = getUsers();
		ArrayList<accountBean> accountList = getAccount();
		JSONArray jsonArray = new JSONArray();
		
		for(int i = 0; i < userList.size(); i++)
		{
			JSONObject account = new JSONObject();
			account.put("email",  accountList.get(i).getEmail());
			account.put("password", accountList.get(i).getPassword());
			account.put("name", userList.get(i).getName());
			account.put("bio", userList.get(i).getBio());
			account.put("country", userList.get(i).getCountry());
			account.put("city", userList.get(i).getCity());
			account.put("cookingInterest", userList.get(i).getCookingInterest());
			account.put("updateTime", accountList.get(i).getUpdateTime());
			jsonArray.put(account);			
		} 
	sendJSONToServer(jsonArray);
	}
	
	/**
	 * Sends the json with account info to the server
	 * @param jsonArray
	 */
	public void sendJSONToServer(JSONArray jsonArray)
	{
		String str = "";
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
				Log.v("RESPONSE", "RESPONSE " + str);
				
			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
	}
	
	/**
	 * Gets the json with it's sync info from the server
	 * @throws JSONException
	 */
	public void getJSONFromServer() throws JSONException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		date.put("updateTime", sharedpreferences.getString("Date", "DEFAULT") );
		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
        HttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm2.aspx");      	   	
		try 
		{
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				
			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
			} 
			
			JSONArray jArray = new JSONArray(str);
            if (jArray.length() != 0) 
            {
                json = jArray.getJSONObject(0);
                for (int i = 0; i < jArray.length(); i++) 
                {
                	List<String> accountInfo = new ArrayList<String>();
                    json = jArray.getJSONObject(i);
                    accountInfo.add(json.getString("name"));
                    accountInfo.add(json.getString("name"));
                    accountInfo.add(json.getString("country"));
                    accountInfo.add(json.getString("bio"));
                    accountInfo.add(json.getString("city"));
                    accountInfo.add(json.getString("cookingInterest"));
                    accountInfo.add(json.getString("email"));
                    accountInfo.add(json.getString("password"));
        			accountModel accountmodel = new accountModel(context);
        			accountmodel.insertAccount(accountInfo);
                }
            }
                    
                    
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	private String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	public void getCurrentDate()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date today = cal.getTime();
        currentDate = dateToString(today);
	}
	
	
}
