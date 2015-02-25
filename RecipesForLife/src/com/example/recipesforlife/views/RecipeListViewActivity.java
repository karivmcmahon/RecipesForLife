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

/**
 * Class to show a list of recipes belonging to a specific cookbook
 * @author Kari
 *
 */
public class RecipeListViewActivity extends Activity {
	util utils;	
	ListView listView;
	String type = "";
	String uniqueid = "";
	ArrayList<recipeBean> recipeList;
	cookbookModel model;
	ArrayAdapter<String> adapter;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview2);
		listView = (ListView) findViewById(R.id.list);

		model = new cookbookModel(getApplicationContext());
		recipeList = new ArrayList<recipeBean>();
		Intent intent = getIntent();
		Log.v("type", "type" + intent.getStringExtra("type"));
		Log.v("uniqid", "uniqid" + intent.getStringExtra("uniqueid"));
		uniqueid = intent.getStringExtra("uniqueid");
		type = intent.getStringExtra("type");
		
		//Gets recipe list and set to adapter
		recipeList = model.selectRecipesByCookbook(intent.getStringExtra("uniqueid"));
		ArrayList<String> recipenames = new ArrayList<String>();
		ArrayList<String> recipeids = new ArrayList<String>();
		for(int a = 0; a < recipeList.size(); a++)
		{
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
		}
		if(recipeList.size() < 6)
		{
			int num = 6 - recipeList.size();
			for(int i = 0; i < num; i++)
			{
				
				recipenames.add("");
				recipeids.add("");
				Log.v("add","add");
			}
		}
		adapter = new CustomRecipeListAdapter(this, recipenames, getApplicationContext(), recipeids);
		listView.setAdapter(adapter); 

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
		//Updates list on resume
		recipeList = model.selectRecipesByCookbook(uniqueid);
		ArrayList<String> recipenames = new ArrayList<String>();
		for(int a = 0; a < recipeList.size(); a++)
		{
			recipenames.add(recipeList.get(a).getName());
		}
	
		adapter.clear();
		adapter.addAll(recipenames);
		adapter.notifyDataSetChanged();
		
	}

}
