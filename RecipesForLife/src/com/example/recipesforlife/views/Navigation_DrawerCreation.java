package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;


/**
 * This class sets up creation of the navigation drawer
 * @author Kari
 *
 */
public class Navigation_DrawerCreation   {

	private ArrayList<String> navBarTitles;
	private ArrayList<Integer> imageids;
	private DrawerLayout drawerLayout;
	private ListView drawerList;
	private ActionBarDrawerToggle drawerToggle;
	ActionBarActivity activity;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String pass = "passwordKey";
	public static final String emailk = "emailKey";
	String title;
	Context context;

	public Navigation_DrawerCreation(ActionBarActivity activity, String title)
	{
		this.activity = activity;
		this.title = title;
	}


	public void createDrawer()
	{
		//Set title of action bar
		activity.getSupportActionBar().setTitle(title);

		//Set up the nav bar titles and icons
		navBarTitles = new ArrayList<String>();
		imageids = new ArrayList<Integer>();
		navBarTitles.add("Home");
		imageids.add(R.drawable.icon_home);
		navBarTitles.add("Explore");
		imageids.add(R.drawable.icon_explore);
		
		//navBarTitles.add("Proffile");
		//imageids.add(R.drawable.icon_user2);
		
		navBarTitles.add("Log Off");
		imageids.add(R.drawable.icon_power);
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		drawerList = (ListView) activity.findViewById(R.id.left_drawer);
		
		// Set the adapter for the list view
		drawerList.setAdapter(new Navigation_Adapter(activity.getApplicationContext(),
				R.layout.navbar_draweritem, navBarTitles, activity, imageids));


		drawerToggle = new ActionBarDrawerToggle(
				activity,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.icon_menu,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				//
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				drawerList.bringToFront();
				drawerLayout.requestLayout();
			}
		};

		// Set the drawer toggle as the DrawerListener
		drawerLayout.setDrawerListener(drawerToggle);

		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		drawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	public void syncState()
	{
		drawerToggle.syncState();
	}

	public void config(Configuration newConfig)
	{
		drawerToggle.onConfigurationChanged(newConfig);
	}

	public boolean drawerToggle(MenuItem item)
	{
		if (drawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return false;
	}

	/**
	 * Handles clicks on the navigation drawer - based on the selected position these links 
	 * goto specific activitys
	 * @author Kari
	 *
	 */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			if(position == 0)
			{
				//Goes to homepage
				Intent i = new Intent(activity, Cookbook_ShelfListView.class);
				activity.startActivity(i);
			}
			else if(position == 1)
			{
				//Goes to explore view
				Intent i = new Intent(activity, Search_ExploreView.class);
				activity.startActivity(i);
			}
			else if(position == 2)
			{
				//Clears shared prefs and logs out
				SharedPreferences sharedpreferences = activity.getSharedPreferences
						(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
				Editor editor = sharedpreferences.edit();
				editor.remove(emailk);
				editor.remove(pass);
				editor.commit();
				Intent i = new Intent(activity, Account_SignUpSignInView.class);
				activity.startActivity(i);
			}
			//closes drawer
			drawerLayout.closeDrawer(drawerList);

		}
	}

}
