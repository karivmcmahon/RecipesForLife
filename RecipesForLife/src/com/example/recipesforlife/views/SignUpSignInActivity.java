package com.example.recipesforlife.views;

import java.io.IOException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.databaseConnection;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
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
		
		TextView emailView = (TextView) findViewById(R.id.emailView);
		emailView.setTypeface(typeFace);
		emailView.setTextColor(Color.parseColor("#FFFFFFFF"));
		
		TextView  passwordView = (TextView) findViewById(R.id.passwordView);
		passwordView.setTypeface(typeFace);
		passwordView.setTextColor(Color.parseColor("#FFFFFFFF"));
		
		Button  button = (Button) findViewById(R.id.button1);
		button.setTypeface(typeFace);
		button.setTextColor(Color.parseColor("#FFFFFFFF"));
		
		TextView  signupView = (TextView) findViewById(R.id.signUpView);
		signupView.setText(Html.fromHtml("<p><u>Click here to sign up</u></p>"));
		signupView.setTypeface(typeFace);
		signupView.setTextColor(Color.parseColor("#FFFFFFFF"));
		signupView.setOnTouchListener(new OnTouchListener() {

		

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				Log.v("CLICK", "CLICK");
				return false;
			}
			});
		
		
		
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
