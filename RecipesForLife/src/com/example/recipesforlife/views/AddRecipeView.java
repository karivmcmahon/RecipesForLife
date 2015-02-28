package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

/**
 * Creates and gets the data for displaying the add recipe dialog
 * @author Kari
 *
 */
public class AddRecipeView extends RecipeListViewActivity {
	
	ActionBarActivity activity;
	Context context;
	util utils;
	ArrayList<ingredientBean> ingredBeanList;
	ArrayList<preperationBean> prepBeanList;
	Dialog recipeAddDialog;
	static Dialog recipeAddDialog2;
	Dialog recipeIngredDialog;
	Dialog recipeAddStepDialog;
	Dialog addRecipeDialog3;
	Button nextButton, nextButton2, addIngredButton, addRecipeButton;
	String name, desc,recipeBook, serves, prep, cooking;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	String uniqueid = "";

	
	 // Handles message from time dialog 1 - preptime
	   static Handler mHandler = new Handler(){
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
	    static Handler mHandler2 = new Handler(){
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
	public AddRecipeView(Context context, ActionBarActivity activity, String uniqueid)
	{
		this.context = context;
		this.activity = activity;
		this.uniqueid = uniqueid;
		utils = new util(context, activity);
	}
	
	 public void addRecipe()
	    {
	    	//Set up dialog style
			setUpInitialRecipeAddDialog();
			//Once next pressed
			nextButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//Get data from the dialog
				    getInitialRecipeAddDialogData();
				}
				});
			recipeAddDialog.show();		
	    }
	    
	    /**
		 * Set up initial dialog style with correct fonts and spinner filled 
		 */
		public void setUpInitialRecipeAddDialog()
		{
			ingredBeanList = new ArrayList<ingredientBean>();
			prepBeanList = new ArrayList<preperationBean>();
			SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			recipeAddDialog = utils.createDialog(activity , R.layout.recipe1dialog);
			utils.setDialogText(R.id.recipeAddView,recipeAddDialog,22);
			utils.setDialogText(R.id.recipeBookView,recipeAddDialog,22);
			utils.setDialogText(R.id.recipeNameView,recipeAddDialog,22);
			utils.setDialogText(R.id.recipeDescView,recipeAddDialog,22);	
			cookbookModel cbmodel = new cookbookModel(context);
			ArrayList<cookbookBean> cbList = cbmodel.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
			List<String> spinnerArray =  new ArrayList<String>();
			for(int i = 0; i < cbList.size(); i++)
			{
				spinnerArray.add(cbList.get(i).getName());
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(
			    activity, R.layout.item, spinnerArray);

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
			recipeAddDialog2 = utils.createDialog(activity, R.layout.recipe2dialog);
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
		    recipeModel model = new recipeModel(context);
		    SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		    //Error checking
		    if(model.selectRecipe(name, uniqueid) == true)
			{
				errorView.setText("You already have a recipe with this name");
			}
		    else if(name.equals(""))
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
					    //Set up style
						setUpStepAddDialog();
						//Once add is pressed
						Button addStepButton = (Button) recipeAddStepDialog.findViewById(R.id.addStepButton);
						addStepButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
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
			recipeIngredDialog = utils.createDialog(activity,R.layout.ingredsdialog);
			utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
			utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
			utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
			utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
			utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
			utils.setButtonTextDialog(R.id.addIngredButton, 22, recipeIngredDialog);
					
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
			    activity, R.layout.item, spinnerArray);

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
			   recipeAddStepDialog = utils.createDialog(activity, R.layout.methoddialog);		
				utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
				utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
				utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
				utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
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
						getThirdDialogData();
						
					}
					});
				addRecipeDialog3.show();
		    }
		}
//		}
		
		/**
		 * Set up third recipe add dialog
		 */
		public void setUpThirdRecipeAddDialog()
		{
				addRecipeDialog3 = utils.createDialog(activity, R.layout.recipe3dialog);		
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
				    activity, R.layout.item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner sItems = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
				sItems.setAdapter(adapter);
		}
		
		/**
		 * Get third dialog data
		 */
		public void getThirdDialogData()
		{	
		    utils.getTextFromDialog(R.id.recipeImagesEditText, addRecipeDialog3);
			utils.getTextFromDialog(R.id.recipeCusineEditText, addRecipeDialog3);
			utils.getTextFromDialog(R.id.recipeDietaryEditText, addRecipeDialog3);
			utils.getTextFromDialog(R.id.recipeTipsEditText, addRecipeDialog3);
			Spinner spinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
			spinner.getSelectedItem().toString();	
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
			SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			recipeBean recipe = new recipeBean();
			recipe.setName(name);
			recipe.setDesc(desc);
			recipe.setCooking(cooking);
			recipe.setServes(serves);
			recipe.setPrep(prep);
			recipe.setRecipeBook(recipeBook);
			recipe.setAddedBy(sharedpreferences.getString(emailk, ""));
			recipeModel model = new recipeModel(context);
			String uid = model.insertRecipe(recipe, false, ingredBeanList, prepBeanList);
			RecipeListViewActivity.recipenames.add(0, name);
			RecipeListViewActivity.recipeids.add(0, uid);
			RecipeListViewActivity.adapter.notifyDataSetChanged(); 
		}

}
