package com.example.recipesforlife.models;

import android.os.AsyncTask;
import android.util.Log;

 public class PostTask extends AsyncTask<String, Integer, String> {
	 
	util utils;
	 public PostTask(util  utils)
	 {
		 this.utils = utils;
	 }
	 
	   @Override
	public String doInBackground(String... arg0 ) {
	     
	 Log.v("Run in bg", "Run in bg");
	   
	         utils.sync();
	      
	      Log.v("All Done!","ALL DONE");
	      return "done";
	   }

	
	   
	   
	 
	
	   }
