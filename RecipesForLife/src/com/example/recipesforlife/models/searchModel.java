package com.example.recipesforlife.models;

import java.util.ArrayList;

import com.example.recipesforlife.controllers.imageBean;
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
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, * FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE (Cookbook.privacyOption='public' AND Recipe.progress='added') AND (Recipe.name LIKE ? OR Recipe.description LIKE ?) GROUP BY Recipe.uniqueid ", new String[] { "%" + word + "%" , "%" + word + "%" });
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
	
	public imageBean selectImages(int id)
	{
		imageBean img = new imageBean();
		//open();
		Cursor cursor2 = database.rawQuery("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON RecipeImages.imageid=Images.imageid WHERE RecipeImages.Recipeid = ?", new String[] { Integer.toString(id) });
		if (cursor2 != null && cursor2.getCount() > 0) {
			for (int i = 0; i < cursor2.getCount(); i++) {
				cursor2.moveToPosition(i);
				img.setImage(cursor2.getBlob(getIndex("image", cursor2)));
				img.setUniqueid(cursor2.getString(getIndex("uniqueid", cursor2)));
			}
		}
		cursor2.close();
		//close();
		return img;
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
		rb.setId(cursor.getInt(getIndex("idr",cursor))); 
		rb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		rb.setProgress(cursor.getString(getIndex("progress", cursor)));
		rb.setCusine(cursor.getString(getIndex("cusine", cursor)));
		rb.setDifficulty(cursor.getString(getIndex("difficulty", cursor)));
		rb.setTips(cursor.getString(getIndex("tips", cursor)));
		rb.setDietary(cursor.getString(getIndex("dietary", cursor)));
		imageBean imgbean = selectImages(cursor.getInt(getIndex("idr",cursor)));
		rb.setImage(imgbean.getImage());
		rb.setRecipeBook(cursor.getString(getIndex("name", cursor)));
		return rb;
	}

}
