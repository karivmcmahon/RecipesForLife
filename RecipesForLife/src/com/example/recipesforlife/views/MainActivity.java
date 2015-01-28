package com.example.recipesforlife.views;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.*;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;

public class MainActivity extends Activity  {
	
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	util utils;
	Typeface typeFace;
	Dialog recipeAddDialog , recipeAddDialog2, recipeIngredDialog, recipeAddStepDialog, addRecipeDialog3;
	Button nextButton, nextButton2, addIngredButton, addRecipeButton;
	ArrayList<String> ingredientList, amountList, valueList, noteList, stepNumList, stepList;
	String name, desc,recipeBook, serves, prep, cooking;

	    // Handles message from time dialog 1
	    Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message m){
	            
	            Bundle b = m.getData();
	            String hour = b.getString("hour");
	            String minute = b.getString("minute");
	            if(hour.length() == 1)
	            {
	            	hour = "0" + hour;
	            }
	            if(minute.length() == 1)
	            {
	            	minute = "0" + minute;
	            }
	            EditText edit = (EditText) recipeAddDialog2.findViewById(R.id.recipePrepEditText);
	            edit.setText(hour + ":" + minute);
	        }
	    };
	    
	    //Handles message from time dialog 2
	    Handler mHandler2 = new Handler(){
	        @Override
	        public void handleMessage(Message m){
	            Bundle b = m.getData();
	            String hour = b.getString("hour");
	            String minute = b.getString("minute");
	            if(hour.length() == 1)
	            {
	            	hour = "0" + hour;
	            }
	            if(minute.length() == 1)
	            {
	            	minute = "0" + minute;
	            }
	            EditText edit = (EditText) recipeAddDialog2.findViewById(R.id.recipeCookingEditText);
	            edit.setText(hour + ":" + minute);
	        }
	    };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.activity_main);
		utils = new util(getApplicationContext(), this);
		
		Button viewButton = (Button) findViewById(R.id.viewButton);
		viewButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent i = new Intent(MainActivity.this, RecipeViewActivity.class);
			      startActivity(i);
			}
			
			
		});
		
		Button viewListButton = (Button) findViewById(R.id.viewListButton);
		viewListButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, RecipeListViewActivity.class);
			      startActivity(i);
				
			}
			
		});
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				typeFace=Typeface.createFromAsset(getAssets(),"fonts/elsie.ttf");
				ingredientList = new ArrayList<String>();
				amountList = new ArrayList<String>();
				noteList = new ArrayList<String>();
				valueList = new ArrayList<String>();
				stepNumList = new ArrayList<String>();
				stepList = new ArrayList<String>();
				//Set up dialog style
				setUpInitialRecipeAddDialog();
				//Once next pressed
				nextButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						//Get data from the dialog
					    getInitialRecipeAddDialogData();
					}
					});
				recipeAddDialog.show();		
				
			}
		
			
		});
		
		
		
		//Log off app code
		Button logOff = (Button) findViewById(R.id.logOffButton);
		logOff.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				  SharedPreferences sharedpreferences = getSharedPreferences
					      (SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
					      Editor editor = sharedpreferences.edit();
					     editor.remove(emailk);
					     editor.remove(pass);
					     // editor.clear();
					      editor.commit();
					     Intent i = new Intent(MainActivity.this, SignUpSignInActivity.class);
					     startActivity(i);
				
			}
			
		});
		
	}
	
	/**
	 * Set up initial dialog style with correct fonts and spinner filled 
	 */
	public void setUpInitialRecipeAddDialog()
	{
		recipeAddDialog = new Dialog(MainActivity.this);
		recipeAddDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		recipeAddDialog.setContentView(R.layout.recipe1dialog);
		recipeAddDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		utils.setDialogText(R.id.recipeAddView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeBookView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeNameView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeDescView,recipeAddDialog,22);		
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("item1");
		spinnerArray.add("item2");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    MainActivity.this, R.layout.item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) recipeAddDialog.findViewById(R.id.recipeBookSpinner);
		sItems.setAdapter(adapter);
		nextButton = (Button) recipeAddDialog.findViewById(R.id.nextButton);
		nextButton.setTypeface(typeFace);
		nextButton.setTextSize(22);
		nextButton.setTextColor(Color.parseColor("#FFFFFFFF"));
	}
	
	/**
	 * Set up second dialog style with correct fonts and spinner filled 
	 */
	public void setUpSecondRecipeAddDialog()
	{
		recipeAddDialog2 = new Dialog(MainActivity.this);
		recipeAddDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
		recipeAddDialog2.setContentView(R.layout.recipe2dialog);
		recipeAddDialog2.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		utils.setDialogText(R.id.recipeAddView2, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeIngredsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeStepsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeServesView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipePrepView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeCookingView, recipeAddDialog2, 22);
		EditText editText = (EditText) recipeAddDialog2.findViewById(R.id.recipePrepEditText);
		editText.setOnTouchListener(new OnTouchListener(){
			//When clicked opens the timepicker fragment
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()  == MotionEvent.ACTION_DOWN)
				{
					  /** Instantiating DatePickerDialogFragment */
	                TimePickerFragment timePicker = new TimePickerFragment(mHandler);
	 
	               
	                /** Getting fragment manger for this activity */
	                android.app.FragmentManager fm = getFragmentManager();
	 
	                /** Starting a fragment transaction */
	                android.app.FragmentTransaction ft = fm.beginTransaction();
	 
	                /** Adding the fragment object to the fragment transaction */
	                ft.add(timePicker, "time_picker");
	 
	                /** Opening the DatePicker fragment */
	                ft.commit();
				}
				
				return false;
			}});
		EditText editText2 = (EditText) recipeAddDialog2.findViewById(R.id.recipeCookingEditText);
		editText2.setOnTouchListener(new OnTouchListener(){
			//When clicked opens the timepicker fragment
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction()  == MotionEvent.ACTION_DOWN)
				{
					 /** Instantiating DatePickerDialogFragment */
	                TimePickerFragment timePicker = new TimePickerFragment(mHandler2);
	 
	               
	                /** Getting fragment manger for this activity */
	                android.app.FragmentManager fm = getFragmentManager();
	 
	                /** Starting a fragment transaction */
	                android.app.FragmentTransaction ft = fm.beginTransaction();
	 
	                /** Adding the fragment object to the fragment transaction */
	                ft.add(timePicker, "time_picker");
	 
	                /** Opening the DatePicker fragment */
	                ft.commit();

				}
				return false;
			}});
		nextButton2 = (Button) recipeAddDialog2.findViewById(R.id.nextButton);
		nextButton2.setTypeface(typeFace);
		nextButton2.setTextSize(22);
		nextButton2.setTextColor(Color.parseColor("#FFFFFFFF"));
		recipeAddDialog2.show();
	}
	
	/**
	 * Get data from the first dialog
	 */
	public void getInitialRecipeAddDialogData()
	{
		TextView errorView = (TextView) recipeAddDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeAddDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		EditText recipeName = (EditText) recipeAddDialog.findViewById(R.id.recipenameEditText);
		name = recipeName.getText().toString();
		EditText recipeDesc = (EditText) recipeAddDialog.findViewById(R.id.recipeDescEdit);
	    desc = recipeDesc.getText().toString();
		Spinner spinner = (Spinner) recipeAddDialog.findViewById(R.id.recipeBookSpinner);
	    recipeBook = spinner.getSelectedItem().toString();
		Context context = getApplicationContext();
	    recipeModel model = new recipeModel(context);
	    SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		if(model.selectRecipe(name, sharedpreferences.getString(emailk, "")) == true)
		{
			errorView.setText("You already have a recipe with this name");
		}
		if(name.equals(""))
		{
			errorView.setText("Please enter a recipe name");
		}
		else if(desc.equals(""))
		{
			errorView.setText("Please enter a description");
		}
		else
		{
			recipeAddDialog.dismiss();
			setUpSecondRecipeAddDialog();
			
			
			
		    
			//If ingredient plus button is pressed - show a dialog to add an ingredient
			ImageButton ingredsPlusButton = (ImageButton) recipeAddDialog2.findViewById(R.id.ingredsAddButton);						
			ingredsPlusButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//Set up the dialog style
					setUpIngredAddDialog();
					//Once add is pressed
					Button addIngredButton = (Button) recipeIngredDialog.findViewById(R.id.addIngredButton);
					addIngredButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							//Get ingredient info
							getIngredient();
						}});
				}});
			
			//If steps plus button is pressed - show a dialog to add a step
			ImageButton stepsPlusButton = (ImageButton) recipeAddDialog2.findViewById(R.id.stepsAddButton);
			stepsPlusButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				    //Set up style
					setUpStepAddDialog();
					//Once add is pressed
					Button addStepButton = (Button) recipeAddStepDialog.findViewById(R.id.addStepButton);
					addStepButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							//Get info
							getRecipeStep();
						}});
				}
				
			});
			//Once next button is clicked
			nextButton2.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					//Get second data and send to the server
					getSecondDialogData();
					
				}
				
				
			});
			
		}
		
		
	}
	
	/**
	 * Setup ingredient add dialog with font and spinnner
	 */
	public void setUpIngredAddDialog()
	{
		recipeIngredDialog = new Dialog(MainActivity.this);
		recipeIngredDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		recipeIngredDialog.setContentView(R.layout.ingredsdialog);
		recipeIngredDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
		Button addIngredButton = (Button) recipeIngredDialog.findViewById(R.id.addIngredButton);
		addIngredButton.setTypeface(typeFace);
		addIngredButton.setTextSize(22);
		addIngredButton.setTextColor(Color.parseColor("#FFFFFFFF"));
		//Spinner set up with varying measurement amounts
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("teaspoon");
		spinnerArray.add("tablespoon");
		spinnerArray.add("cup");
		spinnerArray.add("kg");
		spinnerArray.add("g");
		spinnerArray.add("l");
		spinnerArray.add("ml");
		spinnerArray.add("oz");
		spinnerArray.add("pint");
		spinnerArray.add("quart");
		spinnerArray.add("gallon");
		spinnerArray.add("lb");
		spinnerArray.add("ounces");
		spinnerArray.add("pinch");
		spinnerArray.add(" ");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    MainActivity.this, R.layout.item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		sItems.setAdapter(adapter);
		recipeIngredDialog.show();
	}
	
	/**
	 * Set up step dialog with correct fonts
	 */
	public void setUpStepAddDialog()
	{
		 recipeAddStepDialog = new Dialog(MainActivity.this);
			recipeAddStepDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			recipeAddStepDialog.setContentView(R.layout.methoddialog);
			recipeAddStepDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
			utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
			utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
			Button addStepButton = (Button) recipeAddStepDialog.findViewById(R.id.addStepButton);
			addStepButton.setTypeface(typeFace);
			addStepButton.setTextSize(22);
			addStepButton.setTextColor(Color.parseColor("#FFFFFFFF"));
			recipeAddStepDialog.show();
	}
	
	/**
	 * Get second dialog data 
	 */
	public void getSecondDialogData()
	{
		//Getting text for edit text boxes
		TextView errorView = (TextView) recipeAddDialog2.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeAddDialog2,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		EditText servesEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeServesEditText);
	    serves = servesEdit.getText().toString();
		EditText prepEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipePrepEditText);
        prep = prepEdit.getText().toString();
		EditText cookingEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeCookingEditText);
	    cooking = cookingEdit.getText().toString();
	   
	    EditText ingredsEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeIngredsEditText);
	    EditText methodEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeStepsEditText);
	    String methods = methodEdit.getText().toString();
	    String i = ingredsEdit.getText().toString();
	 Log.v("I "," I " + i);
	    //Error catching before moving to next stage
	    if(i.equals(""))
	    {
	    	errorView.setText("Please enter ingredients");
	    }
	    else if(methods.equals(""))
	    {
	    	errorView.setText("Please enter methods");
	    }
	    else if(serves.equals(""))
	    {
	    	errorView.setText("Please enter a value for serves");
	    }
	    else if(prep.equals(""))
	    {
	    	errorView.setText("Please enter a value for prep time");
	    	
	    }
	    else if(cooking.equals(""))
	    {
	    	errorView.setText("Please enter a value for cooking time");
	    }
	    else
	    {
		recipeAddDialog2.dismiss();
		sendDataToModel();
	/**	 if(utils.checkInternetConnection(getApplicationContext()))
			{
				//syncModel sync = new syncModel(getApplicationContext());
				syncRecipeModel syncRecipe = new syncRecipeModel(getApplicationContext());
				try {
					//sync.getAndCreateAccountJSON();
					//sync.getJSONFromServer();
					syncRecipe.getAndCreateJSON();
					//syncRecipe.getJSONFromServer();
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
					
					
				} **/
			
    } 
		//Third - recipe add dialog - not done yet
		setUpThirdRecipeAddDialog();
	   
		
		addRecipeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getThirdDialogData();
				
			}
			});
		addRecipeDialog3.show();
	}
