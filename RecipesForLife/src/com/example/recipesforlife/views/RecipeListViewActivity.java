package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

public class RecipeListViewActivity extends Activity {
	util utils;	
	ListView listView;
	String type = "";
	ArrayList<recipeBean> recipeList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		listView = (ListView) findViewById(R.id.list);

		cookbookModel model = new cookbookModel(getApplicationContext());
		recipeList = new ArrayList<recipeBean>();
		Intent intent = getIntent();
		Log.v("type", "type" + intent.getStringExtra("type"));
		Log.v("uniqid", "uniqid" + intent.getStringExtra("uniqueid"));
		type = intent.getStringExtra("type");
		recipeList = model.selectRecipesByCookbook(intent.getStringExtra("uniqueid"));
		ArrayList<String> recipenames = new ArrayList<String>();
		for(int a = 0; a < recipeList.size(); a++)
		{
			recipenames.add(recipeList.get(a).getName());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, recipenames);

		listView.setAdapter(adapter); 

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent i = null;

				// TODO Auto-generated method stub
				if(type.equals("view"))
				{
					i = new Intent(RecipeListViewActivity.this, RecipeViewActivity.class);
				}
				else if(type.equals("edit"))
				{
					i = new Intent(RecipeListViewActivity.this, RecipeEditActivity.class);
				}
			

				// ListView Clicked item value
				String  itemValue    = (String) listView.getItemAtPosition(position);
				for(int a = 0; a < recipeList.size(); a++)
				{
					if(itemValue.equals(recipeList.get(a).getName()))
					{
						i.putExtra("uniqueidr", recipeList.get(a).getUniqueid());
					}
				}
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
