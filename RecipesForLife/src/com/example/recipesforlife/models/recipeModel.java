package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

public class recipeModel extends baseDataSource {

	Context context;
	ContentValues values,  prepvalues, preptorecipevalues, ingredValues, ingredToDetailsValues, ingredDetailsValues, ingredToRecipeValues;
	String lastUpdated;
	long recipeID, prepID, ingredID, ingredDetsID;
	protected recipeModel(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public void insertRecipe(List<String> recipeInfo)
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);	
	    values = new ContentValues();
	    values.put("name", "recipe"); 
	    values.put("updateTime", lastUpdated); 
	    values.put("description", "desc"); 
	    values.put("prepTime", "1:00:00"); 
	    values.put("cookingTime", "2:00:00"); 
	    values.put("totalTime", "4:00:00");    
	    values.put("serves", 4); 
	    values.put("addedBy", "jane"); 
    	recipeID = database.insertOrThrow("Recipe", null, values);
    	close();
	}
	
	public void insertPrep(List<String> prepList)
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        prepvalues.put("instruction", "recipes");
        prepvalues.put("instructionNum", 1);
        prepvalues.put("updateTime", lastUpdated); 
        database.insertOrThrow("Preperation", null, prepvalues);
        close();
	}
	
	public void insertPrepToRecipe()
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        preptorecipevalues.put("recipeid", 1);
        preptorecipevalues.put("Preperationid", 1);
        preptorecipevalues.put("updateTime", lastUpdated); 
        prepID = database.insertOrThrow("PrepRecipe", null, prepvalues);
        close();
	}
	
	public void insertIngredient()
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        ingredValues.put("name", "pineapple");
        ingredValues.put("updateTime", lastUpdated); 
        ingredID = database.insertOrThrow("Ingredient", null, ingredValues);
        close();
	}
	
	public void insertIngredientDetails()
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        ingredDetailsValues.put("ingredientId", 1);
        ingredDetailsValues.put("amount", 2);
        ingredDetailsValues.put("note", "er");
        ingredDetailsValues.put("value","kg");
        ingredDetsID = database.insertOrThrow("IngredientDetails", null, ingredDetailsValues);
        close();
	}
	
	public void insertIngredToDetails()
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        ingredToDetailsValues.put("ingredientid",1);
        ingredToDetailsValues.put("IngredientDetailsid",1);
        ingredToDetailsValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("IngredToIngredDetails", null, ingredToDetailsValues);
        close();
	}
	
	public void insertRecipeToIngredient()
	{
		open();
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        Date today = cal.getTime();
        lastUpdated = dateToString(today);
        ingredToRecipeValues.put("Recipeid",recipeID);
        ingredToRecipeValues.put("ingredientDetailsId", ingredDetsID);
        ingredToRecipeValues.put("updateTime", lastUpdated); 
        database.insertOrThrow("RecipeIngredient", null, ingredToRecipeValues);
        close();
	}
	private String dateToString(Date date) 
	{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
}
