package com.example.recipesforlife.models;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.example.recipesforlife.controllers.AccountBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.util.PasswordHashing;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Class handles database details relating to the users account
 * @author Kari
 *
 */
/**
 * @author Kari
 *
 */
/**
 * @author Kari
 *
 */
public class AccountModel extends BaseDataSource
{
	ContentValues values;
	ContentValues accountValues;
	Context context;
	SyncModel sync;
	Utility utils;
	//shared preference details
	SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 

	public AccountModel(Context context) {
		super(context);
		this.context = context;
		sync = new SyncModel(context);
		utils = new Utility();
		//intialise shared preference
		sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	}

	
	/**
	 * Inserts account information into the sqlite database
	 * 
	 * @param account - a controller containing account info
	 * @param user - a controller containing user info
	 * @param server - if this request is coming from the server or from the app
	 */
	public void insertAccount(AccountBean account, UserBean user, boolean server) 
	{
		open();
		database.beginTransaction();
		try
		{
			insertUserData(account, user, server);
			database.setTransactionSuccessful();
			database.endTransaction(); 
		}catch(SQLException e)
		{
			e.printStackTrace();
			database.endTransaction();
			throw e;
		}
		close();
	} 

	
	/**
	 * Selects a list of accounts from database based on email and password
	 * 
	 * @param email
	 * @param password
	 * @return a list of accounts in the form of accountbean controller
	 */
	public ArrayList<AccountBean> selectAccount(String email)
	{
		open();
		ArrayList<AccountBean> accountList = new ArrayList<AccountBean>();
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=? ", new String[] { email  });
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
	 * Selects user information from database based on row id
	 * @param id - row id in database
	 * @return - A list of user information in the form of UserBeans
	 */
	public ArrayList<UserBean> selectUser(int id)
	{
		open();
		ArrayList<UserBean> userList = new ArrayList<UserBean>();
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
	 * Gets information from the database based on the cursor and sets to user bean
	 * @param cursor 
	 * @return user information in the form of a user bean
	 */
	public UserBean cursorToUser(Cursor cursor) {
		UserBean ub = new UserBean();
		ub.setId(cursor.getInt(getIndex("id",cursor)));       
		ub.setName(cursor.getString(getIndex("name", cursor)));
		ub.setBio(cursor.getString(getIndex("bio", cursor)));
		ub.setCity(cursor.getString(getIndex("city", cursor)));
		ub.setCountry(cursor.getString(getIndex("country", cursor)));
		ub.setCookingInterest(cursor.getString(getIndex("cookingInterest", cursor)));
		return ub;
	}




	/**
	 * Checks if an account is valid based on the email and password input by the user
	 * @param email
	 * @param password
	 * @return a boolean stating whether its valid of not
	 */
	public boolean logIn(String email, String password) {
		open();
		boolean valid = false;
		Cursor cursor = database.rawQuery("SELECT * FROM Account WHERE email=?", new String[] { email });
		if (cursor != null && cursor.getCount() > 0) {
			PasswordHashing ph = new PasswordHashing();
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				try {
					//This checks if the password input and the one in the database matches
					valid = ph.validatePassword(password, cursor.getString(getIndex("password", cursor)));
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					e.printStackTrace();
				}
			}
			cursor.close();
			close();
			return valid;
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
	 * Inserts user information into the database
	 * 
	 * @param account - Account information to be inserted
	 * @param user - User information to be inserted
	 * @param server - Boolean which states whether request is coming from server or not
	 */
	public void insertUserData(AccountBean account, UserBean user, boolean server)
	{
		values = new ContentValues();
		values.put("name", user.getName()); 
		//If request is from server set updateTime to shared pref time otherwise set to timestamp for that time
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
			long id = database.insertOrThrow("Users", null, values);
			insertAccountData(account, id, server);
		}
		catch(SQLException e)
		{
			e.printStackTrace();
			throw e;
		}
	}


	/**
	 * Inserts account information into database
	 * @param account - account info to be inserted
	 * @param id - row id of the row user data was inserted into
	 * @param server - whether request was from the server or the application
	 */
	public void insertAccountData(AccountBean account, long id, boolean server)
	{
		//Account values
		accountValues = new ContentValues();
		accountValues.put("id", (int)id);
		accountValues.put("email", account.getEmail());
		accountValues.put("password", account.getPassword());
		//If request is from server set updateTime to shared pref time otherwise set to timestamp for that time
		if(server == true)
		{			
			accountValues.put("updateTime", sharedpreferences.getString("Date", "DEFAULT")); 
		}
		else
		{
			accountValues.put("updateTime", utils.getLastUpdated(false));
		}

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