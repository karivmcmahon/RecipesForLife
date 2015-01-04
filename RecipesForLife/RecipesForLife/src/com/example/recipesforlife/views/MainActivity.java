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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

public class MainActivity extends Activity {
	
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		
		
		
		
		
		
		
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
	      
	    		  
	    		 if(checkInternetConnection(getApplicationContext()))
	    			{
	    				syncModel sync = new syncModel(getApplicationContext());
	    				try {
	    					sync.getAndCreateAccountJSON();
	    					sync.getJSONFromServer();
	    					sharedpreferences = getApplicationContext().getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
	    					 Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
	    					
	    					Calendar cal = Calendar.getInstance(); // creates calendar
	    		            cal.setTime(new Date()); // sets calendar time/date
	    		            cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
	    		            Date today = cal.getTime();
	    		            String lastUpdated = dateToString(today);
	    					Editor editor = sharedpreferences.edit();
	    			        editor.putString("Date", lastUpdated);
	    			        editor.commit();
	    				} catch (JSONException e) {
	    					//uto-generated catch block
	    					e.printStackTrace();
	    					
	    					
	    				}
	    			
	    	
	    				
	    	  
	     }
	      
	   }
	
	
	 private boolean checkInternetConnection(Context context) {
			ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (conMgr.getActiveNetworkInfo() != null && conMgr.getActiveNetworkInfo().isAvailable()
					&& conMgr.getActiveNetworkInfo().isConnected()) {
				return true;
			} else {
				return false;
			}
		}
	private String dateToString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentDate = formatter.format(date);
		return currentDate;
	}

}
