package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.Assert;

import com.example.recipesforlife.models.ApplicationModel_AccountModel;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
import com.example.recipesforlife.util.Util;
import com.example.recipesforlife.views.Account_SignUpSignInView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

public class Util_Activity_TestCase extends AndroidTestCase  {
	Util util;
	RenamingDelegatingContext context;
	ApplicationModel_AccountModel accountmodel;
	
	protected void setUp() throws Exception {
		super.setUp();
	    context 
	        = new RenamingDelegatingContext(getContext(), "test_");

		util = new Util(context, new Activity());
		accountmodel = new ApplicationModel_AccountModel(context);
		copyDataBase();
	
		
		
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = context.getAssets();
	    InputStream myInput = mg.open("databases/mockdv.sqlite");
	
	    // Path to the just created empty db
	    String outFileName = accountmodel.dbHelper.getWritableDatabase().getPath().toString();
	
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
	
	/**private void testSync()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		String response = util.sync();
		Assert.assertEquals(response, "success");
		Editor editor = sharedpreferences.edit();
		editor.putString("Date", "2015-01-01 12:00:00");
		editor.commit();
		editor.putString("Stage", "1");
		editor.commit();
	}**/

}
