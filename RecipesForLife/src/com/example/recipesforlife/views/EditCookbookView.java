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
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
	byte[] byteArray;
	String uid;
	Spinner spinner;
	private static final int SELECT_PHOTO = 101;
	
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

		Button browseButton = utils.setButtonTextDialog(R.id.browseButton, 22, editDialog);
		browseButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent pickIntent = new Intent();
				pickIntent.setType("image/*");
				pickIntent.setAction(Intent.ACTION_GET_CONTENT);

				Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				String pickTitle = "Select or take a new Picture"; // Or get from strings.xml
				Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);
				chooserIntent.putExtra
				(
						Intent.EXTRA_INITIAL_INTENTS, 
						new Intent[] { takePhotoIntent }
						); 

				activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
			}

		});
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
		utils.setDialogText(R.id.cookbookImageView, editDialog, 22);
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
		 spinner.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		spinner.setSelection(utils.getIndex(spinner, cookbook.get(0).getPrivacy()));
	}

	/**
	 * Sets the text in the edit text dialog box
	 */
	public void setText()
	{
		utils.setDialogTextString(R.id.bookNameEditText, editDialog, cookbook.get(0).getName());
		utils.setDialogTextString(R.id.bookDescEditText, editDialog, cookbook.get(0).getDescription());
		byteArray = cookbook.get(0).getImage();
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
			cb.setImage(byteArray);
			try
			{
				model.updateBook(cb, false);
				
				//updates list after book update
				SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
				cookbookModel model = new cookbookModel(context);
				ArrayList<cookbookBean> cookbookList = model.selectCookbooksByUser(sharedpreferences.getString(emailk, ""));
				CookbookListActivity.values.clear();
				CookbookListActivity.ids.clear();
				CookbookListActivity.images.clear();
				for(int i = 0; i < cookbookList.size(); i++)
				{
					CookbookListActivity.values.add(cookbookList.get(i).getName());
					CookbookListActivity.ids.add(cookbookList.get(i).getUniqueid());
					CookbookListActivity.images.add(cookbookList.get(i).getImage());
				}
				//If the list is under 6 then create empty rows to fill the layout of the app
				if(cookbookList.size() < 6)
				{
					int num = 6 - cookbookList.size();
					for(int a = 0; a < num; a++)
					{
						byte[] emptyarr = new byte[0];
						CookbookListActivity.values.add("");
						CookbookListActivity.ids.add("");
						CookbookListActivity.images.add(emptyarr);
					}
				}
				CookbookListActivity.adapter.notifyDataSetChanged();
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
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}

	
}


