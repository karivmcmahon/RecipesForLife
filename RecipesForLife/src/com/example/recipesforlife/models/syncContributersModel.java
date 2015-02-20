package com.example.recipesforlife.models;

import java.io.IOException;
import java.util.ArrayList;

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

import com.example.recipesforlife.controllers.contributerBean;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class syncContributersModel extends baseDataSource {
	
	Context context;


	public syncContributersModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<contributerBean> getContribs()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<contributerBean> contribList = new ArrayList<contributerBean>();
		Cursor cursor = database.rawQuery("SELECT * FROM Contributers WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime)", new String[] { sharedpreferences.getString("Contributers Server", "DEFAULT"), sharedpreferences.getString("Contributers", "DEFAULT")   });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String account = cursor.getString(getIndex("accountid",cursor));
				int cookbookid = cursor.getInt(getIndex("cookbookid",cursor));
				String update = cursor.getString(getIndex("updateTime", cursor));
				String change = cursor.getString(getIndex("changeTime", cursor));
				
				cookbookModel bookmodel = new cookbookModel(context);
				String uid = bookmodel.selectCookbooksByRowID(cookbookid);
				contributerBean contrib = new contributerBean();
				contrib.setAccount(account);
				contrib.setBookUniqId(uid);
				contrib.setChangeTime(change);
				contrib.setUpdateTime(update);
				contribList.add(contrib);
				
			}
		}
		cursor.close();
		close();
		return contribList;
	} 
	
	
	 public void getAndCreateJSON() throws JSONException, IOException
	 {
		ArrayList<contributerBean> contribs = getContribs();
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < contribs.size(); i++)
		{
			JSONObject contrib = new JSONObject();		
			contrib.put("email", contribs.get(i).getAccount());
			contrib.put("bookid", contribs.get(i).getBookUniqId());
			contrib.put("updateTime", contribs.get(i).getUpdateTime());
			contrib.put("changeTime", contribs.get(i).getChangeTime());
			jsonArray.put(contrib);			
		} 
		Log.v("JSON", "JSON contribs " + jsonArray); 
		sendJSONToServer(jsonArray);
	} 
	
	public void sendJSONToServer(JSONArray jsonArray ) throws IOException
	{
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		
		 myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm11.aspx");      	   	
		
		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 2000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 3000);
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
				throw e;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			Toast.makeText(context, 
					"Connection to server failed", Toast.LENGTH_LONG).show();
			throw e;
		}

	}
	
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		date.put("updateTime", sharedpreferences.getString("Contributers", "DEFAULT"));
		
		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm12.aspx");      	   	
		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 2000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 3000);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE contribs " + str);

			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
		    JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Contributer");

			for(int i = 0; i < jArray.length(); i++)
			{


				json = jArray.getJSONObject(i);
				String uniqid  = json.getString("bookid");
				String email = json.getString("email");
				cookbookModel model = new cookbookModel(context);
				int id = model.selectCookbooksIDByUnique(uniqid);
			    model.insertContributers(email, id);
				

			} 



		}
		catch (IOException e) 
		{
			e.printStackTrace();
			Toast.makeText(context, 
					"Connection to server failed", Toast.LENGTH_LONG).show();
			throw e;
		} 
	} 


}