//	}
	
	/**
	 * Set up third recipe add dialog
	 */
	public void setUpThirdRecipeAddDialog()
	{
		 addRecipeDialog3 = new Dialog(MainActivity.this);
			addRecipeDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE);
			addRecipeDialog3.setContentView(R.layout.recipe3dialog);
			addRecipeDialog3.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
			utils.setDialogText(R.id.recipeImagesView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.browseButton, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeAddView3, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeCusineView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeDifficultyView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeDietaryView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeTipsView, addRecipeDialog3, 22);
		    addRecipeButton = (Button) addRecipeDialog3.findViewById(R.id.addRecipeButton);
			addRecipeButton.setTypeface(typeFace);
			addRecipeButton.setTextSize(22);
			addRecipeButton.setTextColor(Color.parseColor("#FFFFFFFF"));
			
			List<String> spinnerArray =  new ArrayList<String>();
			spinnerArray.add("Easy");
			spinnerArray.add("Medium");
			spinnerArray.add("Hard");

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			    MainActivity.this, R.layout.item, spinnerArray);

			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			Spinner sItems = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
			sItems.setAdapter(adapter);
	}
	
	/**
	 * Get third dialog data
	 */
	public void getThirdDialogData()
	{
		EditText imageEdit = (EditText) addRecipeDialog3.findViewById(R.id.recipeImagesEditText);
		String image = imageEdit.getText().toString();
		EditText cusineEdit = (EditText) addRecipeDialog3.findViewById(R.id.recipeCusineEditText);
		String cusine = cusineEdit.getText().toString();
		EditText dietaryEdit = (EditText) addRecipeDialog3.findViewById(R.id.recipeDietaryEditText);
		String dietary = dietaryEdit.getText().toString();
		EditText tipsEdit = (EditText) addRecipeDialog3.findViewById(R.id.recipeTipsEditText);
		String tips = tipsEdit.getText().toString();
		Spinner spinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
		String difficulty = spinner.getSelectedItem().toString();
		
	}
	
	/**
	 * Get information from step dialog
	 */
	public void getRecipeStep()
	{
		//Getting text
		TextView errorView = (TextView) recipeAddStepDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeAddStepDialog,16);
		errorView.setTextColor(Color.parseColor("#FFFFFF"));
		EditText stepNumEdit = (EditText) recipeAddStepDialog.findViewById(R.id.stepNumEditText);
		String stepNum = stepNumEdit.getText().toString();
		EditText stepEdit = (EditText) recipeAddStepDialog.findViewById(R.id.stepEditText);
		String step = stepEdit.getText().toString();
		//Error catching before moving to next dialog stage
		if(stepNum.equals(""))
		{
			errorView.setText("Please enter a step number");
		}
		else if(step.equals(""))
		{
			errorView.setText("Please enter a step");
		}
		else
		{
			stepList.add(step);
			stepNumList.add(stepNum);
			EditText stepsEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeStepsEditText);
			stepsEdit.append(stepNum +  ". " + step +  ", ");
			recipeAddStepDialog.dismiss();
		}
	}
	
	/**
	 * Get information from ingredient dialogs
	 */
	public void getIngredient()
	{
		//Getting text
		EditText ingredEdit = (EditText) recipeIngredDialog.findViewById(R.id.ingredEditText);
		String ingredient = ingredEdit.getText().toString();
		EditText amountEdit = (EditText) recipeIngredDialog.findViewById(R.id.amountEditText);
		String amount = amountEdit.getText().toString();
		EditText noteEdit =  (EditText) recipeIngredDialog.findViewById(R.id.noteEditText);
		String note = noteEdit.getText().toString();
		Spinner spinner = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		String value = spinner.getSelectedItem().toString();
		TextView errorView = (TextView) recipeIngredDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeIngredDialog,16);
		errorView.setTextColor(Color.parseColor("#FFFFFF"));
		//Error catching before moving onto next stage
		if(ingredient.equals(""))
		{
			errorView.setText("Please enter an ingredient name");
		}
		else if(amount.equals(""))
		{
			errorView.setText("Please enter an amount");
		}
		else
		{
			ingredientList.add(ingredient);
			amountList.add(amount);
			noteList.add(note);
			valueList.add(value);
			recipeIngredDialog.dismiss();
			EditText ingredsEdit = (EditText) recipeAddDialog2.findViewById(R.id.recipeIngredsEditText);
			if(note.equals(""))
			{
				ingredsEdit.append(  amount + " " + value + " " + ingredient + " ,");
			}
			else
			{
				ingredsEdit.append( amount + " " + value + " " + ingredient + " - " + note + " ,");
			}
		}
		
		
	}
	
	/**
	 * Prepare the data to send to the model
	 */
	public void sendDataToModel()
	{
		recipeBean recipe = new recipeBean();
		recipe.setName(name);
		recipe.setDesc(desc);
		recipe.setCooking(cooking);
		recipe.setServes(serves);
		recipe.setPrep(prep);
		recipe.setRecipeBook(recipeBook);
		recipe.setIngredients(ingredientList);
		recipe.setNotes(noteList);
		recipe.setValues(valueList);
		recipe.setAmount(amountList);
		recipe.setStepNum(stepNumList);
		recipe.setSteps(stepList);
		Context context = getApplicationContext();
		recipeModel model = new recipeModel(context);
		model.insertRecipe(recipe);
	}
	
	
	 @Override
	   protected void onResume() {
		   super.onResume();
	      sharedpreferences=getSharedPreferences(MyPREFERENCES, 
	      Context.MODE_PRIVATE);
	      
	    		  
	    	 if(utils.checkInternetConnection(getApplicationContext()))
	    			{
	    				syncModel sync = new syncModel(getApplicationContext());
	    				syncRecipeModel syncRecipe = new syncRecipeModel(getApplicationContext());
	    				try {
	    					sync.getJSONFromServer();
	    					sync.getAndCreateAccountJSON();
	    					syncRecipe.getJSONFromServer();
	    					syncRecipe.getAndCreateJSON();
							
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
