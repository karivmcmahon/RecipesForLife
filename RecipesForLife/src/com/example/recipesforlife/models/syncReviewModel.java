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
import android.database.SQLException;
import android.util.Base64;
import android.util.Log;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

public class SyncReviewModel  extends BaseDataSource {
	Context context;


	public SyncReviewModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}


	public ArrayList<ReviewBean> getReviews()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		Cursor cursor;
		ArrayList<ReviewBean> rbList = new ArrayList<ReviewBean>();
		cursor = database.rawQuery("SELECT * FROM Review WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] {  sharedpreferences.getString("Review", "DEFAULT") });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				rbList.add(cursorToReview(cursor));
			}
		}
		cursor.close();
		close();
		return rbList;
	}

	public String selectUniqueId(int reviewid)
	{	
		Log.v("UID", "UID");
		String uniqueid = "";
		//open();
		Cursor cursor = database.rawQuery("SELECT Recipe.uniqueid AS ruid FROM Recipe INNER JOIN ReviewRecipe ON ReviewRecipe.Recipeid = Recipe.id INNER JOIN Review ON Review.reviewId = ReviewRecipe.ReviewId WHERE Review.reviewId = ? GROUP BY Recipe.uniqueid ", new String[] { Integer.toString(reviewid) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				uniqueid = cursor.getString(getIndex("ruid", cursor));
			}
		}
		cursor.close();
		//close();
		return uniqueid;
	}

	public ReviewBean cursorToReview(Cursor cursor) {
		ReviewBean rb = new ReviewBean();
		rb.setComment(cursor.getString(getIndex("review", cursor)));
		rb.setUser(cursor.getString(getIndex("accountid", cursor)));
		rb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		Log.v("JSON", "INT reviewb " + cursor.getInt(getIndex("reviewId", cursor))); 
		String uid = selectUniqueId(cursor.getInt(getIndex("reviewId", cursor)));
		rb.setRecipeuniqueid(uid);
		return rb;
	}

	public void getAndCreateJSON() throws JSONException, IOException
	{
		ArrayList<ReviewBean> reviewList = getReviews();
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < reviewList.size(); i++)
		{
			JSONObject review = new JSONObject();		
			review.put("comment", reviewList.get(i).getComment());
			review.put("user", reviewList.get(i).getUser());
			review.put("recipeuniqueid", reviewList.get(i).getRecipeuniqueid());
			review.put("updateTime", reviewList.get(i).getUpdateTime());
			jsonArray.put(review);			
		} 
		Log.v("JSON", "JSON reviewb " + jsonArray); 
		sendJSONToServer(jsonArray);
	}

	public void sendJSONToServer(JSONArray jsonArray ) throws IOException
	{
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm14.aspx");      	   			
		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));


			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception reviews error");
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

	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		HttpPost myConnection = null;

		date.put("updateTime", sharedpreferences.getString("Review", "DEFAULT"));
		myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm15.aspx");      	   	
		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();

		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception reviews error");
				}


			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
			JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Review");

			for(int i = 0; i < jArray.length(); i++)
			{


				json = jArray.getJSONObject(i);
				ReviewBean review = new ReviewBean();
				review.setComment(json.getString("comment"));
				review.setUser(json.getString("user"));
				RecipeModel rm = new RecipeModel(context);
				RecipeBean recipebean = rm.selectRecipe2(json.getString("recipeuniqueid"));
				review.setRecipeid(recipebean.getId());
				ReviewModel model = new ReviewModel(context);
				try
				{
					model.insertReview(review, true);

				}
				catch(SQLException e)
				{
					throw e;
				}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			throw e;
		}
	}


}
