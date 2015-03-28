package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.ImageLoader;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Class to show edit view for recipes
 * @author Kari
 *
 */
public class RecipeEditActivity extends ActionBarActivity {

	Util utils;
	RecipeBean recipe;
	public static final String MyPREFERENCES = "MyPrefs";
	Dialog titleDialog, servesDialog, imageDialog;
	static Dialog timeDialog;
	Dialog prepDialog;
	Dialog ingredDialog;
	public static final String emailk = "emailKey";
	ArrayList<PreperationBean> prepList, modifiedPrepList, addPrepList;
	ArrayList<IngredientBean> ingredList, modifiedIngredList, addIngredList;
	ImageBean imgBean;
	ArrayList<Integer> prepNumEditIds, prepEditIds, amountEditIds, valueEditIds, ingredEditIds, noteEditIds;
	int id = 1;
	NavigationDrawerCreation nav;
	private static final int SELECT_PHOTO = 100;

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


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipe_view_edit);
		utils = new Util(getApplicationContext(), this);
		recipe = new RecipeBean();
		setStyle();
		setTextForLayout();
		String recipename = utils.getTextView(R.id.recipeTitle);


		nav = new NavigationDrawerCreation(this, "Edit Recipe Name");
		nav.createDrawer();
		SpannableString s = new SpannableString("Edit " + recipename);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		//Set up the various edit buttons for the page
		ImageView titleButton = (ImageView) findViewById(R.id.recipeTitleEditImage);
		titleButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getTitleDialog();
				}
				return false;
			}});

		ImageView imageButton = (ImageView) findViewById(R.id.imageEditImage);
		imageButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//set image dialog style
					imageDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog7);
					final TextView errorView = (TextView) imageDialog.findViewById(R.id.errorView);
					utils.setDialogText(R.id.errorView,imageDialog,16);
					errorView.setTextColor(Color.parseColor("#F70521"));
					utils.setDialogText(R.id.recipeEditImageView, imageDialog, 22);
					utils.setDialogText(R.id.recipeImagesView, imageDialog, 22);

					Button imageSaveButton = utils.setButtonTextDialog(R.id.saveImageButton, 22, imageDialog);
					final Button browseButton = utils.setButtonTextDialog(R.id.bButton, 22, imageDialog);
					browseButton.setOnTouchListener(new OnTouchListener(){

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								// Get image
								Intent chooserIntent = utils.getImageIntent();
								startActivityForResult(chooserIntent, SELECT_PHOTO);
							}
							return false;
						}});
					imageSaveButton.setOnTouchListener(new OnTouchListener() {


						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								// TODO Auto-generated method stub

								imageDialog.dismiss();
								//Loads image for imageview
								ImageView img = (ImageView) findViewById(R.id.foodImage);
								ImageLoader task = new ImageLoader(getApplicationContext(),imgBean, img);
								task.execute();
							}
							return false;
						}});


					imageDialog.show();
					return false;
				}
				return false;
			}});

		//If edit serves icon selected display the serve dialog
		ImageView servesButton = (ImageView) findViewById(R.id.servesEditImage);
		servesButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getServesDialog();
				}
				return false;
			}});

		ImageView methodAddButton = (ImageView) findViewById(R.id.methodAddImage);
		methodAddButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					setUpStepAddDialog();
				}
				return false;
			}});

		ImageView ingredAddButton = (ImageView) findViewById(R.id.ingredAddImage);
		ingredAddButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					setUpIngredAddDialog();
				}
				return false;
			}});

		//If edit time icon  selected display the times dialog
		ImageView timeButton = (ImageView) findViewById(R.id.timeEditImage);
		timeButton.setOnTouchListener(new OnTouchListener(){


			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					getTimeDialog();
				}
				return false;
			}}); 

		//If edit prep icon selected display the prep dialog
		ImageView prepButton = (ImageView) findViewById(R.id.methodEditImage);
		prepButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getAndCreatePrepDialog();
				}
				return false;
			}});

		//If edit ingred icon selected display the ingred dialog
		ImageView ingredButton = (ImageView) findViewById(R.id.ingredEditImage);
		ingredButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getAndCreateIngredDialog();
				}
				return false;
			}});

		ImageView tipsButton = (ImageView) findViewById(R.id.tipsEditImage);
		tipsButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getTipsDialog();
				}
				return false;
			}});

		ImageView chefEditButton = (ImageView) findViewById(R.id.chefEditImage);
		chefEditButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					getChefDialog();
				}
				return false;
			}});

		//If the save button selected - save the recipes
		Button saveButton = (Button) findViewById(R.id.saveButton);
		utils.setButtonText(R.id.saveButton, 22);
		saveButton.setOnTouchListener(new OnTouchListener(){


			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					saveRecipe(false);

				}
				return false;
			}});

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		nav.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		nav.config(newConfig);
	}

	/**
	 * Handles action bar selections
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean result = nav.drawerToggle(item);
		switch (item.getItemId()) {

		default:
			result = false;
		}

		return result;

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_plain, menu);
		utils.setUpSearch(menu);
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Handles on back pressed , checks whether the user wants to save recipe before going back
	 */
	@Override
	public void onBackPressed() {
		//Reminds user to save before leaving page
		final Dialog dialog = utils.createDialog(RecipeEditActivity.this, R.layout.general_savedialog);
		utils.setDialogText(R.id.textView, dialog, 18);
		// Show dialog
		dialog.show();
		Button button = utils.setButtonTextDialog(R.id.yesButton, 22, dialog);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveRecipe(true);
				dialog.dismiss();


			}
		});
		Button button2 = utils.setButtonTextDialog(R.id.noButton, 22, dialog);
		button2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dialog.dismiss();
				finish();
			}
		});
	}
	/**
	 * Set text style
	 */
	public void setStyle()
	{
		utils.setTextPink(R.id.recipeTitle, 26);
		utils.setTextBlackItalic(R.id.recipeDesc, 22);
		utils.setTextPink(R.id.serves, 22);
		utils.setTextBlack(R.id.servesVal, 20);
		utils.setTextPink(R.id.prepTime, 20);
		utils.setTextPink(R.id.cookingTime, 20);
		utils.setTextBlack(R.id.prepTimeVal, 20);
		utils.setTextBlack(R.id.cookingTimeVal, 20);
		utils.setTextPink(R.id.ingredientTitle, 26);
		utils.setTextPink(R.id.methodTitle, 26);
		utils.setTextBlack(R.id.ingredientList, 22);
		utils.setTextBlack(R.id.methodList, 22);	
		utils.setTextPink(R.id.tipsTitle, 26);
		utils.setTextBlack(R.id.tips, 22);
		utils.setTextBlack(R.id.diffVal, 22);
		utils.setTextPink(R.id.difficulty, 22);
		utils.setTextBlack(R.id.cusineVal, 22);
		utils.setTextPink(R.id.cusine, 22);
		utils.setTextBlack(R.id.dietaryVal, 22);
		utils.setTextPink(R.id.dietary, 22); 

	}

	/**
	 * Sets up the text for the edit view page
	 */
	public void setTextForLayout()
	{
		RecipeModel model = new RecipeModel(getApplicationContext());

		prepList = new ArrayList<PreperationBean>();
		ingredList = new ArrayList<IngredientBean>();
		addIngredList = new ArrayList<IngredientBean>();
		addPrepList = new ArrayList<PreperationBean>();
		Intent intent = getIntent();
		recipe = model.selectRecipe2(intent.getStringExtra("uniqueidr"));
		prepList = model.selectPreperation(recipe.getId());
		ingredList = model.selectIngredients(recipe.getId());
		imgBean = model.selectImages(recipe.getId());
		ImageView img = (ImageView) findViewById(R.id.foodImage);
		ImageLoader task = new ImageLoader(getApplicationContext(),imgBean, img);
		task.execute();

		//Orders instructions in order
		TextView instructions = (TextView) findViewById(R.id.methodList);
		Collections.sort(prepList, new Comparator<PreperationBean>() {
			@Override 
			public int compare(PreperationBean p1, PreperationBean p2) {
				return p1.getPrepNum() - p2.getPrepNum(); // Ascending
			}});

		for(int i = 0; i < prepList.size(); i++)
		{
			if(prepList.get(i).getProgress().equals("added"))
			{
				instructions.append(Integer.toString(prepList.get(i).getPrepNum()) + ". " +prepList.get(i).getPreperation().toString() + "\n");

			}
		}

		TextView ingredients = (TextView) findViewById(R.id.ingredientList);
		for(int i = 0; i < ingredList.size(); i++)
		{
			if(ingredList.get(i).getProgress().equals("added"))
			{
				ingredients.append("- " + ingredList.get(i).getAmount() + " "+  ingredList.get(i).getValue() + " " + ingredList.get(i).getName().toString() + " - " + ingredList.get(i).getNote().toString() + "\n");

			}
		}		
		utils.setTextString(R.id.recipeTitle, recipe.getName());
		utils.setTextString(R.id.recipeDesc, recipe.getDesc());
		utils.setTextString(R.id.servesVal, recipe.getServes());
		utils.setTextString(R.id.prepTimeVal, recipe.getPrep());
		utils.setTextString(R.id.cookingTimeVal, recipe.getCooking());
		utils.setTextString(R.id.diffVal, recipe.getDifficulty());
		utils.setTextString(R.id.tips, recipe.getTips());
		utils.setTextString(R.id.dietaryVal, recipe.getDietary());
		utils.setTextString(R.id.cusineVal, recipe.getCusine()); 
	}

	/**
	 * Creates a dialog showing the prep and cooking values which the user can change
	 */
	public void getTimeDialog()
	{
		timeDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog3);
		utils.setDialogText(R.id.recipeEditView, timeDialog, 22);
		utils.setDialogText(R.id.recipePrepView, timeDialog, 22);
		utils.setDialogText(R.id.recipeCookingView, timeDialog, 22);
		utils.setDialogTextString(R.id.recipePrepEditText, timeDialog, recipe.getPrep());
		utils.setDialogTextString(R.id.recipeCookingEditText, timeDialog, recipe.getCooking());
		utils.setTimePickerFrag(timeDialog, R.id.recipePrepEditText, mHandler);
		utils.setTimePickerFrag(timeDialog, R.id.recipeCookingEditText, mHandler2);
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

	/**
	 * Creates a dialog where the user can edit the serves information
	 */
	public void getServesDialog()
	{
		servesDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog2);
		final TextView errorView = (TextView) servesDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,servesDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, servesDialog, 22);
		utils.setDialogText(R.id.recipeServesView, servesDialog, 22);
		utils.setDialogTextString(R.id.recipeServesEditText, servesDialog, recipe.getServes());
		utils.setDialogText(R.id.recipeDifficultyView, servesDialog, 22);

		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Easy");
		spinnerArray.add("Medium");
		spinnerArray.add("Hard");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) servesDialog.findViewById(R.id.recipeDifficultySpinner);
		sItems.getBackground().setColorFilter(this.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setSelection(utils.getIndex(sItems, recipe.getDifficulty()));

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

	/**
	 * Creates a title dialog where the user can edit the title dialog
	 */
	public void getTitleDialog()
	{
		titleDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog1);
		final TextView errorView = (TextView) titleDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,titleDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, titleDialog, 22);
		utils.setDialogText(R.id.recipeNameView, titleDialog, 22);
		utils.setDialogText(R.id.recipeDescView, titleDialog, 22);
		utils.setDialogTextString(R.id.recipenameEditText, titleDialog, recipe.getName());
		utils.setDialogTextString(R.id.recipeDescEdit, titleDialog, recipe.getDesc());
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

	/**
	 * Creates a title dialog where the user can edit the title dialog
	 */
	public void getTipsDialog()
	{
		final Dialog tipsDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog6);
		final TextView errorView = (TextView) tipsDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,tipsDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, tipsDialog, 22);
		utils.setDialogText(R.id.recipeTipsView, tipsDialog, 22);
		utils.setDialogTextString(R.id.recipeTipsEditText, tipsDialog, recipe.getTips());
		Button tipsButton = utils.setButtonTextDialog(R.id.saveButton, 22, tipsDialog);
		tipsButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {

				utils.setTextString(R.id.tips, utils.getTextFromDialog(R.id.recipeTipsEditText, tipsDialog));

				tipsDialog.dismiss();


			}});
		tipsDialog.show(); 
	}

	public void getChefDialog()
	{
		final Dialog cusineDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog8);
		final TextView errorView = (TextView) cusineDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,cusineDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditView, cusineDialog, 22);
		utils.setDialogText(R.id.recipeCusineView, cusineDialog, 22);

		utils.setDialogText(R.id.recipeDietaryView, cusineDialog, 22);

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
				this, R.layout.general_spinner_item, cusineSpinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) cusineDialog.findViewById(R.id.recipeCusineSpinner);
		sItems.getBackground().setColorFilter(this.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setSelection(utils.getIndex(sItems, recipe.getCusine()));

		List<String> dietarySpinnerArray =  new ArrayList<String>();
		dietarySpinnerArray.add("Nut free");
		dietarySpinnerArray.add("Gluten free");
		dietarySpinnerArray.add("Vegeterian");
		dietarySpinnerArray.add("Vegan");
		dietarySpinnerArray.add("N/A");


		ArrayAdapter<String> dietaryAdapter = new ArrayAdapter<String>(
				this, R.layout.general_spinner_item, dietarySpinnerArray);
		dietaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner dietaryItems = (Spinner) cusineDialog.findViewById(R.id.recipeDietarySpinner);
		dietaryItems.getBackground().setColorFilter(this.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		dietaryItems.setAdapter(dietaryAdapter);
		dietaryItems.setSelection(utils.getIndex(sItems, recipe.getDietary()));

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


	/**
	 * Creates a dialog on the fly based on preperation details for database
	 * Note - Had to be created programmatically as we dont know the exact amount of prep steps for each recipe
	 */
	public void getAndCreatePrepDialog()
	{
		//Build dialog in linear layout
		prepDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog4);
		utils.setDialogText(R.id.recipeEditView, prepDialog, 22);
		prepNumEditIds = new ArrayList<Integer>();
		prepEditIds = new ArrayList<Integer>();
		LinearLayout prepDialogLinearLayout = (LinearLayout)prepDialog.findViewById(R.id.editdialog);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,      
				LayoutParams.WRAP_CONTENT
				);
		//Set the text view and edit text data for a linear layout
		for(int i = 0; i < prepList.size(); i++)
		{
			if(prepList.get(i).getProgress().equals("added"))
			{
				final int point = i;
				LinearLayout linearLayoutInDialog = new LinearLayout(RecipeEditActivity.this);
				linearLayoutInDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				params.setMargins(5,5,5,5);
				final TextView prepView = new TextView(RecipeEditActivity.this);
				prepView.setText("Preperation");
				int prepviewid = findId();
				prepView.setId(prepviewid);
				prepView.setLayoutParams(params);

				final TextView prepNumView = new TextView(RecipeEditActivity.this);
				prepNumView.setText("Preperation Num");
				int prepNumViewId = findId();
				prepNumView.setId(prepNumViewId);
				prepNumView.setLayoutParams(params);


				final EditText prepEdit = new EditText(RecipeEditActivity.this);
				int prepEditId = findId();
				prepEditIds.add(prepEditId);
				prepEdit.setId(prepEditId);
				prepEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT);
				lparams.setMargins(5,5,5,5);
				prepEdit.setLayoutParams(lparams);

				final EditText prepNumEdit = new EditText(RecipeEditActivity.this);
				int ids = findId();
				prepNumEditIds.add(ids);
				prepNumEdit.setId(ids);
				prepNumEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				prepNumEdit.setLayoutParams(params);

				ImageButton img = new ImageButton(RecipeEditActivity.this);
				int imgid = findId();
				img.setId(imgid);
				img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_delete));
				LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
				lparams.setMargins(5,5,5,5);
				img.setLayoutParams(lparams2);

				linearLayoutInDialog.addView(prepNumView);
				linearLayoutInDialog.addView(prepNumEdit);
				linearLayoutInDialog.addView(prepView);
				linearLayoutInDialog.addView(prepEdit);
				linearLayoutInDialog.addView(img);
				prepDialogLinearLayout.addView(linearLayoutInDialog);

				//Create styles
				utils.setDialogText(prepviewid, prepDialog, 22);
				prepEdit.setText(prepList.get(i).getPreperation());
				prepEdit.setWidth(400);
				utils.setDialogText(prepNumViewId, prepDialog, 22);
				prepNumEdit.setText(Integer.toString(prepList.get(i).getPrepNum()));


				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.v("POINT", "POINT " + point);
						prepList.get(point).setProgress("deleted");
						prepNumEdit.setVisibility(View.INVISIBLE);
						prepEdit.setVisibility(View.INVISIBLE);
						prepNumView.setVisibility(View.INVISIBLE);
						prepView.setVisibility(View.INVISIBLE);


					}});

			}


		}
		//Create a button and when on click change prep list to what was set in the dialog box
		Button okButton = new Button(RecipeEditActivity.this);
		int buttonId = findId();
		okButton.setId(buttonId);
		okButton.setText("Ok");
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		okButton.setBackgroundResource(R.drawable.drawable_button);
		prepDialogLinearLayout.addView(okButton);
		okButton = utils.setButtonTextDialog(buttonId, 16, prepDialog);

		final TextView errorView = new TextView(RecipeEditActivity.this);
		int errorId = findId();
		errorView.setId(errorId);
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		prepDialogLinearLayout.addView(errorView);
		utils.setDialogText(errorId,prepDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));


		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				modifiedPrepList = new ArrayList<PreperationBean>();
				boolean dismissed = false;
				int a = 0;
				for(int i = 0; i < prepList.size(); i++)
				{

					if(prepList.get(i).getProgress().equals("added"))
					{
						PreperationBean prep = new PreperationBean();
						if(utils.getTextFromDialog(prepEditIds.get(i), prepDialog).equals(""))
						{
							errorView.setText("Please input text into all the textboxes");
						}
						else if(utils.getTextFromDialog(prepNumEditIds.get(i), prepDialog).equals(""))
						{
							errorView.setText("Please input text into all the textboxes");
						}
						else
						{

							prep.setPreperation(utils.getTextFromDialog(prepEditIds.get(i), prepDialog));
							prep.setPrepNum(Integer.parseInt(utils.getTextFromDialog(prepNumEditIds.get(i), prepDialog)));
							prep.setUniqueid(prepList.get(i).getUniqueid());
							prep.setProgress(prepList.get(i).getProgress());
							modifiedPrepList.add(prep);

							a += 2;
							if(a == (prepList.size() * 2))
							{
								prepList = modifiedPrepList;
								prepDialog.dismiss();
								dismissed = true;
							} 
						}
					}
				}

				//Set prep list to new modified list
				if(dismissed == true)
				{
					TextView instructions = (TextView) findViewById(R.id.methodList);
					instructions.setText("");
					//Order list
					Collections.sort(modifiedPrepList, new Comparator<PreperationBean>() {
						@Override 
						public int compare(PreperationBean p1, PreperationBean p2) {
							return p1.getPrepNum() - p2.getPrepNum(); // Ascending
						}});
					//Set on edit page
					for(int i = 0; i < modifiedPrepList.size(); i++)
					{
						if(modifiedPrepList.get(i).getProgress().equals("added"))
						{
							instructions.append(Integer.toString(modifiedPrepList.get(i).getPrepNum()) + ". " +modifiedPrepList.get(i).getPreperation().toString() + "\n");

						} 
					}
				}


			}});
		prepDialog.show();
	}


	/**
	 * Creates a dialog for ingredients on the fly based on information from the database
	 * Note - I had to write this programmatically as we dont know the amount of ingredients for a recipe
	 */
	@SuppressLint("NewApi")
	public void getAndCreateIngredDialog()
	{
		ingredDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe_edit_dialog5);
		utils.setDialogText(R.id.recipeEditView, ingredDialog, 22);
		utils.setDialogText(R.id.amountTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.valueTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.ingredientTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.noteTitleView, ingredDialog, 22);

		amountEditIds = new ArrayList<Integer>();
		noteEditIds = new ArrayList<Integer>();
		valueEditIds = new ArrayList<Integer>();
		ingredEditIds = new ArrayList<Integer>();
		LinearLayout ingredDialogLinearLayout = (LinearLayout)ingredDialog.findViewById(R.id.editdialog);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,      
				LayoutParams.WRAP_CONTENT
				);
		//Create dialog with textviews and edit text from the database
		for(int i = 0; i < ingredList.size(); i++)
		{
			final int point = i;
			if(ingredList.get(i).getProgress().equals("added"))
			{
				LinearLayout linearLayoutInDialog = new LinearLayout(RecipeEditActivity.this);
				linearLayoutInDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


				params.setMargins(5,5,5,5);

				final EditText amountEdit = new EditText(RecipeEditActivity.this);
				int amountEditId = findId();
				amountEditIds.add(amountEditId);
				amountEdit.setId(amountEditId);
				amountEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				amountEdit.setLayoutParams(params);
				amountEdit.setWidth(80);

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
						RecipeEditActivity.this, R.layout.general_spinner_item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				final Spinner sItems = new Spinner(RecipeEditActivity.this);
				sItems.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
				sItems.setAdapter(adapter);
				sItems.setLayoutParams(params);

				int valueEditId = findId();
				valueEditIds.add(valueEditId);
				sItems.setId(valueEditId); 

				final EditText ingredEdit = new EditText(RecipeEditActivity.this);
				int ingredEditId = findId();
				ingredEditIds.add(ingredEditId);
				ingredEdit.setId(ingredEditId);
				ingredEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				ingredEdit.setLayoutParams(params);
				ingredEdit.setWidth(200);

				final TextView view = new TextView(RecipeEditActivity.this);
				int viewid = findId();
				view.setText(" - ");
				view.setId(viewid);

				final EditText noteEdit = new EditText(RecipeEditActivity.this);
				int noteEditId = findId();
				noteEditIds.add(noteEditId);
				noteEdit.setId(noteEditId);
				noteEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				noteEdit.setLayoutParams(params);
				noteEdit.setWidth(160);

				ImageButton img = new ImageButton(RecipeEditActivity.this);
				int imgid = findId();
				img.setId(imgid);
				img.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_delete));
				LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
				lparams2.setMargins(5,5,5,5);
				img.setLayoutParams(lparams2);

				linearLayoutInDialog.addView(amountEdit);
				linearLayoutInDialog.addView(sItems);
				linearLayoutInDialog.addView(ingredEdit);
				linearLayoutInDialog.addView(view);
				linearLayoutInDialog.addView(noteEdit);
				linearLayoutInDialog.addView(img);

				ingredDialogLinearLayout.addView(linearLayoutInDialog);
				amountEdit.setText(Integer.toString(ingredList.get(i).getAmount()));
				ingredEdit.setText(ingredList.get(i).getName());
				noteEdit.setText(ingredList.get(i).getNote());
				sItems.setSelection(utils.getIndex(sItems, ingredList.get(i).getValue()));
				utils.setDialogText(viewid, ingredDialog, 22);

				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.v("POINT", "POINT " + point);
						ingredList.get(point).setProgress("deleted");
						amountEdit.setVisibility(View.INVISIBLE);
						ingredEdit.setVisibility(View.INVISIBLE);
						sItems.setVisibility(View.INVISIBLE);
						view.setVisibility(View.INVISIBLE);
						noteEdit.setVisibility(View.INVISIBLE);


					}});
			}
		}
		Button okButton = new Button(RecipeEditActivity.this);
		int buttonId = findId();
		okButton.setId(buttonId);
		okButton.setText("Ok");
		okButton.setBackgroundResource(R.drawable.drawable_button);
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		ingredDialogLinearLayout.addView(okButton);
		okButton = utils.setButtonTextDialog(buttonId, 16, ingredDialog);

		final TextView errorView = new TextView(RecipeEditActivity.this);
		int errorId = findId();
		errorView.setId(errorId);
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		ingredDialogLinearLayout.addView(errorView);
		utils.setDialogText(errorId,ingredDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));

		//When ok button clicked get new ingredient list
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				modifiedIngredList = new ArrayList<IngredientBean>();
				boolean dismissed = false;
				int b = 0;
				for(int i = 0; i < ingredList.size(); i++)
				{
					IngredientBean ingred = new IngredientBean();
					if(utils.getTextFromDialog(ingredEditIds.get(i), ingredDialog).equals(""))
					{
						errorView.setText("Please input text into all the textboxes");
					}
					else if(utils.getTextFromDialog(amountEditIds.get(i), ingredDialog).equals(""))
					{
						errorView.setText("Please input text into all the textboxes");
					}
					else
					{
						b += 4;
						ingred.setName(utils.getTextFromDialog(ingredEditIds.get(i), ingredDialog));
						ingred.setAmount(Integer.parseInt(utils.getTextFromDialog(amountEditIds.get(i), ingredDialog)));
						ingred.setNote(utils.getTextFromDialog(noteEditIds.get(i), ingredDialog));
						ingred.setProgress(ingredList.get(i).getProgress());
						Spinner spinner = (Spinner) ingredDialog.findViewById(valueEditIds.get(i));
						String value = spinner.getSelectedItem().toString();
						ingred.setValue(value);			
						ingred.setUniqueid(ingredList.get(i).getUniqueid());
						modifiedIngredList.add(ingred);
						if(b == (ingredList.size() * 4))
						{
							//set ingred list to new modified ingred list
							dismissed = true;
							ingredList = modifiedIngredList;
							ingredDialog.dismiss();
						}
					}
				}
				if(dismissed == true)
				{
					//Apply to edit page
					TextView ingredients = (TextView) findViewById(R.id.ingredientList);
					ingredients.setText("");
					for(int i = 0; i < modifiedIngredList.size(); i++)
					{
						if(modifiedIngredList.get(i).getProgress().equals("added"))
						{
							ingredients.append("- " + modifiedIngredList.get(i).getAmount() + " "+  modifiedIngredList.get(i).getValue() + " " + modifiedIngredList.get(i).getName().toString() + " - " + modifiedIngredList.get(i).getNote().toString() + "\n");

						}
					}
				}

			}});

		ingredDialog.show();	

	}

	/**
	 * Saves recipe by updating in the database
	 */
	public void saveRecipe(final boolean close)
	{
		RecipeBean recipechange = new RecipeBean();
		recipechange.setName(utils.getTextView(R.id.recipeTitle));
		recipechange.setDesc(utils.getTextView(R.id.recipeDesc));
		recipechange.setServes(utils.getTextView(R.id.servesVal));
		recipechange.setPrep(utils.getTextView(R.id.prepTimeVal));
		recipechange.setCooking(utils.getTextView(R.id.cookingTimeVal));
		recipechange.setUniqueid(recipe.getUniqueid());
		recipechange.setProgress("added");
		recipechange.setId(recipe.getId());
		recipechange.setTips(utils.getTextView(R.id.tips));
		recipechange.setDietary(utils.getTextView(R.id.dietaryVal));
		recipechange.setDifficulty(utils.getTextView(R.id.diffVal));
		recipechange.setCusine(utils.getTextView(R.id.cusineVal));
		RecipeModel rm = new RecipeModel(getApplicationContext());
		try
		{
			rm.insertPrepFromEdit(false, addPrepList, recipechange);
			addPrepList.clear();
			rm.insertIngredFromEdit(false, addIngredList, recipechange);
			addIngredList.clear();
			rm.updateRecipe(recipechange, prepList, ingredList, imgBean, false );	
			final Dialog dialog = utils.createDialog(RecipeEditActivity.this, R.layout.general_dialog);
			utils.setDialogText(R.id.textView, dialog, 18);
			TextView txtView = (TextView) dialog.findViewById(R.id.textView);
			txtView.setText("Recipe has been saved");
			// Show dialog
			dialog.show();
			Button button = utils.setButtonTextDialog(R.id.okButton, 22, dialog);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					dialog.dismiss();
					if(close == true)
					{
						finish();
					}
				}
			});
		}catch(SQLException e)
		{
			Toast.makeText(getApplicationContext(), "Recipe was not edited", Toast.LENGTH_LONG).show();
		}

	}

	public void setUpStepAddDialog()
	{
		final Dialog recipeAddStepDialog = utils.createDialog(this, R.layout.recipe_add_dialog4);		
		utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
		utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
		utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
		Button addButton = utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getRecipeStep(recipeAddStepDialog);
			}});
		recipeAddStepDialog.show();
	}

	public void setUpIngredAddDialog()
	{
		final Dialog recipeIngredDialog = utils.createDialog(this,R.layout.recipe_add_dialog5);
		utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
		Button addButton = utils.setButtonTextDialog(R.id.addIngredButton, 22, recipeIngredDialog);

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
		spinnerArray.add("other");

		//Fill spinner
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				this, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		sItems.getBackground().setColorFilter(getApplicationContext().getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getIngredient(recipeIngredDialog);

			}}); 
		recipeIngredDialog.show();
	}


	public void getRecipeStep(Dialog recipeAddStepDialog)
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
			recipeAddStepDialog.dismiss();
			//Sets the details to a prep bean
			PreperationBean prepBean = new PreperationBean();
			prepBean.setPreperation(step);
			prepBean.setPrepNum(Integer.parseInt(stepNum));
			prepBean.setProgress("added");
			RecipeModel rm = new RecipeModel(getApplicationContext());
			prepBean.setUniqueid(rm.generateuuid(recipe.getAddedBy(), "Preperation"));
			prepList.add(prepBean);
			addPrepList.add(prepBean);
			TextView instructions = (TextView) findViewById(R.id.methodList);
			instructions.setText("");
			Collections.sort(prepList, new Comparator<PreperationBean>() {
				@Override 
				public int compare(PreperationBean p1, PreperationBean p2) {
					return p1.getPrepNum() - p2.getPrepNum(); // Ascending
				}});

			for(int i = 0; i < prepList.size(); i++)
			{
				if(prepList.get(i).getProgress().equals("added"))
				{
					instructions.append(Integer.toString(prepList.get(i).getPrepNum()) + ". " +prepList.get(i).getPreperation().toString() + "\n");

				}
			}
		}
	}

	/**
	 * Get information from ingredient dialogs
	 */
	public void getIngredient(Dialog recipeIngredDialog)
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
			recipeIngredDialog.dismiss();
			//Sets details to ingredient bean

			IngredientBean ingredBean = new IngredientBean();
			RecipeModel rm = new RecipeModel(getApplicationContext());
			ingredBean.setUniqueid(rm.generateuuid(recipe.getAddedBy(), "IngredientDetails"));
			ingredBean.setName(ingredient);
			ingredBean.setAmount(Integer.parseInt(amount));
			ingredBean.setNote(note);
			ingredBean.setValue(value);
			ingredBean.setProgress("added");
			ingredList.add(ingredBean);
			addIngredList.add(ingredBean);
			recipeIngredDialog.dismiss();
			//How ingredients are displayed in the edit text box
			TextView ingredsEdit = (TextView) findViewById(R.id.ingredientList);
			if(note.equals(""))
			{
				ingredsEdit.append(  amount + " " + value + " " + ingredient + "\n");
			}
			else
			{
				ingredsEdit.append( amount + " " + value + " " + ingredient + " - " + note + "\n");
			} 
		}


	}


	/**
	 * Retrieves result from activity intent
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)
	{
		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					//Gets image and its file and rotates it
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());
					String imageName = f.getName();
					//set image name to edit text
					utils.setDialogTextString(R.id.recipeImagesEditText, imageDialog, imageName);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					//compresses image and set to byte array
					yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray(); 		
					imgBean.setImage(byteArray);			
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}



	/**
	 * Finds current available id's - found online
	 * @return
	 */
	public int findId(){  
		View v = findViewById(id);  
		while (v != null)
		{  
			v = findViewById(++id);  
		}  
		return id++;  
	}


}
