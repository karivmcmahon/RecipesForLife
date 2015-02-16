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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;


public class syncCookbookModel extends baseDataSource {
	Context context;
	
	
	public syncCookbookModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<cookbookBean> getCookbook()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
	    ArrayList<cookbookBean> cbList = new ArrayList<cookbookBean>();
	    Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime)", new String[] { sharedpreferences.getString("Cookbook Server", "DEFAULT"), sharedpreferences.getString("Cookbook", "DEFAULT")   });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                cbList.add(cursorToCookbook(cursor));
            }
        }
        cursor.close();
        close();
        return cbList;
	}
	
	public cookbookBean cursorToCookbook(Cursor cursor) {
        cookbookBean cb = new cookbookBean();
        cb.setName(cursor.getString(getIndex("name",cursor)));
        cb.setDescription(cursor.getString(getIndex("description",cursor)));
        cb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
        cb.setPrivacy(cursor.getString(getIndex("privacyOption",cursor)));
        cb.setCreator(cursor.getString(getIndex("creator",cursor)));
        cb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
        cb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
        return cb;
    }
	
	/**
	 * Builds a json with all the recipe data to send to the server
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateJSON() throws JSONException, IOException
	{
		ArrayList<cookbookBean> bookList = getCookbook();
		JSONArray jsonArray = new JSONArray();
	
		for(int i = 0; i < bookList.size(); i++)
		{
			JSONObject book = new JSONObject();		
			book.put("name", bookList.get(i).getName());
			book.put("description", bookList.get(i).getDescription());
			book.put("creator", bookList.get(i).getCreator());
			book.put("updateTime", bookList.get(i).getUpdateTime());
			book.put("changeTime", bookList.get(i).getChangeTime());
			book.put("uniqueid", bookList.get(i).getUniqueid());	
			book.put("privacyOption", bookList.get(i).getPrivacy());
			jsonArray.put(book);			
		} 
		Log.v("JSON", "JSON cb " + jsonArray); 
	sendJSONToServer(jsonArray);
	}
	
	public void sendJSONToServer(JSONArray jsonArray) throws IOException
	{
		String str = "";
		HttpResponse response = null;
        HttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm7.aspx");      	   	
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
		date.put("updateTime", sharedpreferences.getString("Cookbook", "DEFAULT"));
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
			JSONArray jArray = (JSONArray) jObject.get("Cookbook");
			
			for(int i = 0; i < jArray.length(); i++)
			{
				
				
				json = jArray.getJSONObject(i);
                cookbookBean book = new cookbookBean();
                book.setName( json.getString("name"));
                book.setDescription(json.getString("description"));
                book.setPrivacy(json.getString("privacyOption"));
                book.setUniqueid(json.getString("uniqueid"));
                book.setCreator(json.getString("creator"));
                cookbookModel model = new cookbookModel(context);
                model.insertBook(book, true);
                
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
