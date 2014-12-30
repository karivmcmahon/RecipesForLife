package com.example.recipesforlife.models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
		
		public void createJsonArray() throws UnsupportedEncodingException
		{
			String str = "";
			JSONObject student1 = new JSONObject();
			try {
			    student1.put("id", "1");
			    student1.put("name", "NAME OF STUDENT");
			    

			} catch (JSONException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}

			JSONObject student2 = new JSONObject();
			try {
			    student2.put("id", "2");
			    student2.put("name", "NAME OF STUDENT2");
			   
			} catch (JSONException e) {
			    // TODO Auto-generated catch block
			    e.printStackTrace();
			}
			JSONArray jsonArray = new JSONArray();
			jsonArray.put(student1);
			jsonArray.put(student2);

			JSONObject studentsObj = new JSONObject();
			    try {
					studentsObj.put("Students", jsonArray);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   
			    HttpResponse response = null;
		         HttpClient myClient = new DefaultHttpClient();
		         HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/website/WebApplication1/WebApplication1/WebForm1.aspx");      	   	
			// nameValuePairs.add(new BasicNameValuePair("S", "k"))
				myConnection.setEntity(new ByteArrayEntity(
						jsonArray.toString().getBytes("UTF8")));
				// myConnection.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			     try {
					response = myClient.execute(myConnection);
					
				} catch (ClientProtocolException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			     try {
					str = EntityUtils.toString(response.getEntity(), "UTF-8");
					Log.v("STR", "STR " + str);
				} catch (ParseException e) {
					// TODO Auto-generated catch blo
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			     
		}
		
	
	}
  

