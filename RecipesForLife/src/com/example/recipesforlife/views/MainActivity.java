package com.example.recipesforlife.views;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.*;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends Activity {
	
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;
	util utils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		utils = new util(getApplicationContext(), this);
		
	
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(MainActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(R.layout.recipe1dialog);
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				utils.setDialogText(R.id.recipeAddView,dialog,22);
				utils.setDialogText(R.id.recipeBookView,dialog,22);
				utils.setDialogText(R.id.recipeNameView,dialog,22);
				utils.setDialogText(R.id.recipeDescView,dialog,22);
				List<String> spinnerArray =  new ArrayList<String>();
				spinnerArray.add("item1");
				spinnerArray.add("item2");

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				    MainActivity.this, android.R.layout.simple_spinner_item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner sItems = (Spinner) dialog.findViewById(R.id.recipeBookSpinner);
				sItems.setAdapter(adapter);
				
				dialog.show();
			}
			
		});
		
		
		
		
		Button logOff = (Button) findViewById(R.id.logOffButton);
		logOff.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  SharedPreferences sharedpreferences = getSharedPreferences
					      (SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
					      Editor editor = sharedpreferences.edit();
					      editor.clear();
					      editor.commit();
					     Intent i = new Intent(MainActivity.this, SignUpSignInActivity.class);
					     startActivity(i);
				
			}
			
		});
		
	}
	
	
	
	
	 @Override
	   protected void onResume() {
		   super.onResume();
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, 
	      Context.MODE_PRIVATE);
	      
	    		  
	    		 if(utils.checkInternetConnection(getApplicationContext()))
	    			{
	    				syncModel sync = new syncModel(getApplicationContext());
	    				try {
	    					sync.getAndCreateAccountJSON();
	    					sync.getJSONFromServer();
	    					sharedpreferences = getApplicationContext().getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	    					 Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
	    					
	    					Calendar cal = Calendar.getInstance(); // creates calendar
	    		            cal.setTime(new Date()); // sets calendar time/date
	    		            Date today = cal.getTime();
	    		            String lastUpdated = utils.dateToString(today);
	    					Editor editor = sharedpreferences.edit();
	    			        editor.putString("Date", lastUpdated);
	    			        editor.commit();
	    			        Toast.makeText(getApplicationContext(), 
	    			        	    "App synced", Toast.LENGTH_LONG).show();
	    				} catch (JSONException e) {
	    					//uto-generated catch block
	    					e.printStackTrace();
	    					
	    					
	    				}
	    			
	    	
	    				
	    	  
	     }
	      
	   }
	
	
	

}
