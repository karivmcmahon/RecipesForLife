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

import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
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
	
		ArrayList<ingredientBean> ingredList = new ArrayList<ingredientBean>();
		ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
		recipeBean recipe = new recipeBean();
		recipe.setName("Chicken Soup");
		recipe.setDesc("Soothes the cold");
		recipe.setServes("4");
		recipe.setPrep("1:00");
		recipe.setCooking("1:00");
		recipe.setAddedBy("addison");
		ingredientBean ingred = new ingredientBean();
		ingred.setName("stock");
		ingred.setAmount(1);
	    ingred.setValue("packet");
		ingred.setNote("");
	    preperationBean prep = new preperationBean();
	    prep.setPreperation("boil water");
	    prep.setPrepNum(1);
		prepList.add(prep);
		ingredList.add(ingred);
		
		
		 
		recipemodel.insertRecipe(recipe, false, ingredList, prepList);
		
	/**	recipeBean recipeSelect = new recipeBean();
		recipeSelect = recipemodel.selectRecipe2("Chicken Soup","addison");
		Assert.assertEquals(recipeSelect.getName(), "Chicken Soup");**/
		
		
	} 

public void editRecipe()
{

	ArrayList<ingredientBean> ingredList = new ArrayList<ingredientBean>();
	ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
	recipeBean recipe = new recipeBean();
	recipe.setName("pizza");
	recipe.setDesc("good food");
	recipe.setUniqueid("doeRecipe");
	recipe.setAddedBy("doe");


	
	
	 
	recipemodel.updateRecipe(recipe, prepList, ingredList);
	
/**	recipeBean recipeSelect = new recipeBean();
	recipeSelect = recipemodel.selectRecipe2("pizza","doe");
	Assert.assertEquals(recipeSelect.getName(), "good food");**/
	
	
} 
	

	
	public void testSelectRecipe()
	{
		ArrayList<recipeBean> recipeSelect = new ArrayList<recipeBean>();
		recipeSelect = recipemodel.selectRecipesByUser("doe");
		Assert.assertEquals(recipeSelect.get(0).getName(), "pizza");
		
	}

}
