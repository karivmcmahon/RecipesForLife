package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.controllers.userBean;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
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
	
	public void insertRecipe(recipeBean recipe)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
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
	    values.put("addedBy", sharedpreferences.getString(emailk, "")); 
    	recipeID = database.insertOrThrow("Recipe", null, values);
    	insertIngredient(recipe);
    	insertPrep(recipe);
    	
    	close();
    	sync.getIngred();
	}
	
	public void insertPrep(recipeBean recipe)
	{
		open();
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
        close();
	}
	
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
	
	public void insertIngredient(recipeBean recipe)
	{
		open();
		getLastUpdated();
        ingredValues = new ContentValues();
        for(int i = 0; i < recipe.getIngredients().size(); i++)
        {
        	Log.v( "Ingred " , "Ingred " + recipe.getIngredients().get(i).toString());
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
        close();
	}
	
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
	
	public void insertIngredToDetails()
	{
		getLastUpdated();
        ingredToDetailsValues = new ContentValues();
        ingredToDetailsValues.put("ingredientid",ingredID);
        ingredToDetailsValues.put("IngredientDetailsid",ingredDetsID);
        ingredToDetailsValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("IngredToIngredDetails", null, ingredToDetailsValues);
	}
	
	
	public void insertRecipeToIngredient()
	{
		getLastUpdated();
        ingredToRecipeValues = new ContentValues();
        ingredToRecipeValues.put("Recipeid",recipeID);
        ingredToRecipeValues.put("ingredientDetailsId", ingredDetsID);
        ingredToRecipeValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("RecipeIngredient", null, ingredToRecipeValues);
	}
	
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
	
	
	private String dateToString(Date date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	private void getLastUpdated()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
	}
	
}
