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

import com.example.recipesforlife.controllers.ContributerBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.Account_SignUpSignInView;

/**
 * Gets and creates JSON contributers to send to and from server
 * @author Kari
 *
 */
public class SyncModel_ContributersModel extends Database_BaseDataSource {

	private Context context;
	private Utility util;


	public SyncModel_ContributersModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
	}

	/**
	 * Create a json of the contributers information and sends to the server
	 * 
	 * @param update 			whether the json is for update or insert
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getAndCreateJSON(boolean update) throws JSONException, IOException
	{
		ArrayList<ContributerBean> contribs = getContribs(update);
		JSONArray jsonArray = new JSONArray();

		for(int i = 0; i < contribs.size(); i++)
		{
			JSONObject contrib = new JSONObject();		
			contrib.put("email", contribs.get(i).getAccount());
			contrib.put("bookid", contribs.get(i).getBookUniqId());
			contrib.put("updateTime", contribs.get(i).getUpdateTime());
			contrib.put("changeTime", contribs.get(i).getChangeTime());
			contrib.put("progress", contribs.get(i).getProgress());
			jsonArray.put(contrib);			
		} 
		if(update == true)
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm13.aspx");
		}
		else
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm11.aspx");
		}
	} 

	/**
	 * Gets contributers info from database after a specific date
	 * 
	 * @param update 							whether checking for updates or inserts
	 * @return ArrayList<ContributerBean>		List of contributers
	 */
	private ArrayList<ContributerBean> getContribs(boolean update)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<ContributerBean> contribList = new ArrayList<ContributerBean>();
		Cursor cursor = null;
		if(update == true)
		{
			cursor = database.rawQuery("SELECT * FROM Contributers WHERE changeTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT")   });
		}
		else
		{
			cursor = database.rawQuery("SELECT * FROM Contributers WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Date", "DEFAULT") });
		}

		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String account = cursor.getString(getIndex("accountid",cursor));
				int cookbookid = cursor.getInt(getIndex("cookbookid",cursor));
				String updates = cursor.getString(getIndex("updateTime", cursor));
				String change = cursor.getString(getIndex("changeTime", cursor));
				String progress = cursor.getString(getIndex("progress", cursor));

				ApplicationModel_CookbookModel bookmodel = new ApplicationModel_CookbookModel(context);
				String uid = bookmodel.selectCookbooksByRowID(cookbookid);
				ContributerBean contrib = new ContributerBean();
				contrib.setAccount(account);
				contrib.setBookUniqId(uid);
				contrib.setChangeTime(change);
				contrib.setUpdateTime(updates);
				contrib.setProgress(progress);
				contribList.add(contrib);

			}
		}
		cursor.close();
		close();
		return contribList;
	} 



	/**
	 * Get contributer json from server and either insert or update the contributers in the db
	 * 
	 * @param update 			whether the json is for update or insert
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		String str = "";
		JSONObject json;
		if(update == true)
		{
			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm12.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);
		}
		else
		{
			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm12.aspx", sharedpreferences.getString("Date", "DEFAULT"), false);
		}

		try
		{
			JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Contributer");

			for(int i = 0; i < jArray.length(); i++)
			{


				json = jArray.getJSONObject(i);
				String uniqid  = json.getString("bookid");
				String email = json.getString("email");
				String progress = json.getString("progress");
				ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(context);
				int id = model.selectCookbooksIDByUnique(uniqid);
				if(update == true)
				{
					try
					{
						model.updateContributers(email, id, progress, true);
					}
					catch(SQLException e)
					{
						throw e;
					}
				}
				else
				{
					try
					{
						model.insertContributers(email, id, true);
					}
					catch(SQLException e)
					{
						throw e;
					}
				}

			}


		} 
		catch(JSONException e)
		{
			e.printStackTrace();
			throw e;
		}



	}

} 



