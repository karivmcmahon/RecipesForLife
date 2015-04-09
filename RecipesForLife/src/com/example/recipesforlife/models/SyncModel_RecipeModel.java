package com.example.recipesforlife.models;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Gets and create recipe JSON to send to and from server
 * @author Kari
 *
 */
public class SyncModel_RecipeModel extends Database_BaseDataSource {
	Context context;
	ApplicationModel_RecipeModel rm;
	Utility util;


	public SyncModel_RecipeModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Gets recipe to send to server based on date they were added using shared preferences
	 * @param update - whether its for update or insert
	 * @return ArrayList<recipeBean> recipeList
	 */
	public ArrayList<RecipeBean> getRecipe(boolean update)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<RecipeBean> recipeList = new ArrayList<RecipeBean>();
		Cursor cursor = null;
		if(update == true)
		{
			cursor = database.rawQuery("SELECT * FROM Recipe WHERE changeTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT")  });
		}
		else
		{
			cursor = database.rawQuery("SELECT * FROM Recipe WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT")  });
		}
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
	public RecipeBean cursorToRecipe(Cursor cursor) {
		RecipeBean rb = new RecipeBean();
		rb.setId(cursor.getInt(getIndex("id",cursor)));       
		rb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		rb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
		rb.setName(cursor.getString(getIndex("name", cursor)));
		rb.setDesc(cursor.getString(getIndex("description",cursor)));
		rb.setPrep(cursor.getString(getIndex("prepTime", cursor)));
		rb.setCooking(cursor.getString(getIndex("cookingTime", cursor)));
		rb.setServes(cursor.getString(getIndex("serves", cursor)));
		rb.setAddedBy(cursor.getString(getIndex("addedBy", cursor)));
		rb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		rb.setProgress(cursor.getString(getIndex("progress", cursor)));
		rb.setDietary(cursor.getString(getIndex("dietary",cursor)));
		rb.setDifficulty(cursor.getString(getIndex("difficulty",cursor)));
		rb.setCusine(cursor.getString(getIndex("cusine", cursor)));
		rb.setTips(cursor.getString(getIndex("tips", cursor)));
		return rb;
	}

	/**
	 * Get ingredients for recipes based on recipe id 
	 * @param id
	 * @return ArrayList<IngredientBean>
	 */
	public ArrayList<IngredientBean> getIngred(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<IngredientBean> ingredientList = new ArrayList<IngredientBean>();
		Cursor cursor = database.rawQuery("SELECT ingredientDetailsId From RecipeIngredient WHERE  Recipeid = ?   ", new String[] {   Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int detsId = cursor.getInt(getIndex("ingredientDetailsId", cursor));
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
	public ArrayList<PreperationBean> getPrep(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		Cursor cursor = database.rawQuery("SELECT Preperationid FROM PrepRecipe WHERE recipeId = ?", new String[] {   Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				int prepid = cursor.getInt(getIndex("Preperationid", cursor));
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
		ArrayList<RecipeBean> recipeList = getRecipe(update);
		JSONArray jsonArray = new JSONArray();
		rm = new ApplicationModel_RecipeModel(context);
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
			recipe.put("uniqueid", recipeList.get(i).getUniqueid());
			ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(context);
			String id = model.selectCookbooksUniqueID(recipeList.get(i).getId());
			recipe.put("cookbookid", id);
			ImageBean image = rm.selectImages(recipeList.get(i).getId());
			String stringToStore = new String(Base64.encode(image.getImage(), Base64.DEFAULT));
			recipe.put("image", stringToStore);
			recipe.put("imageid", image.getUniqueid());
			recipe.put("progress", recipeList.get(i).getProgress());
			recipe.put("difficulty", recipeList.get(i).getDifficulty());
			recipe.put("dietary", recipeList.get(i).getDietary());
			recipe.put("tips", recipeList.get(i).getTips());
			recipe.put("cusine", recipeList.get(i).getCusine());

			ArrayList<PreperationBean> prepList = getPrep(recipeList.get(i).getId());
			JSONArray prepStepArray = new JSONArray();
			JSONArray prepNumArray = new JSONArray();
			JSONArray prepIdArray = new JSONArray();
			JSONArray prepProgressArray = new JSONArray();
			for(int x = 0; x < prepList.size(); x++)
			{
				prepStepArray.put(prepList.get(x).getPreperation().toString());
				prepNumArray.put(Integer.toString(prepList.get(x).getPrepNum()));
				prepIdArray.put(prepList.get(x).getUniqueid().toString());
				prepProgressArray.put(prepList.get(x).getProgress().toString());
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

			ArrayList<IngredientBean> ingredList = getIngred(recipeList.get(i).getId());

			JSONArray ingredarray = new JSONArray();
			JSONArray valuearray = new JSONArray();
			JSONArray amountarray = new JSONArray();
			JSONArray notearray = new JSONArray();
			JSONArray ingredidarray = new JSONArray();
			JSONArray ingredprogressarray = new JSONArray();
			for(int y = 0; y < ingredList.size(); y++)
			{
				amountarray.put(Integer.toString(ingredList.get(y).getAmount()));
				valuearray.put(ingredList.get(y).getValue());
				notearray.put(ingredList.get(y).getNote());
				ingredidarray.put(ingredList.get(y).getUniqueid());
				ingredprogressarray.put(ingredList.get(y).getProgress());
				String name = getIngredName(ingredList.get(y).getIngredId());
				ingredarray.put(name);
			}
			newObj = new JSONObject();
			
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
			Log.v("UPDATE INGRED ", "UPDATE INGRED " + ingredidarray);
			recipe.accumulate("Ingredient", newObj);
			newObj = new JSONObject();
			newObj.put("ingredprogress", ingredprogressarray);		
			recipe.accumulate("Ingredient", newObj);			
			jsonArray.put(recipe);			
		} 
		if(update == true)
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm5.aspx" );
		}
		else
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm3.aspx" );
		}
	}

	/**
	 * Gets the json with it's sync info from the server
	 * @param - update - whether its for update or insert
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str = "";
		if(update == true)
		{

			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm6.aspx", sharedpreferences.getString("Date", "DEFAULT"), true);
		}
		else
		{
			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm4.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);
		}

		JSONObject jObject = new JSONObject(str);
		JSONArray jArray = (JSONArray) jObject.get("Recipe");

		for(int i = 0; i < jArray.length(); i++)
		{


			json = jArray.getJSONObject(i);
			RecipeBean recipe = new RecipeBean();
			recipe.setName( json.getString("name"));
			recipe.setDesc(json.getString("description"));
			recipe.setServes(json.getString("serves"));
			recipe.setCooking(json.getString("cookingTime"));
			recipe.setPrep(json.getString("prepTime"));
			recipe.setAddedBy(json.getString("addedBy"));
			recipe.setUniqueid(json.getString("uniqueid"));
			recipe.setProgress(json.getString("progress"));
			recipe.setDifficulty(json.getString("difficulty"));
			recipe.setDietary(json.getString("dietary"));
			recipe.setTips(json.getString("tips"));
			recipe.setCusine(json.getString("cusine"));
			ImageBean imgbean = new ImageBean();
			if(json.optString("image").equals(""))
			{
				byte[] emptyarr = new byte[0];
				imgbean.setImage(emptyarr);
			}
			else
			{
				imgbean.setImage(Base64.decode(json.optString("image"), Base64.DEFAULT));
			}
			imgbean.setUniqueid(json.optString("imageid"));

			
			String cookingid = "";
			cookingid = json.optString("cookingid");
			
			recipe.setRecipeBook(cookingid);

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
					Log.v("NAME " , "NAME " + ingredsArray.get(b).toString());
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
			ApplicationModel_RecipeModel model = new ApplicationModel_RecipeModel(context);

			if(update == true)
			{
				try
				{
					model.updateRecipe(recipe, prepBeanList, ingredBeanList, imgbean,true);
				}
				catch(SQLException e)
				{
					throw e;
				}
			}
			else
			{
				try
				{
					model.insertRecipe(recipe, true, ingredBeanList, prepBeanList, imgbean);
				}
				catch(SQLException e)
				{
					throw e;
				}
			}

		}




	}



}
