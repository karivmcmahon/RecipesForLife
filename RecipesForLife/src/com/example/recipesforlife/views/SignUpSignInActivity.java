package com.example.recipesforlife.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.accountBean;
import com.example.recipesforlife.controllers.userBean;
import com.example.recipesforlife.models.PostTask;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.databaseConnection;
import com.example.recipesforlife.models.util;

/**
 * Class that handles the sign in/sign up page that is showed initially
 * 
 * @author Kari
 * 
 */
public class SignUpSignInActivity extends Activity {
	// Strings to store info from edit text box
	String email, name, password, country, city, interest, bio;
	// List to store account information
	List<String> account;
	accountBean accountBean;
	userBean userBean;
	// Typeface to change to custom font
	Typeface typeFace;
	// Shared prefs to store log in data
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey";
	public static final String pass = "passwordKey";
	util utils;
	int counter;
	SharedPreferences sharedpreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
		.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		counter = 0;
		setContentView(R.layout.signupsigninactivity);
		utils = new util(getApplicationContext(), this);
		accountBean = new accountBean();
		userBean = new userBean();
		// Get shared pref
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		if (sharedpreferences.getBoolean("firstTime", false) == false) {
			Editor editor = sharedpreferences.edit();
			editor.putBoolean("firstTime", true);
			editor.commit();
			Editor editor2 = sharedpreferences.edit();
			editor2.putString("Date", "2015-01-01 12:00:00");
			editor2.commit();
			Editor editor3 = sharedpreferences.edit();
			editor3.putString("Date Server", "2015-01-01 12:00:00");
			editor3.commit();
			editor2.putString("Account Date", "2015-01-01 12:00:00");
			editor2.commit();
			editor3.putString("Account Date Server", "2015-01-01 12:00:00");
			editor3.commit();
			editor3.putString("Change", "2015-01-01 12:00:00");
			editor3.commit();
			editor3.putString("Change Server", "2015-01-01 12:00:00");
			editor3.commit();
			editor3.putString("Cookbook", "2015-01-01 12:00:00");
			editor3.commit();
			editor3.putString("Cookbook Server", "2015-01-01 12:00:00");
			editor3.commit();
			buildDatabase();
			new PostTask(utils).doInBackground("");
		} else {
			new PostTask(utils).doInBackground("");
		}
		// Style for activity
		typeFace = Typeface.createFromAsset(getAssets(), "fonts/elsie.ttf");
		utils.setText(R.id.textView1, 28);
		utils.setText(R.id.emailView, 22);
		utils.setText(R.id.passwordView, 22);
		utils.setButtonText(R.id.button1, 22);

