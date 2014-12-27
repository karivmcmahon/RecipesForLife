package com.example.recipesforlife.views;

import java.io.IOException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.databaseConnection;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SignUpSignInActivity extends Activity {
	
	Typeface typeFace;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signupsigninactivity);
		typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
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
		signupView.setText(Html.fromHtml("<p><u>Create an account</u></p>"));
		signupView.setTypeface(typeFace);
		signupView.setTextColor(Color.parseColor("#FFFFFFFF"));
		signupView.setOnTouchListener(new OnTouchListener() {

		

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()  == MotionEvent.ACTION_DOWN)
				{
				// TODO Auto-generated method stubS
				final Dialog dialog = new Dialog(SignUpSignInActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				Drawable d = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
				d.setAlpha(80);


				//dialog.getWindow().setBackgroundDrawableResource(R.drawable.shape);
				dialog.setContentView(R.layout.signupcustomdialog);
				dialog.getWindow().setBackgroundDrawable(d);
		
	 
				TextView nameV = (TextView)	 dialog.findViewById(R.id.nameView);
				nameV.setTypeface(typeFace);
				nameV.setTextColor(Color.parseColor("#FFFFFFFF"));
				
				TextView emailV = (TextView) dialog.findViewById(R.id.emailView);
				emailV.setTypeface(typeFace);
				emailV.setTextColor(Color.parseColor("#FFFFFFFF"));
				
				TextView passwordV = (TextView)	 dialog.findViewById(R.id.passwordView);
				passwordV.setTypeface(typeFace);
				passwordV.setTextColor(Color.parseColor("#FFFFFFFF"));
				
				TextView createV = (TextView)	 dialog.findViewById(R.id.createView);
				createV.setTypeface(typeFace);
				createV.setTextSize(20);
				createV.setTextColor(Color.parseColor("#FFFFFFFF"));
				
				dialog.show(); 
				Button dialogButton = (Button) dialog.findViewById(R.id.nextButton);
				dialogButton.setTypeface(typeFace);
				dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
				// if button is clicked, close the custom dialog
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
						final Dialog nextDialog = new Dialog(SignUpSignInActivity.this);
						nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						Drawable d = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
						d.setAlpha(80);
						nextDialog.setContentView(R.layout.signupnextcustomdialog);
						nextDialog.getWindow().setBackgroundDrawable(d);
						
						TextView additionalV = (TextView)	 nextDialog.findViewById(R.id.additionalView);
						additionalV.setTypeface(typeFace);
						additionalV.setTextColor(Color.parseColor("#FFFFFFFF"));
						
						TextView cityV = (TextView)	 nextDialog.findViewById(R.id.cityView);
						cityV.setTypeface(typeFace);
						cityV.setTextColor(Color.parseColor("#FFFFFFFF"));
						
						TextView countryV = (TextView)	 nextDialog.findViewById(R.id.countryView);
						countryV.setTypeface(typeFace);
						countryV.setTextColor(Color.parseColor("#FFFFFFFF"));
						
						TextView bioV = (TextView)	 nextDialog.findViewById(R.id.bioView);
						bioV.setTypeface(typeFace);
						bioV.setTextColor(Color.parseColor("#FFFFFFFF"));
						
						TextView interestV = (TextView)	 nextDialog.findViewById(R.id.interestView);
						interestV.setTypeface(typeFace);
						interestV.setTextColor(Color.parseColor("#FFFFFFFF"));
						
						Button nextDialogButton = (Button) nextDialog.findViewById(R.id.signUpButton);
						nextDialogButton.setTypeface(typeFace);
						nextDialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
						nextDialogButton.setOnClickListener(new OnClickListener()
								{

									@Override
									public void onClick(View v) {
										nextDialog.dismiss();
										
									}
							
								});
						
						
						nextDialog.show();
						
					}
				});  
				
			
	 
				
				return false; 
				}
				else
				{
					return false;
				}
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
