package com.example.recipesforlife.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

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

import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

public class syncRecipeModel extends baseDataSource {
	Context context;
	
	
	public syncRecipeModel(Context context) {
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
	    Cursor cursor = database.rawQuery("SELECT * FROM Recipe WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime)", new String[] { sharedpreferences.getString("Date Server", "DEFAULT"), sharedpreferences.getString("Date", "DEFAULT")   });
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
        rb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
       
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
	    Cursor cursor = database.rawQuery("SELECT ingredientDetailsId From RecipeIngredient WHERE  Recipeid = ? ", new String[] {   Integer.toString(id) });
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
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
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
	public ingredientBean cursorToIngredientDetails(Cursor cursor)
	{
		ingredientBean ib = new ingredientBean();
		ib.setAmount(cursor.getInt(getIndex("amount", cursor)));
		ib.setValue(cursor.getString(getIndex("value",cursor)));
		ib.setNote(cursor.getString(getIndex("note", cursor)));
		ib.setIngredId(cursor.getInt(getIndex("ingredientId", cursor)));	
		ib.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
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
	    Cursor cursor = database.rawQuery("SELECT Preperationid FROM PrepRecipe WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime) AND recipeId = ?", new String[] {  sharedpreferences.getString("Date Server", "DEFAULT"), sharedpreferences.getString("Date", "DEFAULT") , Integer.toString(id) });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int prepid = cursor.getInt(getIndex("Preperationid", cursor));
                Cursor cursor2 = database.rawQuery("SELECT * FROM Preperation WHERE datetime(updateTime) > datetime(?) AND datetime(?) > datetime(updateTime) AND id = ?", new String[] {  sharedpreferences.getString("Date Server", "DEFAULT"), sharedpreferences.getString("Date", "DEFAULT") , Integer.toString(prepid) });
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
		pb.setUniqueid(cursor.getString(getIndex("uniqueid",cursor)));
		return pb;
	}
	
	/**
	 * Builds a json with all the recipe data to send to the server
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateJSON() throws JSONException, IOException
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
			recipe.put("uniqueid", recipeList.get(i).getUniqueid());
			cookbookModel model = new cookbookModel(context);
			String id = model.selectCookbooksUniqueID(recipeList.get(i).getId());
			recipe.put("cookbookid", id);
			
			ArrayList<preperationBean> prepList = getPrep(recipeList.get(i).getId());
			JSONArray prepStepArray = new JSONArray();
			JSONArray prepNumArray = new JSONArray();
			JSONArray prepIdArray = new JSONArray();
			for(int x = 0; x < prepList.size(); x++)
			{
				prepStepArray.put(prepList.get(x).getPreperation().toString());
				Log.v("x ", "QWERTY " + prepList.get(x).getPreperation().toString());
				prepNumArray.put(Integer.toString(prepList.get(x).getPrepNum()));
				prepIdArray.put(prepList.get(x).getUniqueid().toString());
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
			
			ArrayList<ingredientBean> ingredList = getIngred(recipeList.get(i).getId());
			
			JSONArray ingredarray = new JSONArray();
			JSONArray valuearray = new JSONArray();
			JSONArray amountarray = new JSONArray();
			JSONArray notearray = new JSONArray();
			JSONArray ingredidarray = new JSONArray();
			for(int y = 0; y < ingredList.size(); y++)
			{
				amountarray.put(Integer.toString(ingredList.get(y).getAmount()));
				valuearray.put(ingredList.get(y).getValue());
				notearray.put(ingredList.get(y).getNote());
				ingredidarray.put(ingredList.get(y).getUniqueid());
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
			jsonArray.put(recipe);			
		} 
		Log.v("WHAT ", "WHAT " + jsonArray);
	sendJSONToServer(jsonArray);
	}
	
	/**
	 * Gets the json with it's sync info from the server
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		date.put("updateTime", sharedpreferences.getString("Date", "DEFAULT"));
		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
        HttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm4.aspx");      	   	
		try 
		{
			//HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 2000);
			//HttpConnectionParams.setSoTimeout(myClient.getParams(), 3000);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				
			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
			JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Recipe");
			
			for(int i = 0; i < jArray.length(); i++)
			{
				
				
				json = jArray.getJSONObject(i);
                recipeBean recipe = new recipeBean();
                recipe.setName( json.getString("name"));
                recipe.setDesc(json.getString("description"));
                recipe.setServes(json.getString("serves"));
                recipe.setCooking(json.getString("cookingTime"));
                recipe.setPrep(json.getString("prepTime"));
                recipe.setAddedBy(json.getString("addedBy"));
                recipe.setUniqueid(json.getString("uniqueid"));
                
                cookbookModel cbmodel = new cookbookModel(context);
                String name = cbmodel.selectCookbooksNameByID(json.getString("cookingid"));
                recipe.setRecipeBook(name);
                
                ArrayList<ingredientBean> ingredBeanList = new ArrayList<ingredientBean>();
                JSONArray ingredArray = (JSONArray) json.get("Ingredient");
                for(int a = 0; a < ingredArray.length(); a++)
                {
                	JSONObject ingredObject = ingredArray.getJSONObject(a);
                	JSONArray ingredsArray = (JSONArray) ingredObject.get("Ingredients");
                	JSONArray notesArray = (JSONArray) ingredObject.get("Notes");
                	JSONArray amountArray = (JSONArray) ingredObject.get("Amount");
                	JSONArray valueArray = (JSONArray) ingredObject.get("Value");
                	JSONArray ingredIdArray = (JSONArray) ingredObject.get("uniqueid");
                	for(int b = 0; b < ingredsArray.length(); b++)
                	{
                		ingredientBean ingredBean = new ingredientBean();
                		ingredBean.setName(ingredsArray.get(b).toString());
                		ingredBean.setNote(notesArray.get(b).toString());
                		ingredBean.setAmount(Integer.parseInt(amountArray.get(b).toString()));
                		ingredBean.setValue(valueArray.get(b).toString());
                		ingredBean.setUniqueid(ingredIdArray.get(b).toString());
                		ingredBeanList.add(ingredBean);
                	}
                }
               
                JSONArray preperationArray = (JSONArray) json.get("Preperation");
                ArrayList<preperationBean> prepBeanList = new ArrayList<preperationBean>();
                for(int c = 0; c < preperationArray.length(); c++)
                {
                	 JSONObject prepObject =  preperationArray.getJSONObject(c);
                	JSONArray prepArray = (JSONArray) prepObject.get("prep");
                	JSONArray numArray = (JSONArray) prepObject.get("prepNums");
                	JSONArray idArray = (JSONArray) prepObject.get("uniqueid");
                	for(int d = 0; d < prepArray.length(); d++)
                	{
                		preperationBean prepBean = new preperationBean();
                		prepBean.setPreperation(prepArray.get(d).toString());
                		prepBean.setPrepNum(Integer.parseInt(numArray.get(d).toString()));
                		prepBean.setUniqueid(idArray.get(d).toString());
                		prepBeanList.add(prepBean);
                	}
                }
                recipeModel model = new recipeModel(context);
                model.insertRecipe(recipe, true, ingredBeanList, prepBeanList);
                
			}
	
                    
                    
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			 Toast.makeText(context, 
		        	    "Connection to server failed", Toast.LENGTH_LONG).show();
			 throw e;
		}
	}
	
	/**
	 * Sends json to the server
	 * @param jsonArray
	 * @throws IOException 
	 */
	public void sendJSONToServer(JSONArray jsonArray) throws IOException
	{
		String str = "";
		HttpResponse response = null;
        HttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm3.aspx");      	   	
		try 
		{
		//	HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 2000);
		//	 HttpConnectionParams.setSoTimeout(myClient.getParams(), 3000);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			 
			
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			 Toast.makeText(context, 
		        	    "Connection to server failed", Toast.LENGTH_LONG).show();
			 throw e;
		}
	
	}
	

}
