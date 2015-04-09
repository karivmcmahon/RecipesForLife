package com.example.recipesforlife.tests;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;













import com.example.recipesforlife.controllers.AccountBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.models.ApplicationModel_AccountModel;
import com.example.recipesforlife.models.Database_BaseDataSource;
import com.example.recipesforlife.models.Database_DatabaseConnection;
import com.example.recipesforlife.models.SyncModel_AccountModel;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;
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
public class Account_TestCase extends AndroidTestCase  {
	SyncModel_AccountModel sync;
	ApplicationModel_AccountModel accountmodel;
	Database_DatabaseConnection dbConnection;
	Resources resources;
	RenamingDelegatingContext context;

	@SuppressLint("NewApi")
	protected void setUp() throws Exception {
		super.setUp();
		context 
		= new RenamingDelegatingContext(getContext(), "test_");
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
		List<AccountBean> account = new ArrayList<AccountBean>();
		List<UserBean> user = new ArrayList<UserBean>();

		AccountBean anAccount = new AccountBean();
		anAccount.setEmail("hilz@aol.co.uk");
		anAccount.setPassword("whisk");
		UserBean anUser = new UserBean();
		anUser.setName("Hilary");
		anUser.setCity("Edinburgh");
		anUser.setCookingInterest("Home cook");
		anUser.setBio("Home cook");
		anUser.setCountry("Scotland");
		accountmodel.insertAccount(anAccount, anUser, false);
		account = accountmodel.selectAccount("hilz@aol.co.uk");
		user = accountmodel.selectUser(account.get(0).getId());
		Assert.assertEquals(account.get(0).getEmail(), "hilz@aol.co.uk");
		Assert.assertEquals(user.get(0).getName(), "Hilary");
	}

	public void testAccountInsertFails()
	{
		Throwable caught = null;
		try
		{
			List<AccountBean> account = new ArrayList<AccountBean>();
			List<UserBean> user = new ArrayList<UserBean>();
			AccountBean anAccount = new AccountBean();
			UserBean anUser = new UserBean();
			anAccount.setEmail("hilz@aol.co.uk");
			anAccount.setPassword("whisk");
			anUser.setName(null);
			anUser.setCity("Edinburgh");
			anUser.setCookingInterest("Home cook");
			anUser.setBio("Home cook");
			anUser.setCountry("Scotland");
			accountmodel.insertAccount(anAccount, anUser, false);
			account = accountmodel.selectAccount("hils@aol.co.uk");
			Assert.assertEquals(account.size(),0);
		}
		catch(Throwable t)
		{
			caught = t;
		}
		assertNotNull(caught);
		assertSame(SQLiteConstraintException.class, caught.getClass());

	} 

	public void testGetUser() throws Exception
	{
		List<UserBean> user = new ArrayList<UserBean>();
		user = accountmodel.selectUser(1);
		Assert.assertEquals(user.get(0).getName(), "doe");
		user = accountmodel.selectUser(10000000);
		Assert.assertEquals(user.size(), 0);

	}

	public void testGetAccount() throws Exception
	{
		List<AccountBean> account = new ArrayList<AccountBean>();
		account = accountmodel.selectAccount("doe");
		Assert.assertEquals(account.get(0).getEmail(), "doe");
		account = accountmodel.selectAccount("danny");
		Assert.assertEquals(account.size(), 0);
	}

	//Needs to test sync - not sure how to approach it without adding test data to server
	//possibly add and delete from server

}
