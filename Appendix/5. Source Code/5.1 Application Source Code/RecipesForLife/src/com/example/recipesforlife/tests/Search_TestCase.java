package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import junit.framework.Assert;
import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.models.ApplicationModel_ReviewModel;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
import com.example.recipesforlife.models.Database_DatabaseConnection;

/**
 * Test cases for search related database querys
 * @author Kari
 *
 */
public class Search_TestCase extends AndroidTestCase {
	ApplicationModel_SearchModel searchmodel;
	Database_DatabaseConnection dbConnection;
	RenamingDelegatingContext context;
	
	protected void setUp() throws Exception {
		super.setUp();
	    context 
	        = new RenamingDelegatingContext(getContext(), "test_");

		searchmodel = new ApplicationModel_SearchModel(context);
		copyDataBase();
	
		
		
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = context.getAssets();
	    InputStream myInput = mg.open("databases/mockdv.sqlite");
	
	    // Path to the just created empty db
	    String outFileName = searchmodel.dbHelper.getWritableDatabase().getPath().toString();
	
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
		//dbConnection.cArrayList<E>		
	}
	
	public void testSelectCookbook()
	{
		ArrayList<CookbookBean> cblist = searchmodel.selectCookbooks("book");
		Assert.assertEquals(cblist.get(0).getName(), "book1");
	}
	
	public void testSeletRandomCookbook()
	{
		ArrayList<CookbookBean> cblist = searchmodel.selectRandomCookbooks();
		Assert.assertEquals(cblist.get(0).getName(), "book1");
	}
	
	public void testSelectRecipe()
	{
		ArrayList<RecipeBean> rblist = searchmodel.selectRecipe("p");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
		
		rblist = searchmodel.selectRecipe("cheesy");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
		
	    rblist = searchmodel.selectRecipe("cheese");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testSelectRecipeByCuisine()
	{
		ArrayList<RecipeBean> rblist = searchmodel.selectRecipeByCuisine("italian");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testSelectRecipeByDietary()
	{
		ArrayList<RecipeBean> rblist = searchmodel.selectRecipeByDietary("nut free");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testSelectRecipeByDifficulty()
	{
		ArrayList<RecipeBean> rblist = searchmodel.selectRecipeByDiff("easy");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testSelectUsers()
	{
		ArrayList<UserBean> ulist = searchmodel.selectUsers("d");
		Assert.assertEquals(ulist.get(0).getEmail(), "doe");
	}
	
	
}
