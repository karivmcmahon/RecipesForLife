package com.example.recipesforlife.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class syncRecipeModel extends baseDataSource {
	Context context;
	String currentDate;
	
	public syncRecipeModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<recipeBean> getRecipe()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
	    ArrayList<recipeBean> recipeList = new ArrayList<recipeBean>();
	    Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE datetime(updateTime) > datetime(?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                recipeList.add(cursorToRecipe(cursor));
            }
        }
        cursor.close();
        close();
        return recipeList;
	}
	
	public recipeBean cursorToRecipe(Cursor cursor) {
        recipeBean rb = new recipeBean();
        rb.setId(cursor.getInt(getIndex("id",cursor)));       
        rb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
        rb.setName(cursor.getString(getIndex("name", cursor)));
        rb.setDesc(cursor.getString(getIndex("description",cursor)));
        rb.setPrep(cursor.getString(getIndex("prepTime", cursor)));
        rb.setCooking(cursor.getString(getIndex("cookingTime", cursor)));
        rb.setServes(cursor.getString(getIndex("serves", cursor)));
        rb.setAddedBy(cursor.getString(getIndex("addedBy", cursor)));
        return rb;
    }
	
	public ArrayList<ingredientBean> getIngred()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
	    ArrayList<ingredientBean> ingredientList = new ArrayList<ingredientBean>();
	    Cursor cursor = database.rawQuery("SELECT * FROM Ingredient,IngredToIngredDetails, RecipeIngredient, IngredientDetails WHERE datetime(Ingredient.updateTime) > datetime(?) OR datetime(IngredToIngredDetails.updateTime) > datetime(?) OR datetime(IngredientDetails.updateTime) > datetime(?) OR datetime(RecipeIngredient.updateTime) > datetime(?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                ingredientList.add(cursorToIngredient(cursor));
            }
        }
        cursor.close();
        close();
        return ingredientList;
	}
	
	public ingredientBean cursorToIngredient(Cursor cursor)
	{
		ingredientBean ib = new ingredientBean();
		ib.setIngredId(cursor.getInt(getIndex("Ingredient.id", cursor)));
		ib.setName(cursor.getString(getIndex("Ingredient.name", cursor)));
		ib.setDetailsId(cursor.getInt(getIndex("IngredientDetails.id", cursor)));
		ib.setIngredDetsId(cursor.getInt(getIndex("IngredientDetails.ingredientId", cursor)));
		ib.setAmount(cursor.getInt(getIndex("IngredientDetails.amount", cursor)));
		ib.setValue(cursor.getString(getIndex("IngredientDetails.value",cursor)));
		ib.setNote(cursor.getString(getIndex("IngredientDetails.note", cursor)));
		ib.setRecipeId(cursor.getInt(getIndex("RecipeIngredient.Recipeid",cursor)));
		ib.setRecipeIngredId(cursor.getInt(getIndex("RecipeIngredient.ingredientDetailsId", cursor)));
		ib.setDetsConnectId(cursor.getInt(getIndex("IngredToIngredDetails.ingredientDetailsid", cursor)));
		ib.setDetsConnectIngredId(cursor.getInt(getIndex("IngredToIngredDetails.ingredientid", cursor)));
		return ib;
	}
	
	public ArrayList<preperationBean> getPrep()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
	    ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
	    Cursor cursor = database.rawQuery("SELECT * FROM Preperation, PrepRecipe WHERE datetime(Preperation.updateTime) > datetime(?) OR datetime(PrepRecipe.updateTime) > datetime(?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                prepList.add(cursorToPreperation(cursor));
            }
        }
        cursor.close();
        close();
        return prepList;
	}
	
	public preperationBean cursorToPreperation(Cursor cursor)
	{
		preperationBean pb = new preperationBean();
		pb.setId(cursor.getInt(getIndex("Preperation.id",cursor)));
		pb.setPreperation(cursor.getString(getIndex("Preperation.instruction", cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("Preperation.instructionNum", cursor)));
		pb.setPrepId(cursor.getInt(getIndex("PrepRecipe.Preperationid", cursor)));
		pb.setRecipeId(cursor.getInt(getIndex("PrepRecipe.recipeId",cursor)));
		return pb;
	}
	private String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}
	
	public void getCurrentDate()
	{
		Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        Date today = cal.getTime();
        currentDate = dateToString(today);
	}

}
