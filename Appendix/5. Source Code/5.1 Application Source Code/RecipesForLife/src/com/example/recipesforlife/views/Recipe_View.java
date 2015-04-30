package com.example.recipesforlife.views;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.models.ApplicationModel_ReviewModel;
import com.example.recipesforlife.util.ImageLoader;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;


/**
 * Class to show recipe view 
 * @author Kari
 *
 */
public class Recipe_View extends ActionBarActivity {
	private Util utils;
	private Navigation_DrawerCreation nav;
	private int counter = 0;
	private int fullScreenCounter = 0;
	private ShareActionProvider mShareActionProvider;
	private String recipeName = "";
	private int recipeFont = 22;
	private int recipeFontHeader = 26;
	private RecipeBean recipe;
	private static final String MyPREFERENCES = "MyPrefs" ;
	private static final String emailk = "emailKey"; 
	private SharedPreferences sharedpreferences;
	private ArrayList<ReviewBean> rbs;
	private Recipe_Review_Adapter adapter;
	private ImageView img;
	private Dialog cloneDialog;
	private TextView errorView;
	private ArrayList<CookbookBean> cbList;
	private ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
	private ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
	private ImageBean imgBean = new ImageBean();
	private File file;
	
	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipe_view);
		
		utils = new Util(getApplicationContext(), this);
		setStyle();
		setTextForLayout();
		
		//set up a nav bar
		nav = new Navigation_DrawerCreation(this, recipeName );
		nav.createDrawer();
		SpannableString s = new SpannableString(recipeName);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		//Gets and displays the reviews for the recipes
		final ApplicationModel_ReviewModel reviewmodel = new ApplicationModel_ReviewModel(getApplicationContext());
		rbs = reviewmodel.selectReviews(recipe.getId());
		ListView listView = (ListView) findViewById(R.id.reviewlist);
		adapter = new Recipe_Review_Adapter( getApplicationContext(), this,  rbs);
		listView.setAdapter(adapter); 

		//Sets review comment style
		utils.setButtonText(R.id.sumbitButton, 22);
		final TextView errorView = (TextView) findViewById(R.id.errorView);
		utils.setText(R.id.errorView,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		
		//when review submited
		Button submitbutton = (Button) findViewById(R.id.sumbitButton);
		submitbutton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				errorView.setText("");
				sharedpreferences = getApplicationContext().getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
				ReviewBean rb = new ReviewBean();
				rb.setComment(utils.getText(R.id.reviewBox));
				rb.setRecipeid(recipe.getId());
				rb.setUser(sharedpreferences.getString(emailk, "DEFAULT"));
				try
				{
					//checks for errors and then submits review
					if(rb.getComment().equals(""))
					{
						errorView.setText("Please enter a comment");
					}
					else
					{
						//Inserts review
						reviewmodel.insertReview(rb, false);
						rbs.add(0, rb);
						adapter.notifyDataSetChanged();
						utils.setTextString(R.id.reviewBox, "");
					}
				}catch(SQLException e)
				{
					Toast.makeText(getApplicationContext(), "Review was not added", Toast.LENGTH_LONG).show();
				}


			}});





	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		nav.syncState();
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		nav.config(newConfig);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Handling action bar selections
		nav.drawerToggle(item);

		//These choices increase or decrease font size
		if(item.getItemId() ==  R.id.action_fontdown)
		{
			recipeFont--;
			recipeFontHeader--;		 
		}
		if(item.getItemId() ==  R.id.action_fontup)
		{
			recipeFont++;
			recipeFontHeader++;		
		}
		//if full screen icon selected then switch the full screen on or off
		if(item.getItemId() ==  R.id.action_fullScreen)
		{
			fullScreenCounter++;
			if(fullScreenCounter == 1)
			{
				getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
				Toast.makeText(getApplicationContext(), 
						"Full screen on", Toast.LENGTH_LONG).show();
			}
			if(fullScreenCounter == 2)
			{
				getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
				Toast.makeText(getApplicationContext(), 
						"Full screen off", Toast.LENGTH_LONG).show();
				fullScreenCounter = 0;
			}

		}
		//switches screen sleep on and off based on selection
		if(item.getItemId() == R.id.action_screenOn)
		{
			counter++;
			if(counter == 1)
			{
				getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(getApplicationContext(), 
						"Screen sleep off", Toast.LENGTH_LONG).show();
			}
			if(counter == 2)
			{
				getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				Toast.makeText(getApplicationContext(), 
						"Screen sleep back on", Toast.LENGTH_LONG).show();
				counter = 0;
			}
		}
		if(item.getItemId() == R.id.action_copy)
		{
			createCloneDialog(); //Displays clone dialog
		}
		setStyle(); 
		return super.onOptionsItemSelected(item);

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_recipe_view, menu);

		//Sets up search manager
		SearchManager searchManager =
				(SearchManager) getSystemService(Context.SEARCH_SERVICE);
		android.support.v7.widget.SearchView searchView =
				(android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		searchView.setSearchableInfo(
				searchManager.getSearchableInfo(getComponentName()));

		MenuItem item = menu.findItem(R.id.action_share);

		//Creates a share recipe link when the share button on the action bar selected
		mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
		if(mShareActionProvider != null)
		{
			Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
			String shareBody = "Check out my recipe for " + recipeName +  " on the android app Recipes For Life" ;
			sharingIntent.setType("image/jpeg");
			sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
			sharingIntent.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(img));
			mShareActionProvider.setShareIntent(sharingIntent);
		}


		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	/**
	 * Sets style for the recipe view
	 */
	private void setStyle()
	{
		utils.setTextPink(R.id.recipeTitle, recipeFontHeader);
		utils.setTextBlackItalic(R.id.recipeDesc, recipeFont);
		utils.setTextPink(R.id.serves, recipeFont);
		utils.setTextBlack(R.id.servesVal, recipeFont);
		utils.setTextPink(R.id.difficulty, recipeFont);
		utils.setTextBlack(R.id.diffVal, recipeFont);
		utils.setTextPink(R.id.dietary, recipeFont);
		utils.setTextBlack(R.id.dietaryVal, recipeFont);
		utils.setTextPink(R.id.cusine, recipeFont);
		utils.setTextBlack(R.id.cusineVal, recipeFont);
		utils.setTextPink(R.id.tipsTitle, recipeFontHeader);
		utils.setTextBlack(R.id.tips, recipeFont);
		utils.setTextPink(R.id.prepTime, recipeFont);
		utils.setTextPink(R.id.cookingTime, recipeFont);
		utils.setTextBlack(R.id.prepTimeVal, recipeFont);
		utils.setTextBlack(R.id.cookingTimeVal, recipeFont);
		utils.setTextPink(R.id.ingredientTitle, recipeFontHeader);
		utils.setTextPink(R.id.methodTitle, recipeFontHeader);
		utils.setTextBlack(R.id.ingredientList, recipeFont);
		utils.setTextBlack(R.id.methodList, recipeFont);	
		utils.setTextPink(R.id.reviewHeader, recipeFontHeader);
	}

	/**
	 * Sets text for the recipe page
	 */
	private void setTextForLayout()
	{
		ApplicationModel_RecipeModel model = new ApplicationModel_RecipeModel(getApplicationContext());
		recipe = new RecipeBean();
		
		//Get details for intent
		Intent intent = getIntent();
		recipe = model.selectRecipe2(intent.getStringExtra("uniqueidr") );
		prepList = model.selectPreperation(recipe.getId());
		ingredList = model.selectIngredients(recipe.getId());
		imgBean = model.selectImages(recipe.getId());


		//Load recipe image
		img = (ImageView) findViewById(R.id.foodImage);
		ImageLoader task = new ImageLoader(getApplicationContext(),imgBean, img);
		task.execute();


		//Set up instruction list
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

		//Set up ingredient list
		TextView ingredients = (TextView) findViewById(R.id.ingredientList);
		for(int i = 0; i < ingredList.size(); i++)
		{
			if(ingredList.get(i).getProgress().equals("added"))
			{
			ingredients.append("- " + ingredList.get(i).getAmount() + " "+  ingredList.get(i).getValue().replace("other", "") + " " + ingredList.get(i).getName().toString() + " - " + ingredList.get(i).getNote().toString() + "\n");
			}	
		}
		recipeName =  recipe.getName();
		
		//set up other items in recipe
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
	 * Gets the local bitmap URI - this is ued for sharing image
	 * Code from here https://guides.codepath.com/android/Sharing-Content-with-Intents
	 * 
	 * @param imageView	Image
	 * @return Uri 		Image URI
	 */
	private Uri getLocalBitmapUri(ImageView imageView) {
		
		// Extract Bitmap from ImageView drawable
		Drawable drawable = imageView.getDrawable();
		Bitmap bmp = null;
		if (drawable instanceof BitmapDrawable){
			bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		} else {
			return null;
		}
		
		// Store image to default external storage directory
		Uri bmpUri = null;
		try {
			file =  new File(Environment.getExternalStoragePublicDirectory(  
					Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
			file.getParentFile().mkdirs();
			FileOutputStream out = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
			out.close();
			bmpUri = Uri.fromFile(file);
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bmpUri;
	}
	
	
	
	/**
	 * Creates the clone dialog - to clone recipes into cookbook
	 */
	private void createCloneDialog()
	{
		//creates clone style
		cloneDialog = utils.createDialog(Recipe_View.this , R.layout.recipe_clone_forview);
		setCloneDialogStyle();

		final ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(getApplicationContext());
		final SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		cbList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));	
		
		final Spinner spinner = fillSpinner(); //fills spinner
		
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, cloneDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				cloneDialog.dismiss();
				
			}});

		Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, cloneDialog);
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//gets a position of select item
				int namepos = spinner.getSelectedItemPosition();
				recipe.setName(utils.getTextFromDialog(R.id.recipenameEditText, cloneDialog));
				recipe.setRecipeBook(cbList.get(namepos).getUniqueid());
				recipe.setAddedBy(sharedpreferences.getString(emailk, ""));

				try
				{
					ApplicationModel_RecipeModel rmodel = new ApplicationModel_RecipeModel(getApplicationContext());
					
					//check for errors
					if(rmodel.selectRecipe( recipe.getName(), cbList.get(namepos).getUniqueid()))
					{
						errorView.setText("You already have a recipe with that name");
					}
					else if(recipe.getName().equals(""))
					{
						errorView.setText("Please enter a recipe name");
					}
					else
					{
						//insert recipe and notify list change
						String uid = rmodel.insertRecipe(recipe, false, ingredList, prepList, imgBean);
						Recipe_ShelfListView.adapter.notifyDataSetChanged(); 
						cloneDialog.dismiss();
					}

				}catch(SQLException e)
				{
					Toast.makeText(getApplicationContext(), "Recipe was not cloned", Toast.LENGTH_LONG).show();
				}



			}}); 

		cloneDialog.show();
	}
	
	/**
	 * Fills spinner for cloning
	 * 
	 * @return filled spinner
	 */
	private Spinner fillSpinner()
	{
		//Fill with a list of recipes the user owns
		List<String> spinnerArray =  new ArrayList<String>();
		for(int i = 0; i < cbList.size(); i++)
		{
			spinnerArray.add(cbList.get(i).getName());
			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				Recipe_View.this, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		final Spinner spinner = (Spinner) cloneDialog.findViewById(R.id.currentCookbooksSpinner);
		spinner.setAdapter(adapter);
		spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		return spinner;
	}
	
	/**
	 * Set up a clone dialog style
	 */
	private void setCloneDialogStyle()
	{
		utils.setDialogText(R.id.cloneTitle,cloneDialog,22);
		utils.setDialogText(R.id.currentCookbooksView,cloneDialog,22);
		utils.setDialogText(R.id.recipeNameView,cloneDialog,22);
		errorView = (TextView) cloneDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,cloneDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
	}

}