package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.recipeModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RecipeListViewActivity extends Activity {
	util utils;
	
	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";

	public static final String emailk = "emailKey";
	ListView listView;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		  listView = (ListView) findViewById(R.id.list);
		  
		 recipeModel model = new recipeModel(getApplicationContext());
		 ArrayList<recipeBean> recipeList = new ArrayList<recipeBean>();
		 SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		 recipeList = model.selectRecipesByUser(sharedpreferences.getString(emailk, ""));
		  ArrayList<String> values = new ArrayList<String>();
		 for(int i = 0; i < recipeList.size(); i++)
		 {
			 values.add(recipeList.get(i).getName());
		 }
		
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_list_item_1, android.R.id.text1, values);
	    
		 listView.setAdapter(adapter); 
		 
		 listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RecipeListViewActivity.this, RecipeViewActivity.class);
				 // ListView Clicked item index
                int itemPosition     = position;
                
                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);
				i.putExtra("name", itemValue);
			    startActivity(i);
				
			}
		 });
		 
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
