package com.example.recipesforlife.models;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.controllers.userBean;
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
	
	public ArrayList<ingredientBean> getIngred(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
	    ArrayList<ingredientBean> ingredientList = new ArrayList<ingredientBean>();
	    Cursor cursor = database.rawQuery("SELECT ingredientDetailsId From RecipeIngredient WHERE  datetime(updateTime) > datetime(?) AND Recipeid = ? ", new String[] { sharedpreferences.getString("Date", "DEFAULT"), Integer.toString(id) });
	    if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int detsId = cursor.getInt(getIndex("ingredientDetailsId", cursor));
                Cursor cursor2 = database.rawQuery("SELECT * FROM IngredientDetails WHERE datetime(updateTime) > datetime(?) AND id = ?", new String[] { sharedpreferences.getString("Date", "DEFAULT") , Integer.toString(detsId) });
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
	
	public String getIngredName(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
		String name = null;
	    Cursor cursor = database.rawQuery("SELECT name From Ingredient WHERE  datetime(updateTime) > datetime(?) AND id = ? ", new String[] { sharedpreferences.getString("Date", "DEFAULT"), Integer.toString(id) });
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
	
	public ingredientBean cursorToIngredientDetails(Cursor cursor)
	{
		ingredientBean ib = new ingredientBean();
		ib.setAmount(cursor.getInt(getIndex("amount", cursor)));
		ib.setValue(cursor.getString(getIndex("value",cursor)));
		ib.setNote(cursor.getString(getIndex("note", cursor)));
		ib.setIngredId(cursor.getInt(getIndex("ingredientId", cursor)));	
		return ib;
	}
	
	public ArrayList<preperationBean> getPrep(int id)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		getCurrentDate();
		open();
	    ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
	    Cursor cursor = database.rawQuery("SELECT Preperationid FROM PrepRecipe WHERE datetime(updateTime) > datetime(?) AND recipeId = ?", new String[] { sharedpreferences.getString("Date", "DEFAULT") , Integer.toString(id) });
        if (cursor != null && cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int prepid = cursor.getInt(getIndex("Preperationid", cursor));
                Cursor cursor2 = database.rawQuery("SELECT * FROM Preperation WHERE datetime(updateTime) > datetime(?) AND id = ?", new String[] { sharedpreferences.getString("Date", "DEFAULT") , Integer.toString(prepid) });
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
      //  cursor2.close();
       close();
        Log.v("PREP ", "PREP " + prepList.get(0).getPreperation());
    /**    try {
			getAndCreateJSON();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} **/
        return prepList;
	}
	
	public preperationBean cursorToPreperation(Cursor cursor)
	{
		preperationBean pb = new preperationBean();
		//pb.setId(cursor.getInt(getIndex("Preperation.id",cursor)));
		pb.setPreperation(cursor.getString(getIndex("instruction", cursor)));
		pb.setPrepNum(cursor.getInt(getIndex("instructionNum", cursor)));
		//pb.setPrepId(cursor.getInt(getIndex("PrepRecipe.Preperationid", cursor)));
		//pb.setRecipeId(cursor.getInt(getIndex("PrepRecipe.recipeId",cursor)));
		return pb;
	}
	
	public void getAndCreateJSON() throws JSONException
	{
		ArrayList<recipeBean> recipeList = getRecipe();
		JSONArray jsonArray = new JSONArray();
		
		ArrayList<String> ingred = new ArrayList<String>();
		
		for(int i = 0; i < recipeList.size(); i++)
		{
			JSONObject recipe = new JSONObject();		
			recipe.put("name", recipeList.get(i).getName());
			recipe.put("description", recipeList.get(i).getDesc());
			recipe.put("prepTime", recipeList.get(i).getPrep());
			recipe.put("cookingTime", recipeList.get(i).getCooking());
			recipe.put("serves", recipeList.get(i).getServes());
			recipe.put("addedBy", recipeList.get(i).getAddedBy());
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
			//ingredObj.put("name", value)
			//ArrayList<ingredientBean> ingredList = getIngred(rcipeList.get(i).getId(
			recipe.put("Ingredient", ingredObj);
			recipe.accumulate("Ingredient", ingredValObj);
			recipe.accumulate("Ingredient", ingredAmountObj);
			recipe.accumulate("Ingredient", ingredNoteObj);
			
			
			
			jsonArray.put(recipe);			
			Log.v("Json", "Json " + jsonArray);
		} 
	sendJSONToServer(jsonArray);
	}
	
	
	public void sendJSONToServer(JSONArray jsonArray)
	{
		String str = "";
		HttpResponse response = null;
        HttpClient myClient = new DefaultHttpClient();
        HttpPost myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm3.aspx");      	   	
		try 
		{
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
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
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
