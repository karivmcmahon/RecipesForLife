package com.example.recipesforlife.models;

import java.util.ArrayList;

import com.example.recipesforlife.controllers.recipeBean;

import android.content.Context;
import android.database.Cursor;

public class searchModel extends baseDataSource {
	
	Context context;
	public searchModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public ArrayList<recipeBean> selectRecipe(String word)
	{	
		ArrayList<recipeBean> rb = new ArrayList<recipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, * FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE (Cookbook.privacyOption='public' AND Recipe.progress='added') AND (Recipe.name LIKE ? OR Recipe.description LIKE ?) GROUP BY Recipe.uniqueid ", new String[] { "%" + word + "%" , "%" + word + "%" });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				rb.add(cursorToRecipe(cursor));

			}
		}
		cursor.close();
		close();
		return rb;
	}
	
	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	public recipeBean cursorToRecipe(Cursor cursor) 
	{
		recipeBean rb = new recipeBean();
		rb.setName(cursor.getString(getIndex("rname",cursor)));
		rb.setDesc(cursor.getString(getIndex("desc",cursor)));
		rb.setServes(cursor.getString(getIndex("serves", cursor)));
		rb.setPrep(cursor.getString(getIndex("prepTime", cursor)));
		rb.setCooking(cursor.getString(getIndex("cookingTime", cursor)));
		rb.setId(cursor.getInt(getIndex("id",cursor))); 
		rb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		rb.setProgress(cursor.getString(getIndex("progress", cursor)));
		rb.setCusine(cursor.getString(getIndex("cusine", cursor)));
		rb.setDifficulty(cursor.getString(getIndex("difficulty", cursor)));
		rb.setTips(cursor.getString(getIndex("tips", cursor)));
		rb.setDietary(cursor.getString(getIndex("dietary", cursor)));
		return rb;
	}

}
