package com.example.recipesforlife.tests;

import java.util.ArrayList;
import java.util.List;




import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.userBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.syncModel;

import android.test.InstrumentationTestCase;
import junit.framework.Assert;
import junit.framework.TestCase;

public class accountTestCase extends InstrumentationTestCase  {
	syncModel sync;
	accountModel accountmodel;

	protected void setUp() throws Exception {
		super.setUp();
		sync = new syncModel(getInstrumentation().getContext());
		accountmodel = new accountModel(getInstrumentation().getContext());
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
		List<String> accountInfo = new ArrayList();
		List<accountBean> account = new ArrayList<accountBean>();
		List<userBean> user = new ArrayList<userBean>();
		accountInfo.add("Hilary");
		accountInfo.add("Hilary");
		accountInfo.add("Scotland");
		accountInfo.add("Home cook");
		accountInfo.add("Edinburgh");
		accountInfo.add("Home cook");
		accountInfo.add("hilz@aol.co.uk");
		accountInfo.add("whisk");	
		accountmodel.insertAccount(accountInfo);
		account = accountmodel.selectAccount("hilz@aol.co.uk", "whisk");
		user = accountmodel.selectUser(account.get(0).getId());
		
	}
	
	public void testGetUser() throws Exception
	{
		
	}
	
	public void testGetAccount() throws Exception
	{
		
	}
	
	public void testSync()
	{
		
	}

}
