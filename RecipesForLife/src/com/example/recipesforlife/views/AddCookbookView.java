package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Displays dialog to create a cookbook
 * @author Kari
 *
 */
public class AddCookbookView extends CookbookListActivity {

	ActionBarActivity activity;
	Context context;
	util utils;
	Dialog bookAddDialog;
	TextView errorView;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String pass = "passwordKey"; 
	public static final String emailk = "emailKey";
	ArrayList<String> bookDetails;
	int finished = 0;

	public AddCookbookView(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new util(context, activity);
	}

	/**
	 * Creates an add a cookbook dialog
	 */
	public void addCookbook()
	{
		bookAddDialog = utils.createDialog(activity , R.layout.addcookbookdialog);
		errorView = (TextView) bookAddDialog.findViewById(R.id.errorView);
		bookDetails = new ArrayList<String>();
		//sets up the dialog style
		setStyle();
		//fills the spinner
		fillSpinner();


		//Clicks to add the data
		Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, bookAddDialog);
		addButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//once book is added then add the details to list and notify the change
				addBook();
				
				CookbookListActivity.values.add(0, bookDetails.get(0));
				CookbookListActivity.ids.add(0, bookDetails.get(1));
				Log.v("cookbook size ", "cookbook size " + CookbookListActivity.values.size());
				CookbookListActivity.adapter.notifyDataSetChanged();		
			}});
		bookAddDialog.show();	
	}

	/**
	 * Sets up the dialog style
	 */
	public void setStyle()
	{
		utils.setDialogText(R.id.errorView,bookAddDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.addBookView,bookAddDialog,22);
		utils.setDialogText(R.id.bookNameView,bookAddDialog,22);
		utils.setDialogText(R.id.bookDescView,bookAddDialog,22);
		utils.setDialogText(R.id.privacyView,bookAddDialog,22);
	}


	/**
	 * Fills the spinner in the dialog
	 */
	public void fillSpinner()
	{
		//Fill spinner
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
		sItems.setAdapter(adapter);
	}

	/**
	 * Checks for any errors and if none adds the cookbook to database
	 */
	public void addBook()
	{
		// Retrieves data and inserts into database
		SharedPreferences sharedpreferences =  context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		cookbookBean book = new cookbookBean();
		cookbookModel model = new cookbookModel(context);
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
			cookbookModel cbmodel = new cookbookModel(context);
			String uniqueid = cbmodel.insertBook(book, false);
			bookDetails.add(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
			bookDetails.add(uniqueid);
			bookAddDialog.dismiss();
		}
	}
}


