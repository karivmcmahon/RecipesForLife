package com.example.recipesforlife.models;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles database details relating to recipes
 * @author Kari
 *
 */
public class RecipeModel extends BaseDataSource {

	Context context;
	ContentValues values,  prepvalues, preptorecipevalues, ingredValues, ingredToDetailsValues, ingredDetailsValues, ingredToRecipeValues;
	String lastUpdated;
	long recipeID, prepID, ingredID, ingredDetsID, recipeUpdateID;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	SharedPreferences sharedpreferences;
	SyncRecipeModel sync;
	Utility utils;

	public RecipeModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		sync = new SyncRecipeModel(context);
		utils = new Utility();
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	}

		/**
		 * Updates a recipe in the database
		 * 
		 * @param newRecipe - recipe info contained in recipebean
		 * @param prepList - prep info for recipe contained in prepbean
		 * @param ingredList - ingred info for recipe contained in ingredbean
		 * @param image - image for recipe
		 * @param server - if request from server or application
		 */
		public void updateRecipe(RecipeBean newRecipe, ArrayList<PreperationBean> prepList,  ArrayList<IngredientBean> ingredList, ImageBean image, boolean server)
		{
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ContentValues recipeUpdateVals = new ContentValues();
		recipeUpdateVals.put("name", newRecipe.getName());
		recipeUpdateVals.put("description", newRecipe.getDesc());
		recipeUpdateVals.put("prepTime", newRecipe.getPrep());
		recipeUpdateVals.put("cookingTime", newRecipe.getCooking());
		recipeUpdateVals.put("serves", newRecipe.getServes());
		recipeUpdateVals.put("progress", newRecipe.getProgress());
		recipeUpdateVals.put("dietary", newRecipe.getDietary());
		recipeUpdateVals.put("difficulty", newRecipe.getDifficulty());
		recipeUpdateVals.put("cusine", newRecipe.getCusine());
		recipeUpdateVals.put("tips", newRecipe.getTips());
		if(server == true)
		{
			//Gets timestamp based on shared pref if from server
			recipeUpdateVals.put("changeTime", sharedpreferences.getString("Date", "DEFAULT"));
		}
		else
		{
			//Gets current timestamp if from application
			recipeUpdateVals.put("changeTime", utils.getLastUpdated(false));
		}
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
				updateRecipePrep(prepList); //updates preperation details for recipe
				updateRecipeIngredient(ingredList); //updates ingred details for recipe
				updateRecipeImage(image); //updates images for recipe
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
	 * Updates preperation details for the  recipe
	 * @param prepList
	 */
	public void updateRecipePrep(ArrayList<PreperationBean> prepList)
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
	 * Update recipe image for recipe
	 * @param img - the img bean which is being updated
	 */
	public void updateRecipeImage(ImageBean img)
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
	public void updateRecipeIngredient(ArrayList<IngredientBean> ingredBeanList)
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
	 * Inserts recipe into the database 
	 * 
	 * @param recipe - recipe info from recipebean
	 * @param server - if request from server or app
	 * @param ingredList  - ingred info for recipe
	 * @param prepList = prep info for recipe
	 * @param img  - image for recipe
	 * @return - String - the unique id of inserted recipeS
	 */
	public String insertRecipe(RecipeBean recipe, boolean server, ArrayList<IngredientBean> ingredList, ArrayList<PreperationBean> prepList, ImageBean img)
	{
		open();
		values = new ContentValues();
		values.put("name", recipe.getName()); 
		values.put("changeTime", "2015-01-01 12:00:00.000");
		values.put("description", recipe.getDesc()); 
		values.put("prepTime", recipe.getPrep()); 
		values.put("cookingTime", recipe.getCooking()); 
		values.put("totalTime", "4:00:00");    
		values.put("serves", recipe.getServes()); 
		values.put("addedBy", recipe.getAddedBy()); 
		values.put("progress", "added");
		values.put("cusine", recipe.getCusine());
		values.put("tips", recipe.getTips());
		values.put("difficulty", recipe.getDifficulty());
		values.put("dietary", recipe.getDietary());
		String uniqueid = "";
		if(server == true)
		{
			uniqueid = recipe.getUniqueid();
			//if from server get unique id and set to shared pref from db
			values.put("uniqueid", uniqueid);
			values.put("updateTime", sharedpreferences.getString("Date", "DEFAULT")); 
		}
		else
		{
			//generates uuid if from application and sets to current timestamp
			uniqueid = utils.generateUUID(recipe.getAddedBy(), "Recipe", database);
			values.put("uniqueid", uniqueid);
			values.put("updateTime", utils.getLastUpdated(false)); 
		}
		database.beginTransaction();
		try
		{
			recipeID = database.insertOrThrow("Recipe", null, values);
			insertIngredient(server, ingredList, recipe.getAddedBy());
			insertPrep(prepList, server, recipe.getAddedBy());
			insertCookbookRecipe(recipe.getRecipeBook());
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
	 * Insert preperation information into database 
	 * @param prepList - prep info
	 * @param server - if request from server or application
	 * @param addedBy - who added the recipe
	 */
	public void insertPrep(ArrayList<PreperationBean> prepList, boolean server, String addedBy)
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
			insertPrepToRecipe();
		}

	}

	/** 
	 * Insert image into database for recipe
	 * @param img - image info
	 * @param server - if request from server or application
	 * @param addedBy - who added recipe
	 */
	public void insertImage(ImageBean img, boolean server, String addedBy)
	{
		ContentValues imagevalues = new ContentValues();
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

	/**
	 * Insert link between image and recipe
	 * @param imageid - id of row when image was insertedd
	 */
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
	 * Insert link between prep and recipe
	 * 
	 */
	public void insertPrepToRecipe()
	{

		preptorecipevalues = new ContentValues();
		preptorecipevalues.put("recipeId", recipeID);
		preptorecipevalues.put("Preperationid", prepID);
		preptorecipevalues.put("updateTime", utils.getLastUpdated(false)); 
		preptorecipevalues.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("PrepRecipe", null, preptorecipevalues);

	}

	/**
	 * Insert the link between cookbook and recipe
	 * @param uid - used  to get cookbooks row id
	 * @param addedBy - user who inserted recipe
	 */
	public void insertCookbookRecipe(String uid)
	{
		ContentValues value = new ContentValues();
		int id = selectCookbooksIDByUnique(uid);
		value.put("Recipeid", recipeID);
		value.put("Cookbookid", id);
		value.put("updateTime", utils.getLastUpdated(false)); 
		value.put("changeTime", "2015-01-01 12:00:00.000");
		database.insertOrThrow("CookbookRecipe", null, value);

	}

	/**
	 * Insert ingredient information into database
	 * @param ingredList - ingredient information
	 * @param addedBy - user who inserted recipe
	 * @param server -if request from server or application
	 */
	public void insertIngredient( boolean server, ArrayList<IngredientBean> ingredList, String addedBy)
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
				ingredID = id; // if it already exists return the id
			}
			insertIngredientDetails(i,ingredList,server, addedBy); //insert ingred details
			insertRecipeToIngredient(); //insert recipe and ingred details
			insertIngredToDetails();
		}

	}

	/**
	 * Insert ingredient details into database
	 * @param i - point in loop
	 * @param ingredList - ingredient info
	 * @param addedBy - who inserted recipe
	 * @param server - if request from app or server
	 */
	public void insertIngredientDetails(int i, ArrayList<IngredientBean> ingredList, boolean server, String addedBy)
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
			//if  from server - get unique id
			ingredDetailsValues.put("uniqueid", ingredList.get(i).getUniqueid());
		}
		else
		{
			//generate uuid if request from application
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
	 * @param name
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
	 * @param name
	 * @param uniqueid
	 * @return boolean - whether it exists or not
	 */
	public boolean selectRecipe(String name, String uniqueid)
	{		
		open();

		Cursor cursor = database.rawQuery("SELECT  Recipe.name AS recipename FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.uniqueid = ?  AND CookbookRecipe.Cookbookid=Cookbook.id AND Recipe.name = ? AND Recipe.progress=?", new String[] { uniqueid, name, "added" });
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
	public RecipeBean cursorToRecipe(Cursor cursor) 
	{
		RecipeBean rb = new RecipeBean();
		rb.setName(cursor.getString(getIndex("name",cursor)));
		rb.setDesc(cursor.getString(getIndex("description",cursor)));
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

	/**
	 * Retrieve recipe by recipe uniqueid
	 * @param uniqueid
	 * @return recipBean
	 */
	public RecipeBean selectRecipe2(String uniqueid)
	{		
		RecipeBean rb = new RecipeBean();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE uniqueid=? AND progress='added'", new String[] { uniqueid });
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
	 * Selects all recipes by a specific user who added the recipe
	 * @param user
	 * @return ArrayList<recipeBean>
	 */
	public ArrayList<RecipeBean> selectRecipesByUser(String user)
	{

		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE addedBy=? AND progress='added'", new String[] {  user });
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
	 * Select all recipes that the user can view
	 * 
	 * @param user
	 * @return List of recipes
	 */
	public ArrayList<RecipeBean> selectAllRecipesUserCanAccess(String user)
	{

		ArrayList<RecipeBean> rb = new ArrayList<RecipeBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT Recipe.name AS rname, Recipe.description AS desc, Recipe.uniqueid AS rid, Recipe.id AS idr, * FROM Recipe INNER JOIN Cookbook ON Cookbook.id = CookbookRecipe.Cookbookid INNER JOIN CookbookRecipe ON CookbookRecipe.Recipeid = Recipe.id INNER JOIN Contributers ON Contributers.cookbookid = Cookbook.id WHERE Cookbook.progress = 'added' AND Recipe.progress = 'added' AND (Recipe.addedBy =? OR Contributers.accountid = ?)", new String[] {  user, user });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				rb.add(cursorToAllRecipes(cursor));	                
			}
		}
		cursor.close();
		close();
		return rb;	
	}

	public boolean doesUserHaveAccess(String user, String uid)
	{

		boolean access;
		open();
		Cursor cursor = database.rawQuery("SELECT  * FROM Cookbook LEFT JOIN Contributers ON Contributers.cookbookid = Cookbook.id WHERE  Cookbook.uniqueid = ? AND ( Contributers.accountid = ? OR Cookbook.creator=?)", new String[] { uid,  user, user });
		if (cursor != null && cursor.getCount() > 0) {
			access = true;
		}
		else
		{
			access = false;
		}
		cursor.close();
		close();
		return access;
	}
	
	/**
	 * Sets info from database to recipe bean
	 * 
	 * @param cursor
	 * @return recipe information
	 */
	public RecipeBean cursorToAllRecipes(Cursor cursor) 
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
		return rb;
	}

	/**
	 * Selects preperation info based on recipe
	 * @param id
	 * @return ArrayList<preperationBean>
	 */
	public ArrayList<PreperationBean> selectPreperation(int id)
	{
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
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


	/**
	 * Select images based on recipe id
	 * @param id - recipe id
	 * @return imageBean - information relating to the image
	 **/
	public ImageBean selectImages(int id)
	{
		ImageBean img = new ImageBean();
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
	 * @return ArrayList<ingredientBean> - ingredient information
	 */
	public ArrayList<IngredientBean> selectIngredients(int id)
	{
		ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
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
	public IngredientBean cursorToIngred(Cursor cursor) {
		IngredientBean ib = new IngredientBean();
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
	public PreperationBean cursorToPrep(Cursor cursor) {
		PreperationBean pb = new PreperationBean();
		pb.setPreperation(cursor.getString(getIndex("instruction",cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		pb.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		return pb;
	}



	/**
	 * Select cookbooks id based on uniqueid
	 * @param uniqueid
	 * @return int id
	 */
	public int selectCookbooksIDByUnique(String uniqueid)
	{
		int id = 0;
		//open();
		Cursor cursor = database.rawQuery("SELECT id FROM Cookbook WHERE uniqueid=?", new String[] { uniqueid});
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
