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
		setText(R.id.textView1); 	
		setText(R.id.emailView);	
		setText(R.id.passwordView);
		setButtonText(R.id.button1);
		
		TextView view = (TextView) findViewById(R.id.textView1);
		view.setTextSize(24);
			
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
				dialog.setContentView(R.layout.signupcustomdialog);
				dialog.getWindow().setBackgroundDrawable(d);
		
				setDialogText(R.id.nameView,dialog);
				setDialogText(R.id.emailView,dialog);
				setDialogText(R.id.passwordView,dialog);
				setDialogText(R.id.createView,dialog);
							
				dialog.show(); 
				
				Button dialogButton = (Button) dialog.findViewById(R.id.nextButton);
				dialogButton.setTypeface(typeFace);
				dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
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
						
						setDialogText(R.id.additionalView,nextDialog);
						setDialogText(R.id.cityView,nextDialog);
						setDialogText(R.id.countryView,nextDialog);
						setDialogText(R.id.bioView,nextDialog);
						setDialogText(R.id.interestView,nextDialog);
						
			
						
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
		
		public void setText(int resource)
		{
			TextView view = (TextView) findViewById(resource);
			view.setTypeface(typeFace);
			view.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		
		public void setButtonText(int resource)
		{
			Button  button = (Button) findViewById(resource);
			button.setTypeface(typeFace);
			button.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		
		public void setDialogText(int resource, Dialog dialog)
		{
			TextView view = (TextView)	 dialog.findViewById(resource);
			view.setTypeface(typeFace);
			view.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		

}
