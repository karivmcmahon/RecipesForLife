package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.imageBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;


/**
 * Class to show recipe view 
 * @author Kari
 *
 */
public class RecipeViewActivity extends ActionBarActivity {
	util utils;
	NavigationDrawerCreation nav;
	int counter = 0;
	int fullScreenCounter = 0;
	private ShareActionProvider mShareActionProvider;
	String recipeName = "";
	int recipeFont = 22;
	int recipeFontHeader = 26;



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipeview);
		utils = new util(getApplicationContext(), this);
		setStyle();
		setTextForLayout();
		nav = new NavigationDrawerCreation(this, recipeName );
		nav.createDrawer();
		SpannableString s = new SpannableString(recipeName);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
		        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		
		// Update the action bar title with the TypefaceSpan instance
				android.support.v7.app.ActionBar actionBar = getSupportActionBar();
				actionBar.setTitle(s);


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

	@SuppressLint("NewApi")
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		nav.drawerToggle(item);

		//These choices increae or decrease font size
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
		setStyle(); 
		return super.onOptionsItemSelected(item);

	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		 getMenuInflater().inflate(R.menu.recipe, menu);
	        MenuItem item = menu.findItem(R.id.action_share);
	        //Creates a share recipe link when the share button on the action bar selected
	        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
	        if(mShareActionProvider != null)
	        {
	        	Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
	    		sharingIntent.setType("text/plain");
	    		String shareBody = "Check out my recipe for " + recipeName +  " on the android app Recipes For Life" ;
	    		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
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
	public void setStyle()
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
	}

	/**
	 * Sets text for the recipe page
	 */
	public void setTextForLayout()
	{
		recipeModel model = new recipeModel(getApplicationContext());
		recipeBean recipe = new recipeBean();
		ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
		ArrayList<ingredientBean> ingredList = new ArrayList<ingredientBean>();
		imageBean imgBean = new imageBean();
		Intent intent = getIntent();
		recipe = model.selectRecipe2(intent.getStringExtra("uniqueidr") );
		prepList = model.selectPreperation(recipe.getId());
		ingredList = model.selectIngredients(recipe.getId());
		imgBean = model.selectImages(recipe.getId());
		
		
		ImageView img = (ImageView) findViewById(R.id.foodImage);
		ImageLoader task = new ImageLoader(getApplicationContext(),imgBean, img);
		task.execute();
		
		
		
		TextView instructions = (TextView) findViewById(R.id.methodList);
		Collections.sort(prepList, new Comparator<preperationBean>() {
			@Override 
			public int compare(preperationBean p1, preperationBean p2) {
				return p1.getPrepNum() - p2.getPrepNum(); // Ascending
			}});
		for(int i = 0; i < prepList.size(); i++)
		{
			instructions.append(Integer.toString(prepList.get(i).getPrepNum()) + ". " +prepList.get(i).getPreperation().toString() + "\n");
		}

		TextView ingredients = (TextView) findViewById(R.id.ingredientList);
		for(int i = 0; i < ingredList.size(); i++)
		{
			ingredients.append("- " + ingredList.get(i).getAmount() + " "+  ingredList.get(i).getValue() + " " + ingredList.get(i).getName().toString() + " - " + ingredList.get(i).getNote().toString() + "\n");
		}			
		recipeName =  recipe.getName();
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

}
