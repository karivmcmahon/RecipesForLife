package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Displays the edit cookbook dialog
 * @author Kari
 *
 */
public class EditCookbookView  {

	Context context;
	Activity activity;
	util utils;
	CustomCookbookListAdapter ccadapter;
	int position;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Dialog editDialog;
	TextView errorView;
	cookbookModel model;
	ArrayList<cookbookBean> cookbook;
	String uid;
	Spinner spinner;

	public EditCookbookView(Context context, Activity activity, CustomCookbookListAdapter adapter, int position)
	{
		this.context = context;
		this.activity = activity;
		ccadapter = adapter;
		this.position = position;
		utils = new util(context, activity);
	}

	/**
	 * Sets up the edit book dialog and enables user to edit the cookbook
	 */
	public void editBook()
	{
		editDialog = utils.createDialog(activity, R.layout.cookbookeditdialog);
		errorView = (TextView) editDialog.findViewById(R.id.errorView);
		model = new cookbookModel(context);
		cookbook = model.selectCookbook(ccadapter.bookids.get(position));
		fillSpinner();
		setStyle();
		setText();
		Button btn = utils.setButtonTextDialog(R.id.updateButton,22, editDialog);
		btn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				edit();
			}

		});

		editDialog.show();
	}

	/**
	 * Sets the dialog styles
	 */
	public void setStyle()
	{
		//Set texts
		utils.setDialogText(R.id.errorView,editDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.editBookView, editDialog, 22);
		utils.setDialogText(R.id.bookNameView, editDialog, 22);
		utils.setDialogText(R.id.bookDescView, editDialog, 22);
		utils.setDialogText(R.id.privacyView, editDialog, 22);
	}

	/**
	 * Fills the dialog spinner
	 */
	public void fillSpinner()
	{
		//Fill adapter
		spinner = (Spinner) editDialog.findViewById(R.id.privacySpinner);
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		uid = ccadapter.bookids.get(position);
		ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
				activity, R.layout.item, spinnerArray);
		spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinneradapter);
		spinner.setSelection(utils.getIndex(spinner, cookbook.get(0).getPrivacy()));
	}

	/**
	 * Sets the text in the edit text dialog box
	 */
	public void setText()
	{
		utils.setDialogTextString(R.id.bookNameEditText, editDialog, cookbook.get(0).getName());
		utils.setDialogTextString(R.id.bookDescEditText, editDialog, cookbook.get(0).getDescription());
	}

	/**
	 * Checks for any errors and edits the cookbook in the database
	 */
	public void edit()
	{
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

			SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			ArrayList<cookbookBean >cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
			ArrayList<String> values = new ArrayList<String>();
			ArrayList<String> ids = new ArrayList<String>();
			for(int i = 0; i < cookbookList.size(); i++)
			{
				values.add(cookbookList.get(i).getName());
				ids.add(cookbookList.get(i).getUniqueid());
			}
			ccadapter.booknames = values;
			ccadapter.bookids = ids;
			ccadapter.notifyDataSetChanged();
			editDialog.dismiss();
		}
	}
}


