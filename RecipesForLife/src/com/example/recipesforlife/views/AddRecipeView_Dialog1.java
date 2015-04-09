package com.example.recipesforlife.views;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.models.CookbookModel;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.Util;


public class AddRecipeView_Dialog1 extends AddRecipeView {
	
ActionBarActivity activity;
Context context;
String bookname, uniqueid = "";
Util utils;

	
	public AddRecipeView_Dialog1(ActionBarActivity activity, Context context, String uniqueid, String bookname)
	{
		super(context, activity, uniqueid, bookname);
		this.activity = activity;
		this.context = context;
		this.bookname = bookname;
		this.uniqueid = uniqueid;
		utils = new Util(context, activity);
	}
	
	/**
	 * Set up initial dialog style with correct fonts and spinner filled 
	 */
	public void setUpInitialRecipeAddDialog()
	{
		ingredBeanList = new ArrayList<IngredientBean>();
		prepBeanList = new ArrayList<PreperationBean>();
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		recipeAddDialog = utils.createDialog(activity , R.layout.recipe_add_dialog1);
		//sets up styles
		utils.setDialogText(R.id.recipeAddView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeBookView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeNameView,recipeAddDialog,22);
		utils.setDialogText(R.id.recipeDescView,recipeAddDialog,22);	
		CookbookModel cbmodel = new CookbookModel(context);
		
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeAddDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recipeAddDialog.dismiss();
				
			}});

		//Fills the spinner with users cookbooks
		ArrayList<CookbookBean> cbList = cbmodel.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));	
		List<String> spinnerArray =  new ArrayList<String>();
		for(int i = 0; i < cbList.size(); i++)
		{
			spinnerArray.add(cbList.get(i).getName());
			cookbookuids.add(cbList.get(i).getUniqueid());
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) recipeAddDialog.findViewById(R.id.recipeBookSpinner);
		sItems.setAdapter(adapter);
		sItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setSelection(utils.getIndex(sItems, bookname));
		nextButton = utils.setButtonTextDialog(R.id.nextButton, 22, recipeAddDialog);
		recipeAddDialog.show();
	}
	
	/**
	 * Get data from the first dialog - does some error checking before getting the data
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
		int pos = spinner.getSelectedItemPosition();
		recipeBook = cookbookuids.get(pos);
		RecipeModel model = new RecipeModel(context);
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
			AddRecipeView_Dialog2 dialog2 = new AddRecipeView_Dialog2(activity, context, uniqueid, bookname);
			dialog2.setUpSecondRecipeAddDialog();

		}


	}
}
