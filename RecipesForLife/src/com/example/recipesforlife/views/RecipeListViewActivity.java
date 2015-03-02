package com.example.recipesforlife.views;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.ingredientBean;
import com.example.recipesforlife.controllers.preperationBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.recipeModel;
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
	public static ArrayList<String> recipeids;
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
		for(int a = 0; a < recipeList.size(); a++)
		{
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
		}
		//if recipes list is less than 6 add extra recipe rows for layout
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
		recipenames.clear();
		recipeids.clear();
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
			add = new AddRecipeView(getApplicationContext(), RecipeListViewActivity.this, uniqueid);
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


