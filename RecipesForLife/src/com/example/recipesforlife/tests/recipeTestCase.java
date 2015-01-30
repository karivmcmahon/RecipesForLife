package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;

import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.baseDataSource;
import com.example.recipesforlife.models.databaseConnection;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.views.SignUpSignInActivity;

import junit.framework.Assert;
import junit.framework.TestCase;

public class recipeTestCase extends AndroidTestCase{
	recipeModel recipemodel;
	SignUpSignInActivity activity;
	databaseConnection dbConnection;
	 RenamingDelegatingContext context;

	protected void setUp() throws Exception {
		super.setUp();
	    context 
	        = new RenamingDelegatingContext(getContext(), "test_");
		
		recipemodel = new recipeModel(context);
		copyDataBase();
	
		
		
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = context.getAssets();
	    InputStream myInput = mg.open("databases/mockdv");
	
	    // Path to the just created empty db
	    String outFileName = recipemodel.dbHelper.getWritableDatabase().getPath().toString();
	
	    //Open the empty db as the output stream
	    OutputStream myOutput = new FileOutputStream(outFileName);
	
	    //Transfer bytes from the inputfile to the outputfile
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = myInput.read(buffer)) > 0) 
	    {
	        myOutput.write(buffer, 0, length);
	    }
	    //Close the streams
	    myOutput.flush();
	    myOutput.close();
	    myInput.close();
	}
	

	protected void tearDown() throws Exception {
		super.tearDown();
	//	dbConnection.close();
		
	}
	
	
	
	public void testInsertRecipe()
	{
		ArrayList<String> ingredients = new ArrayList<String>();
		ArrayList<String> amount = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		ArrayList<String> notes = new ArrayList<String>();
		ArrayList<String> prepNum = new ArrayList<String>();
		ArrayList<String> prep = new ArrayList<String>();
		
		recipeBean recipe = new recipeBean();
		recipe.setName("Chicken Soup");
		recipe.setDesc("Soothes the cold");
		recipe.setServes("4");
		recipe.setPrep("1:00");
		recipe.setCooking("1:00");
		recipe.setAddedBy("addison");
		ingredients.add("stock");
		amount.add("1");
		value.add("packet");
		notes.add("");
		recipe.setIngredients(ingredients);
		recipe.setNotes(notes);
		recipe.setValues(value);
		recipe.setAmount(amount);
		
		prep.add("boil water");
		prepNum.add("1");
		recipe.setSteps(prep);
		recipe.setStepNum(prepNum);
		recipemodel.insertRecipe(recipe);
		
		recipeBean recipeSelect = new recipeBean();
		recipeSelect = recipemodel.selectRecipe2("Chicken Soup","addison");
		Assert.assertEquals(recipeSelect.getName(), "Chicken Soup");
		
		
	}
	
	public void testInsertRecipeError() throws Exception
	{
		ArrayList<String> ingredients = new ArrayList<String>();
		ArrayList<String> amount = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		ArrayList<String> notes = new ArrayList<String>();
		ArrayList<String> prepNum = new ArrayList<String>();
		ArrayList<String> prep = new ArrayList<String>();
		
		try
		{
		recipeBean recipe = new recipeBean();
		recipe.setName("Chicken Soup");
		recipe.setDesc("Soothes the cold");
		recipe.setServes("k");
		recipe.setPrep("1:00");
		recipe.setCooking("1:00");
		recipe.setAddedBy("addison");
		ingredients.add("stock");
		amount.add("k");
		value.add("packet");
		notes.add("");
		recipe.setIngredients(ingredients);
		recipe.setNotes(notes);
		recipe.setValues(value);
		recipe.setAmount(amount);
		
		prep.add("boil water");
		prepNum.add("1");
		recipe.setSteps(prep);
		recipe.setStepNum(prepNum);
		recipemodel.insertRecipe(recipe);
		 fail("Expected NumberFormatException");
		}
		catch(NumberFormatException e) {
			  // no-op (pass)
			}
		
		
		
	}
	
	public void testSelectRecipe()
	{
		ArrayList<recipeBean> recipeSelect = new ArrayList<recipeBean>();
		recipeSelect = recipemodel.selectRecipesByUser("doe");
		Assert.assertEquals(recipeSelect.get(0).getName(), "pizza");
		
	}

}
