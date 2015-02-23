package com.example.recipesforlife.models;

import com.example.recipesforlife.views.MainActivity;
import com.example.recipesforlife.views.SignUpSignInActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * AsyncTask code enables sync code to be done in the background
 * @author Kari
 *
 */
public class PostTask extends AsyncTask<String, Integer, String> {

	util utils;
	Intent i;
	Context context;
	String response = "";
	public PostTask(util  utils, Context context)
	{
		this.utils = utils;
		this.context = context;

	}

	@Override
	public String doInBackground(String... arg0 ) {

		Log.v("Run in bg", "Run in bg");

		String message = utils.sync();

		Log.v("All Done!","ALL DONE");
		response = message;
		publishProgress(1);
		return message;
	}
	
	 @Override
	 protected void onProgressUpdate(Integer... integers) {
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
	    }







}
