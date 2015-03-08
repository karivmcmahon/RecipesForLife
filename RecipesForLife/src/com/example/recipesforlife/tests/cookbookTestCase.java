package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.recipeBean;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test cases for cookbook and contributers related database code
 * @author Kari
 *
 */
public class cookbookTestCase  extends AndroidTestCase {

	Resources resources;
	RenamingDelegatingContext context;
	cookbookModel cookbookmodel;

	@SuppressLint("NewApi")
	protected void setUp() throws Exception {
		super.setUp();
	     context 
	        = new RenamingDelegatingContext(getContext(), "test_");
		cookbookmodel = new cookbookModel(context);
	    copyDataBase();
		
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = context.getAssets();
	    InputStream myInput = mg.open("databases/mockdv.sqlite");
	
	    // Path to the just created empty db
	    String outFileName = cookbookmodel.dbHelper.getWritableDatabase().getPath().toString();
	
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
	
	}
	
	public void testSelectCookbook()
	{
		ArrayList<cookbookBean> cblist = cookbookmodel.selectCookbook("cookbookuniqueid1");
		Assert.assertEquals(cblist.get(0).getName(), "book1");
		Assert.assertEquals(cblist.get(0).getCreator(), "doe");
	}
	
	public void testInsertCookbook()
	{
		cookbookBean cb = new cookbookBean();
		cb.setName("cookbook");
		cb.setCreator("doe");
		cb.setChangeTime("2015-01-01 12:00:00");
		cb.setUpdateTime("2015-01-01 12:00:00");
		cb.setDescription("My personal cookbook");
		cb.setPrivacy("public");
		cb.setUniqueid("cookbookuniqueid2");
		cookbookmodel.insertBook(cb, false);
		
		ArrayList<cookbookBean> cblist = cookbookmodel.selectCookbooksByCreator("doe");
		Assert.assertEquals(cblist.get(1).getName(), "cookbook");
	}
	
	public void testUpdateCookbook()
	{
		cookbookBean cb = new cookbookBean();
		cb.setName("Fun Foods");
		cb.setDescription("fun");
		cb.setChangeTime("2015-01-01 12:00:00");
		cb.setPrivacy("public");
		cb.setUniqueid("cookbookuniqueid1");
		cookbookmodel.updateBook(cb, false);
		
		ArrayList<cookbookBean> cblist = cookbookmodel.selectCookbook("cookbookuniqueid1");
		Assert.assertEquals(cblist.get(0).getName(), "Fun Foods");
	}
	
	public void testCookbookRecipes()
	{
		ArrayList<recipeBean> rbList = cookbookmodel.selectRecipesByCookbook("cookbookuniqueid1");
		Assert.assertEquals(rbList.get(0).getName(), "pizza");
	}
	
	public void testInsertContribs()
	{
		cookbookmodel.insertContributers("beyonce@hotmail.co.uk", 1, false);
		ArrayList<String> contribs = cookbookmodel.selectCookbookContributers("cookbookuniqueid1", "added");
		Assert.assertEquals(contribs.get(1), "beyonce@hotmail.co.uk");
	}
	
	public void testSelectContribs()
	{
		cookbookmodel.insertContributers("kimk@aol.co.uk", 1, false);
		ArrayList<String> contribs = cookbookmodel.selectCookbookContributers("cookbookuniqueid1", "added");
		Assert.assertEquals(contribs.get(0), "kimk@aol.co.uk");
	}
	
	public void testUpdateContribs()
	{
		cookbookmodel.updateContributers("kimk@aol.co.uk", 1, "deleted", false);
		ArrayList<String> contribs = cookbookmodel.selectCookbookContributers("cookbookuniqueid1", "deleted");
		Assert.assertEquals(contribs.get(0), "kimk@aol.co.uk");
	}
	
	public void testSelectContributer()
	{
		boolean exists = cookbookmodel.selectContributer("kimk@aol.co.uk",1);
		Assert.assertEquals(true, exists);
	}
	
	public void testSelectCookbooksByUser()
	{
		ArrayList<cookbookBean> cblist = cookbookmodel.selectCookbooksByUser("doe");
		Assert.assertEquals(cblist.get(0).getName(), "book1");
		ArrayList<cookbookBean> cblist1 = cookbookmodel.selectCookbooksByUser("kimk@aol.co.uk");
		Assert.assertEquals(cblist1.get(0).getName(), "book1");
		
	}
	
	
	
	
}



