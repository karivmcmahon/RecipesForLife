package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.widget.LinearLayout.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Class to display a list of cookbooks involving the users
 * @author Kari
 *
 */
public class CookbookListActivity extends Activity {

	ListView listView;
	public static final String MyPREFERENCES = "MyPrefs";
	ArrayList<cookbookBean> cookbookList;
	String type = "";
	util utils;

	public static final String emailk = "emailKey";
	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		
		getActionBar().setTitle("My Cookbooks");
		centerActionBarTitle();

	
		
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
		
		//When item clicked move to next activity
		/**listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Intent i = new Intent(CookbookListActivity.this, RecipeListViewActivity.class);
				

				// ListView Clicked item value - gets matching uniqueid
				String  itemValue    = (String) listView.getItemAtPosition(position);
				String uniqueid = "";
				for(int a = 0; a < cookbookList.size(); a++)
				{
					if(itemValue.equals(cookbookList.get(a).getName()))
					{
						uniqueid = cookbookList.get(a).getUniqueid();
					}
				}
				i.putExtra("uniqueid", uniqueid);
				i.putExtra("type", type);
				startActivity(i);

			}
		}); **/
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
	
	


	private void centerActionBarTitle()
	    {

	        int titleId = 0;
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
	        {
	            titleId = getResources().getIdentifier("action_bar_title", "id", "android");
	        }
	        else
	        {
	            // This is the id is from your app's generated R class when ActionBarActivity is used 
	            // for SupportActionBar
	         //   titleId = R.id.ac
	        }

	        // Final check for non-zero invalid id
	        if (titleId > 0)
	        {
	            TextView titleTextView = (TextView) findViewById(titleId);

	            DisplayMetrics metrics = getResources().getDisplayMetrics();

	            // Fetch layout parameters of titleTextView (LinearLayout.LayoutParams : Info from HierarchyViewer)
	            LinearLayout.LayoutParams txvPars = (LayoutParams) titleTextView.getLayoutParams();
	            txvPars.gravity = Gravity.CENTER_HORIZONTAL;
	            txvPars.width = metrics.widthPixels;
	            titleTextView.setLayoutParams(txvPars);
	            Typeface typeFace=Typeface.createFromAsset(getApplicationContext().getAssets(),"fonts/elsie.ttf");
	            titleTextView.setTypeface(typeFace);
	            titleTextView.setTextSize(28);
	            titleTextView.setText("My Cookbooks");
	            titleTextView.setGravity(Gravity.CENTER);
	        }
	    }

}
