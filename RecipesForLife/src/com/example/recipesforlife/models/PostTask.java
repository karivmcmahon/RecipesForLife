package com.example.recipesforlife.models;


import java.util.ArrayList;

import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.views.CookbookListActivity;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * AsyncTask code enables sync code to be done in the background
 * @author Kari
 *
 */
public class PostTask extends AsyncTask<Void, Void, String> {

	util utils;
	Intent i;
	Context context;
	String response = "";
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey";
	public PostTask(util  utils, Context context)
	{
		this.utils = utils;
		this.context = context;

	}

	@Override
	public String doInBackground(Void... arg0 ) {

		Log.v("Run in bg", "Run in bg");

		String message = utils.sync();

		Log.v("All Done!","ALL DONE");
		response = message;
		//publishProgress(1);
		return message;
		//return null;
	}
	
	// @Override
	/** protected void onProgressUpdate(Integer... integers) {
	        // Here you can execute what you want to execute
	        // after the background task completes
		 if(response.equals("success"))
		 {
		 Toast.makeText(context, 
			"App synced ", Toast.LENGTH_LONG).show();
		 }
		 else if(response.equals("fail"))
		 {
			 Toast.makeText(context, 
						"App sync failed", Toast.LENGTH_LONG).show();
		 }
	    } **/

	 @Override
	    protected void onPostExecute(String response) {
		 super.onPostExecute(response);
		 if(response.equals("success"))
		 {
		 Toast.makeText(context, 
			"App synced ", Toast.LENGTH_LONG).show();
		 SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
	 	cookbookModel model = new cookbookModel(context);
		 ArrayList<cookbookBean> cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
			CookbookListActivity.values.clear();
			CookbookListActivity.ids.clear();
			CookbookListActivity.images.clear();
			for(int i = 0; i < cookbookList.size(); i++)
			{
				CookbookListActivity.values.add(cookbookList.get(i).getName());
				CookbookListActivity.ids.add(cookbookList.get(i).getUniqueid());
				CookbookListActivity.images.add(cookbookList.get(i).getImage());
			}
			//If the list is under 6 then create empty rows to fill the layout of the app
			if(cookbookList.size() < 6)
			{
				int num = 6 - cookbookList.size();
				for(int a = 0; a < num; a++)
				{
					byte[] emptyarr = new byte[0];
					CookbookListActivity.values.add("");
					CookbookListActivity.ids.add("");
					CookbookListActivity.images.add(emptyarr);
				}
			}
			CookbookListActivity.adapter.notifyDataSetChanged();
		 }
		 else if(response.equals("fail"))
		 {
			 Toast.makeText(context, 
						"App sync failed", Toast.LENGTH_LONG).show();
		 }
	        //Toast.makeText(GameScreen_bugfix.this, "music loaded!", Toast.LENGTH_SHORT).show();
		 Log.v("All Done!","ALL DONE");
	    }






}
