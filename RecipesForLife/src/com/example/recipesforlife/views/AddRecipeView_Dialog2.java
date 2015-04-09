package com.example.recipesforlife.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.util.Util;

public class AddRecipeView_Dialog2  extends AddRecipeView {
	
ActionBarActivity activity;
Context context;
String bookname, uniqueid = "";
Util utils;
//Handles message from time dialog 1 - preptime
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

	//Handles message from time dialog 2 - cooking time
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

	
	public AddRecipeView_Dialog2(ActionBarActivity activity, Context context, String uniqueid, String bookname)
	{
		super(context, activity, uniqueid, bookname);
		this.activity = activity;
		this.context = context;
		this.bookname = bookname;
		this.uniqueid = uniqueid;
		utils = new Util(context, activity);
	}
	
	/**
	 * Set up second dialog style with correct fonts and spinner filled 
	 */
	public void setUpSecondRecipeAddDialog()
	{
		recipeAddDialog2 = utils.createDialog(activity, R.layout.recipe_add_dialog2);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeAddDialog2);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recipeAddDialog2.dismiss();
				
			}});
		utils.setDialogText(R.id.recipeAddView2, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeIngredsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeStepsView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeServesView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipePrepView, recipeAddDialog2, 22);
		utils.setDialogText(R.id.recipeCookingView, recipeAddDialog2, 22);
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipePrepEditText, mHandler, "prepare");
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipeCookingEditText, mHandler2, "cook");
		nextButton2 = utils.setButtonTextDialog(R.id.nextButton, 22, recipeAddDialog2);
		recipeAddDialog2.show();
		handleIngredPrepClicks();
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
			AddRecipeView_Dialog3 dialog3 = new AddRecipeView_Dialog3(activity, context, uniqueid, bookname);
			//Displays and gets third dialog data
			dialog3.setUpThirdRecipeAddDialog();
		/**	addRecipeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					getThirdDialogData();

				}
			}); **/
		}
	}
	
	public void handleIngredPrepClicks()
	{
		final AddRecipeView_IngredPrepDialogs ingredprepdialog = new AddRecipeView_IngredPrepDialogs(activity, context, uniqueid, bookname);
		ImageButton ingredsPlusButton = (ImageButton) recipeAddDialog2.findViewById(R.id.ingredsAddButton);						
		ingredsPlusButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Set up the dialog style
			ingredprepdialog.setUpIngredAddDialog();
				//Once add is pressed
				Button addIngredButton = (Button) recipeIngredDialog.findViewById(R.id.addIngredButton);
				addIngredButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						//Get ingredient info
						ingredprepdialog.getIngredient();
					}}); 
			}});

		//If steps plus button is pressed - show a dialog to add a step
		ImageButton stepsPlusButton = (ImageButton) recipeAddDialog2.findViewById(R.id.stepsAddButton);
		stepsPlusButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				//Set up style
				ingredprepdialog.setUpStepAddDialog();
				//Once add is pressed
			Button addStepButton = (Button) recipeAddStepDialog.findViewById(R.id.addStepButton);
				addStepButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						//Get info
						ingredprepdialog.getRecipeStep();
					}}); 
			}

		});
	}

}
