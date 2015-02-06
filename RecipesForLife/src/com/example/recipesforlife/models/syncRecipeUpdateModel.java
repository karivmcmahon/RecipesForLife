package com.example.recipesforlife.models;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

public class syncRecipeUpdateModel  extends baseDataSource {

Context context;
	
	
	public syncRecipeUpdateModel(Context context) {
		super(context);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Gets recipe to send to server based on date they were added using shared preferecnes
	 * @return ArrayList<recipeBean> recipeList
	 */
	public ArrayList<recipeBean> getRecipe()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
	    ArrayList<recipeBean> recipeList = new ArrayList<recipeBean>();
	    Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime)", new String[] { sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT")   });
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
	
	/**
	 * Sets the recipe data from the database to recipebean
	 * @param cursor
	 * @return recipeBean
	 */
	public recipeBean cursorToRecipe(Cursor cursor) {
        recipeBean rb = new recipeBean();
        rb.setId(cursor.getInt(getIndex("id",cursor)));       
        rb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
        rb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
        rb.setName(cursor.getString(getIndex("name", cursor)));
        rb.setDesc(cursor.getString(getIndex("description",cursor)));
        rb.setPrep(cursor.getString(getIndex("prepTime", cursor)));
        rb.setCooking(cursor.getString(getIndex("cookingTime", cursor)));
        rb.setServes(cursor.getString(getIndex("serves", cursor)));
        rb.setAddedBy(cursor.getString(getIndex("addedBy", cursor)));
        return rb;
    }
	
	/**
	 * Get ingredients for recipes based on recipe id within the date time frame
	 * @param id
	 * @return ArrayList<IngredientBean>
	 */
	public ArrayList<ingredientBean> getIngred(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
	    ArrayList<ingredientBean> ingredientList = new ArrayList<ingredientBean>();
	    Cursor cursor = database.rawQuery("SELECT ingredientDetailsId From RecipeIngredient WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime) AND Recipeid = ? ", new String[] {  sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT"), Integer.toString(id) });
	    if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int detsId = cursor.getInt(getIndex("ingredientDetailsId", cursor));
                Cursor cursor2 = database.rawQuery("SELECT * FROM IngredientDetails WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime) AND id = ?", new String[] {  sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT") , Integer.toString(detsId) });
                if (cursor2 != null && cursor2.getCount() > 0) {
                    for (int x = 0; x < cursor2.getCount(); x++) {
                        cursor2.moveToPosition(x);
                        ingredientList.add(cursorToIngredientDetails(cursor2));
                    }
                }
                cursor2.close();
            }
        }
        cursor.close();
       close();
        return ingredientList;
	}
	
	/**
	 * Retrieves name of ingredient based on id then returns name
	 * @param id
	 * @return name
	 */
	public String getIngredName(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		String name = null;
	    Cursor cursor = database.rawQuery("SELECT name From Ingredient WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime) AND id = ? ", new String[] {  sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT"), Integer.toString(id) });
	    if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                 name = cursor.getString(getIndex("name", cursor));
               
            }
        }
        cursor.close();
        close();
        return name;
	}
	
	/**
	 * Sets the ingredient data from the database to ingredientbean
	 * @param cursor
	 * @return recipeBean
	 */
	public ingredientBean cursorToIngredientDetails(Cursor cursor)
	{
		ingredientBean ib = new ingredientBean();
		ib.setAmount(cursor.getInt(getIndex("amount", cursor)));
		ib.setValue(cursor.getString(getIndex("value",cursor)));
		ib.setNote(cursor.getString(getIndex("note", cursor)));
		ib.setIngredId(cursor.getInt(getIndex("ingredientId", cursor)));	
		return ib;
	}
	
	/**
	 * Gets preperation information from recipe within date frame for syncing
	 * @param id
	 * @return ArrayList<preperationBean>
	 */
	public ArrayList<preperationBean> getPrep(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
	    ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
	    Cursor cursor = database.rawQuery("SELECT Preperationid FROM PrepRecipe WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime) AND recipeId = ?", new String[] {  sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT") , Integer.toString(id) });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int prepid = cursor.getInt(getIndex("Preperationid", cursor));
                Cursor cursor2 = database.rawQuery("SELECT * FROM Preperation WHERE datetime(changeTime) > datetime(?) AND datetime(?) > datetime(changeTime) AND id = ?", new String[] {  sharedpreferences.getString("Change Server", "DEFAULT"), sharedpreferences.getString("Change", "DEFAULT") , Integer.toString(prepid) });
                if (cursor2 != null && cursor2.getCount() > 0) {
                    for (int x = 0; x < cursor2.getCount(); x++) {
                        cursor2.moveToPosition(x);
                        prepList.add(cursorToPreperation(cursor2));
                    }
                }
                cursor2.close();
            }
           
        }
        cursor.close();
       close();
       
        return prepList;
	}
	
	/**
	 * Set preperation bean data based on database
	 * @param cursor
	 * @return preperationBean
	 */
	public preperationBean cursorToPreperation(Cursor cursor)
	{
		preperationBean pb = new preperationBean();
		pb.setPreperation(cursor.getString(getIndex("instruction", cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		return pb;
	}
	
	/**
	 * Builds a json with all the recipe data to send to the server
	 * @throws JSONException
	 */
	public void getAndCreateJSON() throws JSONException
	{
		ArrayList<recipeBean> recipeList = getRecipe();
		JSONArray jsonArray = new JSONArray();
	
		for(int i = 0; i < recipeList.size(); i++)
		{
			JSONObject recipe = new JSONObject();		
			recipe.put("name", recipeList.get(i).getName());
			recipe.put("description", recipeList.get(i).getDesc());
			recipe.put("prepTime", recipeList.get(i).getPrep());
			recipe.put("cookingTime", recipeList.get(i).getCooking());
			recipe.put("serves", recipeList.get(i).getServes());
			recipe.put("addedBy", recipeList.get(i).getAddedBy());
			recipe.put("updateTime", recipeList.get(i).getUpdateTime());
			recipe.put("changeTime", recipeList.get(i).getChangeTime());
			ArrayList<preperationBean> prepList = getPrep(recipeList.get(i).getId());
			ArrayList<String> prepSteps = new ArrayList<String>();
			ArrayList<String> prepNums = new ArrayList<String>();
			JSONObject prepObj = new JSONObject();
			JSONObject prepNumObj = new JSONObject();
			for(int x = 0; x < prepList.size(); x++)
			{
				prepSteps.add(prepList.get(x).getPreperation().toString());
				prepNums.add(Integer.toString(prepList.get(x).getPrepNum()));
		    }
			ArrayList<ingredientBean> ingredList = getIngred(recipeList.get(i).getId());
			JSONArray prepStepArray = new JSONArray(prepSteps);
			JSONArray prepNumArray = new JSONArray(prepNums);
			prepObj.put("prep", prepStepArray);
			JSONObject p2 = new JSONObject();
			prepNumObj.put("prepNums", prepNumArray);	
			recipe.put("Preperation", prepObj );
			recipe.accumulate("Preperation", prepNumObj );
			
			JSONObject ingredObj = new JSONObject();
			JSONObject ingredValObj = new JSONObject();
			JSONObject ingredNoteObj = new JSONObject();
			JSONObject ingredAmountObj = new JSONObject();
			ArrayList<String> ingredsList = new ArrayList<String>();
			ArrayList<String> ingredAmount = new ArrayList<String>();
			ArrayList<String> ingredNote = new ArrayList<String>();
			ArrayList<String> ingredValue = new ArrayList<String>();
			for(int y = 0; y < ingredList.size(); y++)
			{
				ingredAmount.add(Integer.toString(ingredList.get(y).getAmount()));
				ingredValue.add(ingredList.get(y).getValue());
				ingredNote.add(ingredList.get(y).getNote());
				String name = getIngredName(ingredList.get(y).getIngredId());
				ingredsList.add(name);
				
			}
			JSONArray ingredarray = new JSONArray(ingredsList);
			ingredObj.put("Ingredients", ingredarray);
			JSONArray valuearray = new JSONArray(ingredValue);
			ingredValObj.put("Value", valuearray);
			JSONArray amountarray = new JSONArray(ingredAmount);
			ingredAmountObj.put("Amount", amountarray);
			JSONArray notearray = new JSONArray(ingredNote);
			ingredNoteObj.put("Notes", notearray);	
			recipe.put("Ingredient", ingredObj);
			recipe.accumulate("Ingredient", ingredValObj);
			recipe.accumulate("Ingredient", ingredAmountObj);
			recipe.accumulate("Ingredient", ingredNoteObj);
			
			
			
			jsonArray.put(recipe);			
		} 
	//sendJSONToServer(jsonArray);
		Log.v("UPDATE JSON ", "UPDATE JSON " + jsonArray);
	}
}
