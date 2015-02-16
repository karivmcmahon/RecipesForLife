package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

public class CookbookContribListViewActivity extends Activity {
	
	ListView listView;

	private SharedPreferences sharedpreferences;
	public static final String MyPREFERENCES = "MyPrefs";
	util utils;
	cookbookModel model;
	ArrayList<cookbookBean> cookbookList;
	Activity activity;
	 CustomContribListAdapter adapter2;
	 ArrayList<String> contribs;
	 String type="";

	public static final String emailk = "emailKey";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.listview);
		activity = CookbookContribListViewActivity.this;
	    Intent intent = getIntent();
	    type = intent.getStringExtra("type");
		utils = new util(getApplicationContext(), CookbookContribListViewActivity.this);
		listView = (ListView) findViewById(R.id.list);
		  
		  model = new cookbookModel(getApplicationContext());
		 cookbookList = new ArrayList<cookbookBean>();
		 SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		 cookbookList = model.selectCookbooksByCreator(sharedpreferences.getString(emailk, ""));
		 ArrayList<String> values = new ArrayList<String>();
		// values.add("");
		 for(int i = 0; i < cookbookList.size(); i++)
		 {
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
				//Intent i = new Intent(CookbookListActivity.this, RecipeListViewActivity.class);
				 // ListView Clicked item index
               int itemPosition     = position;
               final String  itemValue    = (String) listView.getItemAtPosition(position);
               String uniqueid = "";
               for(int i = 0; i < cookbookList.size(); i++)
               {
      			 if(itemValue.equals(cookbookList.get(i).getName()))
      			 {
      				 uniqueid = cookbookList.get(i).getUniqueid();
      			 }
      		 	}
               
	     	if(type.equals("manage"))
	     	{
               Dialog contribDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.contributersdialog);
               utils.setDialogText(R.id.contributerTitle, contribDialog, 22);
               ImageButton addButton = (ImageButton) contribDialog.findViewById(R.id.contributerAddButton);
             
               ListView listView2 = (ListView) contribDialog.findViewById(R.id.lists);
                contribs = model.selectCookbookContributers(uniqueid);
	            
               adapter2 = new
            	        CustomContribListAdapter(activity, contribs, getApplicationContext(), uniqueid);
	     	    
	     	  listView2.setAdapter(adapter2); 
               
               addButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
					  final Dialog addContribDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.contributeradddialog);
					//Getting data
						final TextView errorView = (TextView) addContribDialog.findViewById(R.id.errorView);
						utils.setDialogText(R.id.errorView,addContribDialog,16);
						errorView.setTextColor(Color.parseColor("#F70521"));
					  
					  utils.setDialogText(R.id.contributersView, addContribDialog, 22);
		              utils.setDialogText(R.id.emailContributerView, addContribDialog, 22);
		             
		              Button addContribButton = utils.setButtonTextDialog(R.id.addContribButton, 22, addContribDialog);
		              addContribButton.setOnTouchListener(new OnTouchListener()
		              {

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
							// TODO Auto-generated method stub
							String uniqueid = "";
							int id = 0;
							Log.v("s", "s " + utils.getTextFromDialog(R.id.emailEditText, addContribDialog));
							accountModel am = new accountModel(getApplicationContext());
							boolean exists = am.checkEmail( utils.getTextFromDialog(R.id.emailEditText, addContribDialog));
							if (exists == false)
							{
								errorView.setText("The user entered does not exist");
							}
							else if( utils.getTextFromDialog(R.id.emailEditText, addContribDialog).equals(""))
							{
								errorView.setText("Please enter a user");
							}
							else
							{
								for(int i = 0; i < cookbookList.size(); i++)
								 {
									 if(itemValue.equals(cookbookList.get(i).getName()))
									 {
										 uniqueid = cookbookList.get(i).getUniqueid();
									 }
								 }
								id = model.selectCookbooksIDByUnique(uniqueid);
								Log.v("s", "s " + exists);
								Log.v("s", "s " + uniqueid);
								Log.v("s", "s " + id);
								model.insertContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
								
								adapter2.clear();
							   contribs = model.selectCookbookContributers(uniqueid);
							   adapter2.addAll(contribs);
								adapter2.notifyDataSetChanged();
								addContribDialog.dismiss();
							}
							}
							return false;
							
						}
		            	  
		              });
		              addContribDialog.show();
					Log.v("CLICK BUTTON", "CLICK BUTTON");
					
					
					}
					return false;
				}});
               contribDialog.show();
	     	}
	     	else if(type.equals("edit"))
	     	{
	     		  final Dialog editDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.cookbookeditdialog);
	               utils.setDialogText(R.id.editBookView, editDialog, 22);
	               utils.setDialogText(R.id.bookNameView, editDialog, 22);
	               utils.setDialogText(R.id.bookDescView, editDialog, 22);
	               utils.setDialogText(R.id.privacyView, editDialog, 22);
	              Button btn = utils.setButtonTextDialog(R.id.updateButton,22, editDialog);
	               ArrayList<cookbookBean> cookbook = model.selectCookbook(uniqueid);
	               utils.setDialogTextString(R.id.bookNameEditText, editDialog, cookbook.get(0).getName());
	               utils.setDialogTextString(R.id.bookDescEditText, editDialog, cookbook.get(0).getDescription());
	               final Spinner spinner = (Spinner) editDialog.findViewById(R.id.privacySpinner);
	               List<String> spinnerArray =  new ArrayList<String>();
	               spinnerArray.add("public");
				    spinnerArray.add("private");
				    ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
						    CookbookContribListViewActivity.this, R.layout.item, spinnerArray);

						spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						
						spinner.setAdapter(spinneradapter);
						spinner.setSelection(getIndex(spinner, cookbook.get(0).getPrivacy()));
	             
						btn.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								String name = utils.getTextFromDialog(R.id.bookNameEditText, editDialog);
								String desc = utils.getTextFromDialog(R.id.bookDescEditText, editDialog);
								String value = spinner.getSelectedItem().toString();
								Log.v("n ", "N " + name + desc + value);
								editDialog.dismiss();
								
							}});
						
						editDialog.show();
	     	}
				
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
	 
	 private int getIndex(Spinner spinner, String myString)
	 {
		 int index = 0;	
		  for (int i=0;i<spinner.getCount();i++)
		  {
		   if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString))
		   {
		    index = i;
		    i=spinner.getCount();//will stop the loop, kind of break, by making condition false
		   }
		  }
		  return index;
	 } 

}