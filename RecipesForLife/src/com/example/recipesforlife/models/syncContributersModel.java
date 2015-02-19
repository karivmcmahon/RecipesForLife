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
	
	/**public HashMap<String, String> getContribs()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
	//	ArrayList<recipeBean> recipeList = new ArrayList<recipeBean>();
		Cursor cursor = database.rawQuery("SELECT * FROM Contributers WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime)", new String[] { sharedpreferences.getString("Contributers Server", "DEFAULT"), sharedpreferences.getString("Contributers", "DEFAULT")   });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String account = cursor.getString(getIndex("accountid",cursor));
				int cookbook cursor.getInt(getIndex("cookbookid",cursor));
				cookbookModel bookmodel = new cookbookModel(context)
				String uid = bookmodel.selectCookbooksByRowId();
				place in a map and send send to json
			}
		}
		cursor.close();
		close();
		return recipeList;
	} **/
	
	
	/** public void getAndCreateJSON() throws JSONException, IOException
	{
		 = getContribs(update);
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < bookList.size(); i++)
		{
			JSONObject contribs = new JSONObject();		
			contribs.put("email", bookList.get(i).getName());
			contribs.put("bookid", bookList.get(i).getDescription());
			jsonArray.put(contribs);			
		} 
		Log.v("JSON", "JSON cb " + jsonArray); 
		sendJSONToServer(jsonArray);
	} **/
	
	/**public void sendJSONToServer(JSONArray jsonArray, boolean update ) throws IOException
	{
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		if(update == true)
		{
		//	myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm7.aspx");      	   	
		}
		else
		{
		 myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm7.aspx");      	   	
		}
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

	}**/
	
	/**public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		if(update == true)
		{
			date.put("chTime", sharedpreferences.getString("Cookbook Update", "DEFAULT"));
		}
		else
		{
			date.put("updateTime", sharedpreferences.getString("Cookbook", "DEFAULT"));
		}
		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm8.aspx");      	   	
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
				Log.v("RESPONSE", "RESPONSE cb " + str);

			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
			JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Contributers");

			for(int i = 0; i < jArray.length(); i++)
			{


				json = jArray.getJSONObject(i);
				String uniqid  = json.getString("uniqueid"));
				String email = json.getString("accountid");
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
	} **/


}
