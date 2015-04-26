package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Activity which handles the explore view which is shown to the user
 * @author Kari
 *
 */
public class Search_ExploreView  extends ActionBarActivity {
	
	private Navigation_DrawerCreation nav;
	private Util utils;

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.explore_listview);

		utils = new Util(getApplicationContext(), this);
		
		//Sets up nav bar
		nav = new Navigation_DrawerCreation(Search_ExploreView.this, "Explore");
		nav.createDrawer();
		SpannableString s = new SpannableString("Explore");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		
		//Set header
		TextView headertv = (TextView) findViewById(R.id.exploreheader);
		headertv.setText("Explore");
		utils.setText(R.id.exploreheader, 26);
		
		//Set up categories
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("Recipes By Difficulty");
		categories.add("Recipes By Cuisine");
		categories.add("Recipes By Dietary requirements");
		categories.add("Cookbooks");
		ListView categorieslistView = (ListView) findViewById(R.id.list);
	
		Search_Explore_Adapter exploreadapter = new Search_Explore_Adapter( getApplicationContext(), this,  categories);
		categorieslistView.setAdapter(exploreadapter);
		
		categorieslistView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//Depending on position click will take to different activity
				if(position == 0)
				{
					//Searches recipe by difficulty
					Intent i = new Intent(Search_ExploreView.this, Search_Explore_DifficultyView.class);
					startActivity(i);
				}
				if(position == 1)
				{
					//Searches recipes by cusine
					Intent i = new Intent(Search_ExploreView.this, Search_Explore_CusineView.class);
					startActivity(i);
				}
				if(position == 2)
				{
					//Searches recipes by dietary
					Intent i = new Intent(Search_ExploreView.this, Search_Explore_DietaryView.class);
					startActivity(i);
				}
				if(position == 3)
				{
					//Searches recipes by cookbook
					Intent i = new Intent(Search_ExploreView.this, Search_Explore_CookbookView.class);
					startActivity(i);
				}
				
			}
			
			
		});

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_plain, menu);
		utils.setUpSearch(menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		nav.syncState();
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		nav.config(newConfig);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
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

}
