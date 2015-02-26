package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;












import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Class to display a list of cookbooks involving the users
 * @author Kari
 *
 */
public class CookbookListActivity extends ActionBarActivity {

	ListView listView;
	public static final String MyPREFERENCES = "MyPrefs";
	ArrayList<cookbookBean> cookbookList;
	public static final String pass = "passwordKey"; 
	String type = "";
	util utils;
	
	 private String[] mPlanetTitles;
	    private DrawerLayout mDrawerLayout;
	    private ListView mDrawerList;
	    private CharSequence mTitle;
	    private ActionBarDrawerToggle mDrawerToggle;

	public static final String emailk = "emailKey";
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		
		getSupportActionBar().setTitle("My Cookbooks");
		//centerActionBarTitle();

	
		 
        mPlanetTitles = new String[]{"Home", "Settings", "Profile", "Log Off"};
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
 
        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPlanetTitles));
       
 
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.menu,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {
 
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("My Cookbooks");
            }
 
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("My Cookbooks");
                mDrawerList.bringToFront();
                mDrawerLayout.requestLayout();
            }
        };
 
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
 
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
     //  getSupportActionBar().setHomeButtonEnabled(true);
       mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		utils = new util(getApplicationContext(), this);
		

		Intent intent = getIntent();
		type = intent.getStringExtra("type");
		listView = (ListView) findViewById(R.id.list);
		cookbookModel model = new cookbookModel(getApplicationContext());

		//Gets list of cookbooks and displays them
		cookbookList = new ArrayList<cookbookBean>();
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Fill list adapter with cookbook names
		cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
		ArrayList<String> values = new ArrayList<String>();
		ArrayList<String> ids = new ArrayList<String>();
		for(int i = 0; i < cookbookList.size(); i++)
		{
			values.add(cookbookList.get(i).getName());
			ids.add(cookbookList.get(i).getUniqueid());
		}
		
		if(cookbookList.size() < 6)
		{
			int num = 6 - cookbookList.size();
			for(int a = 0; a < num; a++)
			{
				values.add("");
				ids.add("");
			}
		}

		CustomCookbookListAdapter adapter = new CustomCookbookListAdapter(this, values, getApplicationContext(), ids);
		listView.setAdapter(adapter); 
		
		
	}



	@Override
	protected void onResume() {
		super.onResume();
	}
	
	 @Override
	    protected void onPostCreate(Bundle savedInstanceState) {
	        super.onPostCreate(savedInstanceState);
	        // Sync the toggle state after onRestoreInstanceState has occurred.
	        mDrawerToggle.syncState();
	    }
	 
	  @Override
	    public void onConfigurationChanged(Configuration newConfig) {
	        super.onConfigurationChanged(newConfig);
	        mDrawerToggle.onConfigurationChanged(newConfig);
	    }
	 
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	Log.v("Click c", "Click c");
	        // Pass the event to ActionBarDrawerToggle, if it returns
	        // true, then it has handled the app icon touch event
	        if (mDrawerToggle.onOptionsItemSelected(item)) {
	            return true;
	        }
	        
	        // Handle your other action bar items...
	 
	        return super.onOptionsItemSelected(item);
	    }
	 
	    /**
	     * Swaps fragments in the main content view
	     */
	  
	 
	    @Override
	    public void setTitle(CharSequence title) {
	        mTitle = title;
	        getSupportActionBar().setTitle(mTitle);
	    }
	    
	    private class DrawerItemClickListener implements ListView.OnItemClickListener {
	        @Override
	        public void onItemClick(AdapterView parent, View view, int position, long id) {
	        	if(position == 0)
	        	{
	        		Intent i = new Intent(CookbookListActivity.this, CookbookListActivity.class);
	        		startActivity(i);
	        	}
	        	else if(position == 3)
	        	{
	        		  SharedPreferences sharedpreferences = getSharedPreferences
						      (SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
						      Editor editor = sharedpreferences.edit();
						     editor.remove(emailk);
						     editor.remove(pass);
						     // editor.clear();
						      editor.commit();
						      Intent i = new Intent(CookbookListActivity.this, SignUpSignInActivity.class);
						     startActivity(i);
	        	}
	            Toast.makeText(CookbookListActivity.this, ((TextView)view).getText(), Toast.LENGTH_LONG).show();
	            mDrawerLayout.closeDrawer(mDrawerList);
	 
	        }
	    }
	 
	   


	private void centerActionBarTitle()
	    {
			
	        int titleId = 0;
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	            titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	            Log.v("titleid", "titleid" + titleId);
	        } else {
	        	titleId = R.id.action_bar_title;
	            
	        }

	        // Final check for non-zero invalid id
	        if (titleId > 0)
	        {
	            TextView titleTextView = (TextView) findViewById(titleId);

	            DisplayMetrics metrics = getResources().getDisplayMetrics();

	            // Fetch layout parameters of titleTextView (LinearLayout.LayoutParams : Info from HierarchyViewer)
	          //  LinearLayout.LayoutParams txvPars = (LayoutParams) titleTextView.getLayoutParams();
	          //  txvPars.gravity = Gravity.CENTER_HORIZONTAL;
	           // txvPars.width = metrics.widthPixels;
	          //  titleTextView.setLayoutParams(txvPars);
	          //  Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/elsie.ttf");
	         //   titleTextView.setTypeface(typeFace);
	            titleTextView.setTextSize(28);
	           // titleTextView.setText("My Cookbooks");
	          //  titleTextView.setGravity(Gravity.CENTER);
	        }
	    }



   }


