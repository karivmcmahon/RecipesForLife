package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.recipesforlife.R;
import com.example.recipesforlife.util.Util;

/**
 * This class handles the third dialog displayed for adding a recipe. Handles the style and retrieval of data from dialog
 * @author Kari
 *
 */
class Recipe_AddView_Dialog3  extends Recipe_AddView {

	private ActionBarActivity activity;
	private Util utils;


	Recipe_AddView_Dialog3(ActionBarActivity activity, Context context, String uniqueid, String bookname)
	{
		super(context, activity, uniqueid, bookname);
		this.activity = activity;
		utils = new Util(context, activity);
	}

	/**
	 * Set up third recipe add dialog
	 */
	void setUpThirdRecipeAddDialog()
	{
		//create and set up style
		addRecipeDialog3 = utils.createDialog(activity, R.layout.recipe_add_dialog3);		
		utils.setDialogText(R.id.recipeImagesView, addRecipeDialog3, 22);
		utils.setDialogText(R.id.browseButton, addRecipeDialog3, 22);
		utils.setDialogText(R.id.recipeAddView3, addRecipeDialog3, 22);
		utils.setDialogText(R.id.recipeCusineView, addRecipeDialog3, 22);
		utils.setDialogText(R.id.recipeDifficultyView, addRecipeDialog3, 22);
		utils.setDialogText(R.id.recipeDietaryView, addRecipeDialog3, 22);
		utils.setDialogText(R.id.recipeTipsView, addRecipeDialog3, 22);
		addRecipeButton = utils.setButtonTextDialog(R.id.addRecipeButton, 22, addRecipeDialog3);

		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, addRecipeDialog3);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addRecipeDialog3.dismiss();

			}});

		//Gets image from phone when browse pressed
		Button browseButton = utils.setButtonTextDialog(R.id.browseButton, 22, addRecipeDialog3);
		browseButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{			
					Intent chooserIntent = utils.getImageIntent();
					activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
					utils.setDialogTextString(R.id.recipeImagesEditText, addRecipeDialog3, imageName);
				}
				return false;

			}});


		//Fill spinner for diffculty
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Easy");
		spinnerArray.add("Medium");
		spinnerArray.add("Hard");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
		sItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);


		//Fill spinner for cuisine
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
		ArrayAdapter<String> cusineAdapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, cusineSpinnerArray);
		cusineAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner cusineItems = (Spinner) addRecipeDialog3.findViewById(R.id.recipeCusineSpinner);
		cusineItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		cusineItems.setAdapter(cusineAdapter);

		//Fill spinner for dietary requirements
		List<String> dietarySpinnerArray =  new ArrayList<String>();
		dietarySpinnerArray.add("Nut free");
		dietarySpinnerArray.add("Gluten free");
		dietarySpinnerArray.add("Vegeterian");
		dietarySpinnerArray.add("Vegan");
		dietarySpinnerArray.add("N/A");

		ArrayAdapter<String> dietaryAdapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, dietarySpinnerArray);
		dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner dietaryItems = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDietarySpinner);
		dietaryItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		dietaryItems.setAdapter(dietaryAdapter);
		addRecipeDialog3.show();
		addRecipeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				getThirdDialogData();

			}
		}); 
	}

	/**
	 * Get third dialog data
	 */
	private void getThirdDialogData()
	{	

		Spinner dietaryspinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDietarySpinner);
		dietary = dietaryspinner.getSelectedItem().toString();
		tips = utils.getTextFromDialog(R.id.recipeTipsEditText, addRecipeDialog3);
		Spinner spinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeDifficultySpinner);
		difficulty = spinner.getSelectedItem().toString();
		Spinner cusinespinner = (Spinner) addRecipeDialog3.findViewById(R.id.recipeCusineSpinner);
		cusine = cusinespinner.getSelectedItem().toString();
		//if no image is selected
		if(utils.getTextFromDialog(R.id.recipeImagesEditText, addRecipeDialog3).equals(""))
		{
			//then set a default image
			Bitmap bitmap = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.image_default_recipe)).getBitmap();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();
			array = byteArray;
		}  
		sendDataToModel(); //sends all the data thats been stored and sends to the model
		addRecipeDialog3.dismiss();
	}

}
