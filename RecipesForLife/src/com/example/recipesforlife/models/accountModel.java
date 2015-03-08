package com.example.recipesforlife.models;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.userBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles database details relating to the users account
 * @author Kari
 *
 */
public class accountModel extends baseDataSource
{
	long id;
	String str;
	ContentValues values;
	ContentValues accountValues;
	Context context;
	syncModel sync;
	utility utils;
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 

	public accountModel(Context context) {
		super(context);
		// TODO Auto-generated constructor st
		this.context = context;
		sync = new syncModel(context);
		utils = new utility();
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	/**
	 * Code to insert sign up information into sqlite database
	 * @param accountInfo
	 */
	public void insertAccount(accountBean account, userBean user, boolean server) 
	{
		open();
		database.beginTransaction();
		try
		{
			insertUserData(account, user, server);
			database.setTransactionSuccessful();
			database.endTransaction(); 
			Log.v("suc", "suc");
		}catch(SQLException e)
		{
			e.printStackTrace();
			database.endTransaction();
			Log.v("Trans fail", "Trans fail");
			throw e;
			
		}
		close();
	} 

	/**
	 * Select account information where email and password matches
	 * @param email
	 * @param password
	 * @return A list of accounts
	 */
	public ArrayList<accountBean> selectAccount(String email, String password)
	{
		open();
		ArrayList<accountBean> accountList = new ArrayList<accountBean>();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=? AND password=?", new String[] { email, password  });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				accountList.add(sync.cursorToAccount(cursor));
			}
		}
		cursor.close();
		close();
		return accountList;
	}

	/**
	 * Select users where the id matches
	 * @param id
	 * @return A list of users
	 */
	public ArrayList<userBean> selectUser(int id)
	{
		open();
		ArrayList<userBean> userList = new ArrayList<userBean>();
		Cursor cursor = database.rawQuery("SELECT * FROM Users WHERE id=?", new String[] { Integer.toString(id) });
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				userList.add(sync.cursorToUser(cursor));
			}
		}
		cursor.close();
		close();
		return userList;
	}

	/**
	 * Sets info from db to the controller
	 * @param cursor
	 * @return userBean
	 */
	public userBean cursorToUser(Cursor cursor) {
		userBean ub = new userBean();
		ub.setId(cursor.getInt(getIndex("id",cursor)));       
		ub.setName(cursor.getString(getIndex("name", cursor)));
		ub.setBio(cursor.getString(getIndex("bio", cursor)));
		ub.setCity(cursor.getString(getIndex("city", cursor)));
		ub.setCountry(cursor.getString(getIndex("country", cursor)));
		ub.setCookingInterest(cursor.getString(getIndex("cookingInterest", cursor)));
		return ub;
	}



	/**
	 * Check if details provided are an account
	 * @param email
	 * @param password
	 * @return true if an account or false if not
	 */
	public boolean logIn(String email, String password) {
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=? AND password=?", new String[] { email, password });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			close();
			return true;
		}
		else
		{
			cursor.close();
			close();
			return false;
		} 	
	}

	/**
	 * Checks if email is already in use
	 * @param email
	 * @return true if in use and false if not
	 */
	public boolean checkEmail(String email )
	{
		open();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=?", new String[] { email });
		if (cursor != null && cursor.getCount() > 0) {
			cursor.close();
			close();
			return true;
		}
		else
		{
			cursor.close();
			close();
			return false;
		} 	
	}

	/**
	 * Insert user info into the user sqlite table
	 * @param accountInfo
	 */
	public void insertUserData(accountBean account, userBean user, boolean server)
	{
		//User values
		values = new ContentValues();
		values.put("name", user.getName()); 
		if(server == true)
		{
			values.put("updateTime", sharedpreferences.getString("Account Date", "DEFAULT")); 
		}
		else
		{
			values.put("updateTime", utils.getLastUpdated(false)); 
		}
		
		values.put("country", user.getCountry()); 
		values.put("bio", user.getBio()); 
		values.put("city", user.getCity()); 
		values.put("cookingInterest", user.getCookingInterest()); 
		try
		{
		id = database.insertOrThrow("Users", null, values);
		insertAccountData(account, id, server);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Insert account info into account sqlite table
	 * @param accountInfo
	 * @param id
	 */
	public void insertAccountData(accountBean account, long id, boolean server)
	{
		//Account values
		accountValues = new ContentValues();
		accountValues.put("id", (int)id);
		accountValues.put("email", account.getEmail());
		if(server == true)
		{
			accountValues.put("updateTime", sharedpreferences.getString("Account Date", "DEFAULT")); 
		}
		else
		{
			accountValues.put("updateTime", utils.getLastUpdated(false));
		}
		accountValues.put("password", account.getPassword());
		try
		{
		database.insertOrThrow("Account", null, accountValues);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			throw e;
		}
	}




}