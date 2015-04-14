package com.example.recipesforlife.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;

import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Class that handles review information to and from the database
 * @author Kari
 *
 */
public class ApplicationModel_ReviewModel extends Database_BaseDataSource  {

	private static final String emailk = "emailKey";
	private long reviewID; 
	private SharedPreferences sharedpreferences;
	private Utility utils;
	private ContentValues values;

	public ApplicationModel_ReviewModel(Context context)  {
		super(context);
		utils = new Utility();
		sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	/**
	 * Gets information from database and set to review bean
	 * 
	 * @param cursor		Results from database query
	 * @return reviewBean 	review info from database
	 */
	private ReviewBean cursorToReview(Cursor cursor) {
		ReviewBean rb = new ReviewBean();
		rb.setComment(cursor.getString(getIndex("review", cursor)));
		rb.setUser(cursor.getString(getIndex("accountid", cursor)));
		return rb;
	}

	/**
	 * Insert review into database
	 * 
	 * @param review    review info
	 * @param server    if request comes from server or from application
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
	 * 
	 * @param review 	review info
	 * @param server 	if request from db or server
	 */
	private void insertReviewLink(ReviewBean review, boolean server)
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
	 * 
	 * @param recipeid
	 * @return ArrayList<ReviewBean> 	List of reviews
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

}
