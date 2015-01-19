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
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends Activity {
	
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;
	util utils;
	Typeface typeFace;
	
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
				    MainActivity.this, R.layout.item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner sItems = (Spinner) dialog.findViewById(R.id.recipeBookSpinner);
				sItems.setAdapter(adapter);
				
				 typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
				Button dialogButton = (Button) dialog.findViewById(R.id.nextButton);
				dialogButton.setTypeface(typeFace);
				dialogButton.setTextSize(22);
				dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
				dialogButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						EditText recipeName = (EditText) dialog.findViewById(R.id.recipenameEditText);
						String name = recipeName.getText().toString();
						EditText recipeDesc = (EditText) dialog.findViewById(R.id.recipeDescEdit);
						String desc = recipeDesc.getText().toString();
						Spinner spinner = (Spinner) dialog.findViewById(R.id.recipeBookSpinner);
						String recipeBook = spinner.getSelectedItem().toString();
						dialog.dismiss();
						
						
						final Dialog dialog2 = new Dialog(MainActivity.this);
						dialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog2.setContentView(R.layout.recipe2dialog);
						dialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
						utils.setDialogText(R.id.recipeAddView2, dialog2, 22);
						utils.setDialogText(R.id.recipeIngredsView, dialog2, 22);
						utils.setDialogText(R.id.recipeStepsView, dialog2, 22);
						utils.setDialogText(R.id.recipeServesView, dialog2, 22);
						utils.setDialogText(R.id.recipePrepView, dialog2, 22);
						utils.setDialogText(R.id.recipeCookingView, dialog2, 22);
						Button dialogButton = (Button) dialog2.findViewById(R.id.nextButton);
						dialogButton.setTypeface(typeFace);
						dialogButton.setTextSize(22);
						dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
						dialog2.show();
						
						ImageButton ingredsButton = (ImageButton) dialog2.findViewById(R.id.ingredsAddButton);
						
						ingredsButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								final Dialog dialog3 = new Dialog(MainActivity.this);
								dialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog3.setContentView(R.layout.ingredsdialog);
								dialog3.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
								utils.setDialogText(R.id.addIngredientView, dialog3, 22);
								utils.setDialogText(R.id.ingredsView, dialog3, 22);
								utils.setDialogText(R.id.valueView, dialog3, 22);
								utils.setDialogText(R.id.amountView, dialog3, 22);
								utils.setDialogText(R.id.noteView, dialog3, 22);
								Button dialogButton = (Button) dialog3.findViewById(R.id.addIngredButton);
								dialogButton.setTypeface(typeFace);
								dialogButton.setTextSize(22);
								dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
								List<String> spinnerArray =  new ArrayList<String>();
								spinnerArray.add("item1");
								spinnerArray.add("item2");

								ArrayAdapter<String> adapter = new ArrayAdapter<String>(
								    MainActivity.this, R.layout.item, spinnerArray);

								adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
								Spinner sItems = (Spinner) dialog3.findViewById(R.id.valueSpinner);
								sItems.setAdapter(adapter);
								dialog3.show();
								
							}});
						
						ImageButton stepsButton = (ImageButton) dialog2.findViewById(R.id.stepsAddButton);
						stepsButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								final Dialog dialog4 = new Dialog(MainActivity.this);
								dialog4.requestWindowFeature(Window.FEATURE_NO_TITLE);
								dialog4.setContentView(R.layout.methoddialog);
								dialog4.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
								utils.setDialogText(R.id.stepNumView,dialog4,22);
								utils.setDialogText(R.id.stepView, dialog4, 22);
								utils.setDialogText(R.id.addStepView, dialog4, 22);
								Button dialogButton = (Button) dialog4.findViewById(R.id.addStepButton);
								dialogButton.setTypeface(typeFace);
								dialogButton.setTextSize(22);
								dialogButton.setTextColor(Color.parseColor("#FFFFFFFF"));
								dialog4.show();
								
							}
							
						});
						
						dialogButton.setOnClickListener(new OnClickListener()
						{

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								EditText ingredsEdit = (EditText) dialog2.findViewById(R.id.recipeIngredsEditText);
								String ingreds = ingredsEdit.getText().toString();
								EditText stepsEdit = (EditText) dialog2.findViewById(R.id.recipeStepsEditText);
								String steps = stepsEdit.getText().toString();
								EditText servesEdit = (EditText) dialog2.findViewById(R.id.recipeServesEditText);
								String serves = servesEdit.getText().toString();
								EditText prepEdit = (EditText) dialog2.findViewById(R.id.recipePrepEditText);
								String prep = prepEdit.getText().toString();
								EditText cookingEdit = (EditText) dialog2.findViewById(R.id.recipeCookingEditText);
								String cooking = cookingEdit.getText().toString();
								Log.v("DETS", "DETS " + ingreds + " " + steps + " " + serves + " " + prep + " " + cooking);
							
								
							}
							
							
						});
						
					}});
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
