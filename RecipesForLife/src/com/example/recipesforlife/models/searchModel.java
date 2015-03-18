package com.example.recipesforlife.models;

import java.util.ArrayList;

import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.imageBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.controllers.userBean;

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
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, Cookbook.name AS cname, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.privacyOption='public' AND Recipe.progress='added' AND Cookbook.progress='added' AND (Recipe.name LIKE ? OR Recipe.description LIKE ?) GROUP BY Recipe.uniqueid ", new String[] { "%" + word + "%" , "%" + word + "%" });
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
		rb.setUniqueid(cursor.getString(getIndex("rid", cursor)));
		rb.setProgress(cursor.getString(getIndex("progress", cursor)));
		rb.setCusine(cursor.getString(getIndex("cusine", cursor)));
		rb.setDifficulty(cursor.getString(getIndex("difficulty", cursor)));
		rb.setTips(cursor.getString(getIndex("tips", cursor)));
		rb.setDietary(cursor.getString(getIndex("dietary", cursor)));
		imageBean imgbean = selectImages(cursor.getInt(getIndex("idr",cursor)));
		rb.setImage(imgbean.getImage());
		rb.setRecipeBook(cursor.getString(getIndex("cname", cursor)));
		return rb;
	}
	
	public ArrayList<cookbookBean> selectCookbooks(String word)
	{	
		ArrayList<cookbookBean> cb = new ArrayList<cookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT  * FROM Cookbook WHERE Cookbook.privacyOption='public' AND Cookbook.progress='added' AND (Cookbook.name LIKE ? OR Cookbook.description LIKE  ?)", new String[] { "%" + word + "%" , "%" + word + "%" });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cb.add(cursorToCookbook(cursor));

			}
		}
		cursor.close();
		close();
		return cb;
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
		cb.setImage(cursor.getBlob(getIndex("image", cursor)));
		cb.setProgress(cursor.getString(getIndex("progress",cursor)));
		return cb;
	}
	
	public ArrayList<userBean> selectUsers(String word)
	{	
		ArrayList<userBean> ub = new ArrayList<userBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT  * FROM Account INNER JOIN Users ON Account.id = Users.id WHERE Account.email LIKE ? OR Users.name LIKE ?", new String[] { "%" + word + "%" , "%" + word + "%" });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ub.add(cursorToUsers(cursor));

			}
		}
		cursor.close();
		close();
		return ub;
	}
	
	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	public userBean cursorToUsers(Cursor cursor) {
		userBean ub = new userBean();      
		ub.setName(cursor.getString(getIndex("name", cursor)));
		ub.setBio(cursor.getString(getIndex("bio", cursor)));
		ub.setCity(cursor.getString(getIndex("city", cursor)));
		ub.setCountry(cursor.getString(getIndex("country", cursor)));
		ub.setCookingInterest(cursor.getString(getIndex("cookingInterest", cursor)));
		ub.setEmail(cursor.getString(getIndex("email", cursor)));
		return ub;
	}

}
