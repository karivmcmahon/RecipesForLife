package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.searchModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

public class SearchResultsActivity extends ActionBarActivity {

	NavigationDrawerCreation nav;
	util utils;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.searchlistview);
		
		utils = new util(getApplicationContext(), this);
		//Sets up nav bar
		nav = new NavigationDrawerCreation(SearchResultsActivity.this, "Search");
		nav.createDrawer();
		SpannableString s = new SpannableString("Search");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
       handleIntent(getIntent());
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
	/**	SearchManager searchManager =
		           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		android.support.v7.widget.SearchView searchView =
		    		(android.support.v7.widget.SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
		    searchView.setSearchableInfo(
		            searchManager.getSearchableInfo(getComponentName())); **/
		    
		return true;
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//handles nav drawer clicks
		boolean result = nav.drawerToggle(item);

		switch (item.getItemId()) {

		
		default:
			result = false;
		}

		return result;

	}


    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchModel sm = new searchModel(getApplicationContext());
            ArrayList<recipeBean> rb = sm.selectRecipe(query);
            
            TextView tv = (TextView) findViewById(R.id.recipeheader);
            tv.setText("Recipes that feature '" + query + "' :");
            utils.setText(R.id.recipeheader, 30);
            utils.setText(R.id.cookbookheader, 30);
            utils.setText(R.id.userheader, 30);
            utils.setTextPink(R.id.cookbookrows, 26);
            utils.setTextPink(R.id.userrows, 26);
           
            ListView listView = (ListView) findViewById(R.id.list);
    		
    		
    		CustomRecipeSearchAdapter adapter = new CustomRecipeSearchAdapter( getApplicationContext(), this,  rb);
    		listView.setAdapter(adapter); 
    		
           
        }
    }
    
}