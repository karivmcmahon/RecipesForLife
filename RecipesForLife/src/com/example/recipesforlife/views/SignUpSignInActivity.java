package com.example.recipesforlife.views;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.databaseConnection;
import com.example.recipesforlife.models.syncModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Class that handles the sign in/sign up page that is showed initially
 * @author Kari
 *
 */
public class SignUpSignInActivity extends Activity {
	//Edit text stores
	EditText emailEdit,nameEdit,passwordEdit,countryEdit, cityEdit, interestEdit, bioEdit;
	//Strings to store info from edit text box
	String email,name,password,country,city,interest,bio;
	//List to store account information
	List<String> account;
	//Typeface to change to custom font
	Typeface typeFace;
	//Shared prefs to store log in data
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	util utils;
	int counter;
	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		counter = 0;
		//View for activity
		setContentView(R.layout.signupsigninactivity);
		utils = new util(getApplicationContext(), this);
		//Get shared pref
		sharedpreferences = this.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//If false its the first time been created - set initial sync time
		if (sharedpreferences.getBoolean("firstTime",false) == false) {
			Editor editor = sharedpreferences.edit();
			editor.putBoolean("firstTime", false);
			editor.commit();
			Editor editor2 = sharedpreferences.edit();
			editor2.putString("Date", "2015-01-01 12:00:00");
			editor2.commit();

		} else {
			Editor editor = sharedpreferences.edit();
			editor.putBoolean("firstTime", true);
			editor.commit();
		}
		//Style for activity
		typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		utils.setText(R.id.textView1, 28); 	
		utils.setText(R.id.emailView, 22);	
		utils.setText(R.id.passwordView, 22);
		utils.setButtonText(R.id.button1, 22);


