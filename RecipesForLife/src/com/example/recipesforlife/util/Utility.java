package com.example.recipesforlife.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Utility class handling common methods for multiple classes - this is mainly for common mehtods used in the
 * model classes
 * @author Kari
 *
 */
public class Utility  {

	public Utility()
	{


	}

	/**
	 * Convert date to string
	 * 
	 * @param date
	 * @return String	string with date
	 */
	@SuppressLint("SimpleDateFormat")
	private String dateToString(Date date, boolean inappstring) {
		SimpleDateFormat formatter;
		Calendar cal = Calendar.getInstance();
		      TimeZone tz = TimeZone.getTimeZone("Europe/London");	     
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		 formatter.setTimeZone(tz);
		String currentDate = formatter.format(cal.getTime());
		return currentDate;
	}



	/**
	 * Get current date and time
	 * 
	 * @param appstring		whether request is coming from app
	 */
	public String getLastUpdated(boolean appstring)
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
		cal.setTime(new Date()); // sets calendar time/date
		Date today = cal.getTime();
		String lastUpdated = dateToString(today, appstring);
		return lastUpdated;
	}



	/**
	 * Generates UUID then adds the name and type of table - to create a more detailed unique id
	 * 
	 * @param addedBy		Who is inserting into the db
	 * @param table			Table for which the uuid request is coming from
	 * @return String		Unique ID
	 */
	public String generateUUID(String addedBy, String table, SQLiteDatabase database ) {
		final String uuid = UUID.randomUUID().toString();
		String uniqueid = addedBy + table + uuid;
		boolean exists = selectUUID(table, uniqueid,database);
		if(exists == true)
		{
			selectUUID(table, uniqueid,database);
		}
		return uniqueid;
	}

	/**
	 * Checks if unique id exists - if so create another one
	 * 
	 * @param table		Table for which the uuid request is coming from
	 * @param uuid		Generated unique id
	 * @return boolean	true or false depending on if UUID exists
	 */
	@SuppressWarnings("unused")
	private boolean selectUUID(String table, String uuid, SQLiteDatabase database )
	{		
		Cursor cursor = database.rawQuery("SELECT uniqueid FROM " + table + " WHERE uniqueid=?", new String[] { uuid});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				return true;
			}
		}
		cursor.close();
		return false;
	}

	/**
	 * Http post request sends json to the server
	 * 
	 * @param jsonArray		JSON to send to server
	 * @param update		Whether the request is update or not
	 * @param url			URL it's being sent to
	 *
	 * @throws IOException
	 */
	public void sendJSONToServer(JSONArray jsonArray, boolean update, String url ) throws IOException
	{
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = new HttpPost(url);


		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 7200);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8"))); //set json to entity


			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception error");
				}

			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}

	}

	
	/**
	 * Http post request to retrieves JSON from server
	 * 
	 * @param url 		url to retrieve JSON
	 * @param pref 		datetime from a shared preference
	 * @param update 	whether its for an update or insert
	 * @return String	 A JSON string recieved from server
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	public String retrieveFromServer(String url, String pref, boolean update) throws IOException, JSONException
	{

		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();

		if(update == true)
		{
			date.put("changeTime", pref);
		}
		else
		{
			date.put("updateTime", pref);
		}

		jsonArray.put(date);
		String str = "";

		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		myConnection = new HttpPost(url); 

		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 7200);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8"))); //set to json entity 
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception contributers error");
				}


			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
		return str;

	}
}
