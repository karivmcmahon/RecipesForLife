package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.util.Util;

public class EditRecipeMisc extends RecipeEditActivity{

	ActionBarActivity activity;
	Context context;
	Util utils;
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
					EditText edit = (EditText) timeDialog.findViewById(R.id.recipePrepEditText);
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
					EditText edit = (EditText) timeDialog.findViewById(R.id.recipeCookingEditText);
					edit.setText(hour + ":" + minute);
				}
			};

	
	public EditRecipeMisc(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}
	
	/**
	 * Creates a dialog where the user can edit the serves information
	 */
	public void getServesDialog()
	{
		final Dialog servesDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog2);
		final TextView errorView = (TextView) servesDialog.findViewById(R.id.errorView);
		setUpServesDialog(servesDialog, errorView);
		final Spinner sItems = setUpServesSpinner(servesDialog);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, servesDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				servesDialog.dismiss();

			}});

		Button servesButton = utils.setButtonTextDialog(R.id.saveButton, 22, servesDialog);
		servesButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(utils.getTextFromDialog(R.id.recipeServesEditText,servesDialog).equals(""))
				{
					errorView.setText("Please enter a value for serves");
				}
				else
				{
					utils.setTextString(R.id.servesVal, utils.getTextFromDialog(R.id.recipeServesEditText, servesDialog));
					utils.setTextString(R.id.diffVal, sItems.getSelectedItem().toString());
					servesDialog.dismiss();
				}

			}});
		servesDialog.show();
	}
	
	public void setUpServesDialog(Dialog servesDialog, TextView errorView)
	{
		utils.setDialogText(R.id.errorView,servesDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, servesDialog, 22);
		utils.setDialogText(R.id.recipeServesView, servesDialog, 22);
		utils.setDialogTextString(R.id.recipeServesEditText, servesDialog, utils.getTextView(R.id.servesVal));
		utils.setDialogText(R.id.recipeDifficultyView, servesDialog, 22);
	}
	
	public Spinner setUpServesSpinner(Dialog servesDialog)
	{
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Easy");
		spinnerArray.add("Medium");
		spinnerArray.add("Hard");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) servesDialog.findViewById(R.id.recipeDifficultySpinner);
		sItems.getBackground().setColorFilter(context.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setSelection(utils.getIndex(sItems, utils.getTextView(R.id.diffVal)));
		return sItems;
	}
	
	/**
	 * Creates a title dialog where the user can edit the title dialog
	 */
	public void getTitleDialog()
	{
		final Dialog titleDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog1);
		final TextView errorView = (TextView) titleDialog.findViewById(R.id.errorView);
		setUpTitleDialogStyle(titleDialog, errorView);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, titleDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				titleDialog.dismiss();

			}});
		Button titleButton = utils.setButtonTextDialog(R.id.saveButton, 22, titleDialog);
		titleButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(utils.getTextFromDialog(R.id.recipenameEditText, titleDialog).equals(""))
				{
					errorView.setText("Please enter a name");
				}
				else if(utils.getTextFromDialog(R.id.recipeDescEdit, titleDialog).equals(""))
				{
					errorView.setText("Please enter a description");
				}
				else
				{
					utils.setTextString(R.id.recipeTitle, utils.getTextFromDialog(R.id.recipenameEditText, titleDialog));
					utils.setTextString(R.id.recipeDesc, utils.getTextFromDialog(R.id.recipeDescEdit, titleDialog));
					titleDialog.dismiss();
				}

			}});
		titleDialog.show();
	}
	
	public void getChefDialog()
	{
		final Dialog cusineDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog8);
		final TextView errorView = (TextView) cusineDialog.findViewById(R.id.errorView);
		setUpChefDialogStyle(cusineDialog, errorView);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, cusineDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cusineDialog.dismiss();

			}});		
		final Spinner sItems = createCusineSpinner(cusineDialog);
		final Spinner dietaryItems = createDietarySpinner(cusineDialog);
		Button cusineButton = utils.setButtonTextDialog(R.id.saveButton, 22, cusineDialog);
		cusineButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				utils.setTextString(R.id.dietaryVal, dietaryItems.getSelectedItem().toString());
				utils.setTextString(R.id.cusineVal, sItems.getSelectedItem().toString());
				cusineDialog.dismiss();
			}});
		cusineDialog.show();
	}

	public Spinner createCusineSpinner(Dialog cusineDialog)
	{
		List<String> cusineSpinnerArray =  new ArrayList<String>();
		cusineSpinnerArray.add("Italian");
		cusineSpinnerArray.add("Indian");
		cusineSpinnerArray.add("Chinese");
		cusineSpinnerArray.add("Thai");
		cusineSpinnerArray.add("Spanish");
		cusineSpinnerArray.add("French");
		cusineSpinnerArray.add("American");
		cusineSpinnerArray.add("English");
		cusineSpinnerArray.add("African");
		cusineSpinnerArray.add("Middle Eastern");
		cusineSpinnerArray.add("Other");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, cusineSpinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) cusineDialog.findViewById(R.id.recipeCusineSpinner);
		sItems.getBackground().setColorFilter(context.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setSelection(utils.getIndex(sItems, utils.getTextView(R.id.cusineVal)));
		return sItems;
	}
	
	public Spinner createDietarySpinner(Dialog cusineDialog)
	{
		List<String> dietarySpinnerArray =  new ArrayList<String>();
		dietarySpinnerArray.add("Nut free");
		dietarySpinnerArray.add("Gluten free");
		dietarySpinnerArray.add("Vegeterian");
		dietarySpinnerArray.add("Vegan");
		dietarySpinnerArray.add("N/A");


		ArrayAdapter<String> dietaryAdapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, dietarySpinnerArray);
		dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner dietaryItems = (Spinner) cusineDialog.findViewById(R.id.recipeDietarySpinner);
		dietaryItems.getBackground().setColorFilter(context.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		dietaryItems.setAdapter(dietaryAdapter);
		dietaryItems.setSelection(utils.getIndex(dietaryItems, utils.getTextView(R.id.dietaryVal)));
		return dietaryItems;
	}
	
	public void setUpChefDialogStyle(Dialog cusineDialog, TextView errorView)
	{
		utils.setDialogText(R.id.errorView,cusineDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, cusineDialog, 22);
		utils.setDialogText(R.id.recipeCusineView, cusineDialog, 22);
		utils.setDialogText(R.id.recipeDietaryView, cusineDialog, 22);
	}
	
	public void setUpTitleDialogStyle(Dialog titleDialog, TextView errorView)
	{
		utils.setDialogText(R.id.errorView,titleDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, titleDialog, 22);
		utils.setDialogText(R.id.recipeNameView, titleDialog, 22);
		utils.setDialogText(R.id.recipeDescView, titleDialog, 22);
		utils.setDialogTextString(R.id.recipenameEditText, titleDialog, utils.getTextView(R.id.recipeTitle));
		utils.setDialogTextString(R.id.recipeDescEdit, titleDialog, utils.getTextView(R.id.recipeDesc));
	}
	
	/**
	 * Creates a dialog showing the prep and cooking values which the user can change
	 */
	public void getTimeDialog()
	{
		timeDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog3);
		setUpTimeDialogStyle(timeDialog);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, timeDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeDialog.dismiss();

			}});
		Button timeButton = utils.setButtonTextDialog(R.id.saveButton, 22, timeDialog);
		timeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				utils.setTextString(R.id.prepTimeVal, utils.getTextFromDialog(R.id.recipePrepEditText, timeDialog));
				utils.setTextString(R.id.cookingTimeVal, utils.getTextFromDialog(R.id.recipeCookingEditText, timeDialog));
				timeDialog.dismiss();
			}});
		timeDialog.show();

	}
	
	public void setUpTimeDialogStyle(Dialog timeDialog)
	{
		utils.setDialogText(R.id.recipeEditView, timeDialog, 22);
		utils.setDialogText(R.id.recipePrepView, timeDialog, 22);
		utils.setDialogText(R.id.recipeCookingView, timeDialog, 22);
		utils.setDialogTextString(R.id.recipePrepEditText, timeDialog, utils.getTextView(R.id.prepTimeVal));
		utils.setDialogTextString(R.id.recipeCookingEditText, timeDialog, utils.getTextView(R.id.cookingTimeVal));
		utils.setTimePickerFrag(timeDialog, R.id.recipePrepEditText, mHandler,"prepare");
		utils.setTimePickerFrag(timeDialog, R.id.recipeCookingEditText, mHandler2, "cook");
	}
	
	/**
	 * Creates a title dialog where the user can edit the title dialog
	 */
	public void getTipsDialog()
	{
		final Dialog tipsDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog6);
		final TextView errorView = (TextView) tipsDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,tipsDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, tipsDialog, 22);
		utils.setDialogText(R.id.recipeTipsView, tipsDialog, 22);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, tipsDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tipsDialog.dismiss();

			}});
		utils.setDialogTextString(R.id.recipeTipsEditText, tipsDialog, utils.getTextView(R.id.tips));
		Button tipsButton = utils.setButtonTextDialog(R.id.saveButton, 22, tipsDialog);
		tipsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				utils.setTextString(R.id.tips, utils.getTextFromDialog(R.id.recipeTipsEditText, tipsDialog));

				tipsDialog.dismiss();


			}});
		tipsDialog.show(); 
	}
	
}


