package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.content.Intent;
import android.content.res.Configuration;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Displays a listview with the dietary results
 * @author Kari
 *
 */
public class Search_Explore_DietaryView extends ActionBarActivity {
	
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
		setContentView(R.layout.explore_listview2);

		utils = new Util(getApplicationContext(), this);
	
		//Sets up nav bar
		nav = new Navigation_DrawerCreation(Search_Explore_DietaryView.this, "Explore By Dietary Requirements");
		nav.createDrawer();
		SpannableString s = new SpannableString("Explore By Dietary Requirements");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);
		utils.setTextString(R.id.exploreheader, "Explore recipes that are ");
		utils.setTextString(R.id.exploreheadercont, "");
		utils.setText(R.id.exploreheader, 26);
		utils.setText(R.id.exploreheadercont, 26);
		
		//Fills spinner with dietary
		ArrayList<String> dietarySpinnerArray =  new ArrayList<String>();
		dietarySpinnerArray.add("Nut free");
		dietarySpinnerArray.add("Gluten free");
		dietarySpinnerArray.add("Vegeterian");
		dietarySpinnerArray.add("Vegan");
		dietarySpinnerArray.add("N/A");
		General_SpinnerAdapter adapter = new General_SpinnerAdapter(getApplicationContext(), this,
                R.layout.general_spinner_item2, dietarySpinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner sItems = (Spinner) findViewById(R.id.spinner);
		
		
		//makes spinner triangle white
		sItems.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		
		//When item selected in spinner
		sItems.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
		    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) { 
		    	boolean empty = false;
		 
		    	//Get results for selected item
		        ApplicationModel_SearchModel sm = new ApplicationModel_SearchModel(getApplicationContext());
				final ArrayList<RecipeBean> rb = sm.selectRecipeByDietary(sItems.getSelectedItem().toString());
				ListView listView = (ListView) findViewById(R.id.list);
			
				//If no results - set empty to true and add to list
				if(rb.size() == 0)
				{
					RecipeBean recipebean = new RecipeBean();
					recipebean.setName("empty");
					rb.add(recipebean);
					empty = true;
				}
				Search_RecipeAdapter recipeadapter = new Search_RecipeAdapter( getApplicationContext(), Search_Explore_DietaryView.this,  rb);
				listView.setAdapter(recipeadapter);
				
				//If empty false then enable clicks which allows the user to move to the next activity
				if(empty == false)
				{
					listView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view,
								int position, long id) {
							//if clicked take to that recipe
							Intent i = new Intent(Search_Explore_DietaryView.this, Recipe_View.class);
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


