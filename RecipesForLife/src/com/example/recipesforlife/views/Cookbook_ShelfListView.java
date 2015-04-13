package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.widget.SearchView;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.util.PostTask;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Class to display a list of cookbooks involving the users
 * @author Kari
 *
 */
public class Cookbook_ShelfListView extends ActionBarActivity {

	ListView listView;
	public static final String MyPREFERENCES = "MyPrefs";
	static ArrayList<CookbookBean> cookbookList;
	public static final String pass = "passwordKey"; 
	String type = "";
	Util utils;
	public static Cookbook_ListAdapter adapter;
	public static ArrayList<String> values, ids;
	public static ArrayList<byte[]> images;
	Cookbook_AddView add;
	private Handler mHandler = new Handler();
	static ApplicationModel_CookbookModel model;
	static Context context;


	Navigation_DrawerCreation nav;

	public static final String emailk = "emailKey";
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.cookbook_list);

		context = getApplicationContext();

		//Sets up nav bar
		nav = new Navigation_DrawerCreation(this, "My Cookbooks");
		nav.createDrawer();
		SpannableString s = new SpannableString("My Cookbooks");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		utils = new Util(getApplicationContext(), this);
		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		listView = (ListView) findViewById(R.id.list);


		model = new ApplicationModel_CookbookModel(getApplicationContext());

		//Gets list of cookbooks and displays them
		cookbookList = new ArrayList<CookbookBean>();
		updateCookbookList(false);
		adapter = new Cookbook_ListAdapter(this, values, getApplicationContext(), ids, images);
		listView.setAdapter(adapter); 
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		//sets up search
		utils.setUpSearch(menu);

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		//Sync for apps to be done in background on resume
		mHandler.postDelayed(new Runnable() {
			public void run() {
				AsyncTask posttask = new PostTask(utils, getApplicationContext(), true).execute();	
			}
		}, 3000);



	}

	/**
	 * Updates the cookbook list - called when data has changed in database
	 * @param update
	 */
	public static void updateCookbookList(boolean update)
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Fill list adapter with cookbook names
		cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
		values = new ArrayList<String>();
		ids = new ArrayList<String>();
		images = new ArrayList<byte[]>();
		for(int i = 0; i < cookbookList.size(); i++)
		{
			values.add(cookbookList.get(i).getName());
			ids.add(cookbookList.get(i).getUniqueid());
			images.add(cookbookList.get(i).getImage());
		}
		//If the list is under 6 then create empty rows to fill the layout of the app
		if(cookbookList.size() < 6)
		{
			int num = 6 - cookbookList.size();
			for(int a = 0; a < num; a++)
			{
				byte[] emptyarr = new byte[0];
				values.add("");
				ids.add("");
				images.add(emptyarr);
			}
		}

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

	/**
	 * Handles action bar clicks
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//handles nav drawer clicks
		boolean result = nav.drawerToggle(item);

		switch (item.getItemId()) {

		//If the user presses add button - show add cookbook
		case R.id.action_bookadd:
			add = new Cookbook_AddView(getApplicationContext(), this);
			add.addCookbook();
		default:
			result = false;
		}

		return result;

	}

	@Override
	/**
	 * Retrieves activity result - calls appropriate method based on result code
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		if(requestCode == 100)
		{
			add.resultRecieved(requestCode, resultCode, imageReturnedIntent);
		}
		else if(requestCode == 101)
		{
			adapter.resultRecieved(requestCode, resultCode, imageReturnedIntent);
		}

	}










}