		// Sign in button
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Check login details
				checkLogin();
			}

		});

		// Style and on touch listener for create account
		TextView signupView = (TextView) findViewById(R.id.signUpView);
		signupView.setText(Html.fromHtml("<p><u>Create an account</u></p>"));
		signupView.setTypeface(typeFace);
		signupView.setTextSize(22);
		signupView.setTextColor(Color.parseColor("#FFFFFFFF"));
		signupView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					account = new ArrayList<String>();
					// Creates a custom dialog
					final Dialog dialog = utils.createDialog(SignUpSignInActivity.this, R.layout.signupcustomdialog);

					// Set dialogs style
					utils.setDialogText(R.id.nameView, dialog, 22);
					utils.setDialogText(R.id.emailView, dialog, 22);
					utils.setDialogText(R.id.passwordView, dialog, 22);
					utils.setDialogText(R.id.createView, dialog, 28);
					// Show dialog
					dialog.show();

					// Next button on dialog
					Button dialogButton = utils.setButtonTextDialog(R.id.nextButton, 22, dialog);
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							counter++;
							// get the info from the dialog
							boolean show = getInitialDialogInfo(dialog);
							if (show == true) {
								// Show another dialog
								final Dialog nextDialog = utils.createDialog(
										SignUpSignInActivity.this, R.layout.signupnextcustomdialog);

								// Set style
								utils.setDialogText(R.id.additionalView,
										nextDialog, 28);
								utils.setDialogText(R.id.cityView, nextDialog,
										22);
								utils.setDialogText(R.id.countryView,
										nextDialog, 22);
								utils.setDialogText(R.id.bioView, nextDialog,
										22);
								utils.setDialogText(R.id.interestView,
										nextDialog, 22);
								// Set button click
								Button nextDialogButton = utils.setButtonTextDialog(R.id.signUpButton, 22, nextDialog);
								nextDialogButton
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										getSecondDialogInfo(nextDialog);
									}

								});
								nextDialog.show();
							}
						}
					});

					return false;
				} else {
					return false;
				}
			}
		});
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
		sharedpreferences = getSharedPreferences(MyPREFERENCES,
				Context.MODE_PRIVATE);
		if (sharedpreferences.contains(emailk)) {
			if (sharedpreferences.contains(pass)) {

				Intent i = new Intent(SignUpSignInActivity.this,
						MainActivity.class);
				startActivity(i);
			}
		}
	}

	/**
	 * Build database
	 */
	public void buildDatabase() {
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
	public void checkLogin() {
		String email = utils.getText(R.id.editText1);
		String password = utils.getText(R.id.editText2);
		// Check if these details are correct
		Context t = getApplicationContext();
		accountModel accountmodel = new accountModel(t);
		boolean access = accountmodel.logIn(email, password);
		// If allowed access save pref
		if (access == true) {
			// Store details in shared preferences
			Editor editor = sharedpreferences.edit();
			editor.putString(emailk, email);
			editor.putString(pass, password);
			editor.commit();
			// Start activity
			Intent i = new Intent(SignUpSignInActivity.this, MainActivity.class);
			startActivity(i);
		} else {
			final Dialog dialog = utils.createDialog(SignUpSignInActivity.this, R.layout.textviewdialog);

			// Set dialogs style
			utils.setDialogText(R.id.textView, dialog, 18);
			TextView txtView = (TextView) dialog.findViewById(R.id.textView);
			txtView.setText("Error : The details you have entered are incorrect, please try again");

			// Show dialog
			dialog.show();
			Button button = utils.setButtonTextDialog(R.id.okButton, 22, dialog);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		}
	}

	/**
	 * Get information from the first dialog box when creating account
	 * 
	 * @param dialog
	 */
	public boolean getInitialDialogInfo(Dialog dialog) {

		TextView errorView = (TextView) dialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView, dialog, 16);
		errorView.setTextColor(Color.parseColor("#F70521"));

		email = utils.getTextFromDialog(R.id.emailEdit, dialog);
		password = utils.getTextFromDialog(R.id.passwordEdit, dialog);
		name = utils.getTextFromDialog(R.id.nameEdit, dialog);

		Context t = getApplicationContext();
		accountModel accountmodel = new accountModel(t);
		if (accountmodel.checkEmail(email) == true) {
			errorView.setText("Email already in use \n");
		} else if (email.equals("")) {
			errorView.setText("Please enter an email address \n");
		} else if (password.equals("")) {
			errorView.setText("Please enter a password \n");
		} else if (name.equals("")) {
			errorView.setText("Please enter a name \n");
		}

		else {
			dialog.dismiss();
			return true;
		}
		return false;
	}

	/**
	 * Get information from the second dialog box when creating account Adds to
	 * a list and then sends to model to insert into database
	 * 
	 * @param nextDialog
	 */
	public void getSecondDialogInfo(Dialog nextDialog) {
		// Get info from textboxes
		TextView errorView = (TextView) nextDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView, nextDialog, 16);
		errorView.setTextColor(Color.parseColor("#F70521"));

		city =utils.getTextFromDialog(R.id.cityEdit, nextDialog);
		country = utils.getTextFromDialog(R.id.countryEdit, nextDialog);
		bio = utils.getTextFromDialog(R.id.bioEditText, nextDialog);
		interest = utils.getTextFromDialog(R.id.interestEditText, nextDialog);

		if (country.equals("")) {
			errorView.setText("Please enter a country \n");
		} else {
			// Add info to list
			accountBean.setEmail(email);
			accountBean.setPassword(password);
			userBean.setName(name);
			userBean.setCity(city);
			userBean.setBio(bio);
			userBean.setCountry(country);
			userBean.setCookingInterest(interest);
			// Insert to db
			try {
				accountModel accountmodel = new accountModel(getApplicationContext());
				accountmodel.insertAccount(accountBean, userBean);
				nextDialog.dismiss();

			} catch (Exception e) {
				Log.v("Error ", "Error with account insert. Exception " + e);
				errorView.setText("Error creating account");

			}
		}
	}

}
