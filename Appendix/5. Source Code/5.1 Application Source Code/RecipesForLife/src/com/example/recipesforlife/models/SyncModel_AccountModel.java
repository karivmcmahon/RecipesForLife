package com.example.recipesforlife.models;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;

import com.example.recipesforlife.controllers.AccountBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * This class syncs the sever and phone databases for account JSONs
 * @author Kari
 *
 */
public class SyncModel_AccountModel extends Database_BaseDataSource
{
	private Context context;
	private Utility util;
	public SyncModel_AccountModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
		
	}

	/**
	 * Sets info from db to the account controller
	 * 
	 * @param cursor			Results from db query
	 * @return accountBean		Stores query results
	 */
	public AccountBean cursorToAccount(Cursor cursor) {
		AccountBean ab = new AccountBean();
		ab.setId(cursor.getInt(getIndex("id",cursor)));       
		ab.setEmail(cursor.getString(getIndex("email", cursor)));
		ab.setPassword(cursor.getString(getIndex("password", cursor)));
		ab.setUpdateTime(cursor.getString(getIndex("updateTime",cursor)));
		return ab;
	}

	/**
	 * Sets info from db to the user controller
	 * 
	 * @param cursor			Results from db query
	 * @return userBean			Stores query results
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
	 * Gets the accounts after a certain date
	 * 
	 * @return ArrayList<AccountBean>	List of accounts
	 */
	public ArrayList<AccountBean> getAccount()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<AccountBean> accountList = new ArrayList<AccountBean>();
		Cursor cursor;
		cursor = database.rawQuery("SELECT * FROM Account WHERE  updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });        
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				accountList.add(cursorToAccount(cursor));
			}
		}
		cursor.close();
		close();
		return accountList;
	}

	/**
	 * Create a json with the recently added account info to send to server
	 * 
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateAccountJSON() throws JSONException, IOException
	{
		ArrayList<UserBean> userList = getUsers();
		ArrayList<AccountBean> accountList = getAccount();
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < accountList.size(); i++)
		{
			JSONObject account = new JSONObject();
			account.put("email",  accountList.get(i).getEmail());
			account.put("password", accountList.get(i).getPassword());
			account.put("name", userList.get(i).getName());
			account.put("bio", userList.get(i).getBio());
			account.put("country", userList.get(i).getCountry());
			account.put("city", userList.get(i).getCity());
			account.put("cookingInterest", userList.get(i).getCookingInterest());
			account.put("updateTime", accountList.get(i).getUpdateTime());
			jsonArray.put(account);			
		} 
		util.sendJSONToServer(jsonArray, false, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm1.aspx");
	}

	/**
	 * Gets the json with it's sync info from the server and inserts into database
	 * 
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getJSONFromServer() throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);

		JSONObject json;
		String str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm2.aspx", sharedpreferences.getString("Date", "DEFAULT") , false);

		JSONArray jArray = new JSONArray(str);
		if (jArray.length() != 0) 
		{
			json = jArray.getJSONObject(0);
			for (int i = 0; i < jArray.length(); i++) 
			{
				AccountBean account = new AccountBean();
				UserBean user = new UserBean();
				json = jArray.getJSONObject(i);
				account.setEmail(json.getString("email"));
				account.setPassword(json.getString("password"));
				user.setName(json.getString("name"));
				user.setBio(json.getString("bio"));
				user.setCity(json.getString("city"));
				user.setCookingInterest(json.getString("cookingInterest"));
				user.setCountry(json.getString("country"));
				ApplicationModel_AccountModel accountmodel = new ApplicationModel_AccountModel(context);
				try
				{
					accountmodel.insertAccount(account, user, true);
				}
				catch(SQLException e)
				{
					throw e;
				}
			}
		}


	}



	/**
	 * Gets	the users info after a datetime range
	 * 
	 * @return ArrayList<UserBean> 	List of users after this datetime range
	 */
	public ArrayList<UserBean> getUsers()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<UserBean> userList = new ArrayList<UserBean>();
		Cursor cursor;
		cursor = database.rawQuery("SELECT * FROM Users WHERE  updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT")});
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				userList.add(cursorToUser(cursor));
			}
		}
		cursor.close();
		close();
		return userList;
	}

}





