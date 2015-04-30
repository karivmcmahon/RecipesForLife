package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.util.Util;

/**
 * Displays the edit cookbook dialog
 * @author Kari
 *
 */
class Cookbook_EditView  {

	private Context context;
	private Activity activity;
	private Util utils;
	private Cookbook_ListAdapter ccadapter;
	private int position;
	private static final String emailk = "emailKey";
	private static final String MyPREFERENCES = "MyPrefs";
	private Dialog editDialog;
	private TextView errorView;
	private ApplicationModel_CookbookModel model;
	private ArrayList<CookbookBean> cookbook;
	private byte[] byteArray;
	private String uid;
	private Spinner spinner;
	private static final int SELECT_PHOTO = 101;

	Cookbook_EditView(Context context, Activity activity, Cookbook_ListAdapter adapter, int position)
	{
		this.context = context;
		this.activity = activity;
		ccadapter = adapter;
		this.position = position;
		utils = new Util(context, activity);
	}

	/**
	 * Sets up the edit book dialog and enables user to edit the cookbook
	 */
	void editBook()
	{
		//Sets up dialog
		editDialog = utils.createDialog(activity, R.layout.cookbook_editdialog);
		errorView = (TextView) editDialog.findViewById(R.id.errorView);

		//Sets up models
		model = new ApplicationModel_CookbookModel(context);
		cookbook = model.selectCookbook(ccadapter.bookids.get(position));

		//Fill spinner for dialog
		fillSpinner();

		//Set style for dialog
		setStyle();

		//Set text for dialog
		setText();

		//Dismiss dialog
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, editDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				editDialog.dismiss();

			}});

		//Starts intent to get image from phone if selected
		Button browseButton = utils.setButtonTextDialog(R.id.browseButton, 22, editDialog);
		browseButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				Intent chooserIntent = utils.getImageIntent();
				activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
			}

		});


		//edits cookbook if selected
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
	private void setStyle()
	{
		//Set texts
		utils.setDialogText(R.id.errorView,editDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.editBookView, editDialog, 22);
		utils.setDialogText(R.id.bookNameView, editDialog, 22);
		utils.setDialogText(R.id.bookDescView, editDialog, 22);
		utils.setDialogText(R.id.privacyView, editDialog, 22);
		utils.setDialogText(R.id.cookbookImageView, editDialog, 22);
	}

	/**
	 * Fills the dialog spinner
	 */
	private void fillSpinner()
	{
		//Fill adapter
		spinner = (Spinner) editDialog.findViewById(R.id.privacySpinner);
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		uid = ccadapter.bookids.get(position);
		ArrayAdapter<String> spinneradapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		spinneradapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinneradapter);
		spinner.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		spinner.setSelection(utils.getIndex(spinner, cookbook.get(0).getPrivacy()));
	}

	/**
	 * Sets the text in the edit text dialog box
	 */
	private void setText()
	{
		utils.setDialogTextString(R.id.bookNameEditText, editDialog, cookbook.get(0).getName());
		utils.setDialogTextString(R.id.bookDescEditText, editDialog, cookbook.get(0).getDescription());
		byteArray = cookbook.get(0).getImage();
	}

	/**
	 * Checks for any errors and edits the cookbook in the database
	 */
	private void edit()
	{
		CookbookBean cb = new CookbookBean();

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
			cb.setImage(byteArray);
			cb.setProgress("added");
			try
			{
				model.updateBook(cb, false); //update book in db

				//updates list after book update
				SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
				ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(context);

				ArrayList<CookbookBean> cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));

				//Clear list
				Cookbook_ShelfListView.values.clear();
				Cookbook_ShelfListView.ids.clear();
				Cookbook_ShelfListView.images.clear();

				//Update list
				for(int i = 0; i < cookbookList.size(); i++)
				{
					Cookbook_ShelfListView.values.add(cookbookList.get(i).getName());
					Cookbook_ShelfListView.ids.add(cookbookList.get(i).getUniqueid());
					Cookbook_ShelfListView.images.add(cookbookList.get(i).getImage());
				}

				//If the list is under 6 then create empty rows to fill the layout of the app
				if(cookbookList.size() < 6)
				{
					int num = 6 - cookbookList.size();
					for(int a = 0; a < num; a++)
					{
						byte[] emptyarr = new byte[0];
						Cookbook_ShelfListView.values.add("");
						Cookbook_ShelfListView.ids.add("");
						Cookbook_ShelfListView.images.add(emptyarr);
					}
				}

				//notifys list change
				Cookbook_ShelfListView.adapter.notifyDataSetChanged();
			}

			catch(SQLException e)
			{
				Toast.makeText(context, "Cookbook was not edited", Toast.LENGTH_LONG).show();
			}
			editDialog.dismiss();
		}
	}

	/**
	 * Recieve result from intent
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == activity.RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					//Gets image and file and rotate
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());

					//Set to dialog and compresses then set to byte array
					String imageName = f.getName();
					utils.setDialogTextString(R.id.cookbookImageEditText, editDialog, imageName);
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byteArray = stream.toByteArray(); 

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

}


