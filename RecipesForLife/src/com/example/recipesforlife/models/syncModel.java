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

public class syncModel extends baseDataSource
{
	Context context;
	public syncModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}

	String currentDate;

	

	
	public ArrayList<userBean> getUsers()
	{
		  SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		Log.v("Current Date", "Curr" + currentDate);
		Log.v("Last updated", "Last" + sharedpreferences.getString("Date", "DEFAULT"));
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
	
	public ArrayList<accountBean> getAccount()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
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
	

	private accountBean cursorToAccount(Cursor cursor) {
	        accountBean ab = new accountBean();
	        ab.setId(cursor.getInt(getIndex("id",cursor)));       
	        ab.setEmail(cursor.getString(getIndex("email", cursor)));
	        ab.setPassword(cursor.getString(getIndex("password", cursor)));
	        ab.setUpdateTime(cursor.getString(getIndex("updateTime",cursor)));
	        return ab;
	    }
	 
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
	
	public void getAndCreateAccountJSON() throws JSONException
	{
		ArrayList<userBean> userList = getUsers();
		Log.v("USe list" , "User list " + userList.size());
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
	
	public void recieveUserJSON()
	{
		
	}
	
	public void recieveAccountJSON()
	{
		
	}
	
	public void insertUserJSON()
	{
		
	}
	
	public void insertAccountJSON()
	{
		
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
