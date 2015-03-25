package com.example.recipesforlife.models;

import java.util.ArrayList;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.UserBean;

import android.content.Context;
import android.database.Cursor;

/**
 * Class handles database queries relating to the application search
 * @author Kari
 *
 */
public class SearchModel extends BaseDataSource {

	Context context;
	public SearchModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * Selects recipes which contains the word or sentance that was searched
	 * @param word - query from search
	 * @return List of recipes
	 */
	public ArrayList<RecipeBean> selectRecipe(String word)
	{	
		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, Cookbook.name AS cname, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId INNER JOIN IngredientDetails ON IngredientDetails.id = RecipeIngredient.ingredientDetailsID INNER JOIN RecipeIngredient ON  RecipeIngredient.Recipeid  = Recipe.id WHERE Cookbook.privacyOption='public' AND Recipe.progress='added' AND Cookbook.progress='added' AND (Recipe.name LIKE ? OR Recipe.description LIKE ? OR Ingredient.name LIKE ?) GROUP BY Recipe.uniqueid ", new String[] { "%" + word + "%" , "%" + word + "%" , "%" + word + "%" });
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
	
	public ArrayList<RecipeBean> selectRecipeByDiff(String word)
	{	
		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, Cookbook.name AS cname, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.privacyOption='public' AND Recipe.progress='added' AND Cookbook.progress='added' AND Recipe.difficulty LIKE ?  ORDER BY RANDOM() LIMIT 10; ", new String[] { "%" + word + "%" });
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
	
	public ArrayList<RecipeBean> selectRecipeByCuisine(String word)
	{	
		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, Cookbook.name AS cname, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.privacyOption='public' AND Recipe.progress='added' AND Cookbook.progress='added' AND Recipe.cusine LIKE ?  ORDER BY RANDOM() LIMIT 10; ", new String[] { "%" + word + "%" });
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
	
	public ArrayList<RecipeBean> selectRecipeByDietary(String word)
	{	
		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, Cookbook.name AS cname, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.privacyOption='public' AND Recipe.progress='added' AND Cookbook.progress='added' AND Recipe.dietary LIKE ?  ORDER BY RANDOM() LIMIT 10; ", new String[] { "%" + word + "%" });
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
	 * Select image from recipe
	 * @param id
	 * @return ImageBean - image info
	 */
	public ImageBean selectImages(int id)
	{
		ImageBean img = new ImageBean();
		Cursor cursor2 = database.rawQuery("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON RecipeImages.imageid=Images.imageid WHERE RecipeImages.Recipeid = ?", new String[] { Integer.toString(id) });
		if (cursor2 != null && cursor2.getCount() > 0) {
			for (int i = 0; i < cursor2.getCount(); i++) {
				cursor2.moveToPosition(i);
				img.setImage(cursor2.getBlob(getIndex("image", cursor2)));
				img.setUniqueid(cursor2.getString(getIndex("uniqueid", cursor2)));
			}
		}
		cursor2.close();
		return img;
	}

	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return RecipeBean - recipe information
	 */
	public RecipeBean cursorToRecipe(Cursor cursor) 
	{
		RecipeBean rb = new RecipeBean();
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
		ImageBean imgbean = selectImages(cursor.getInt(getIndex("idr",cursor)));
		rb.setImage(imgbean.getImage());
		rb.setRecipeBook(cursor.getString(getIndex("cname", cursor)));
		return rb;
	}

	/**
	 * Select cookbooks based on search query where cookbooks contain the word searhed
	 * @param word
	 * @return List of cookbooks
	 */
	public ArrayList<CookbookBean> selectRandomCookbooks()
	{	
		ArrayList<CookbookBean> cb = new ArrayList<CookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT  * FROM Cookbook WHERE Cookbook.privacyOption='public' AND Cookbook.progress='added' ORDER BY RANDOM() LIMIT 10;", new String[] {  });
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

	public ArrayList<CookbookBean> selectCookbooks(String word)
	{	
		ArrayList<CookbookBean> cb = new ArrayList<CookbookBean>();
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
	/**
	 * Sets info from database to cookbook bean
	 * @param cursor
	 * @return CookbookBean  - cookbook info
	 */
	public CookbookBean cursorToCookbook(Cursor cursor) {
		CookbookBean cb = new CookbookBean();
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

	/**
	 * Select users from the database based on the search query
	 * @param word
	 * @return A list of user beans
	 */
	public ArrayList<UserBean> selectUsers(String word)
	{	
		ArrayList<UserBean> ub = new ArrayList<UserBean>();
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
	public UserBean cursorToUsers(Cursor cursor) {
		UserBean ub = new UserBean();      
		ub.setName(cursor.getString(getIndex("name", cursor)));
		ub.setBio(cursor.getString(getIndex("bio", cursor)));
		ub.setCity(cursor.getString(getIndex("city", cursor)));
		ub.setCountry(cursor.getString(getIndex("country", cursor)));
		ub.setCookingInterest(cursor.getString(getIndex("cookingInterest", cursor)));
		ub.setEmail(cursor.getString(getIndex("email", cursor)));
		return ub;
	}

}
