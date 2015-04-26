package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.util.Util;

/**
 * The class handles the dialog displayed for adding a recipe. Handles the style and retrieval of data from dialog
 * @author Kari
 *
 */
class Recipe_AddView_IngredPrepDialog extends Recipe_AddView {

	private ActionBarActivity activity;
	private Util utils;


	Recipe_AddView_IngredPrepDialog(ActionBarActivity activity, Context context, String uniqueid, String bookname)
	{
		super(context, activity, uniqueid, bookname);
		this.activity = activity;		
		utils = new Util(context, activity);
	}

	/**
	 * Setup ingredient add dialog with font and spinnner
	 */
	void setUpIngredAddDialog()
	{
		recipeIngredDialog = utils.createDialog(activity,R.layout.recipe_add_dialog5);
		utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
		utils.setButtonTextDialog(R.id.addIngredButton, 22, recipeIngredDialog);

		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeIngredDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				recipeIngredDialog.dismiss();

			}});

		//Spinner set up with varying measurement amounts
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("teaspoon");
		spinnerArray.add("tablespoon");
		spinnerArray.add("cup");
		spinnerArray.add("");
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
		spinnerArray.add("other");

		//Fill spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		sItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		recipeIngredDialog.show();
	}

	/**
	 * Set up step dialog with correct fonts
	 */
	void setUpStepAddDialog()
	{
		recipeAddStepDialog = utils.createDialog(activity, R.layout.recipe_add_dialog4);		
		utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
		utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
		utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
		utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
		EditText stepEditText = (EditText) recipeAddStepDialog.findViewById(R.id.stepNumEditText);
		stepEditText.setText(Integer.toString(prepnumcount));
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeAddStepDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				recipeAddStepDialog.dismiss();

			}});
		recipeAddStepDialog.show();
	}

	/**
	 * Get information from step dialog
	 */
	void getRecipeStep()
	{
		//Getting  data from the text boxes
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
			prepnumcount = Integer.parseInt(stepNum) + 1;
			
			//Sets the details to a prep bean
			PreperationBean prepBean = new PreperationBean();
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
	void getIngredient()
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
			//Sets details to ingredient bean
			IngredientBean ingredBean = new IngredientBean();
			ingredBean.setName(ingredient);
			ingredBean.setAmount(Integer.parseInt(amount));
			ingredBean.setNote(note);
			ingredBean.setValue(value);
			ingredBeanList.add(ingredBean);
			recipeIngredDialog.dismiss();
			
			//How ingredients are displayed in the edit text box
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


}
