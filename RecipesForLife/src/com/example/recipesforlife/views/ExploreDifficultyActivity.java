package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ExploreDifficultyActivity extends ActionBarActivity {
	
	NavigationDrawerCreation nav;
	Util utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.explore_listview2);

		utils = new Util(getApplicationContext(), this);
		//Sets up nav bar
		nav = new NavigationDrawerCreation(ExploreDifficultyActivity.this, "Explore By Difficulty");
		nav.createDrawer();
		SpannableString s = new SpannableString("Explore By Difficulty");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		utils.setText(R.id.exploreheader, 26);
		utils.setText(R.id.exploreheadercont, 26);
		
		ArrayList<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Easy");
		spinnerArray.add("Medium");
		spinnerArray.add("Hard");
		CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(getApplicationContext(), this,
                R.layout.general_spinner_item2, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) findViewById(R.id.diffSpinner);
		
		
		//makes spinner triangle white
		sItems.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);

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
