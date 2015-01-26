package com.example.recipesforlife.views;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class RecipeViewActivity extends Activity {
	util utils;
	
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";

	public static final String emailk = "emailKey";
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipeview);
		utils = new util(getApplicationContext(), this);
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
		
		recipeModel model = new recipeModel(getApplicationContext());
		recipeBean recipe = new recipeBean();
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		recipe = model.selectRecipe2("pizza", sharedpreferences.getString(emailk, "") );
		
		TextView recipetitle = (TextView) findViewById(R.id.recipeTitle);
		recipetitle.setText(recipe.getName());
		
		TextView recipedesc = (TextView) findViewById(R.id.recipeDesc);
		recipedesc.setText(recipe.getDesc());
		
		TextView recipeserves = (TextView) findViewById(R.id.servesVal);
		recipeserves.setText(recipe.getServes());
		
		TextView recipeprep = (TextView) findViewById(R.id.prepTimeVal);
		recipeprep.setText(recipe.getPrep());
		
		TextView recipecooking = (TextView) findViewById(R.id.cookingTimeVal);
		recipecooking.setText(recipe.getCooking());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 @Override
	   protected void onResume() {
		   super.onResume();
	 }

}