		//Sign in button
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{	
				//Check login details
				checkLogin();
			}

		});

		//Style and on touch listener for create account
		TextView  signupView = (TextView) findViewById(R.id.signUpView);
		signupView.setText(Html.fromHtml("<p><u>Create an account</u></p>"));
		signupView.setTypeface(typeFace);
		signupView.setTextSize(22);
		signupView.setTextColor(Color.parseColor("#FFFFFFFF"));
		signupView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction()  == MotionEvent.ACTION_DOWN)
				{
					account = new ArrayList<String>();
					//Creates a custom dialog
					final Dialog dialog = new Dialog(SignUpSignInActivity.this);
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.signupcustomdialog);
					dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
					//Set dialogs style
					utils.setDialogText(R.id.nameView,dialog,22);
					utils.setDialogText(R.id.emailView,dialog,22);
					utils.setDialogText(R.id.passwordView,dialog,22);
					utils.setDialogText(R.id.createView,dialog,28);
					//Show dialog
					dialog.show(); 

					//Next button on dialog
					Button dialogButton = (Button) dialog.findViewById(R.id.nextButton);
					dialogButton.setTypeface(typeFace);
					dialogButton.setTextSize(22);
					dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							counter++;
							//get the info from the dialog
							boolean show = getInitialDialogInfo(dialog);
							if(show == true)
							{
							//Show another dialog
							final Dialog nextDialog = new Dialog(SignUpSignInActivity.this);
							nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
							//Drawable d = new ColorDrawable(Color.parseColor("#A6A6A6"));
							//d.setAlpha(80);
							nextDialog.setContentView(R.layout.signupnextcustomdialog);
							nextDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
							
							//Set style
							utils.setDialogText(R.id.additionalView,nextDialog,28);
							utils.setDialogText(R.id.cityView,nextDialog,22);
							utils.setDialogText(R.id.countryView,nextDialog,22);
							utils.setDialogText(R.id.bioView,nextDialog,22);
							utils.setDialogText(R.id.interestView,nextDialog,22);
							//Set button click
							Button nextDialogButton = (Button) nextDialog.findViewById(R.id.signUpButton);
							nextDialogButton.setTypeface(typeFace);
							nextDialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
							nextDialogButton.setTextSize(22);
							nextDialogButton.setOnClickListener(new OnClickListener()
							{
								@Override
								public void onClick(View v) 
								{
									getSecondDialogInfo(nextDialog);
								}

							});					
							nextDialog.show();						
						}
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
		sync();
	}
		
		

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
		/**
		 * On resume takes user to activity if logged in
		 */
	   @Override
	   protected void onResume() {
		   super.onResume();
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, 
	      Context.MODE_PRIVATE);
	    //Style for activity
			typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
	 //     if (sharedpreferences.contains(emailk))
	 //     {
	    	 // if(sharedpreferences.contains(pass))
	    	 // {   
	    		//  sync();
	    		  			
		    	  Intent i = new Intent(SignUpSignInActivity.this, MainActivity.class);
			      startActivity(i);
	    	 // }
	     //}
	   }
	      
	   
	   /**
	    * Syncs the databases
	    */
	   public void sync()
	   {
		   if(utils.checkInternetConnection(getApplicationContext()))
			{
				syncModel sync = new syncModel(getApplicationContext());
				try {
					
					sync.getAndCreateAccountJSON();
					sync.getJSONFromServer();
					sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
					 Log.v("LAST UPDATE", "LAST UPDATE " + sharedpreferences.getString("Date", "DEFAULT"));
					
					Calendar cal = Calendar.getInstance(); // creates calendar
		            cal.setTime(new Date()); // sets calendar time/dat
		            Date today = cal.getTime();
		            String lastUpdated = utils.dateToString(today);
					Editor editor = sharedpreferences.edit();
			        editor.putString("Date", lastUpdated);
			        editor.commit();
			        Toast.makeText(getApplicationContext(), 
			        	    "App synced", Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
								
				}
			}
	   }
	
	 	/**
	 	 * Build database
	 	 */
		public void buildDatabase() 
		{
			databaseConnection dbConnection = new databaseConnection(this);
			dbConnection.deleteDatabase();
			try {
				dbConnection.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
				
				
			}
		}
		
		
		
		
		
		
		/**
		 * Checks the details entered is a valid account and logs them in
		 */
		public void checkLogin()
		{
			//Get text from edit textbox
			EditText emailEdit = (EditText) findViewById(R.id.editText1);
			EditText passwordEdit = (EditText) findViewById(R.id.editText2);
			String email = emailEdit.getText().toString();
			String password = passwordEdit.getText().toString();
			//Check if these details are correct
			Context t = getApplicationContext();
			accountModel accountmodel = new accountModel(t);
			boolean access = accountmodel.logIn(email, password);
			//If allowed access save pref
			if(access == true)
			{
			  //Store details in shared preferences
			  Editor editor = sharedpreferences.edit();
		      editor.putString(emailk, email);
		      editor.putString(pass, password);
		      editor.commit();
		      //Start activity
		      Intent i = new Intent(SignUpSignInActivity.this, MainActivity.class);
		      startActivity(i);
			}
			else
			{
				final Dialog dialog = new Dialog(SignUpSignInActivity.this);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//Drawable d = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
				//d.setAlpha(80);
				dialog.setContentView(R.layout.textviewdialog);
				dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
				//Set dialogs style
				utils.setDialogText(R.id.textView,dialog,18);
				TextView txtView =  (TextView) dialog.findViewById(R.id.textView);
				txtView.setText("Error : The details you have entered are incorrect, please try again");
				
				//Show dialog
				dialog.show(); 
				Button button = (Button) dialog.findViewById(R.id.okButton);
				button.setTypeface(typeFace);
				button.setTextSize(22);
				button.setTextColor(Color.parseColor("#FFFFFFFF"));
				button.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						dialog.dismiss();
					}});
			}
		}
		
		/**
		 * Get information from the first dialog box when creating account
		 * @param dialog
		 */
		public boolean getInitialDialogInfo(Dialog dialog)
		{
			//Get info from edit text box
			emailEdit = (EditText) dialog.findViewById(R.id.emailEdit);
			nameEdit = (EditText) dialog.findViewById(R.id.nameEdit);
			passwordEdit = (EditText) dialog.findViewById(R.id.passwordEdit);
			TextView errorView = (TextView) dialog.findViewById(R.id.errorView);
			
			utils.setDialogText(R.id.errorView,dialog,16);
			errorView.setTextColor(Color.parseColor("#F70521"));
			name= nameEdit.getText().toString();
			password = passwordEdit.getText().toString();
			email = emailEdit.getText().toString(); 
			Context t = getApplicationContext();
			accountModel accountmodel = new accountModel(t);
			if(accountmodel.checkEmail(email) == true)
			{
				errorView.setText("Email already in use \n");
			}
			else if(email.equals(""))
			{
				errorView.setText("Please enter an email address \n" );
			}
			else if(password.equals("") )
			{
				errorView.setText("Please enter a password \n");
			}
			else if(name.equals("") )
			{
				errorView.setText("Please enter a name \n");
			}
			
			else
			{
				dialog.dismiss();
				return true;
			}
			return false;
		}
		
		/**
		 * Get information from the second dialog box when creating account
		 * Adds to a list and then sends to model to insert into database
		 * @param nextDialog
		 */
		public void getSecondDialogInfo(Dialog nextDialog)
		{
			//Get info from textboxes
			cityEdit = (EditText) nextDialog.findViewById(R.id.cityEdit);
			countryEdit = (EditText) nextDialog.findViewById(R.id.countryEdit);
			bioEdit = (EditText) nextDialog.findViewById(R.id.bioEditText);
			interestEdit = (EditText) nextDialog.findViewById(R.id.interestEditText);
			TextView errorView = (TextView) nextDialog.findViewById(R.id.errorView);
			utils.setDialogText(R.id.errorView,nextDialog,16);
			errorView.setTextColor(Color.parseColor("#F70521"));
			city = cityEdit.getText().toString();
			country = countryEdit.getText().toString();
			bio = bioEdit.getText().toString();
			interest = interestEdit.getText().toString();
			if(country.equals(""))
			{
				errorView.setText("Please enter a country \n");
			}
			else
			{
				//Add info to list
				account.add(name);
				account.add(name);
				account.add(country);
				account.add(bio);
				account.add(city);
				account.add(interest);
				account.add(email);
				account.add(password);	
				//Insert to db
				try
				{
					Context t = getApplicationContext();
					accountModel accountmodel = new accountModel(t);
					accountmodel.insertAccount(account);
					nextDialog.dismiss();
					
				}
				catch(Exception e)
				{
					Log.v("Error ", "Error with account insert. Exception " + e);
					errorView.setText("Error creating account");
					
				}
			}
		}
		
		
	}
		


