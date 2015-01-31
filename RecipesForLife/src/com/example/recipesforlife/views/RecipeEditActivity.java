package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

public class RecipeEditActivity extends Activity {
	
	util utils;
	
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
    Dialog titleDialog, servesDialog, timeDialog;
	public static final String emailk = "emailKey";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipeeditview);
		utils = new util(getApplicationContext(), this);
		setStyle();
		setTextForLayout();
		
		ImageView titleButton = (ImageView) findViewById(R.id.recipeTitleEditImage);
		titleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				titleDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe1editdialog);
				titleDialog.show();
			}});
		
		ImageView servesButton = (ImageView) findViewById(R.id.servesEditImage);
		servesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				servesDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe2editdialog);
				servesDialog.show();
			}});
		
		ImageView timeButton = (ImageView) findViewById(R.id.timeEditImage);
		timeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe3editdialog);
				timeDialog.show();
				
			}});

			
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
			recipe = model.selectRecipe2("pizza", sharedpreferences.getString(emailk, "") );
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
