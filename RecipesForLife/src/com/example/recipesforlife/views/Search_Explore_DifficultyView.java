package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Displays a listview with difficulty results
 * @author Kari
 *
 */
public class Search_Explore_DifficultyView extends ActionBarActivity {
	
	Navigation_DrawerCreation nav;
	Util utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.explore_listview2);

		utils = new Util(getApplicationContext(), this);
		//Sets up nav bar
		nav = new Navigation_DrawerCreation(Search_Explore_DifficultyView.this, "Explore By Difficulty");
		nav.createDrawer();
		SpannableString s = new SpannableString("Explore By Difficulty");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		utils.setTextString(R.id.exploreheader, "Explore recipes by ");
		utils.setTextString(R.id.exploreheadercont, "difficulty ");
		utils.setText(R.id.exploreheader, 26);
		utils.setText(R.id.exploreheadercont, 26);
		
		ArrayList<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("Easy");
		spinnerArray.add("Medium");
		spinnerArray.add("Hard");
		General_SpinnerAdapter adapter = new General_SpinnerAdapter(getApplicationContext(), this,
                R.layout.general_spinner_item2, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) findViewById(R.id.spinner);
		
		
		//makes spinner triangle white
		sItems.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
		    	boolean empty = false;
		        Log.v("SPINNER", "SPINNER" + sItems.getSelectedItem().toString());
		        ApplicationModel_SearchModel sm = new ApplicationModel_SearchModel(getApplicationContext());
				final ArrayList<RecipeBean> rb = sm.selectRecipeByDiff(sItems.getSelectedItem().toString());
				ListView listView = (ListView) findViewById(R.id.list);
				if(rb.size() == 0)
				{
					RecipeBean recipebean = new RecipeBean();
					recipebean.setName("empty");
					rb.add(recipebean);
					empty = true;
				}
				Search_RecipeAdapter recipeadapter = new Search_RecipeAdapter( getApplicationContext(), Search_Explore_DifficultyView.this,  rb);
				listView.setAdapter(recipeadapter);
				
				if(empty == false)
				{
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							// TODO Auto-generated method stub
							Intent i = new Intent(Search_Explore_DifficultyView.this, Recipe_View.class);
							i.putExtra("uniqueidr", rb.get(position).getUniqueid());
							i.putExtra("name", rb.get(position).getName());
							startActivity(i);

						}                 
					});
				}
		    } 

		    public void onNothingSelected(AdapterView<?> adapterView) {
		        return;
		    } 
		}); 
	/**	SearchModel sm = new SearchModel(getApplicationContext());
		final ArrayList<RecipeBean> rb = sm.selectRecipeByDiff(sItems.getSelectedItem().toString());
		ListView listView = (ListView) findViewById(R.id.list);
		if(rb.size() == 0)
		{
			RecipeBean recipebean = new RecipeBean();
			recipebean.setName("empty");
			rb.add(recipebean);
			//empty = true;
		}
		CustomRecipeSearchAdapter recipeadapter = new CustomRecipeSearchAdapter( getApplicationContext(), this,  rb);
		listView.setAdapter(recipeadapter); **/

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
