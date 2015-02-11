package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class CookbookViewActivity extends Activity{
	
	util utils;
	Dialog bookAddDialog;
	public static final String MyPREFERENCES = "MyPrefs" ;
	public static final String emailk = "emailKey"; 
	SharedPreferences sharedpreferences;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.cookbookview);
		utils = new util(getApplicationContext(), CookbookViewActivity.this);
		
		Button addButton = (Button) findViewById(R.id.addButton);
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				bookAddDialog = utils.createDialog(CookbookViewActivity.this , R.layout.addcookbookdialog);
				utils.setDialogText(R.id.addBookView,bookAddDialog,22);
				utils.setDialogText(R.id.bookNameView,bookAddDialog,22);
				utils.setDialogText(R.id.bookDescView,bookAddDialog,22);
				utils.setDialogText(R.id.privacyView,bookAddDialog,22);
				List<String> spinnerArray =  new ArrayList<String>();
				spinnerArray.add("public");
				spinnerArray.add("private");
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				    CookbookViewActivity.this, R.layout.item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				Spinner sItems = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
				sItems.setAdapter(adapter);
				Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, bookAddDialog);
				addButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						sharedpreferences =  getApplicationContext().getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
					    cookbookBean book = new cookbookBean();
						book.setName(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
						book.setDescription(utils.getTextFromDialog(R.id.bookDescEditText, bookAddDialog));
						Spinner spinner = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
					    book.setPrivacy(spinner.getSelectedItem().toString());
					    book.setCreator(sharedpreferences.getString(emailk, "DEFAULT"));
					    cookbookModel cbmodel = new cookbookModel(getApplicationContext());
					    cbmodel.insertBook(book, false);
					    
					    bookAddDialog.dismiss();
					}});
				
				bookAddDialog.show();
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
