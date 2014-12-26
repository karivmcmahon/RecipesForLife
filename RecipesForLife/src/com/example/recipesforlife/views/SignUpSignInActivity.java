package com.example.recipesforlife.views;

import java.io.IOException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.databaseConnection;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SignUpSignInActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signupsigninactivity);
		Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		TextView titleView = (TextView) findViewById(R.id.textView1);
		titleView.setTypeface(typeFace);
		titleView.setTextSize(24);
		titleView.setTextColor(Color.parseColor("#FFFFFFFF"));
		buildDatabase();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// building the database.
		public void buildDatabase() 
		{
			databaseConnection dbConnection = new databaseConnection(this);
			//myDbHelper.deleteDatabase();
			try {
				dbConnection.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
		}

}
