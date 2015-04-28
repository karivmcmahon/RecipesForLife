package com.example.recipesforlife.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;

import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Class gets and sends review JSON to server
 * @author Kari
 *
 */
public class SyncModel_ReviewModel  extends Database_BaseDataSource {
	private Context context;
	private Utility util;


	public SyncModel_ReviewModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
	}


	/**
	 * Sets information from database to review bean
	 * 
	 * @param cursor		Query results
	 * @return ReviewBean 	Stores query results
	 */
	private ReviewBean cursorToReview(Cursor cursor) {
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
	 * 
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
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str= "";


		str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm15.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);


		JSONObject jObject = new JSONObject(str);
		JSONArray jArray = (JSONArray) jObject.get("Review");

		for(int i = 0; i < jArray.length(); i++)
		{
			json = jArray.getJSONObject(i);
			
			//retrieve from json and set to bean
			ReviewBean review = new ReviewBean();
			review.setComment(json.getString("comment"));
			review.setUser(json.getString("user"));
			
			ApplicationModel_RecipeModel rm = new ApplicationModel_RecipeModel(context);
			
			//get recipe id
			RecipeBean recipebean = rm.selectRecipe2(json.getString("recipeuniqueid"));
			review.setRecipeid(recipebean.getId());
			ApplicationModel_ReviewModel model = new ApplicationModel_ReviewModel(context);
			try
			{
				model.insertReview(review, true); //insert review once all data retrieve

			}
			catch(SQLException e)
			{
				throw e;
			}
		}
	}

	
	/**
	 * Get reviews from database based on a certain datetime range
	 * 
	 * @return ArrayList<ReviewBean>	 List of review info 
	 */
	public ArrayList<ReviewBean> getReviews()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
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
	 * Select recipe unique id based on review id
	 * 
	 * @param reviewid		Review id
	 * @return uniqueid		Recipes unique id
	 */
	private String selectUniqueId(int reviewid)
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

}



