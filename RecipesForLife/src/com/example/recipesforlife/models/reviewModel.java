package com.example.recipesforlife.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;

import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class that handles review information to and from the database
 * @author Kari
 *
 */
public class ReviewModel extends BaseDataSource  {

	Context context;
	ContentValues values;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	SharedPreferences sharedpreferences;
	Utility utils;
	long reviewID;

	public ReviewModel(Context context)  {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		utils = new Utility();
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	/**
	 * Insert review into database
	 * @param review - review info
	 * @param server - if request comes from server or from application
	 */
	public void insertReview(ReviewBean review, boolean server)
	{
		open();
		values = new ContentValues();
		values.put("review", review.getComment());

		if(server == true)
		{
			values.put("accountid", review.getUser());
			values.put("updateTime", sharedpreferences.getString("Date", "DEFAULT")); 
		}
		else
		{
			values.put("accountid", sharedpreferences.getString(emailk, "DEFAULT"));
			values.put("updateTime", utils.getLastUpdated(false)); 
		}
		database.beginTransaction();
		try
		{
			reviewID = database.insertOrThrow("Review", null, values);
			insertReviewLink(review, server);
			database.setTransactionSuccessful();
			database.endTransaction(); 
			close();    	
		}catch(SQLException e)
		{
			e.printStackTrace();
			database.endTransaction();
			close();    	
			throw e;
		} 
		close(); 

	}

	/**
	 * Insert link between reviews and recipe
	 * @param review - review info
	 * @param server - if request from db or server
	 */
	public void insertReviewLink(ReviewBean review, boolean server)
	{

		ContentValues value = new ContentValues();
		value.put("ReviewId", reviewID);
		value.put("Recipeid", review.getRecipeid());
		if(server == true)
		{

			value.put("updateTime", sharedpreferences.getString("Date", "DEFAULT")); 
		}
		else
		{

			value.put("updateTime", utils.getLastUpdated(false)); 
		}
		database.insertOrThrow("ReviewRecipe", null, value);		
	}


	/**
	 * Select reviews for a recipe based on the recipe ids
	 * @param recipeid
	 * @return
	 */
	public ArrayList<ReviewBean> selectReviews(int recipeid)
	{	
		ArrayList<ReviewBean> rb = new ArrayList<ReviewBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Review INNER JOIN ReviewRecipe ON Review.reviewId = ReviewRecipe.ReviewId WHERE ReviewRecipe.Recipeid =? ", new String[] { Integer.toString(recipeid) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				rb.add(cursorToReview(cursor));      

			}
		}
		cursor.close();
		close();
		return rb;

	}

	/**
	 * Gets information from database and set to review bean
	 * @param cursor
	 * @return reviewBean - review info from database
	 */
	public ReviewBean cursorToReview(Cursor cursor) {
		ReviewBean rb = new ReviewBean();
		rb.setComment(cursor.getString(getIndex("review", cursor)));
		rb.setUser(cursor.getString(getIndex("accountid", cursor)));
		return rb;
	}

}
