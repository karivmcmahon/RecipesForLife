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
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.util.ImageLoader;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Class to show edit view for recipes
 * @author Kari
 *
 */
public class Recipe_EditView extends ActionBarActivity {

	Util utils;
	static RecipeBean recipe;
	public static final String MyPREFERENCES = "MyPrefs";
	Dialog imageDialog, prepDialog, ingredDialog;
	static Dialog timeDialog;
    static final String emailk = "emailKey";
    static ArrayList<PreperationBean> prepList, modifiedPrepList, addPrepList;
    static ArrayList<IngredientBean> ingredList, modifiedIngredList, addIngredList;
	ImageBean imgBean;
	ArrayList<Integer> amountEditIds, valueEditIds, ingredEditIds, noteEditIds, prepNumEditIds, prepEditIds;
	public static int id = 1;
	Navigation_DrawerCreation nav;
	private static final int SELECT_PHOTO = 100;
	Recipe_EditView_EditPreperation prep;
	Recipe_EditView_EditIngredient ingred;
	Recipe_EditView_EditMisc misc;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipe_view_edit);

		prep = new  Recipe_EditView_EditPreperation( getApplicationContext(), this);
		ingred = new Recipe_EditView_EditIngredient(getApplicationContext(), this);
		misc = new Recipe_EditView_EditMisc(getApplicationContext(), this);
		utils = new Util(getApplicationContext(), this);
		recipe = new RecipeBean();
		setStyle();
		setTextForLayout();
		String recipename = utils.getTextView(R.id.recipeTitle);


		nav = new Navigation_DrawerCreation(this, "Edit Recipe Name");
		nav.createDrawer();
		SpannableString s = new SpannableString("Edit " + recipename);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		handleIngredPrepButtonClicks();
		handleMiscImageButtonClicks();

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
		final Dialog dialog = utils.createDialog(Recipe_EditView.this, R.layout.general_savedialog);
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
		ApplicationModel_RecipeModel model = new ApplicationModel_RecipeModel(getApplicationContext());

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
		updatePrepList();
		updateIngredList();

		
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
	 * Handle clicks on the edit view for ingredients and prep
	 */
	public void handleIngredPrepButtonClicks()
	{
		ImageView ingredAddButton = (ImageView) findViewById(R.id.ingredAddImage);
		ingredAddButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					ingred.setUpIngredAddDialog();
				}
				return false;
			}});
		
		ImageView methodAddButton = (ImageView) findViewById(R.id.methodAddImage);
		methodAddButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					prep.setUpStepAddDialog();
				}
				return false;
			}});
		
		//If edit prep icon selected display the prep dialog
				ImageView prepButton = (ImageView) findViewById(R.id.methodEditImage);
				prepButton.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							prep.getPreperation();
						}
						return false;
					}});

				//If edit ingred icon selected display the ingred dialog
				ImageView ingredButton = (ImageView) findViewById(R.id.ingredEditImage);
				ingredButton.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							ingred.getIngredient();
						}
						return false;
					}});
				
	}
	
	/**
	 * Updates the ingred list in edit view
	 */
	public void updateIngredList()
	{
		TextView ingredients = (TextView) findViewById(R.id.ingredientList);
		for(int i = 0; i < ingredList.size(); i++)
		{
			if(ingredList.get(i).getProgress().equals("added"))
			{
				ingredients.append("- " + ingredList.get(i).getAmount() + " "+  ingredList.get(i).getValue().replace("other", "") + " " + ingredList.get(i).getName().toString() + " - " + ingredList.get(i).getNote().toString() + "\n");

			}
		}		
	}

	/**
	 * Updates the prep list in edit view
	 */
	public void updatePrepList()
	{
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
				instructions.append(Integer.toString(prepList.get(i).getPrepNum()) + ". " +prepList.get(i).getPreperation().toString() + "\n \n");

			}
		}
	}
	
	/**
	 * Handles image clicks on the edit view
	 */
	public void handleMiscImageButtonClicks()
	{
		//Set up the various edit buttons for the page
		ImageView titleButton = (ImageView) findViewById(R.id.recipeTitleEditImage);
		titleButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					misc.getTitleDialog();
				}
				return false;
			}});

		ImageView imageButton = (ImageView) findViewById(R.id.imageEditImage);
		imageButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					//set image dialog style
					handleImageChange();
				}
					return false;
				}});
		
		//If edit serves icon selected display the serve dialog
		ImageView servesButton = (ImageView) findViewById(R.id.servesEditImage);
		servesButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					misc.getServesDialog();
				}
				return false;
			}});	

		//If edit time icon  selected display the times dialog
		ImageView timeButton = (ImageView) findViewById(R.id.timeEditImage);
		timeButton.setOnTouchListener(new OnTouchListener(){


			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					misc.getTimeDialog();
				}
				return false;
			}}); 

		ImageView tipsButton = (ImageView) findViewById(R.id.tipsEditImage);
		tipsButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					misc.getTipsDialog();
				}
				return false;
			}});

		ImageView chefEditButton = (ImageView) findViewById(R.id.chefEditImage);
		chefEditButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					misc.getChefDialog();
				}
				return false;
			}});
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
		ApplicationModel_RecipeModel rm = new ApplicationModel_RecipeModel(getApplicationContext());
		try
		{
			rm.insertPrepFromEdit(false, addPrepList, recipechange);
			addPrepList.clear();
			rm.insertIngredFromEdit(false, addIngredList, recipechange);
			addIngredList.clear();
			rm.updateRecipe(recipechange, prepList, ingredList, imgBean, false );	
			final Dialog dialog = utils.createDialog(Recipe_EditView.this, R.layout.general_dialog);
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
	 * If the user edits the image this methods handles the image change
	 */
	public void handleImageChange()
	{
		imageDialog = utils.createDialog(Recipe_EditView.this, R.layout.recipe_edit_dialog7);
		final TextView errorView = (TextView) imageDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,imageDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.recipeEditImageView, imageDialog, 22);
		utils.setDialogText(R.id.recipeImagesView, imageDialog, 22);

		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, imageDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imageDialog.dismiss();

			}});

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
					imageDialog.dismiss();
					//Loads image for imageview
					ImageView img = (ImageView) findViewById(R.id.foodImage);
					ImageLoader task = new ImageLoader(getApplicationContext(),imgBean, img);
					task.execute();
				}
				return false;
			}});
		imageDialog.show();
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
