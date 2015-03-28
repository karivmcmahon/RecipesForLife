package com.example.recipesforlife.models;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Base64;
import android.util.Log;

import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

public class SyncRecipeDetails extends BaseDataSource {
	Context context;
	RecipeModel rm;
	Utility util;
	String recipeingredid = "";
	String recipeprepid = "";


	public SyncRecipeDetails(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
		rm = new RecipeModel(context);
		// TODO Auto-generated constructor stub
	}

	

	/**
	 * Get ingredients for recipes based on recipe id 
	 * @param id
	 * @return ArrayList<IngredientBean>
	 */
	public ArrayList<IngredientBean> getIngred()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<IngredientBean> ingredientList = new ArrayList<IngredientBean>();
		Cursor cursor = database.rawQuery("SELECT ingredientDetailsId, Recipeid From RecipeIngredient WHERE   updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?) ", new String[] {    sharedpreferences.getString("Date", "DEFAULT") });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int detsId = cursor.getInt(getIndex("ingredientDetailsId", cursor));
				int id = cursor.getInt(getIndex("Recipeid", cursor));
				Log.v("REC ID ", "REC ID " + id);
				recipeingredid =  rm.selectRecipeByID(id);
				Cursor cursor2 = database.rawQuery("SELECT * FROM IngredientDetails WHERE  id = ?", new String[] {  Integer.toString(detsId) });
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
		open();
		String name = null;
		Cursor cursor = database.rawQuery("SELECT name From Ingredient WHERE  id = ? ", new String[] {   Integer.toString(id) });
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
	public IngredientBean cursorToIngredientDetails(Cursor cursor)
	{
		IngredientBean ib = new IngredientBean();
		ib.setAmount(cursor.getInt(getIndex("amount", cursor)));
		ib.setValue(cursor.getString(getIndex("value",cursor)));
		ib.setNote(cursor.getString(getIndex("note", cursor)));
		ib.setIngredId(cursor.getInt(getIndex("ingredientId", cursor)));	
		ib.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		ib.setProgress(cursor.getString(getIndex("progress", cursor)));
		return ib;
	}

