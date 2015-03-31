package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.CookbookModel;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.Util;

/**
 * Creates and gets the data for displaying the add recipe dialog
 * @author Kari
 *
 */
public class AddRecipeView extends RecipeListViewActivity {

	ActionBarActivity activity;
	Context context;
	Util utils;
	ArrayList<IngredientBean> ingredBeanList;
	ArrayList<PreperationBean> prepBeanList;
	static Dialog recipeAddDialog2;
	Dialog recipeIngredDialog, recipeAddStepDialog, addRecipeDialog3, recipeAddDialog;
	Button nextButton, nextButton2, addIngredButton, addRecipeButton;
	String name, desc,recipeBook, serves, prep, cooking, cusine, difficulty, tips, dietary, uniqueid, imageName, bookname;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	private static final int SELECT_PHOTO = 100;
	byte[] array;
	ArrayList<String> cookbookuids = new ArrayList<String>();

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
	public AddRecipeView(Context context, ActionBarActivity activity, String uniqueid, String bookname)
	{
		this.context = context;
		this.activity = activity;
		this.uniqueid = uniqueid;
		this.bookname = bookname;
		utils = new Util(context, activity);
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
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipePrepEditText, mHandler);
		utils.setTimePickerFrag(recipeAddDialog2, R.id.recipeCookingEditText, mHandler2);
		nextButton2 = utils.setButtonTextDialog(R.id.nextButton, 22, recipeAddDialog2);
		recipeAddDialog2.show();
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
				// TODO Auto-generated method stub
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
	public void setUpStepAddDialog()
	{
		recipeAddStepDialog = utils.createDialog(activity, R.layout.recipe_add_dialog4);		
		utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
		utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
		utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
		utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeAddStepDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recipeAddStepDialog.dismiss();
				
			}});
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
			//Displays and gets third dialog data
			setUpThirdRecipeAddDialog();
			addRecipeButton.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					getThirdDialogData();

				}
			});
		}
	}

	/**
	 * Set up third recipe add dialog
	 */
	public void setUpThirdRecipeAddDialog()
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
	}

	/**
	 * Get third dialog data
	 */
	public void getThirdDialogData()
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

	/**
	 * Get information from step dialog
	 */
	public void getRecipeStep()
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

	/**
	 * Prepare the data to send to the model where the data will be inserted 
	 */
	public void sendDataToModel()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		RecipeBean recipe = new RecipeBean();
		recipe.setName(name);
		recipe.setDesc(desc);
		recipe.setCooking(cooking);
		recipe.setServes(serves);
		recipe.setPrep(prep);
		recipe.setRecipeBook(recipeBook);
		recipe.setDifficulty(difficulty);
		recipe.setDietary(dietary);
		recipe.setTips(tips);
		recipe.setCusine(cusine);
		recipe.setAddedBy(sharedpreferences.getString(emailk, ""));
		ImageBean imgBean = new ImageBean();
		imgBean.setImage(array);
		RecipeModel model = new RecipeModel(context);
		try
		{
			String uid = model.insertRecipe(recipe, false, ingredBeanList, prepBeanList, imgBean);
			//Updates recipe list once inserted
			RecipeListViewActivity.recipenames.add(0, name);
			RecipeListViewActivity.recipeids.add(0, uid);
			RecipeListViewActivity.recipeimages.add(0, imgBean.getImage());
			RecipeListViewActivity.adapter.notifyDataSetChanged(); 

		}catch(SQLException e)
		{
			Toast.makeText(context, "Recipe was not added", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Retrieves result from intent
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					//Get image and file and rotate correctly
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());
					//Set image name in edit text
					imageName = f.getName();					
					utils.setDialogTextString(R.id.recipeImagesEditText, addRecipeDialog3, imageName);
					//compress image
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					//set image to byte array
					byte[] byteArray = stream.toByteArray(); 
					array = byteArray;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}



}



