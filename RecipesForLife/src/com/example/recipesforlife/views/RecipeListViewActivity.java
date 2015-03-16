package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.imageBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Class to show a list of recipes belonging to a specific cookbook
 * @author Kari
 *
 */
public class RecipeListViewActivity extends ActionBarActivity {
	util utils;	
	ListView listView;
	String type = "";
	String uniqueid = "";
	ArrayList<recipeBean> recipeList;
	cookbookModel model;
	public static CustomRecipeListAdapter adapter;
	NavigationDrawerCreation nav;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	public  static ArrayList<String> recipenames;
	public static ArrayList<String> recipeids, recipeimagesid;
	public static ArrayList<byte[]> recipeimages;
	AddRecipeView add;



	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview2);

		//Setup
		listView = (ListView) findViewById(R.id.list);
		utils = new util(getApplicationContext(), this);
		model = new cookbookModel(getApplicationContext());
		recipeList = new ArrayList<recipeBean>();
		Intent intent = getIntent();
		uniqueid = intent.getStringExtra("uniqueid");
		type = intent.getStringExtra("type");
		String bookname = intent.getStringExtra("bookname");

		//Sets up the navigation drawer
		nav = new NavigationDrawerCreation(this, "My Recipes");
		nav.createDrawer();
		SpannableString s = new SpannableString("My Recipes in " + bookname);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		//Gets recipe list and set to adapter
		recipeList = model.selectRecipesByCookbook(intent.getStringExtra("uniqueid"));
		recipenames = new ArrayList<String>();
		recipeids = new ArrayList<String>();
		recipeimages = new ArrayList<byte[]>();
		
		for(int a = 0; a < recipeList.size(); a++)
		{
			imageBean image = model.selectImage(recipeList.get(a).getId());
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
			recipeimages.add(image.getImage());
		}
		//if recipes list is less than 6 add extra recipe rows for layout
		if(recipeList.size() < 6)
		{
			int num = 6 - recipeList.size();
			for(int i = 0; i < num; i++)
			{
				byte[] arr = new byte[0];
				recipenames.add("");
				recipeids.add("");
				recipeimages.add(arr);
			}
		}
		adapter = new CustomRecipeListAdapter(this, recipenames, getApplicationContext(), recipeids, recipeimages);
		listView.setAdapter(adapter); 

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager =
		           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		android.support.v7.widget.SearchView searchView =
		    		(android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		    searchView.setSearchableInfo(
		            searchManager.getSearchableInfo(getComponentName()));
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {   
	    return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Updates list on resume
		recipenames.clear();
		recipeids.clear();
		recipeimages.clear();
		recipeList = model.selectRecipesByCookbook(uniqueid);
		for(int a = 0; a < recipeList.size(); a++)
		{
			imageBean image = model.selectImage(recipeList.get(a).getId());
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
			recipeimages.add(image.getImage());
			
		}
		//if recipes list is less than 6 add extra recipe rows for layout
		if(recipeList.size() < 6)
		{
			int num = 6 - recipeList.size();
			for(int i = 0; i < num; i++)
			{
				byte[] arr = new byte[0];
				recipenames.add("");
				recipeids.add("");
				recipeimages.add(arr);
				Log.v("add","add");
			}
		}
		//Updates the list view
		adapter.notifyDataSetChanged();

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle 
		nav.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		nav.config(newConfig);
	}

	/**
	 * Handles action bar selection
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//Checks for nav drawer selection
		boolean result = nav.drawerToggle(item);

		switch (item.getItemId()) 
		{
		//If add button selected on action bar then display add recipe dialog
		case R.id.action_bookadd:
			Intent intent = getIntent();
			add = new AddRecipeView(getApplicationContext(), RecipeListViewActivity.this, uniqueid, intent.getStringExtra("bookname"));
			add.addRecipe();
			result = true;
		default:
			result = false;
		}
		return result;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		add.resultRecieved(requestCode, resultCode, imageReturnedIntent);

	}


}


