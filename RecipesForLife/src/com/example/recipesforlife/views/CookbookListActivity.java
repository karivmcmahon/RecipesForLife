package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
	CustomCookbookListAdapter adapter;
	ArrayList<String> values;
	ArrayList<String> ids;
	
	 private ArrayList<String> mPlanetTitles;
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

		 mPlanetTitles = new ArrayList<String>();
		 
        mPlanetTitles.add("Home");
        mPlanetTitles.add("Settings");
        mPlanetTitles.add("Proffile");
        mPlanetTitles.add("Log Off");
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
 
        // Set the adapter for the list view
        mDrawerList.setAdapter(new CustomNavArrayAdapter(getApplicationContext(),
                R.layout.drawer_list_item, mPlanetTitles, CookbookListActivity.this));
       
 
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
		values = new ArrayList<String>();
		ids = new ArrayList<String>();
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

		adapter = new CustomCookbookListAdapter(this, values, getApplicationContext(), ids);
		listView.setAdapter(adapter); 
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	        switch (item.getItemId()) {
	      
	        case R.id.action_bookadd:
	            Log.v("add click", "add click");
	            addDialog();
	            return true;
	        default:
	          
	 
	        return super.onOptionsItemSelected(item);
	        }
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
	
	public void addDialog()
	{
		final Dialog bookAddDialog = utils.createDialog(CookbookListActivity.this , R.layout.addcookbookdialog);
		final TextView errorView = (TextView) bookAddDialog.findViewById(R.id.errorView);
		//Fills information into text view
		utils.setDialogText(R.id.errorView,bookAddDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
	    utils.setDialogText(R.id.addBookView,bookAddDialog,22);
		utils.setDialogText(R.id.bookNameView,bookAddDialog,22);
		utils.setDialogText(R.id.bookDescView,bookAddDialog,22);
		utils.setDialogText(R.id.privacyView,bookAddDialog,22);
		
		//Fill spinner
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				CookbookListActivity.this, R.layout.item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
		sItems.setAdapter(adapter);

		//Clicks to add the data
		Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, bookAddDialog);
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// Retrieves data and inserts into database
				SharedPreferences sharedpreferences =  getApplicationContext().getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
				cookbookBean book = new cookbookBean();
				cookbookModel model = new cookbookModel(getApplicationContext());
				int id = model.selectCookbooksID(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog), sharedpreferences.getString(emailk, "DEFAULT"));
				//Check for errors
				if(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog).equals(""))
				{
					errorView.setText("Please enter the name");
				}
				else if(id != 0)
				{
					errorView.setText("You already have a cookbook with that name");
				}
				else if(utils.getTextFromDialog(R.id.bookDescEditText, bookAddDialog).equals(""))
				{
					errorView.setText("Please enter the description");
				}
				else
				{
					//Insert cookbook
					book.setName(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
					book.setDescription(utils.getTextFromDialog(R.id.bookDescEditText, bookAddDialog));
					Spinner spinner = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
					book.setPrivacy(spinner.getSelectedItem().toString());
					book.setCreator(sharedpreferences.getString(emailk, "DEFAULT"));
					cookbookModel cbmodel = new cookbookModel(getApplicationContext());
					String uniqueid = cbmodel.insertBook(book, false);
					values.add(book.getName());
					ids.add(uniqueid);
				   adapter.notifyDataSetChanged(); 
					bookAddDialog.dismiss();
				}
			}});

		bookAddDialog.show();
	}



   }


