package com.example.recipesforlife.models;

import java.util.ArrayList;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.imageBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles database details relating to cookbooks
 * @author Kari
 *
 */
public class cookbookModel extends baseDataSource {

	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	recipeModel recipemodel;
	SharedPreferences sharedpreferences;
	Context context;
	utility utils;

	public cookbookModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		utils = new utility();
		recipemodel = new recipeModel(context);
	}

	/**
	 * Inserts a cookbook into the database
	 * @param book
	 * @param server
	 */
	public String insertBook(cookbookBean book, boolean server)
	{

		open();
		ContentValues values = new ContentValues();
		values.put("name", book.getName()); 
		values.put("updateTime", utils.getLastUpdated(false)); 
		values.put("changeTime", "2015-01-01 12:00:00.000"); 
		values.put("creator", book.getCreator()); 
		values.put("privacyOption", book.getPrivacy()); 
		values.put("description", book.getDescription());
		String uid = "";
		if(server == true)
		{
			
			values.put("uniqueid", book.getUniqueid());
			uid = book.getUniqueid();
		}
		else
		{
			uid = utils.generateUUID(book.getCreator(), "Cookbook", database);
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
			database.endTransaction();
			close();
		} 
		close();
		return uid;

	}

	/**
	 * Updates a cookbook in the database
	 * @param cookbook
	 */
	public void updateBook(cookbookBean cookbook)
	{
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ContentValues updateVals = new ContentValues();
		updateVals.put("name", cookbook.getName());
		updateVals.put("description", cookbook.getDescription());
		updateVals.put("privacyOption", cookbook.getPrivacy());
		updateVals.put("changeTime", utils.getLastUpdated(false));	
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
		}
		close();

	}

	/**
	 * Insert contributers into the database where we know the contributers name and cookbook id
	 * @param email
	 * @param cookbookid
	 */
	public void insertContributers(String email, int cookbookid)
	{

		open();
		ContentValues values = new ContentValues();
		values.put("cookbookid", cookbookid); 
		values.put("updateTime", utils.getLastUpdated(false)); 
		values.put("changeTime", "2015-01-01 12:00:00.000"); 
		values.put("accountid", email); 
		values.put("progress", "added"); 
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
			close();
		} 
		close();


	}
	
	/**
	 * Insert contributers into the database where we know the contributers name and cookbook id
	 * @param email
	 * @param cookbookid
	 */
	public void updateContributers(String email, int cookbookid, String progress)
	{

		open();
		ContentValues values = new ContentValues();
		values.put("cookbookid", cookbookid); 
		values.put("changeTime", utils.getLastUpdated(false)); 
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
			database.endTransaction();
			close();
		} 
		close();


	}

	/**
	 * Delete contributers from the database based on cookbook id and account id 
	 * @param id
	 * @param user
	 */
/**	public void  deleteContributers(int id, String user)
	{		
		open();
		database.delete("Contributers","cookbookid=? AND accountid=?", new String[] { Integer.toString(id), user }); 
		close();        
	} **/

	/**
	 * Select contributers for a specific cookbook
	 * @param uniqueid
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
	 * Select cookbooks which belong to a user 
	 * @param user
	 * @return ArrayList<cookbookBean> - List of cookbooks
	 */
	public ArrayList<cookbookBean> selectCookbooksByUser(String user)
	{

		ArrayList<cookbookBean> cbList = new ArrayList<cookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook LEFT JOIN Contributers ON Cookbook.id=Contributers.cookbookid WHERE creator=? OR Contributers.accountid=? GROUP BY uniqueid ", new String[] { user, user });
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
	 * Select cookbooks where the user is a crearoe
	 * @param user
	 * @return ArrayList<cookbookBean> - List of cookbooks
	 */
	public ArrayList<cookbookBean> selectCookbooksByCreator(String user)
	{

		ArrayList<cookbookBean> cbList = new ArrayList<cookbookBean>();
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
	 * Select cookbooks where the user is a crearoe
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
	 * Select cookbooks by uniqueid
	 * @param uniqueid
	 * @return List of cookbooks
	 */
	public ArrayList<cookbookBean> selectCookbook(String uniqueid)
	{		
		ArrayList<cookbookBean> cbList = new ArrayList<cookbookBean>();
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook   WHERE uniqueid=?", new String[] { uniqueid });
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
	 * Select cookbooks id based on uniqueid
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
	 * Selecting a list of recipes based on a cookbook's id
	 * @param uniqueid
	 * @return List of recipes
	 */
	public ArrayList<recipeBean> selectRecipesByCookbook(String uniqueid)
	{
		open();
		ArrayList<recipeBean> names = new ArrayList<recipeBean>();
		Cursor cursor = database.rawQuery("SELECT Recipe.id AS recipeid, Recipe.name AS recipename, Recipe.uniqueid AS rid FROM Recipe INNER JOIN Cookbook INNER JOIN CookbookRecipe ON Recipe.id = CookbookRecipe.Recipeid WHERE Cookbook.uniqueid = ?  AND CookbookRecipe.Cookbookid=Cookbook.id ", new String[] { uniqueid });
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
	 * Creating a cookbookBean based on cursor
	 * @param cursor
	 * @return cookbookBean
	 */
	public cookbookBean cursorToCookbook(Cursor cursor) {
		cookbookBean cb = new cookbookBean();
		cb.setName(cursor.getString(getIndex("name",cursor)));
		cb.setDescription(cursor.getString(getIndex("description",cursor)));
		cb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		cb.setPrivacy(cursor.getString(getIndex("privacyOption",cursor)));
		cb.setCreator(cursor.getString(getIndex("creator",cursor)));
		cb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		cb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
		return cb;
	}

	/** 
	 * Creating a recipebean based on cursor
	 * @param cursor
	 * @return recipeBean - recipe info
	 */
	public recipeBean cursorToRecipe(Cursor cursor) {
		recipeBean rb = new recipeBean();
		int id = cursor.getInt(getIndex("recipeid",cursor));
		rb.setName(cursor.getString(getIndex("recipename",cursor)));   
		rb.setUniqueid(cursor.getString(getIndex("rid", cursor)));
		rb.setImage(selectImage(id));
		return rb;
	}


	public byte[] selectImage(int id)
	{
		imageBean img = new imageBean();
		open();
		Cursor cursor = database.rawQuery("SELECT image FROM Images INNER JOIN RecipeImages ON RecipeImages.imageid=Images.imageid WHERE RecipeImages.Recipeid = ?", new String[] { Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				img.setImage(cursor.getBlob(getIndex("image", cursor)));
			}
		}
		cursor.close();
		close();
		return img.getImage();
	}

	/**
	 * Select cookbooks id based on creator and name of the book
	 * @param name
	 * @param user
	 * @return int - the id
	 */
	public int selectCookbooksID(String name, String user)
	{
		int id = 0;
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Cookbook WHERE creator=? AND name=?", new String[] { user , name });
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
