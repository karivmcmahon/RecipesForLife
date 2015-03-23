package com.example.recipesforlife.models;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles database details relating to cookbooks
 * @author Kari
 *
 */
public class CookbookModel extends BaseDataSource {

	
	RecipeModel recipemodel;
	Context context;
	Utility utils;
	//Shared prefs to store user details
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 

	public CookbookModel(Context context) {
		super(context);
		this.context = context;
		utils = new Utility();
		recipemodel = new RecipeModel(context);
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	}


	/**
	 *  Inserts cookbook information into sqlite database
	 * 
	 * @param book - cookbook information to be inserted into db
	 * @param server - Whether the request came from the server or the application
	 * @return String - the unique id generated for the cookbook
	 */
	public String insertBook(CookbookBean book, boolean server)
	{
		open();
		ContentValues values = new ContentValues();
		values.put("name", book.getName()); 
		values.put("changeTime", "2015-01-01 12:00:00.000"); //An initial time set for change time when inserted
		values.put("creator", book.getCreator()); 
		values.put("privacyOption", book.getPrivacy()); 
		values.put("description", book.getDescription());
		values.put("image", book.getImage());
		values.put("progress", "added");
		String uid = "";
		if(server == true)
		{
			values.put("updateTime", sharedpreferences.getString("Cookbook", "DEFAULT")); //if server use shared pref time
			values.put("uniqueid", book.getUniqueid()); //gets unique id if from server
			uid = book.getUniqueid();
		}
		else
		{
			values.put("updateTime", utils.getLastUpdated(false));  //if application get time from timestamp
			uid = utils.generateUUID(book.getCreator(), "Cookbook", database); //generate uuid for when being inserted
			values.put("uniqueid", uid);
		}
		database.beginTransaction();
		try
		{
			database.insertOrThrow("Cookbook", null, values);
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
		return uid;

	}

	
	/**
	 * Updates the cookbook in the database
	 * @param cookbook - cookbook information to be updated
	 * @param server - if request from the server or the application
	 */
	public void updateBook(CookbookBean cookbook, boolean server)
	{

		open();
		ContentValues updateVals = new ContentValues();
		updateVals.put("name", cookbook.getName());
		updateVals.put("description", cookbook.getDescription());
		updateVals.put("privacyOption", cookbook.getPrivacy());
		updateVals.put("progress", cookbook.getProgress());
		if(server == true)
		{
			//Get timestamp based on shared pref if request from server
			updateVals.put("changeTime", sharedpreferences.getString("Cookbook Update", "DEFAULT"));
		}
		else
		{
			//Sets time from timestamp if request from the application
			updateVals.put("changeTime", utils.getLastUpdated(false));	
		}
		updateVals.put("image", cookbook.getImage());
		String[] args = new String[]{cookbook.getUniqueid()};
		database.beginTransaction();
		try
		{
			database.update("Cookbook", updateVals, "uniqueid=?", args);
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

	
	/**
	 * Inserts contributers information into the database
	 * 
	 * @param email - email address of contributer
	 * @param cookbookid - id of cookbook
	 * @param server - if request from server or the application
	 */
	public void insertContributers(String email, int cookbookid, boolean server)
	{

		open();
		ContentValues values = new ContentValues();
		values.put("cookbookid", cookbookid); 
		if(server == true)
		{
			//Get timestamp based on the shared pref if request from the server
			values.put("updateTime", sharedpreferences.getString("Contributers", "DEFAULT"));
		}
		else
		{
			//Get current timestamp if from application
			values.put("updateTime", utils.getLastUpdated(false)); 
		}
		values.put("changeTime", "2015-01-01 12:00:00.000"); 
		values.put("accountid", email); 
		values.put("progress", "added"); //progress set to help manage if the contributer is added/deleted
		database.beginTransaction();
		try
		{ 
			database.insertOrThrow("Contributers", null, values);
			database.setTransactionSuccessful();
			database.endTransaction(); 
			close();
		}catch(SQLException e)
		{
			database.endTransaction();
			e.printStackTrace();
			close();
			throw e;
		} 
		close();


	}

	
	/**
	 * Update contributers in the database
	 * 
	 * @param email - email of the contributer
	 * @param cookbookid - id of the cookbook
	 * @param progress - progress of contributer account whether added or deleted
	 * @param server - if the request from the server or from the application
	 */
	public void updateContributers(String email, int cookbookid, String progress, boolean server)
	{

		open();
		ContentValues values = new ContentValues();
		values.put("cookbookid", cookbookid); 
		if(server == true)
		{
			//Get timestamp based on the shared pref if request from server
			values.put("changeTime", sharedpreferences.getString("Contributers Update", "DEFAULT"));
		}
		else
		{
			//Get current timestamp if from application
			values.put("changeTime", utils.getLastUpdated(false)); 
		}
		values.put("accountid", email); 
		values.put("progress", progress); 
		database.beginTransaction();
		try
		{ 
			database.update("Contributers", values, "cookbookid=? AND accountid=?", new String[] { Integer.toString(cookbookid), email });
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
	 * Select contributers for a specific cookbook
	 * @param uniqueid - coobooks unique id
	 * @return ArrayList<String> of contributers emails
	 */
	public ArrayList<String> selectCookbookContributers(String uniqueid, String progress)
	{

		ArrayList<String> names = new ArrayList<String>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM  Contributers  INNER JOIN Cookbook ON Contributers.cookbookid=Cookbook.id WHERE Cookbook.uniqueid=? AND Contributers.progress=? ", new String[] { uniqueid, progress });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				names.add(cursor.getString(getIndex("accountid",cursor)));
			}
		}
		cursor.close();
		close();
		return names;	
	}

	/**
	 * Check if contributer exists where email and cookbookid matches
	 * @param email
	 * @param id
	 * @return boolean - whether contrib exists
	 */
	public boolean selectContributer(String email, int id)
	{
		boolean exists = false;
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM  Contributers  WHERE cookbookid=? AND accountid=?", new String[] { Integer.toString(id) , email});
		if (cursor != null && cursor.getCount() > 0) {
			exists = true;
		}
		else
		{
			exists = false;
		}
		cursor.close();
		close();
		return exists;
	}

	/**
	 * Select cookbooks which belong to a user whether they are creator or a contributer
	 * @param user
	 * @return ArrayList<cookbookBean> - List of cookbooks
	 */
	public ArrayList<CookbookBean> selectCookbooksByUser(String user)
	{

		ArrayList<CookbookBean> cbList = new ArrayList<CookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook LEFT JOIN Contributers ON Cookbook.id=Contributers.cookbookid WHERE creator=? AND Cookbook.progress=? OR Contributers.accountid=? AND Cookbook.progress=?  GROUP BY uniqueid ", new String[] { user, "added", user, "added"});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cbList.add(cursorToCookbook(cursor));               
			}
		}
		cursor.close();
		close();
		return cbList;	
	}

	/**
	 * Select cookbooks where the user is a creator
	 * @param user
	 * @return ArrayList<cookbookBean> - List of cookbooks
	 */
	public ArrayList<CookbookBean> selectCookbooksByCreator(String user)
	{

		ArrayList<CookbookBean> cbList = new ArrayList<CookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE creator=?", new String[] { user });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cbList.add(cursorToCookbook(cursor));           
			}
		}
		cursor.close();
		close();
		return cbList;	
	}

	/**
	 * Select the creator from a specific cookbook
	 * @param user
	 * @return ArrayList<cookbookBean> - List of cookbooks
	 */
	public String creatorForCookbook(String uniqueid)
	{

		String creator = "";
		open();
		Cursor cursor = database.rawQuery("SELECT creator FROM Cookbook WHERE uniqueid=?", new String[] { uniqueid });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				creator = cursor.getString(getIndex("creator", cursor));        
			}
		}
		cursor.close();
		close();
		return creator;	
	}

	/**
	 * Select cookbooks by a uniqueid
	 * @param uniqueid
	 * @return List of cookbooks
	 */
	public ArrayList<CookbookBean> selectCookbook(String uniqueid)
	{		
		ArrayList<CookbookBean> cbList = new ArrayList<CookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook   WHERE uniqueid=? AND progress='added'", new String[] { uniqueid });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cbList.add(cursorToCookbook(cursor));                
			}
		}
		cursor.close();
		close();
		return cbList;	
	}

	/**
	 * Select cookbooks id based on a uniqueid of the cookbook
	 * @param uniqueid
	 * @return int id
	 */
	public int selectCookbooksIDByUnique(String uniqueid)
	{
		int id = 0;
		open();
		Cursor cursor = database.rawQuery("SELECT id FROM Cookbook WHERE uniqueid=?", new String[] { uniqueid});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = cursor.getInt(getIndex("id",cursor));


			}
		}
		cursor.close(); 
		close();
		return id;	
	}

	/**
	 * Select cookbooks unique id based on a recipe id
	 * @param recipeid
	 * @return String uniqueid
	 */
	public String selectCookbooksUniqueID(int recipeid)
	{
		String id = "";
		open();
		Cursor cursor = database.rawQuery("SELECT uniqueid FROM Cookbook INNER JOIN CookbookRecipe WHERE CookbookRecipe.Recipeid=? AND  Cookbook.id=CookbookRecipe.Cookbookid", new String[] { Integer.toString(recipeid) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = cursor.getString(getIndex("uniqueid",cursor));


			}
		}
		cursor.close();
		close();
		return id;	
	}

	/**
	 * Select cookbooks name based on uniqueid
	 * @param uniqueid
	 * @return String cookbook name
	 */
	public String selectCookbooksNameByID(String uniqueid)
	{
		String name = "";
		open();
		Cursor cursor = database.rawQuery("SELECT name FROM Cookbook WHERE uniqueid=?", new String[] { uniqueid });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				name = cursor.getString(getIndex("name",cursor));


			}
		}
		cursor.close();
		close();
		return name;	
	}

	/**
	 * Selecting a list of recipes based on a cookbook's unique id
	 * @param uniqueid
	 * @return List of recipes
	 */
	public ArrayList<RecipeBean> selectRecipesByCookbook(String uniqueid)
	{
		open();
		ArrayList<RecipeBean> names = new ArrayList<RecipeBean>();
		Cursor cursor = database.rawQuery("SELECT Recipe.id AS recipeid, Recipe.name AS recipename, Recipe.uniqueid AS rid FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.uniqueid = ?  AND CookbookRecipe.Cookbookid=Cookbook.id AND Recipe.progress=?", new String[] { uniqueid, "added" });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				names.add(cursorToRecipe(cursor));
			}
		}
		cursor.close();
		close();
		return names;
	}

	/**
	 * Creating a cookbookBean based on cursor from the database
	 * @param cursor
	 * @return cookbookBean - cookbook info
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
	 * Creating a recipebean based on cursor from the database
	 * @param cursor
	 * @return recipeBean - recipe info
	 */
	public RecipeBean cursorToRecipe(Cursor cursor) {
		RecipeBean rb = new RecipeBean();
		rb.setId(cursor.getInt(getIndex("recipeid",cursor)));
		rb.setName(cursor.getString(getIndex("recipename",cursor)));   
		rb.setUniqueid(cursor.getString(getIndex("rid", cursor)));
		return rb;
	}


	/**
	 * Select images based on recipe id
	 * @param id
	 * @return imageBean - image info
	 */
	public ImageBean selectImage(int id)
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
	 * Select cookbooks id based on creator or contribs of the book  and name of the book
	 * @param name - name of book
	 * @param user
	 * @return int - the id
	 */
	public int selectCookbooksID(String name, String user)
	{
		int id = 0;
		open();
		Cursor cursor = database.rawQuery("SELECT Cookbook.id AS cid FROM Cookbook LEFT JOIN Contributers ON Cookbook.id=Contributers.cookbookid WHERE Cookbook.name=? AND Cookbook.creator=? AND Cookbook.progress=? OR Cookbook.name=? AND Contributers.accountid=?  AND Cookbook.progress=? GROUP BY uniqueid ", new String[] {name, user, "added", name, user, "added"});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = cursor.getInt(getIndex("cid",cursor));
			}
		}
		cursor.close(); 
		close();
		return id;	
	}

	/**
	 * Select uniqueid for a cookbook where the cookbook is at a rowid
	 * @param rowid
	 * @return String
	 */
	public String selectCookbooksByRowID(int rowid)
	{
		String id = "";
		open();
		Cursor cursor = database.rawQuery("SELECT uniqueid FROM Cookbook WHERE id=?", new String[] { Integer.toString(rowid) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				id = cursor.getString(getIndex("uniqueid",cursor));
			}
		}
		cursor.close(); 
		close();
		return id;	
	}


}
