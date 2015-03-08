package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;











import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.userBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.baseDataSource;
import com.example.recipesforlife.models.databaseConnection;
import com.example.recipesforlife.models.syncModel;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.RenamingDelegatingContext;
import android.util.Log;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test cases for account related database code
 * @author Kari
 *
 */
public class accountTestCase extends AndroidTestCase  {
	syncModel sync;
	accountModel accountmodel;
	databaseConnection dbConnection;
	Resources resources;
	RenamingDelegatingContext context;

	@SuppressLint("NewApi")
	protected void setUp() throws Exception {
		super.setUp();
	     context 
	        = new RenamingDelegatingContext(getContext(), "test_");
		accountmodel = new accountModel(context);
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
	
	}
	
	
	public void testCheckEmail() throws Exception
	{
		boolean exists = accountmodel.checkEmail("doe");
		Assert.assertEquals(exists,true);
	} 
	
	public void testLogin() throws Exception
	{
		boolean  loggedIn = accountmodel.logIn("doe", "doe");
		Assert.assertEquals(loggedIn,true);
	} 
	
public void testAccountInsert() throws Exception
	{
		List<accountBean> account = new ArrayList<accountBean>();
		List<userBean> user = new ArrayList<userBean>();
		
		accountBean anAccount = new accountBean();
		anAccount.setEmail("hilz@aol.co.uk");
		anAccount.setPassword("whisk");
		userBean anUser = new userBean();
		anUser.setName("Hilary");
		anUser.setCity("Edinburgh");
		anUser.setCookingInterest("Home cook");
		anUser.setBio("Home cook");
		anUser.setCountry("Scotland");
		accountmodel.insertAccount(anAccount, anUser, false);
		account = accountmodel.selectAccount("hilz@aol.co.uk", "whisk");
		user = accountmodel.selectUser(account.get(0).getId());
		Assert.assertEquals(account.get(0).getEmail(), "hilz@aol.co.uk");
		Assert.assertEquals(user.get(0).getName(), "Hilary");
		
		anAccount.setEmail("hilz@aol.co.uk");
		anAccount.setPassword("whisk");
		anUser.setName(null);
		anUser.setCity("Edinburgh");
		anUser.setCookingInterest("Home cook");
		anUser.setBio("Home cook");
		anUser.setCountry("Scotland");
		accountmodel.insertAccount(anAccount, anUser, false);
		account = accountmodel.selectAccount("hils@aol.co.uk", "whisk");
		Assert.assertEquals(account.size(),0);
		
		
	} 
	
	public void testGetUser() throws Exception
	{
		List<userBean> user = new ArrayList<userBean>();
		user = accountmodel.selectUser(1);
		Assert.assertEquals(user.get(0).getName(), "doe");
		user = accountmodel.selectUser(10000000);
		Assert.assertEquals(user.size(), 0);
		
	}
	
	public void testGetAccount() throws Exception
	{
		List<accountBean> account = new ArrayList<accountBean>();
		account = accountmodel.selectAccount("doe", "doe");
		Assert.assertEquals(account.get(0).getEmail(), "doe");
		account = accountmodel.selectAccount("danny", "vause");
		Assert.assertEquals(account.size(), 0);
	}
	
	//Needs to test sync - not sure how to approach it without adding test data to server
	//possibly add and delete from server
	
}
