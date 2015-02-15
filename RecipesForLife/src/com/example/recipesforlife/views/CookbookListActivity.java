package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.recipeModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class CookbookListActivity extends Activity {
	
	ListView listView;

	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
	 ArrayList<cookbookBean> cookbookList;
	 String type = "";

	public static final String emailk = "emailKey";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		Intent intent = getIntent();
		 Log.v("type", "type" + intent.getStringExtra("type"));
		type = intent.getStringExtra("type");
		listView = (ListView) findViewById(R.id.list);
		  
		 cookbookModel model = new cookbookModel(getApplicationContext());
		 cookbookList = new ArrayList<cookbookBean>();
		 SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		 cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
		 ArrayList<String> values = new ArrayList<String>();
		 for(int i = 0; i < cookbookList.size(); i++)
		 {
			 Log.v("Booook", "Booooook" + cookbookList.get(i).getName());
			 values.add(cookbookList.get(i).getName());
		 }
		
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	              android.R.layout.simple_list_item_1, android.R.id.text1, values);
	    
		 listView.setAdapter(adapter); 
		 
		 listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent(CookbookListActivity.this, RecipeListViewActivity.class);
				 // ListView Clicked item index
               int itemPosition     = position;
               
               // ListView Clicked item value
               String  itemValue    = (String) listView.getItemAtPosition(position);
               String uniqueid = "";
               for(int a = 0; a < cookbookList.size(); a++)
      		 {
      			 if(itemValue.equals(cookbookList.get(a).getName()))
      			{
      				 uniqueid = cookbookList.get(a).getUniqueid();
      				 Log.v("uniqid", "uniqid " + uniqueid);
      			 }
      		 }
				i.putExtra("uniqueid", uniqueid);
				 Log.v("uniqid", "uniqid " + uniqueid);
				 i.putExtra("type", type);
			    startActivity(i);
				
			}
		 });
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

}
