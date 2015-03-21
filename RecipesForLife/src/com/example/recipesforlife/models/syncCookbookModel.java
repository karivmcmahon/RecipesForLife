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

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.util.Utility;
import com.example.recipesforlife.views.SignUpSignInActivity;

/**
 * Gets and sends cookbook JSON to and from server
 * @author Kari
 *
 */
public class SyncCookbookModel extends BaseDataSource {
	Context context;
	Utility util;


	public SyncCookbookModel(Context context) {
		super(context);
		this.context = context;
		util = new Utility();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Get cookbooks inserted or updated within a certain time frame
	 * @param update - whether checking for updates or inserts
	 * @return list of cookbook's
	 */
	public ArrayList<CookbookBean> getCookbook(boolean update)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		open();
		Cursor cursor;
		ArrayList<CookbookBean> cbList = new ArrayList<CookbookBean>();
		if(update == true)
		{
			cursor = database.rawQuery("SELECT * FROM Cookbook WHERE changeTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] { sharedpreferences.getString("Cookbook Update", "DEFAULT")  });
		}
		else
		{	
			cursor = database.rawQuery("SELECT * FROM Cookbook WHERE updateTime > STRFTIME('%Y-%m-%d %H:%M:%f', ?)", new String[] {  sharedpreferences.getString("Cookbook", "DEFAULT") });
		}
		if (cursor != null && cursor.getCount() > 0) {
			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToPosition(i);
				cbList.add(cursorToCookbook(cursor));
			}
		}
		cursor.close();
		close();
		return cbList;
	}

	/**
	 * Get data from cursor and place in a cookbook bean
	 * @param cursor
	 * @return cookbookBean
	 */
	public CookbookBean cursorToCookbook(Cursor cursor) {
		CookbookBean cb = new CookbookBean();
		cb.setName(cursor.getString(getIndex("name",cursor)));
		cb.setDescription(cursor.getString(getIndex("description",cursor)));
		cb.setUniqueid(cursor.getString(getIndex("uniqueid", cursor)));
		cb.setPrivacy(cursor.getString(getIndex("privacyOption",cursor)));
		cb.setCreator(cursor.getString(getIndex("creator",cursor)));
		cb.setUpdateTime(cursor.getString(getIndex("updateTime", cursor)));
		cb.setChangeTime(cursor.getString(getIndex("changeTime", cursor)));
		cb.setImage(cursor.getBlob(getIndex("image", cursor)));
		cb.setProgress(cursor.getString(getIndex("progress", cursor)));
		return cb;
	}

	/**
	 * Builds a json with all the cookbook data to send to the server
	 * @param - builds json and sends to server based on update or insert
	 * @throws JSONException
	 * @throws IOException 
	 */
	public void getAndCreateJSON(boolean update) throws JSONException, IOException
	{
		ArrayList<CookbookBean> bookList = getCookbook(update);
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < bookList.size(); i++)
		{
			JSONObject book = new JSONObject();		
			book.put("name", bookList.get(i).getName());
			book.put("description", bookList.get(i).getDescription());
			book.put("creator", bookList.get(i).getCreator());
			book.put("updateTime", bookList.get(i).getUpdateTime());
			book.put("changeTime", bookList.get(i).getChangeTime());
			book.put("uniqueid", bookList.get(i).getUniqueid());	
			book.put("privacyOption", bookList.get(i).getPrivacy());
			book.put("progress", bookList.get(i).getProgress());
			String image64 = new String(Base64.encode(bookList.get(i).getImage(), Base64.DEFAULT));
			book.put("image", image64);
			jsonArray.put(book);			
		} 
		if(update == true)
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm9.aspx");
		}
		else
		{
			util.sendJSONToServer(jsonArray, update, "https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm7.aspx");
		}
	}



	/**
	 * Gets and decodes json from server and inserts or updates cookbook
	 * @param update - whether the json is for update or insert
	 * @throws JSONException
	 * @throws IOException
	 */
	public void getJSONFromServer(boolean update) throws JSONException, IOException
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		JSONObject json;
		String str = "";
		if(update == true)
		{

			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm10.aspx", sharedpreferences.getString("Cookbook Update", "DEFAULT"), true);
		}
		else
		{
			str = util.retrieveFromServer("https://zeno.computing.dundee.ac.uk/2014-projects/karimcmahon/wwwroot/WebForm8.aspx", sharedpreferences.getString("Cookbook", "DEFAULT"), false);
		}

		JSONObject jObject = new JSONObject(str);
		JSONArray jArray = (JSONArray) jObject.get("Cookbook");

		for(int i = 0; i < jArray.length(); i++)
		{


			json = jArray.getJSONObject(i);
			CookbookBean book = new CookbookBean();
			book.setName( json.getString("name"));
			book.setDescription(json.getString("description"));
			book.setPrivacy(json.getString("privacyOption"));
			book.setUniqueid(json.getString("uniqueid"));
			book.setCreator(json.getString("creator"));
			book.setImage(Base64.decode(json.getString("image"), Base64.DEFAULT));
			book.setProgress(json.getString("progress"));
			CookbookModel model = new CookbookModel(context);
			if(update == true)
			{
				try
				{
					model.updateBook(book, true);
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
					model.insertBook(book, true);

				}
				catch(SQLException e)
				{
					throw e;
				}
			}

		} 

	}

}


