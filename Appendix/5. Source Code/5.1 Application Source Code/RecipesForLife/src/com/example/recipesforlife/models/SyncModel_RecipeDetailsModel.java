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
import android.util.Log;

import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Class syncs additional recipe details between server and application.
 * This is additional ingreds or prep details that were inserted after the recipe was inserted
 * @author Kari
 *
 */
public class SyncModel_RecipeDetailsModel extends Database_BaseDataSource {
	private Context context;
	private ApplicationModel_RecipeModel rm;
	private Utility util;
	private String recipeingredid = "";
	private String recipeprepid = "";


	public SyncModel_RecipeDetailsModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
		rm = new ApplicationModel_RecipeModel(context);
		
	}

	

	/**
	 * Sets the ingredient data from the database to ingredientbean
	 * 
	 * @param cursor			Query results
	 * @return IngreientBean	Stores query results
	 */
	private IngredientBean cursorToIngredientDetails(Cursor cursor)
	{
		IngredientBean ib = new IngredientBean();
		ib.setAmount(cursor.getInt(getIndex("amount", cursor)));
		ib.setValue(cursor.getString(getIndex("value",cursor)));
		ib.setNote(cursor.getString(getIndex("note", cursor)));
		ib.setIngredId(cursor.getInt(getIndex("ingredientId", cursor)));	
		ib.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		ib.setProgress(cursor.getString(getIndex("progress", cursor)));
		ib.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		return ib;
	}

	/**
	 * Set preperation bean data based on database
	 * 
	 * @param cursor				Query results
	 * @return preperationBean		Store query results
	 */
	private PreperationBean cursorToPreperation(Cursor cursor)
	{
		PreperationBean pb = new PreperationBean();
		pb.setPreperation(cursor.getString(getIndex("instruction", cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		pb.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		pb.setProgress(cursor.getString(getIndex("progress", cursor)));
		pb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		return pb;
	}

	/**
	 * Builds a json with all the additional recipe details to send to the server
	 * 
	 * @param update 			Whether its for update or insert
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateJSON(boolean update) throws JSONException, IOException
	{
			JSONArray jsonArray = new JSONArray();
		
			JSONObject recipe = new JSONObject();		
			recipe.put("changeTime", "2015-01-01 12:00:00");
			

			ArrayList<PreperationBean> prepList = getPrep();
			JSONArray prepStepArray = new JSONArray();
			JSONArray prepNumArray = new JSONArray();
			JSONArray prepIdArray = new JSONArray();
			JSONArray prepProgressArray = new JSONArray();
			JSONArray prepRecipeArray = new JSONArray();
			JSONArray prepUpdateArray = new JSONArray();
			for(int x = 0; x < prepList.size(); x++)
			{
				prepStepArray.put(prepList.get(x).getPreperation().toString());
				prepNumArray.put(Integer.toString(prepList.get(x).getPrepNum()));
				prepIdArray.put(prepList.get(x).getUniqueid().toString());
				prepProgressArray.put(prepList.get(x).getProgress().toString());
				prepRecipeArray.put(recipeprepid);
				prepUpdateArray.put(prepList.get(x).getUpdateTime().toString());
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
			newObj = new JSONObject();
			newObj.put("updateTime", prepUpdateArray);
			recipe.accumulate("Preperation", newObj );
		
			ArrayList<IngredientBean> ingredList = getIngred();

			JSONArray ingredarray = new JSONArray();
			JSONArray valuearray = new JSONArray();
			JSONArray amountarray = new JSONArray();
			JSONArray notearray = new JSONArray();
			JSONArray ingredidarray = new JSONArray();
			JSONArray ingredprogressarray = new JSONArray();
			JSONArray ingredrecipearray = new JSONArray();
			JSONArray ingredupdatearray = new JSONArray();
			for(int y = 0; y < ingredList.size(); y++)
			{
				amountarray.put(Integer.toString(ingredList.get(y).getAmount()));
				valuearray.put(ingredList.get(y).getValue());
				notearray.put(ingredList.get(y).getNote());
				ingredidarray.put(ingredList.get(y).getUniqueid());
				ingredprogressarray.put(ingredList.get(y).getProgress());
				ingredrecipearray.put(recipeingredid);
				ingredupdatearray.put(ingredList.get(y).getUpdateTime().toString());
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
			newObj = new JSONObject();
			newObj.put("ingredrecipeid", ingredrecipearray);		
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("updateTime", ingredupdatearray);		
			recipe.accumulate("Ingredient", newObj);	
			jsonArray.put(recipe);			
		
	
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm16.aspx" );
		
	}

	/**
	 * Get ingredients for recipes
	 * 
	 * @return ArrayList<IngredientBean>	List of ingredients
	 */
	public ArrayList<IngredientBean> getIngred()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<IngredientBean> ingredientList = new ArrayList<IngredientBean>();
		Cursor cursor = database.rawQuery("SELECT ingredientDetailsId, Recipeid From RecipeIngredient WHERE   updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?) ", new String[] {    sharedpreferences.getString("Date", "DEFAULT") });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
			
				int detsId = cursor.getInt(getIndex("ingredientDetailsId", cursor));
				
				int id = cursor.getInt(getIndex("Recipeid", cursor));
			
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
	 * 
	 * @param id		ingredient id
	 * @return name		ingredient name
	 */	
	private String getIngredName(int id)
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
	 * Gets JSON from server and inserts the details
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str = "";
		str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm17.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);
		
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
				JSONArray ingredrecipeArray = (JSONArray) ingredObject.get("ingredrecipe");
				for(int b = 0; b < ingredsArray.length(); b++)
				{
					IngredientBean ingredBean = new IngredientBean();
					ingredBean.setName(ingredsArray.get(b).toString());
					ingredBean.setNote(notesArray.get(b).toString());
					ingredBean.setAmount(Integer.parseInt(amountArray.get(b).toString()));
					ingredBean.setValue(valueArray.get(b).toString());
					ingredBean.setUniqueid(ingredIdArray.get(b).toString());
					ingredBean.setProgress(ingredProgressArray.get(b).toString());
					ingredBean.setRecipeid(ingredrecipeArray.get(b).toString());
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
				JSONArray preprecipeArray = (JSONArray) prepObject.get("preprecipe");
				for(int d = 0; d < prepArray.length(); d++)
				{
					PreperationBean prepBean = new PreperationBean();
					prepBean.setPreperation(prepArray.get(d).toString());
					prepBean.setPrepNum(Integer.parseInt(numArray.get(d).toString()));
					prepBean.setUniqueid(idArray.get(d).toString());
					prepBean.setProgress(progressArray.getString(d).toString());
					prepBean.setRecipeid(preprecipeArray.getString(d).toString());
					prepBeanList.add(prepBean);
				}
			}
			
		
			try
			{
				rm.insertIngredExtras(true, ingredBeanList);
				rm.insertPrepExtras(true, prepBeanList);
			}
			catch(SQLException e)
			{
				throw e;
			}
			

		}




	}

	/**
	 * Gets preperation information 
	 * 
	 * @return ArrayList<preperationBean>		List of prep details
	 */
	public ArrayList<PreperationBean> getPrep()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
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





}




