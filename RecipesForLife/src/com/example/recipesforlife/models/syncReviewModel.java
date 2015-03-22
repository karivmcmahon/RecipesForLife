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
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Gets and sends review JSON to server
 * @author Kari
 *
 */
public class SyncReviewModel  extends BaseDataSource {
	Context context;
	Utility util;


	public SyncReviewModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
	}


	/**
	 * Get reviews from database based on a certain datetime range
	 * @return List of review info in the form of review beans
	 */
	public ArrayList<ReviewBean> getReviews()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		Cursor cursor;
		ArrayList<ReviewBean> rbList = new ArrayList<ReviewBean>();
		cursor = database.rawQuery("SELECT * FROM Review WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] {  sharedpreferences.getString("Date", "DEFAULT") });
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

	/**
	 * Select recipe unique id based on review d
	 * @param reviewid
	 * @return String - uniqueid
	 */
	public String selectUniqueId(int reviewid)
	{	
		String uniqueid = "";
		Cursor cursor = database.rawQuery("SELECT Recipe.uniqueid AS ruid FROM Recipe INNER JOIN ReviewRecipe ON ReviewRecipe.Recipeid = Recipe.id INNER JOIN Review ON Review.reviewId = ReviewRecipe.ReviewId WHERE Review.reviewId = ? GROUP BY Recipe.uniqueid ", new String[] { Integer.toString(reviewid) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				uniqueid = cursor.getString(getIndex("ruid", cursor));
			}
		}
		cursor.close();
		return uniqueid;
	}

	
	/**
	 * Sets information from database to bean
	 * @param cursor
	 * @return ReviewBean containing info from database
	 */
	public ReviewBean cursorToReview(Cursor cursor) {
		ReviewBean rb = new ReviewBean();
		rb.setComment(cursor.getString(getIndex("review", cursor)));
		rb.setUser(cursor.getString(getIndex("accountid", cursor)));
		rb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		String uid = selectUniqueId(cursor.getInt(getIndex("reviewId", cursor)));
		rb.setRecipeuniqueid(uid);
		return rb;
	}

	
	/**
	 * Create review JSON and send to server
	 * @throws JSONException
	 * @throws IOException
	 */
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

		util.sendJSONToServer(jsonArray, false, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm14.aspx" );
	}



	/**
	 * Gets review JSON from server and insert it into sqlite database
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str= "";


		str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm15.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);


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

}



