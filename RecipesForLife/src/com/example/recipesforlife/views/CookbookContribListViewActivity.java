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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Class to display the view of a list of contributers as well as the ability to add a contributer
 * @author Kari
 *
 */
public class CookbookContribListViewActivity extends Activity {

	ListView listView;

	public static final String MyPREFERENCES = "MyPrefs";
	util utils;
	cookbookModel model;
	ArrayList<cookbookBean> cookbookList;
	Activity activity;
	CustomContribListAdapter adapter2;
	ArrayList<String> contribs;
	String type="";
	String cbuniqueid = "";

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
		
		//Get cookbook name values and attach them to an listview adapter
		cookbookList = model.selectCookbooksByCreator(sharedpreferences.getString(emailk, ""));
		ArrayList<String> values = new ArrayList<String>();
		for(int i = 0; i < cookbookList.size(); i++)
		{
			values.add(cookbookList.get(i).getName());
		}	
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);	    
		listView.setAdapter(adapter); 	
		//When items clicked
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				//Get selected cookbooks uniqueid
				final String  itemValue = (String) listView.getItemAtPosition(position);
				for(int i = 0; i < cookbookList.size(); i++)
				{
					if(itemValue.equals(cookbookList.get(i).getName()))
					{
						cbuniqueid = cookbookList.get(i).getUniqueid();
					}
				}

				if(type.equals("manage"))
				{
					//Displays dialog to manage contributers
					manageDialog(itemValue);
				}
				else if(type.equals("edit"))
				{
					//Displays dialog to edit cookbook
					editDialog(itemValue);
				}

			}
		});
	}

	/**
	 * Displays a list of contributers and enables the user to add a contributer
	 * @param itemValue
	 */
	public void manageDialog(final String itemValue)
	{
		Dialog contribDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.contributersdialog);
		utils.setDialogText(R.id.contributerTitle, contribDialog, 22);
		ImageButton addButton = (ImageButton) contribDialog.findViewById(R.id.contributerAddButton);

		//Show list of contributers
		ListView listView2 = (ListView) contribDialog.findViewById(R.id.lists);
		contribs = model.selectCookbookContributers(cbuniqueid, "added");
		adapter2 = new
				CustomContribListAdapter(activity, contribs, getApplicationContext(), cbuniqueid);
		listView2.setAdapter(adapter2); 

		//Display dialog to add a contributer
		addButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					final Dialog addContribDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.contributeradddialog);
					//Getting data
					final TextView errorView = (TextView) addContribDialog.findViewById(R.id.errorView);
					utils.setDialogText(R.id.errorView,addContribDialog,16);
					errorView.setTextColor(Color.parseColor("#F70521"));
					utils.setDialogText(R.id.contributersView, addContribDialog, 22);
					utils.setDialogText(R.id.emailContributerView, addContribDialog, 22);
					//When user has choosen to add someone
					Button addContribButton = utils.setButtonTextDialog(R.id.addContribButton, 22, addContribDialog);
					addContribButton.setOnTouchListener(new OnTouchListener()
					{

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								String uniqueid = "";
								int id = 0;
								accountModel am = new accountModel(getApplicationContext());
								boolean exists = am.checkEmail( utils.getTextFromDialog(R.id.emailEditText, addContribDialog));
								//Check for any errors
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
									//If it exists either update or insert contributer
									boolean contribExists = model.selectContributer(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
									if(contribExists == true)
									{
										model.updateContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id, "added");
									}
									else
									{
										model.insertContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
									}
									//Updates contributer list
									contribs = model.selectCookbookContributers(uniqueid, "added");
									adapter2.clear();
									adapter2.addAll(contribs);
									adapter2.notifyDataSetChanged();
									addContribDialog.dismiss();
								}
							}
							return false;

						}

					});
					addContribDialog.show();



				}
				return false;
			}});
		contribDialog.show();
	}


	/**
	 * Displays a dialog to edit a cookbook
	 * @param itemValue
	 */
	public void editDialog(String itemValue)
	{
		final Dialog editDialog = utils.createDialog(CookbookContribListViewActivity.this, R.layout.cookbookeditdialog);
		final TextView errorView = (TextView) editDialog.findViewById(R.id.errorView);
		//Set texts
		utils.setDialogText(R.id.errorView,editDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.editBookView, editDialog, 22);
		utils.setDialogText(R.id.bookNameView, editDialog, 22);
		utils.setDialogText(R.id.bookDescView, editDialog, 22);
		utils.setDialogText(R.id.privacyView, editDialog, 22);
		Button btn = utils.setButtonTextDialog(R.id.updateButton,22, editDialog);
		ArrayList<cookbookBean> cookbook = model.selectCookbook(cbuniqueid);
		utils.setDialogTextString(R.id.bookNameEditText, editDialog, cookbook.get(0).getName());
		utils.setDialogTextString(R.id.bookDescEditText, editDialog, cookbook.get(0).getDescription());
		
		//Fill adapter
		final Spinner spinner = (Spinner) editDialog.findViewById(R.id.privacySpinner);
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		final String uid = cbuniqueid;
		ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
				CookbookContribListViewActivity.this, R.layout.item, spinnerArray);
		spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinneradapter);
		spinner.setSelection(utils.getIndex(spinner, cookbook.get(0).getPrivacy()));

		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				cookbookBean cb = new cookbookBean();
				//Error checking
				if(utils.getTextFromDialog(R.id.bookNameEditText, editDialog).equals(""))
				{
					errorView.setText("Please enter a cookbook name");
				}
				else if(utils.getTextFromDialog(R.id.bookDescEditText, editDialog).equals(""))
				{
					errorView.setText("Please enter a description");
				}
				else
				{
					//Update cookbook
					cb.setName(utils.getTextFromDialog(R.id.bookNameEditText, editDialog));
					cb.setDescription(utils.getTextFromDialog(R.id.bookDescEditText, editDialog));
					cb.setPrivacy(spinner.getSelectedItem().toString());
					cb.setUniqueid(uid);
					model.updateBook(cb);
					editDialog.dismiss();
				}

			}});

		editDialog.show();
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