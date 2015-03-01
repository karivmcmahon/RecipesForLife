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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;


/**
 * This class sets up creation of the navigation drawer
 * @author Kari
 *
 */
public class NavigationDrawerCreation   {

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

	public NavigationDrawerCreation(ActionBarActivity activity, String title)
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
		imageids.add(R.drawable.home168);
		navBarTitles.add("Settings");
		imageids.add(R.drawable.settings21);
		navBarTitles.add("Proffile");
		imageids.add(R.drawable.user58);
		navBarTitles.add("Log Off");
		imageids.add(R.drawable.power18);
		drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
		drawerList = (ListView) activity.findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		drawerList.setAdapter(new CustomNavArrayAdapter(activity.getApplicationContext(),
				R.layout.drawer_list_item, navBarTitles, activity, imageids));


		drawerToggle = new ActionBarDrawerToggle(
				activity,                  /* host Activity */
				drawerLayout,         /* DrawerLayout object */
				R.drawable.menu,  /* nav drawer icon to replace 'Up' caret */
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
		//  getSupportActionBar().setHomeButtonEnabled(true);
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
				Intent i = new Intent(activity, CookbookListActivity.class);
				activity.startActivity(i);
			}
			else if(position == 3)
			{
				SharedPreferences sharedpreferences = activity.getSharedPreferences
						(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
				Editor editor = sharedpreferences.edit();
				editor.remove(emailk);
				editor.remove(pass);
				// editor.clear();
				editor.commit();
				Intent i = new Intent(activity, SignUpSignInActivity.class);
				activity.startActivity(i);
			}
			// Toast.makeText(activity, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
			drawerLayout.closeDrawer(drawerList);

		}
	}

}
