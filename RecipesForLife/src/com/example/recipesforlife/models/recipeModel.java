package com.example.recipesforlife.models;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.imageBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles recipe details relating to recipes
 * @author Kari
 *
 */
public class recipeModel extends baseDataSource {

	Context context;
	ContentValues values,  prepvalues, preptorecipevalues, ingredValues, ingredToDetailsValues, ingredDetailsValues, ingredToRecipeValues;
	String lastUpdated;
	long recipeID, prepID, ingredID, ingredDetsID, recipeUpdateID;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	SharedPreferences sharedpreferences;
	syncRecipeModel sync;
	utility utils;
	public recipeModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		sync = new syncRecipeModel(context);
		utils = new utility();
	}

	/**
	 * Updates a recipe
	 * @param newRecipe
	 * @param prepList
	 * @param ingredList
	 */
	public void updateRecipe(recipeBean newRecipe, ArrayList<preperationBean> prepList,  ArrayList<ingredientBean> ingredList, imageBean image)
	{
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ContentValues recipeUpdateVals = new ContentValues();
		recipeUpdateVals.put("name", newRecipe.getName());
		recipeUpdateVals.put("description", newRecipe.getDesc());
		recipeUpdateVals.put("prepTime", newRecipe.getPrep());
		recipeUpdateVals.put("cookingTime", newRecipe.getCooking());
		recipeUpdateVals.put("serves", newRecipe.getServes());
		recipeUpdateVals.put("changeTime", utils.getLastUpdated(false));
		Cursor cursor = database.rawQuery("SELECT id FROM Recipe WHERE uniqueid=?", new String[] {newRecipe.getUniqueid()});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				recipeUpdateID = (cursor.getInt(getIndex("id",cursor))); 
			}

			cursor.close();
			String[] args = new String[]{newRecipe.getUniqueid()};
			database.beginTransaction();
			try
			{
				database.update("Recipe", recipeUpdateVals, "uniqueid=?", args);
				updateRecipePrep(prepList);
				updateRecipeIngredient(ingredList);
				updateRecipeImage(image);
				database.setTransactionSuccessful();
				database.endTransaction(); 
			}
			catch(SQLException e)
			{
				database.endTransaction();
				throw e;
			}
			close();

		}
	}

	/**
	 * Update recipe preperation details
	 * @param prepList
	 */
	public void updateRecipePrep(ArrayList<preperationBean> prepList)
	{
		Cursor cursor = database.rawQuery("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid FROM PrepRecipe INNER JOIN Preperation WHERE PrepRecipe.recipeid=? AND Preperation.id = PrepRecipe.Preperationid", new String[] {Long.toString(recipeUpdateID)});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String prepid = (cursor.getString(getIndex("uniqueid",cursor))); 
				for (int a = 0; a < prepList.size(); a++)
				{
					ContentValues prepUpdateVals = new ContentValues();

					if(prepid.equals(prepList.get(a).getUniqueid().toString()))
					{               	
						prepUpdateVals.put("instruction", prepList.get(i).getPreperation());
						prepUpdateVals.put("instructionNum", prepList.get(i).getPrepNum());
						prepUpdateVals.put("changeTime", utils.getLastUpdated(false));
						String[] args = new String[]{prepid};
						database.update("Preperation", prepUpdateVals, "uniqueid=?", args);				

					}

				}    
			}
		}
		cursor.close();		
	}


	/**
	 * Update recipe preperation details
	 * @param prepList
	 */
	public void updateRecipeImage(imageBean img)
	{
		ContentValues imageUpdateVals = new ContentValues();
		imageUpdateVals.put("image", img.getImage());
		imageUpdateVals.put("changeTime", utils.getLastUpdated(false));
		String[] args = new String[]{img.getUniqueid()};
		database.update("Images", imageUpdateVals, "uniqueid=?", args);				

	}





	/**
	 * Update recipe ingredient details
	 * @param ingredBeanList
	 */
	public void updateRecipeIngredient(ArrayList<ingredientBean> ingredBeanList)
	{
		Cursor cursor = database.rawQuery("SELECT *, IngredientDetails.id AS ID FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = ?;", new String[] { Long.toString(recipeUpdateID) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String detsId =  (cursor.getString(getIndex("uniqueid",cursor)));
				for (int a = 0; a < ingredBeanList.size(); a++)
				{
					if(detsId.equals(ingredBeanList.get(i).getUniqueid()))
					{
						long id = selectIngredientByName(ingredBeanList.get(i).getName());
						ContentValues ingredVals = new ContentValues();
						ingredVals.put("amount", ingredBeanList.get(i).getAmount() );
						ingredVals.put("value", ingredBeanList.get(i).getValue());
						ingredVals.put("note", ingredBeanList.get(i).getNote());
						ingredVals.put("changeTime", utils.getLastUpdated(false));
						ingredVals.put("ingredientId", id);
						String[] args = new String[]{detsId};
						database.update("IngredientDetails", ingredVals, "uniqueid=?", args);
					}
				}

			}
		}
		cursor.close();
	}

	/**
	 * Inserts recipeBean data into database
	 * @param recipe
	 */
	public String insertRecipe(recipeBean recipe, boolean server, ArrayList<ingredientBean> ingredList, ArrayList<preperationBean> prepList, imageBean img)
	{
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		values = new ContentValues();
		values.put("name", recipe.getName()); 
		values.put("updateTime", utils.getLastUpdated(false)); 
		values.put("changeTime", "2015-01-01 12:00:00.000");
		values.put("description", recipe.getDesc()); 
		values.put("prepTime", recipe.getPrep()); 
		values.put("cookingTime", recipe.getCooking()); 
		values.put("totalTime", "4:00:00");    
		values.put("serves", recipe.getServes()); 
		values.put("addedBy", recipe.getAddedBy()); 
		String uniqueid = "";
		if(server == true)
		{
			uniqueid = recipe.getUniqueid();
			values.put("uniqueid", uniqueid);
		}
		else
		{
			uniqueid = utils.generateUUID(recipe.getAddedBy(), "Recipe", database);
			values.put("uniqueid", uniqueid);
		}
		database.beginTransaction();
		try
		{
			recipeID = database.insertOrThrow("Recipe", null, values);
			insertIngredient(server, ingredList, recipe.getAddedBy());
			insertPrep(prepList, server, recipe.getAddedBy());
			insertCookbookRecipe(recipe.getRecipeBook(),recipe.getAddedBy());
			insertImage(img, server, recipe.getAddedBy());
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
		return uniqueid;
	}

	/**
	 * Insert preperation information into database based on recipeBean
	 * @param recipe
	 */
	public void insertPrep(ArrayList<preperationBean> prepList, boolean server, String addedBy)
	{

		prepvalues = new ContentValues();
		for(int i = 0; i < prepList.size(); i++)
		{
			prepvalues.put("instruction", prepList.get(i).getPreperation().toString());
			prepvalues.put("instructionNum", prepList.get(i).getPrepNum());
			if(server == true)
			{
				prepvalues.put("uniqueid", prepList.get(i).getUniqueid());
			}
			else
			{
				prepvalues.put("uniqueid", utils.generateUUID(addedBy, "Preperation", database));
			}
			prepvalues.put("updateTime", utils.getLastUpdated(false)); 
			prepvalues.put("changeTime", "2015-01-01 12:00:00.000");
			prepID = database.insertOrThrow("Preperation", null, prepvalues);
			insertPrepToRecipe(server);
		}

	}

	public void insertImage(imageBean img, boolean server, String addedBy)
	{
		ContentValues imagevalues = new ContentValues();
		Log.v("Immmmmmm", "Immmmmmm" +img.getImage());
		imagevalues.put("image", img.getImage() );
		imagevalues.put("updateTime", utils.getLastUpdated(false)); 
		imagevalues.put("changeTime", "2015-01-01 12:00:00.000");
		String uniqueid = "";
		if(server == true)
		{
			uniqueid = img.getUniqueid();
			imagevalues.put("uniqueid", uniqueid);
		}
		else
		{
			uniqueid = utils.generateUUID(addedBy, "Images", database);
			imagevalues.put("uniqueid", uniqueid);
		}
		long imageID = database.insertOrThrow("Images", null, imagevalues);
		insertImageLink(imageID);	
	};

	public void insertImageLink(long imageid)
	{
		ContentValues imagevalues = new ContentValues();

		imagevalues.put("imageid", imageid );
		imagevalues.put("Recipeid", recipeID);
		imagevalues.put("updateTime", utils.getLastUpdated(false)); 
		imagevalues.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("RecipeImages", null, imagevalues);

	} 
	/**
	 * Insert prep id and recipe ids into PrepRecipe in the database
	 */
	public void insertPrepToRecipe(boolean server)
	{

		preptorecipevalues = new ContentValues();
		preptorecipevalues.put("recipeId", recipeID);
		preptorecipevalues.put("Preperationid", prepID);
		preptorecipevalues.put("updateTime", utils.getLastUpdated(false)); 
		preptorecipevalues.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("PrepRecipe", null, preptorecipevalues);

	}

	/**
	 * Insert the cookbook id and recipe id into linking table
	 * @param name
	 * @param addedBy
	 */
	public void insertCookbookRecipe(String name, String addedBy)
	{

		ContentValues value = new ContentValues();
		//cookbookModel model = new cookbookModel(context);
		int id = selectCookbooksID(name, addedBy);
		value.put("Recipeid", recipeID);
		value.put("Cookbookid", id);
		value.put("updateTime", utils.getLastUpdated(false)); 
		value.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("CookbookRecipe", null, value);

	}

	/**
	 * Insert ingredient information into database
	 * @param recipe
	 */
	public void insertIngredient( boolean server, ArrayList<ingredientBean> ingredList, String addedBy)
	{
		ingredValues = new ContentValues();
		for(int i = 0; i < ingredList.size(); i++)
		{
			int id = selectIngredient(ingredList.get(i).getName());
			if(id == 0)
			{
				ingredValues.put("name", ingredList.get(i).getName());
				ingredValues.put("updateTime", utils.getLastUpdated(false)); 
				ingredValues.put("changeTime", "2015-01-01 12:00:00.000");
				ingredID = database.insertOrThrow("Ingredient", null, ingredValues);
			}
			else
			{
				ingredID = id;
			}
			insertIngredientDetails(i,ingredList,server, addedBy);
			insertRecipeToIngredient();
			insertIngredToDetails();
		}

	}

	/**
	 * Insert ingredient details into database
	 * @param i
	 * @param recipe
	 */
	public void insertIngredientDetails(int i, ArrayList<ingredientBean> ingredList, boolean server, String addedBy)
	{
		ingredDetailsValues = new ContentValues();
		ingredDetailsValues.put("ingredientId", ingredID);
		ingredDetailsValues.put("amount", ingredList.get(i).getAmount());
		ingredDetailsValues.put("note", ingredList.get(i).getNote());
		ingredDetailsValues.put("value", ingredList.get(i).getValue());
		ingredDetailsValues.put("updateTime", utils.getLastUpdated(false)); 
		ingredDetailsValues.put("changeTime", "2015-01-01 12:00:00.000");
		if(server == true)
		{
			ingredDetailsValues.put("uniqueid", ingredList.get(i).getUniqueid());
		}
		else
		{
			ingredDetailsValues.put("uniqueid", utils.generateUUID(addedBy, "IngredientDetails", database));
		}
		ingredDetsID = database.insertOrThrow("IngredientDetails", null, ingredDetailsValues);

	}

	/**
	 * Insert connected ingred details id and ingred id into database
	 */
	public void insertIngredToDetails()
	{
		ingredToDetailsValues = new ContentValues();
		ingredToDetailsValues.put("ingredientid",ingredID);
		ingredToDetailsValues.put("IngredientDetailsid",ingredDetsID);
		ingredToDetailsValues.put("updateTime", utils.getLastUpdated(false)); 
		ingredToDetailsValues.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("IngredToIngredDetails", null, ingredToDetailsValues);
	}


	/**
	 * Insert connected ingred details id and recipe id into database
	 */
	public void insertRecipeToIngredient()
	{
		ingredToRecipeValues = new ContentValues();
		ingredToRecipeValues.put("Recipeid",recipeID);
		ingredToRecipeValues.put("ingredientDetailsId", ingredDetsID);
		ingredToRecipeValues.put("updateTime", utils.getLastUpdated(false));
		ingredToRecipeValues.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("RecipeIngredient", null, ingredToRecipeValues);
	}

	/**
	 * Retrieve ingredient from database the helps us know whether to insert ingredient. If id 0 then insert
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public int selectIngredient(String name)
	{	
		int id = 0;
		Cursor cursor = database.rawQuery("SELECT * FROM Ingredient WHERE name=?", new String[] { name });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = (cursor.getInt(getIndex("id",cursor)));       

			}
		}
		cursor.close();
		return id;
	}

	/**
	 * Select ingredient by name and if it doesn't exist insert it and return id
	 * @param name
	 * @return ingredientid
	 */
	public long selectIngredientByName(String name)
	{
		long id = 0;
		Cursor cursor = database.rawQuery("SELECT * FROM Ingredient WHERE name=?", new String[] {name });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = (cursor.getInt(getIndex("id",cursor)));       

			}
		}
		else
		{			ContentValues ingredAddValues = new ContentValues();
		ingredAddValues.put("name", name);
		ingredAddValues.put("updateTime", utils.getLastUpdated(false)); 
		ingredAddValues.put("changeTime", "2015-01-01 12:00:00.000");
		id = database.insertOrThrow("Ingredient", null, ingredAddValues);
		}
		cursor.close();
		return id;
	}
	/**
	 * Retrieve recipe from database by a certain a user so we do not add duplicate recipes
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public boolean selectRecipe(String name, String uniqueid)
	{		
		open();

		Cursor cursor = database.rawQuery("SELECT  Recipe.name AS recipename FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.uniqueid = ?  AND CookbookRecipe.Cookbookid=Cookbook.id AND Recipe.name = ?", new String[] { uniqueid, name });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				return true;

			}
		}
		cursor.close();
		close();
		return false;
	}

	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	public recipeBean cursorToRecipe(Cursor cursor) 
	{
		recipeBean rb = new recipeBean();
		rb.setName(cursor.getString(getIndex("name",cursor)));
		rb.setDesc(cursor.getString(getIndex("description",cursor)));
		rb.setServes(cursor.getString(getIndex("serves", cursor)));
		rb.setPrep(cursor.getString(getIndex("prepTime", cursor)));
		rb.setCooking(cursor.getString(getIndex("cookingTime", cursor)));
		rb.setId(cursor.getInt(getIndex("id",cursor))); 
		rb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		return rb;
	}

	/**
	 * Retrieve recipe from database by a certain a user so we do not add duplicate recipes
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public recipeBean selectRecipe2(String uniqueid)
	{		
		recipeBean rb = new recipeBean();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE uniqueid=?", new String[] { uniqueid });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				rb = cursorToRecipe(cursor);


			}
		}
		cursor.close();
		close();
		return rb;	
	}

	/**
	 * Selects all recipes by a specific user 
	 * @param user
	 * @return ArrayList<recipeBean>
	 */
	public ArrayList<recipeBean> selectRecipesByUser(String user)
	{

		ArrayList<recipeBean> rb = new ArrayList<recipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE addedBy=?", new String[] {  user });
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
	 * Selects preperation info based on recipe
	 * @param id
	 * @return ArrayList<preperationBean>
	 */
	public ArrayList<preperationBean> selectPreperation(int id)
	{
		ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = ?", new String[] { Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				prepList.add(cursorToPrep(cursor));


			}
		}
		cursor.close();
		close();
		return prepList;
	}

	public imageBean selectImages(int id)
	{
		imageBean img = new imageBean();
		open();
		Cursor cursor = database.rawQuery("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON RecipeImages.imageid=Images.imageid WHERE RecipeImages.Recipeid = ?", new String[] { Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				img.setImage(cursor.getBlob(getIndex("image", cursor)));
				img.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
			}
		}
		cursor.close();
		close();
		return img;
	}

	/**
	 * Select ingredients info based on recipe id
	 * @param id
	 * @return ArrayList<ingredientBean>
	 */
	public ArrayList<ingredientBean> selectIngredients(int id)
	{
		ArrayList<ingredientBean> ingredList = new ArrayList<ingredientBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = ?;", new String[] { Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				ingredList.add(cursorToIngred(cursor));               
			}
		}
		cursor.close();
		close();
		return ingredList;
	}

	/**
	 * Sets info from db to the ingredient controller
	 * @param cursor
	 * @return ingredientBean
	 */
	public ingredientBean cursorToIngred(Cursor cursor) {
		ingredientBean ib = new ingredientBean();
		ib.setName(cursor.getString(getIndex("name",cursor)));
		ib.setAmount(cursor.getInt(getIndex("amount",cursor)));
		ib.setValue(cursor.getString(getIndex("value", cursor)));
		ib.setNote(cursor.getString(getIndex("note",cursor)));
		ib.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		return ib;
	}

	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	public preperationBean cursorToPrep(Cursor cursor) {
		preperationBean pb = new preperationBean();
		pb.setPreperation(cursor.getString(getIndex("instruction",cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		pb.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		return pb;
	}



	public int selectCookbooksID(String name, String user)
	{
		int id = 0;
		//	open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE creator=? AND name=?", new String[] { user , name });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = cursor.getInt(getIndex("id",cursor));


			}
		}
		cursor.close(); 
		//close();
		return id;	
	}

}
