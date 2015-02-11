package com.example.recipesforlife.views;



import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.TimePickerFragment;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

public class MainActivity extends Activity  {
	
	public static final String MyPREFERENCES = "MyPrefs";
	private SharedPreferences sharedpreferences;
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	util utils;
	Typeface typeFace;
	ArrayList<ingredientBean> ingredBeanList;
	ArrayList<preperationBean> prepBeanList;
	Dialog recipeAddDialog , recipeAddDialog2, recipeIngredDialog, recipeAddStepDialog, addRecipeDialog3;
	Button nextButton, nextButton2, addIngredButton, addRecipeButton;
	String name, desc,recipeBook, serves, prep, cooking;

	    // Handles message from time dialog 1 - preptime
	    Handler mHandler = new Handler(){
	        @Override
	        public void handleMessage(Message m){
	            //Bundle retrieves data
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
	            //Displays it in edittext once set
	            EditText edit = (EditText) recipeAddDialog2.findViewById(R.id.recipePrepEditText);
	            edit.setText(hour + ":" + minute);
	        }
	    };
	    
	    //Handles message from time dialog 2
	    Handler mHandler2 = new Handler(){
	        @Override
	        public void handleMessage(Message m){
	        	 //Bundle retrieves data
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
	            //Displays it in edittext once set
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
		ingredBeanList = new ArrayList<ingredientBean>();
		prepBeanList = new ArrayList<preperationBean>();
		Button viewButton = (Button) findViewById(R.id.viewButton);
		viewButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 Intent i = new Intent(MainActivity.this, RecipeEditListViewActivity.class);
			      startActivity(i);
			}
			
			
		});
		
		Button cookbookButton = (Button) findViewById(R.id.cookbookButton);
		cookbookButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent(MainActivity.this, CookbookViewActivity.class);
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
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		recipeAddDialog = utils.createDialog(MainActivity.this , R.layout.recipe1dialog);
		utils.setDialogText(R.id.recipeAddView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeBookView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeNameView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeDescView,recipeAddDialog,22);	
		cookbookModel cbmodel = new cookbookModel(getApplicationContext());
		ArrayList<cookbookBean> cbList = cbmodel.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
		List<String> spinnerArray =  new ArrayList<String>();
		for(int i = 0; i < cbList.size(); i++)
		{
			spinnerArray.add(cbList.get(i).getName());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
		    MainActivity.this, R.layout.item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) recipeAddDialog.findViewById(R.id.recipeBookSpinner);
		sItems.setAdapter(adapter);
		nextButton = utils.setButtonTextDialog(R.id.nextButton, 22, recipeAddDialog);
	}
	
	/**
	 * Set up second dialog style with correct fonts and spinner filled 
	 */
	public void setUpSecondRecipeAddDialog()
	{
		recipeAddDialog2 = utils.createDialog(MainActivity.this, R.layout.recipe2dialog);
		utils.setDialogText(R.id.recipeAddView2, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeIngredsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeStepsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeServesView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipePrepView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeCookingView, recipeAddDialog2, 22);
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipePrepEditText, mHandler);
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipeCookingEditText, mHandler2);
		nextButton2 = utils.setButtonTextDialog(R.id.nextButton, 22, recipeAddDialog2);
		recipeAddDialog2.show();
	}
	
	/**
	 * Get data from the first dialog
	 */
	public void getInitialRecipeAddDialogData()
	{
		//Getting data
		TextView errorView = (TextView) recipeAddDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeAddDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		name = utils.getTextFromDialog(R.id.recipenameEditText, recipeAddDialog);
		desc = utils.getTextFromDialog(R.id.recipeDescEdit, recipeAddDialog);
		Spinner spinner = (Spinner) recipeAddDialog.findViewById(R.id.recipeBookSpinner);
	    recipeBook = spinner.getSelectedItem().toString();
		Context context = getApplicationContext();
	    recipeModel model = new recipeModel(context);
	    SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
	    //Error checking
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
			//Set up second dialog
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
		recipeIngredDialog = utils.createDialog(MainActivity.this,R.layout.ingredsdialog);
		utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
		Button addIngredButton = utils.setButtonTextDialog(R.id.addIngredButton, 22, recipeIngredDialog);
				
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
		   recipeAddStepDialog = utils.createDialog(MainActivity.this, R.layout.methoddialog);		
			utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
			utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
			utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
			Button addStepButton = utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
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
		
	    
	    serves = utils.getTextFromDialog(R.id.recipeServesEditText, recipeAddDialog2);
	    prep = utils.getTextFromDialog(R.id.recipePrepEditText, recipeAddDialog2);
		cooking = utils.getTextFromDialog(R.id.recipeCookingEditText, recipeAddDialog2);
		String methods = utils.getTextFromDialog(R.id.recipeIngredsEditText, recipeAddDialog2);
	    String ingredient = utils.getTextFromDialog(R.id.recipeStepsEditText, recipeAddDialog2);
	    //Error catching before moving to next stage
	    if(ingredient.equals(""))
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
			//insert data to database
			sendDataToModel();
	
			
   
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
	}
