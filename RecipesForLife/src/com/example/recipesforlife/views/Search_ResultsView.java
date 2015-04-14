package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SearchRecentSuggestions;
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
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.models.ApplicationModel_SearchModel;
import com.example.recipesforlife.util.SampleRecentSuggestionsProvider;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Class to handle retrieving and displaying search results
 * @author Kari
 *
 */
public class Search_ResultsView extends ActionBarActivity {

	private Navigation_DrawerCreation nav;
	private Util utils;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.search_listview);

		utils = new Util(getApplicationContext(), this);

		//Sets up nav bar
		nav = new Navigation_DrawerCreation(Search_ResultsView.this, "Search");
		nav.createDrawer();
		SpannableString s = new SpannableString("Search");
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		//handles search intent
		handleIntent(getIntent());
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

	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}

	/**
	 * Handles the search intent
	 * 
	 * @param intent
	 */
	private void handleIntent(Intent intent) {

		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			//Get query
			String query = intent.getStringExtra(SearchManager.QUERY);

			//Save query with recent suggestions
			SearchRecentSuggestions suggestions = 
					new SearchRecentSuggestions(this, 
							SampleRecentSuggestionsProvider.AUTHORITY, 
							SampleRecentSuggestionsProvider.MODE); 
			suggestions.saveRecentQuery(query, null);

			ApplicationModel_SearchModel sm = new ApplicationModel_SearchModel(getApplicationContext());

			//Gets results for search
			final ArrayList<RecipeBean> rb = sm.selectRecipe(query);
			final ArrayList<CookbookBean> cb = sm.selectCookbooks(query);
			final ArrayList<UserBean> ub = sm.selectUsers(query);

			//Creates listviews for the different searches 
			getRecipesBasedOnQuery(query, rb);
			getCookbooksBasedOnQuery(query, cb);
			getUsersBasedOnQuery(query, ub);

		}
	}

	/**
	 * Gets users based on the search query and places them in listview
	 * 
	 * @param query		What the user searched for
	 * @param ub		List of users retrieved from db
	 */
	private void getUsersBasedOnQuery(String query, final ArrayList<UserBean> ub)
	{
		TextView usertv = (TextView) findViewById(R.id.userheader);
		usertv.setText("Accounts that feature '" + query + "' :");
		ListView userlistView = (ListView) findViewById(R.id.userlist);
		if(ub.size() == 0)
		{
			UserBean userbean = new UserBean();
			userbean.setName("empty");
			ub.add(userbean);
		} 
		Search_UserAdapter useradapter = new Search_UserAdapter( getApplicationContext(), this,  ub);
		userlistView.setAdapter(useradapter); 
	}

	/**
	 * Gets cookbooks based on search query and places them in listview
	 * 
	 * @param query		What the user searched for
	 * @param cb 		List of cookbooks retrieved from db
	 */
	private void getCookbooksBasedOnQuery(String query, final ArrayList<CookbookBean> cb)
	{
		boolean empty = false;
		TextView cookbooktv = (TextView) findViewById(R.id.cookbookheader);
		cookbooktv.setText("Cookbooks that feature '" + query + "' :");
		ListView cookbooklistView = (ListView) findViewById(R.id.cookbooklist);

		//If empty show no results
		if(cb.size() == 0)
		{
			CookbookBean cookbookbean = new CookbookBean();
			cookbookbean.setName("empty");
			cb.add(cookbookbean); 
			empty = true;
		} 
		Search_CookbookAdapter cookbookadapter = new Search_CookbookAdapter( getApplicationContext(), this,  cb);
		cookbooklistView.setAdapter(cookbookadapter); 

		if(empty == false)
		{
			//Enables clicks to take you to the recipe list view
			cookbooklistView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub
					Intent i = new Intent(Search_ResultsView.this, Recipe_ShelfListView.class);
					//intents used on getting the recipes		
					i.putExtra("uniqueid", cb.get(position).getUniqueid());
					i.putExtra("type", "view");
					i.putExtra("bookname", cb.get(position).getName());
					startActivity(i);

				}                 
			});
		}
	}

	/**
	 * Gets recipes based on the search query and places them in a listview
	 * 
	 * @param query 	What the user searched for
	 * @param rb 		List of recipes found from the search
	 */
	private void getRecipesBasedOnQuery(String query, final ArrayList<RecipeBean> rb)
	{
		boolean empty = false;
		TextView tv = (TextView) findViewById(R.id.recipeheader);
		tv.setText("Recipes that feature '" + query + "' :");
		utils.setText(R.id.recipeheader, 30);
		utils.setText(R.id.cookbookheader, 30);
		utils.setText(R.id.userheader, 30);

		ListView listView = (ListView) findViewById(R.id.list);
		//If list is 0 shows no results
		if(rb.size() == 0)
		{
			RecipeBean recipebean = new RecipeBean();
			recipebean.setName("empty");
			rb.add(recipebean);
			empty = true;
		}
		Search_RecipeAdapter adapter = new Search_RecipeAdapter( getApplicationContext(), this,  rb);
		listView.setAdapter(adapter); 

		//If list view isnt empty enable clicks which takes the user to the recipe view activity
		if(empty == false)
		{
			listView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i = new Intent(Search_ResultsView.this, Recipe_View.class);
					i.putExtra("uniqueidr", rb.get(position).getUniqueid());
					i.putExtra("name", rb.get(position).getName());
					startActivity(i);

				}                 
			});
		}

	}

}