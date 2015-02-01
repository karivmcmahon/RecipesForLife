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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecipeEditActivity extends Activity {
	
	util utils;
	recipeBean recipe; 
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
    Dialog titleDialog, servesDialog, timeDialog, prepDialog;
	public static final String emailk = "emailKey";
	ArrayList<preperationBean> prepList;
	int id = 1;
	
	 // Handles message from time dialog 1 - preptime
    Handler mHandler = new Handler(){
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
    Handler mHandler2 = new Handler(){
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
		setContentView(R.layout.recipeeditview);
		utils = new util(getApplicationContext(), this);
		recipe = new recipeBean();
		setStyle();
		setTextForLayout();
		
		ImageView titleButton = (ImageView) findViewById(R.id.recipeTitleEditImage);
		titleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				titleDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe1editdialog);
				utils.setDialogText(R.id.recipeEditView, titleDialog, 22);
				utils.setDialogText(R.id.recipeNameView, titleDialog, 22);
				utils.setDialogText(R.id.recipeDescView, titleDialog, 22);
				utils.setDialogTextString(R.id.recipenameEditText, titleDialog, recipe.getName());
				utils.setDialogTextString(R.id.recipeDescEdit, titleDialog, recipe.getDesc());
				Button titleButton = utils.setButtonTextDialog(R.id.saveButton, 22, titleDialog);
				titleButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						utils.setTextString(R.id.recipeTitle, utils.getTextFromDialog(R.id.recipenameEditText, titleDialog));
						utils.setTextString(R.id.recipeDesc, utils.getTextFromDialog(R.id.recipeDescEdit, titleDialog));
						titleDialog.dismiss();
						
					}});
				titleDialog.show();
			}});
		
		ImageView servesButton = (ImageView) findViewById(R.id.servesEditImage);
		servesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				servesDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe2editdialog);
				utils.setDialogText(R.id.recipeEditView, servesDialog, 22);
				utils.setDialogText(R.id.recipeServesView, servesDialog, 22);
				utils.setDialogTextString(R.id.recipeServesEditText, servesDialog, recipe.getServes());
				Button servesButton = utils.setButtonTextDialog(R.id.saveButton, 22, servesDialog);
				servesButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						utils.setTextString(R.id.servesVal, utils.getTextFromDialog(R.id.recipeServesEditText, servesDialog));
						servesDialog.dismiss();
						
					}});
				servesDialog.show();
			}});
		
		ImageView timeButton = (ImageView) findViewById(R.id.timeEditImage);
		timeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				timeDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe3editdialog);
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
						// TODO Auto-generated method stub
						utils.setTextString(R.id.prepTimeVal, utils.getTextFromDialog(R.id.recipePrepEditText, timeDialog));
						utils.setTextString(R.id.cookingTimeVal, utils.getTextFromDialog(R.id.recipeCookingEditText, timeDialog));
						timeDialog.dismiss();
					}});
				timeDialog.show();
				
			}});
		
		ImageView prepButton = (ImageView) findViewById(R.id.methodEditImage);
	    prepButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				prepDialog = utils.createDialog(RecipeEditActivity.this, R.layout.recipe4editdialog);
				utils.setDialogText(R.id.recipeEditView, prepDialog, 22);
				LinearLayout linearLayout = (LinearLayout)prepDialog.findViewById(R.id.editdialog);
				for(int i = 0; i < prepList.size(); i++)
				{
					LinearLayout linearLayout2 = new LinearLayout(RecipeEditActivity.this);
					linearLayout2.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
					TextView prepView = new TextView(RecipeEditActivity.this);
					prepView.setText("Preperation");
					prepView.setId(i);
					
					TextView prepNumView = new TextView(RecipeEditActivity.this);
					prepNumView.setText("Preperation Num");
					int ids = findId();
					prepNumView.setId(ids);
					
					
					EditText prepEdit = new EditText(RecipeEditActivity.this);
					prepEdit.setId(i);
					prepEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
					
					EditText prepNumEdit = new EditText(RecipeEditActivity.this);
					prepNumEdit.setId(ids);
					prepNumEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
					
					linearLayout2.addView(prepNumView);
					linearLayout2.addView(prepNumEdit);
					linearLayout2.addView(prepView);
					linearLayout2.addView(prepEdit);
					linearLayout.addView(linearLayout2);
					utils.setDialogText(i, prepDialog, 22);
					prepEdit.setText(prepList.get(i).getPreperation());
					utils.setDialogText(ids, prepDialog, 22);
					prepNumEdit.setText(Integer.toString(prepList.get(i).getPrepNum()));
				}
		//		Button buttton = utils.setButtonTextDialog(resource, fontSize, dialog)
				prepDialog.show();
			}});
		
	/**	LinearLayout linearLayout = (LinearLayout)findViewById(R.layout.recipe4editdialog);
		TextView tv = new TextView(RecipeEditActivity.this);
		tv.setId(1);
		linearLayout.addView(tv); **/


			
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
			
		    prepList = new ArrayList<preperationBean>();
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
	 
	 public int findId(){  
		    View v = findViewById(id);  
		    while (v != null){  
		        v = findViewById(++id);  
		    }  
		    return id++;  
		}

}