//	}
	
	/**
	 * Set up third recipe add dialog
	 */
	public void setUpThirdRecipeAddDialog()
	{
			addRecipeDialog3 = utils.createDialog(MainActivity.this, R.layout.recipe3dialog);		
			utils.setDialogText(R.id.recipeImagesView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.browseButton, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeAddView3, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeCusineView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeDifficultyView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeDietaryView, addRecipeDialog3, 22);
			utils.setDialogText(R.id.recipeTipsView, addRecipeDialog3, 22);
		    addRecipeButton = utils.setButtonTextDialog(R.id.addRecipeButton, 22, addRecipeDialog3);
		   
			
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
		String image = utils.getTextFromDialog(R.id.recipeImagesEditText, addRecipeDialog3);
		String cusine =  utils.getTextFromDialog(R.id.recipeCusineEditText, addRecipeDialog3);
		String dietary =  utils.getTextFromDialog(R.id.recipeDietaryEditText, addRecipeDialog3);
		String tips =  utils.getTextFromDialog(R.id.recipeTipsEditText, addRecipeDialog3);
		Spinner spinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
		String difficulty = spinner.getSelectedItem().toString();	
	}
	
	/**
	 * Get information from step dialog
	 */
	public void getRecipeStep()
	{
		//Getting 
		TextView errorView = (TextView) recipeAddStepDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeAddStepDialog,16);
		errorView.setTextColor(Color.parseColor("#FFFFFF"));
		String stepNum = utils.getTextFromDialog(R.id.stepNumEditText, recipeAddStepDialog);
		String step = utils.getTextFromDialog(R.id.stepEditText, recipeAddStepDialog);
		
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
			preperationBean prepBean = new preperationBean();
			prepBean.setPreperation(step);
			prepBean.setPrepNum(Integer.parseInt(stepNum));
			prepBeanList.add(prepBean);
			//Append steps to edit text box
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
		String ingredient = utils.getTextFromDialog(R.id.ingredEditText, recipeIngredDialog);
		String amount = utils.getTextFromDialog(R.id.amountEditText, recipeIngredDialog);
		String note = utils.getTextFromDialog(R.id.noteEditText, recipeIngredDialog);
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
			ingredientBean ingredBean = new ingredientBean();
			ingredBean.setName(ingredient);
			ingredBean.setAmount(Integer.parseInt(amount));
			ingredBean.setNote(note);
			ingredBean.setValue(value);
			ingredBeanList.add(ingredBean);
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
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		recipeBean recipe = new recipeBean();
		recipe.setName(name);
		recipe.setDesc(desc);
		recipe.setCooking(cooking);
		recipe.setServes(serves);
		recipe.setPrep(prep);
		recipe.setRecipeBook(recipeBook);
		recipe.setAddedBy(sharedpreferences.getString(emailk, ""));
		Context context = getApplicationContext();
		recipeModel model = new recipeModel(context);
		model.insertRecipe(recipe, false, ingredBeanList, prepBeanList);
	}
	
	
	 @Override
	   protected void onResume() {
		   super.onResume();
	       utils.sync();
	    		 
	      
	   }
	
	
	

}
