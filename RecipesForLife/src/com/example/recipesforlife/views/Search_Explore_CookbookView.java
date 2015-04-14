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
import android.widget.Spinner;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Displays a result listview for explore cookbooks
 * @author Kari
 *
 */
public class Search_Explore_CookbookView extends ActionBarActivity {
	
	private Navigation_DrawerCreation nav;
	private Util utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.explore_listview2);

		utils = new Util(getApplicationContext(), this);
		
		//Sets up nav bar
		nav = new Navigation_DrawerCreation(Search_Explore_CookbookView.this, "Explore Cookbooks");
		nav.createDrawer();
		SpannableString s = new SpannableString("Explore Cookbooks");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

	
		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		utils.setTextString(R.id.exploreheader, "Explore Cookbooks");
		
		utils.setText(R.id.exploreheader, 26);
		utils.setText(R.id.exploreheadercont, 26);
		
		final Spinner sItems = (Spinner) findViewById(R.id.spinner);
		sItems.setVisibility(View.INVISIBLE);
		boolean empty = false;
	
	        ApplicationModel_SearchModel sm = new ApplicationModel_SearchModel(getApplicationContext());
			final ArrayList<CookbookBean> cb = sm.selectRandomCookbooks();
			ListView listView = (ListView) findViewById(R.id.list);
			
			//if empty then add following to the list
			if(cb.size() == 0)
			{
				CookbookBean cookbookbean = new CookbookBean();
				cookbookbean.setName("empty");
				cb.add(cookbookbean);
				empty = true;
			}
			Search_CookbookAdapter cookbookadapter = new Search_CookbookAdapter( getApplicationContext(), Search_Explore_CookbookView.this,  cb);
			listView.setAdapter(cookbookadapter);
			
			//If not empty then enable clicks on items
			if(empty == false)
			{
				listView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						Intent i = new Intent(Search_Explore_CookbookView.this, Recipe_ShelfListView.class);
						//intents used on getting the recipes		
						i.putExtra("uniqueid", cb.get(position).getUniqueid());
						i.putExtra("type", "view");
						i.putExtra("bookname", cb.get(position).getName());
						startActivity(i);

					}                 
				});
			}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_plain, menu);
		utils.setUpSearch(menu);
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

}