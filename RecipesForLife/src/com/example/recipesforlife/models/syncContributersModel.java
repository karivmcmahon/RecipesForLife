package com.example.recipesforlife.models;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.recipesforlife.controllers.ContributerBean;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;

/**
 * Gets and creates JSON contributers too send to and from server
 * @author Kari
 *
 */
public class SyncContributersModel extends BaseDataSource {

	Context context;


	public SyncContributersModel(Context context) {
		super(context);
		this.context = context;
	}

	/**
	 * Gets contributers within a specific date range
	 * @param update
	 * @return List of contributers
	 */
	public ArrayList<ContributerBean> getContribs(boolean update)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		ArrayList<ContributerBean> contribList = new ArrayList<ContributerBean>();
		Cursor cursor = null;
		if(update == true)
		{
			cursor = database.rawQuery("SELECT * FROM Contributers WHERE changeTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Contributers Update", "DEFAULT")   });
		}
		else
		{
			cursor = database.rawQuery("SELECT * FROM Contributers WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Contributers", "DEFAULT") });
		}

		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				String account = cursor.getString(getIndex("accountid",cursor));
				int cookbookid = cursor.getInt(getIndex("cookbookid",cursor));
				String updates = cursor.getString(getIndex("updateTime", cursor));
				String change = cursor.getString(getIndex("changeTime", cursor));
				String progress = cursor.getString(getIndex("progress", cursor));

				CookbookModel bookmodel = new CookbookModel(context);
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
	 * Create a json of the contributers information
	 * @param update
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
		sendJSONToServer(jsonArray, update);
	} 

	/**
	 * Send json to the server
	 * @param jsonArray
	 * @param update
	 * @throws IOException
	 */
	public void sendJSONToServer(JSONArray jsonArray, boolean update ) throws IOException
	{
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		if(update == true)
		{
			myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm13.aspx");   
		}
		else
		{
			myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm11.aspx");   
		}

		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));


			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception contributers error");
				}

			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}

	}

	/**
	 * Get contributer json from server and either insert or update the contributer
	 * @param update
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject date = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		JSONObject json;
		if(update == true)
		{
			date.put("updateTime", sharedpreferences.getString("Contributers Update", "DEFAULT"));
			date.put("change", "true");
		}
		else
		{
			date.put("updateTime", sharedpreferences.getString("Contributers", "DEFAULT"));
			date.put("change", "false");
		}

		jsonArray.put(date);
		String str = "";
		HttpResponse response = null;
		HttpClient myClient = new DefaultHttpClient();
		HttpPost myConnection = null;
		myConnection = new HttpPost("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm12.aspx"); 

		try 
		{
			HttpConnectionParams.setConnectionTimeout(myClient.getParams(), 3000);
			HttpConnectionParams.setSoTimeout(myClient.getParams(), 7200);
			myConnection.setEntity(new ByteArrayEntity(
					jsonArray.toString().getBytes("UTF8")));
			try 
			{
				response = myClient.execute(myConnection);
				str = EntityUtils.toString(response.getEntity(), "UTF-8");
				Log.v("RESPONSE", "RESPONSE " + str);
				if(str.startsWith("Error"))
				{
					throw new ClientProtocolException("Exception contributers error");
				}


			} 
			catch (ClientProtocolException e) 
			{							
				e.printStackTrace();
				throw e;
			} 
			JSONObject jObject = new JSONObject(str);
			JSONArray jArray = (JSONArray) jObject.get("Contributer");

			for(int i = 0; i < jArray.length(); i++)
			{


				json = jArray.getJSONObject(i);
				String uniqid  = json.getString("bookid");
				String email = json.getString("email");
				String progress = json.getString("progress");
				CookbookModel model = new CookbookModel(context);
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
		catch (IOException e) 
		{
			e.printStackTrace();
			throw e;
		}
		catch(JSONException e)
		{
			e.printStackTrace();
			throw e;
		}
	} 


}
