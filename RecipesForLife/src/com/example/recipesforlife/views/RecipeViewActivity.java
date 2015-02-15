package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
		setStyle();
		setTextForLayout();
		
		
		
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
	 }
	 
	 public void setTextForLayout()
	 {
		 recipeModel model = new recipeModel(getApplicationContext());
			recipeBean recipe = new recipeBean();
			ArrayList<preperationBean> prepList = new ArrayList<preperationBean>();
			ArrayList<ingredientBean> ingredList = new ArrayList<ingredientBean>();
			Intent intent = getIntent();
			SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			 Log.v("uniqid", "uniqid rs " + intent.getStringExtra("uniqueidr"));
			recipe = model.selectRecipe2(intent.getStringExtra("uniqueidr") );
			prepList = model.selectPreperation(recipe.getId());
			ingredList = model.selectIngredients(recipe.getId());
			
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
			utils.setTextString(R.id.recipeTitle, recipe.getName());
			utils.setTextString(R.id.recipeDesc, recipe.getDesc());
			utils.setTextString(R.id.servesVal, recipe.getServes());
			utils.setTextString(R.id.prepTimeVal, recipe.getPrep());
			utils.setTextString(R.id.cookingTimeVal, recipe.getCooking());
			
	 }

}
