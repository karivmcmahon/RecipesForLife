package com.example.recipesforlife.views;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.databaseConnection;
import com.example.recipesforlife.models.sync;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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

public class SignUpSignInActivity extends Activity {
	EditText emailEdit,nameEdit,passwordEdit,countryEdit, cityEdit, interestEdit, bioEdit;
	String email,name,password,country,city,interest,bio;
	List<String> account;
	Typeface typeFace;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	SharedPreferences sharedpreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

         StrictMode.setThreadPolicy(policy);
	//	sync syncs = new sync();
		/**syncs.getJson();
		try {
			syncs.createJsonArray();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}**/
		setContentView(R.layout.signupsigninactivity);
		
		//Style for activity
		typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
		setText(R.id.textView1, 28); 	
		setText(R.id.emailView, 22);	
		setText(R.id.passwordView, 22);
		setButtonText(R.id.button1, 22);
		
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				TextView emailEdit = (TextView) findViewById(R.id.editText1);
				TextView passwordEdit = (TextView) findViewById(R.id.editText2);
				String email = emailEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				Log.v("SIGN IN ", "Email " + email + " password " + password);
				Context t = getApplicationContext();
				accountModel accountmodel = new accountModel(t);
				boolean access = accountmodel.logIn(email, password);
				if(access == true)
				{
					Log.v("prefs saved","prefs saved");
					 Editor editor = sharedpreferences.edit();
				     
				      editor.putString(emailk, email);
				      editor.putString(pass, password);
				      editor.commit();
				}
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
				Drawable d = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
				d.setAlpha(80);
				dialog.setContentView(R.layout.signupcustomdialog);
				dialog.getWindow().setBackgroundDrawable(d);
				//Set dialogs style
				setDialogText(R.id.nameView,dialog,22);
				setDialogText(R.id.emailView,dialog,22);
				setDialogText(R.id.passwordView,dialog,22);
				setDialogText(R.id.createView,dialog,28);
	
				
				//Show dialogs
				dialog.show(); 
				
				//Next button on dialog
				Button dialogButton = (Button) dialog.findViewById(R.id.nextButton);
				dialogButton.setTypeface(typeFace);
				dialogButton.setTextSize(22);
				dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
				dialogButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						emailEdit = (EditText) dialog.findViewById(R.id.emailEdit);
						nameEdit = (EditText) dialog.findViewById(R.id.nameEdit);
						passwordEdit = (EditText) dialog.findViewById(R.id.passwordEdit);
						name= nameEdit.getText().toString();
						password = passwordEdit.getText().toString();
						email = emailEdit.getText().toString(); 
						
						Log.v("NAME ", "NAME " + name);
						//Dismiss dialog
						dialog.dismiss();
						//Show another dialog
						final Dialog nextDialog = new Dialog(SignUpSignInActivity.this);
						nextDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						Drawable d = new ColorDrawable(Color.parseColor("#FFFFFFFF"));
						d.setAlpha(80);
						nextDialog.setContentView(R.layout.signupnextcustomdialog);
						nextDialog.getWindow().setBackgroundDrawable(d);
						//Set style
						setDialogText(R.id.additionalView,nextDialog,28);
						setDialogText(R.id.cityView,nextDialog,22);
						setDialogText(R.id.countryView,nextDialog,22);
						setDialogText(R.id.bioView,nextDialog,22);
						setDialogText(R.id.interestView,nextDialog,22);
						
						
						
						
						
						
						//Set button click
						Button nextDialogButton = (Button) nextDialog.findViewById(R.id.signUpButton);
						nextDialogButton.setTypeface(typeFace);
						nextDialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
						nextDialogButton.setTextSize(22);
						nextDialogButton.setOnClickListener(new OnClickListener()
								{

									@Override
									public void onClick(View v) {
										cityEdit = (EditText) nextDialog.findViewById(R.id.cityEdit);
										countryEdit = (EditText) nextDialog.findViewById(R.id.countryEdit);
										bioEdit = (EditText) nextDialog.findViewById(R.id.bioEditText);
										interestEdit = (EditText) nextDialog.findViewById(R.id.interestEditText);
										city = cityEdit.getText().toString();
										country = countryEdit.getText().toString();
										bio = bioEdit.getText().toString();
										interest = interestEdit.getText().toString();
										
										account.add(name);
										account.add(name);
										account.add(country);
										account.add(bio);
										account.add(city);
										account.add(interest);
										account.add(email);
										account.add(password);
										
										//Insert to db
										Context t = getApplicationContext();
										accountModel accountmodel = new accountModel(t);
										accountmodel.insertAccount(account);
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
	
	 @Override
	   protected void onResume() {
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, 
	      Context.MODE_PRIVATE);
	      if (sharedpreferences.contains(emailk))
	      {
	      if(sharedpreferences.contains(pass)){
	        //LOG IN
	    	  Log.v("LOG IN", "LOG IN");
	      }
	      }
	      super.onResume();
	   }
	 
	 /**
	  * LOG out code future
	   public void logout(View view){
      SharedPreferences sharedpreferences = getSharedPreferences
      (MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);
      Editor editor = sharedpreferences.edit();
      editor.clear();
      editor.commit();
      moveTaskToBack(true); 
      Welcome.this.finish();
   }
	  */
	
	// building the database.
		public void buildDatabase() 
		{
			databaseConnection dbConnection = new databaseConnection(this);
		//	dbConnection.deleteDatabase();
			try {
				dbConnection.createDataBase();
			} catch (IOException ioe) {
				throw new Error("Unable to create database");
			}
		}
		
		public void setText(int resource,int fontSize)
		{
			TextView view = (TextView) findViewById(resource);
			view.setTypeface(typeFace);
			view.setTextSize(fontSize);
			view.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		
		public void setButtonText(int resource, int fontSize)
		{
			Button  button = (Button) findViewById(resource);
			button.setTypeface(typeFace);
			button.setTextSize(fontSize);
			button.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		
		public void setDialogText(int resource, Dialog dialog, int fontSize)
		{
			TextView view = (TextView)	 dialog.findViewById(resource);
			view.setTypeface(typeFace);
			view.setTextSize(fontSize);
			view.setTextColor(Color.parseColor("#FFFFFFFF"));
		}
		

}