	/**
	 * Gets preperation information based on recipe id
	 * @param id
	 * @return ArrayList<preperationBean>
	 */
	public ArrayList<PreperationBean> getPrep()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		Cursor cursor = database.rawQuery("SELECT Preperationid, recipeId FROM PrepRecipe WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?) ", new String[] {    sharedpreferences.getString("Date", "DEFAULT") });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int prepid = cursor.getInt(getIndex("Preperationid", cursor));
				int id = cursor.getInt(getIndex("recipeId", cursor));
				recipeprepid = rm.selectRecipeByID(id);
				Cursor cursor2 = database.rawQuery("SELECT * FROM Preperation WHERE  id = ?", new String[] {  Integer.toString(prepid) });
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
	public PreperationBean cursorToPreperation(Cursor cursor)
	{
		PreperationBean pb = new PreperationBean();
		pb.setPreperation(cursor.getString(getIndex("instruction", cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		pb.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		pb.setProgress(cursor.getString(getIndex("progress", cursor)));

		return pb;
	}

	/**
	 * Builds a json with all the recipe data to send to the server
	 * @param update - whether its for update or insert
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateJSON(boolean update) throws JSONException, IOException
	{
		
		JSONArray jsonArray = new JSONArray();
		
			JSONObject recipe = new JSONObject();		
			recipe.put("updateTime", util.getLastUpdated(false));
			recipe.put("changeTime", "2015-01-01 12:00:00");
			

			ArrayList<PreperationBean> prepList = getPrep();
			JSONArray prepStepArray = new JSONArray();
			JSONArray prepNumArray = new JSONArray();
			JSONArray prepIdArray = new JSONArray();
			JSONArray prepProgressArray = new JSONArray();
			JSONArray prepRecipeArray = new JSONArray();
			for(int x = 0; x < prepList.size(); x++)
			{
				prepStepArray.put(prepList.get(x).getPreperation().toString());
				prepNumArray.put(Integer.toString(prepList.get(x).getPrepNum()));
				prepIdArray.put(prepList.get(x).getUniqueid().toString());
				prepProgressArray.put(prepList.get(x).getProgress().toString());
				prepRecipeArray.put(recipeprepid);
			}

			JSONObject newObj = new JSONObject();			
			newObj.put("prep", prepStepArray);
			recipe.put("Preperation", newObj);
			newObj = new JSONObject();	
			newObj.put("prepNums", prepNumArray);
			recipe.accumulate("Preperation", newObj );
			newObj = new JSONObject();
			newObj.put("uniqueid", prepIdArray);
			recipe.accumulate("Preperation", newObj );
			newObj = new JSONObject();
			newObj.put("prepprogress", prepProgressArray);
			recipe.accumulate("Preperation", newObj );
			newObj = new JSONObject();
			newObj.put("preprecipeid", prepRecipeArray);
			recipe.accumulate("Preperation", newObj );
		
			ArrayList<IngredientBean> ingredList = getIngred();

			JSONArray ingredarray = new JSONArray();
			JSONArray valuearray = new JSONArray();
			JSONArray amountarray = new JSONArray();
			JSONArray notearray = new JSONArray();
			JSONArray ingredidarray = new JSONArray();
			JSONArray ingredprogressarray = new JSONArray();
			JSONArray ingredrecipearray = new JSONArray();
			for(int y = 0; y < ingredList.size(); y++)
			{
				amountarray.put(Integer.toString(ingredList.get(y).getAmount()));
				valuearray.put(ingredList.get(y).getValue());
				notearray.put(ingredList.get(y).getNote());
				ingredidarray.put(ingredList.get(y).getUniqueid());
				ingredprogressarray.put(ingredList.get(y).getProgress());
				ingredrecipearray.put(recipeingredid);
				String name = getIngredName(ingredList.get(y).getIngredId());
				ingredarray.put(name);
			}

			newObj.put("Ingredients", ingredarray);
			recipe.put("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("Value", valuearray);	
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("Amount", amountarray);
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("Notes", notearray);	
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("uniqueid", ingredidarray);	
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("ingredprogress", ingredprogressarray);		
			recipe.accumulate("Ingredient", newObj);	
			newObj.put("ingredrecipeid", ingredrecipearray);		
			recipe.accumulate("Ingredient", newObj);	
			jsonArray.put(recipe);			
		
	
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm16.aspx" );
		
	}

	public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str = "";
		str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm17.aspx", sharedpreferences.getString("Date", "DEFAULT"), true);
		
		JSONObject jObject = new JSONObject(str);
		JSONArray jArray = (JSONArray) jObject.get("Recipe");
		for(int i = 0; i < jArray.length(); i++)
		{
			json = jArray.getJSONObject(i);

			ArrayList<IngredientBean> ingredBeanList = new ArrayList<IngredientBean>();
			JSONArray ingredArray = (JSONArray) json.get("Ingredient");
			for(int a = 0; a < ingredArray.length(); a++)
			{
				JSONObject ingredObject = ingredArray.getJSONObject(a);
				JSONArray ingredsArray = (JSONArray) ingredObject.get("Ingredients");
				JSONArray notesArray = (JSONArray) ingredObject.get("Notes");
				JSONArray amountArray = (JSONArray) ingredObject.get("Amount");
				JSONArray valueArray = (JSONArray) ingredObject.get("Value");
				JSONArray ingredIdArray = (JSONArray) ingredObject.get("uniqueid");
				JSONArray ingredProgressArray = (JSONArray) ingredObject.get("ingredprogress");
				for(int b = 0; b < ingredsArray.length(); b++)
				{
					IngredientBean ingredBean = new IngredientBean();
					ingredBean.setName(ingredsArray.get(b).toString());
					ingredBean.setNote(notesArray.get(b).toString());
					ingredBean.setAmount(Integer.parseInt(amountArray.get(b).toString()));
					ingredBean.setValue(valueArray.get(b).toString());
					ingredBean.setUniqueid(ingredIdArray.get(b).toString());
					ingredBean.setProgress(ingredProgressArray.get(b).toString());
					ingredBeanList.add(ingredBean);
				}
			}

			JSONArray preperationArray = (JSONArray) json.get("Preperation");
			ArrayList<PreperationBean> prepBeanList = new ArrayList<PreperationBean>();
			for(int c = 0; c < preperationArray.length(); c++)
			{
				JSONObject prepObject =  preperationArray.getJSONObject(c);
				JSONArray prepArray = (JSONArray) prepObject.get("prep");
				JSONArray numArray = (JSONArray) prepObject.get("prepNums");
				JSONArray idArray = (JSONArray) prepObject.get("uniqueid");
				JSONArray progressArray = (JSONArray) prepObject.get("prepprogress");
				for(int d = 0; d < prepArray.length(); d++)
				{
					PreperationBean prepBean = new PreperationBean();
					prepBean.setPreperation(prepArray.get(d).toString());
					prepBean.setPrepNum(Integer.parseInt(numArray.get(d).toString()));
					prepBean.setUniqueid(idArray.get(d).toString());
					prepBean.setProgress(progressArray.getString(d).toString());
					prepBeanList.add(prepBean);
				}
			}
			RecipeModel model = new RecipeModel(context);
		
			try
			{
				model.insertRecipe(recipe, true, ingredBeanList, prepBeanList );
			}
			catch(SQLException e)
			{
				throw e;
			}
			

		}




	}





}




