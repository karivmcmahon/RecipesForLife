package com.example.recipesforlife.models;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class sync {
	
	public void getJson()
	{
	 
		JSONObject json;
		String str = "";		
	     try
	     {
	    	 HttpResponse response;
	         HttpClient myClient = new DefaultHttpClient();
	         HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/website/WebApplication1/WebApplication1/WebForm1.aspx");      
	    	// List nameValuePairs = new ArrayList(1);
		    // myConnection.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		     response = myClient.execute(myConnection);
		     str = EntityUtils.toString(response.getEntity(), "UTF-8"); 
		     Log.v("STR ", "STR " + str);
	     }
	     catch(Exception e)
	     {
	    	 Log.v("Exception", "Exception " + e);
	     }
     	    
		try
		{
			
			
			 JSONObject parentObject = new JSONObject(str);
			 String name = parentObject.getString("name"); 
			 String age =parentObject.getString("age");
			 Log.v("AGE", "AGE" + age);
			 Log.v("NAME", "NAME" + name);

		} catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
	
	}
}  

