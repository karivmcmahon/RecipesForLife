package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

public class recipeModel extends baseDataSource {

	Context context;
	ContentValues values,  prepvalues, preptorecipevalues, ingredValues, ingredToDetailsValues, ingredDetailsValues, ingredToRecipeValues;
	String lastUpdated;
	long recipeID, prepID, ingredID, ingredDetsID;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	syncRecipeModel sync;
	public recipeModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		sync = new syncRecipeModel(context);
	}
	
	/**
	 * Inserts recipeBean data into database
	 * @param recipe
	 */
	public void insertRecipe(recipeBean recipe)
	{
		
		open();
		getLastUpdated();
	    values = new ContentValues();
	    values.put("name", recipe.getName()); 
	    values.put("updateTime", lastUpdated); 
	    values.put("description", recipe.getDesc()); 
	    values.put("prepTime", recipe.getPrep()); 
	    values.put("cookingTime", recipe.getCooking()); 
	    values.put("totalTime", "4:00:00");    
	    values.put("serves", recipe.getServes()); 
	    values.put("addedBy", recipe.getAddedBy()); 
	    database.beginTransaction();
        try
        {
        	recipeID = database.insertOrThrow("Recipe", null, values);
        	insertIngredient(recipe);
        	insertPrep(recipe);
        	database.setTransactionSuccessful();
        	database.endTransaction(); 
        	Log.v("suc", "suc");
        }catch(SQLException e)
        {
        	database.endTransaction();
        	Log.v("Transaction fail", "Transaction fail for recipe insert");
        }
    	close();
    	
    	
	}
	
	/**
	 * Insert preperation information into database based on recipeBean
	 * @param recipe
	 */
	public void insertPrep(recipeBean recipe)
	{
	//	open();
		getLastUpdated();
        prepvalues = new ContentValues();
        for(int i = 0; i < recipe.getStepNum().size(); i++)
        {
	        prepvalues.put("instruction", recipe.getSteps().get(i).toString());
	        prepvalues.put("instructionNum", Integer.parseInt(recipe.getStepNum().get(i).toString()));
	        prepvalues.put("updateTime", lastUpdated); 
	        prepID = database.insertOrThrow("Preperation", null, prepvalues);
	        insertPrepToRecipe();
        }
       // close();
	}
	
	/**
	 * Insert prep id and recipe ids into PrepRecipe in the database
	 */
	public void insertPrepToRecipe()
	{
		
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        preptorecipevalues = new ContentValues();
        preptorecipevalues.put("recipeId", recipeID);
        preptorecipevalues.put("Preperationid", prepID);
        preptorecipevalues.put("updateTime", lastUpdated); 
        database.insertOrThrow("PrepRecipe", null, preptorecipevalues);
        
	}
	
	/**
	 * Insert ingredient information into database
	 * @param recipe
	 */
	public void insertIngredient(recipeBean recipe)
	{
	//	open();
		getLastUpdated();
        ingredValues = new ContentValues();
        for(int i = 0; i < recipe.getIngredients().size(); i++)
        {
        	int id = selectIngredient(recipe, i);
        	if(id == 0)
        	{
	        	ingredValues.put("name", recipe.getIngredients().get(i).toString().toLowerCase());
	            ingredValues.put("updateTime", lastUpdated); 
	            ingredID = database.insertOrThrow("Ingredient", null, ingredValues);
        	}
        	else
        	{
        		ingredID = id;
        	}
            insertIngredientDetails(i, recipe);
            insertRecipeToIngredient();
            insertIngredToDetails();
        }
     //   close();
	}
	
	/**
	 * Insert ingredient details into database
	 * @param i
	 * @param recipe
	 */
	public void insertIngredientDetails(int i, recipeBean recipe)
	{
		getLastUpdated();
        ingredDetailsValues = new ContentValues();
        ingredDetailsValues.put("ingredientId", ingredID);
        ingredDetailsValues.put("amount", Integer.parseInt(recipe.getAmount().get(i).toString()));
        ingredDetailsValues.put("note", recipe.getNotes().get(i).toString());
        ingredDetailsValues.put("value", recipe.getValues().get(i).toString());
        ingredDetailsValues.put("updateTime", lastUpdated); 
        ingredDetsID = database.insertOrThrow("IngredientDetails", null, ingredDetailsValues);
        
	}
	
	/**
	 * Insert connected ingred details id and ingred id into database
	 */
	public void insertIngredToDetails()
	{
		getLastUpdated();
        ingredToDetailsValues = new ContentValues();
        ingredToDetailsValues.put("ingredientid",ingredID);
        ingredToDetailsValues.put("IngredientDetailsid",ingredDetsID);
        ingredToDetailsValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("IngredToIngredDetails", null, ingredToDetailsValues);
	}
	
	
	/**
	 * Insert connected ingred details id and recipe id into database
	 */
	public void insertRecipeToIngredient()
	{
		getLastUpdated();
        ingredToRecipeValues = new ContentValues();
        ingredToRecipeValues.put("Recipeid",recipeID);
        ingredToRecipeValues.put("ingredientDetailsId", ingredDetsID);
        ingredToRecipeValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("RecipeIngredient", null, ingredToRecipeValues);
	}
	
	/**
	 * Retrieve ingredient from database the helps us know whether to insert ingredient. If id 0 then insert
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public int selectIngredient(recipeBean recipe, int x)
	{	
			int id = 0;
	        Cursor cursor = database.rawQuery("SELECT * FROM Ingredient WHERE name=?", new String[] { recipe.getIngredients().get(x).toString().toLowerCase() });
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
	 * Retrieve recipe from database by a certain a user so we do not add duplicate recipes
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public boolean selectRecipe(String name, String user)
	{		
			open();
	        Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE name=? and addedBy=?", new String[] { name, user });
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
	        return rb;
	    }
	
	/**
	 * Retrieve recipe from database by a certain a user so we do not add duplicate recipes
	 * @param recipe
	 * @param x
	 * @return id 
	 */
	public recipeBean selectRecipe2(String name, String user)
	{		
		    recipeBean rb = new recipeBean();
		    open();
	        Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE name=? and addedBy=?", new String[] { name, user });
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
        Cursor cursor = database.rawQuery("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = ?", new String[] { Integer.toString(id) });
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
	        return pb;
	    }
	
	
	/**
	 * Create date to string
	 * @param date
	 * @return
	 */
	private String dateToString(Date date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	/**
	 * Get current date
	 */
	private void getLastUpdated()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
	}
	
}
