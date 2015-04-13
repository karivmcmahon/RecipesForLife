package com.example.recipesforlife.util;


import java.util.ArrayList;

import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.views.Cookbook_ShelfListView;
import com.example.recipesforlife.views.Account_SignUpSignInView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * This class enables the sync to be done in the background
 *  AsyncTask code enables sync code to be done in the background
 * @author Kari
 *
 */
public class PostTask extends AsyncTask<Void, Void, String> 
{

	Util utils;
	Intent i;
	Context context;
	String response = "";
	boolean cookbook = false;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey";
	
	public PostTask(Util  utils, Context context, boolean cookbook)
	{
		this.utils = utils;
		this.context = context;
		this.cookbook = cookbook;

	}

	@Override
	//The sync works in the background
	public String doInBackground(Void... arg0 ) {
		String message = utils.sync();
		response = message;	
		return message;
	}


	@Override
	//Once sync complete display whether the sync was successful or not
	protected void onPostExecute(String response) {
		super.onPostExecute(response);
		if(response.equals("success"))
		{
			//Displays success message
			Toast.makeText(context, "App synced ", Toast.LENGTH_LONG).show();
			if(cookbook == true)
			{
				//If cookbook page requests sync - refresh the list view
				SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
				ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(context);
				ArrayList<CookbookBean> cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
				Cookbook_ShelfListView.values.clear();
				Cookbook_ShelfListView.ids.clear();
				Cookbook_ShelfListView.images.clear();
				for(int i = 0; i < cookbookList.size(); i++)
				{
					Cookbook_ShelfListView.values.add(cookbookList.get(i).getName());
					Cookbook_ShelfListView.ids.add(cookbookList.get(i).getUniqueid());
					Cookbook_ShelfListView.images.add(cookbookList.get(i).getImage());
				}
				//If the list is under 6 then create empty rows to fill the layout of the app
				if(cookbookList.size() < 6)
				{
					int num = 6 - cookbookList.size();
					for(int a = 0; a < num; a++)
					{
						byte[] emptyarr = new byte[0];
						Cookbook_ShelfListView.values.add("");
						Cookbook_ShelfListView.ids.add("");
						Cookbook_ShelfListView.images.add(emptyarr);
					}
				}
				Cookbook_ShelfListView.adapter.notifyDataSetChanged();
			}
		}
		else if(response.equals("fail"))
		{
			Toast.makeText(context, 
					"App sync failed", Toast.LENGTH_LONG).show();
		}
	}
}
