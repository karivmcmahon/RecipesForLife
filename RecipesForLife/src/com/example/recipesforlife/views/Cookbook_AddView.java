package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
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
 * Displays dialog to create/add a cookbook and handles the view code for adding a cookbook
 * 
 * @author Kari
 *
 */
class Cookbook_AddView extends Cookbook_ShelfListView {

	private ActionBarActivity activity;
	private Context context;
	private Util utils;
	private Dialog bookAddDialog;
	private TextView errorView; 
	private static final String emailk = "emailKey";
	private static final int SELECT_PHOTO = 100;
	private byte[] byteArray;
	private SharedPreferences sharedpreferences;

	Cookbook_AddView(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}

	/**
	 * Checks for any errors with the users input
	 * If no errors inserts into the database
	 */
	private void addBook()
	{
		// Set up
		sharedpreferences =  context.getSharedPreferences(Account_SignUpSignInView.MyPREFERENCES, Context.MODE_PRIVATE);
		CookbookBean book = new CookbookBean();

		//Check for errors
		if(errorCheck() == false)
		{
			//Sets cookbook data
			book.setName(utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
			book.setDescription(utils.getTextFromDialog(R.id.bookDescEditText, bookAddDialog));
			Spinner spinner = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
			book.setPrivacy(spinner.getSelectedItem().toString());
			book.setCreator(sharedpreferences.getString(emailk, "DEFAULT"));

			//if an image is not set - then set a default
			if(utils.getTextFromDialog(R.id.cookbookImageEditText, bookAddDialog).equals(""))
			{
				setDefaultImage();
			}  

			book.setImage(byteArray); // set image to byte array

			ApplicationModel_CookbookModel cbmodel = new ApplicationModel_CookbookModel(context);
	
			try
			{
				//insert book into database and get unique id
				String uniqueid = cbmodel.insertBook(book, false);

				//Add on to the list which displays on the shelf
				Cookbook_ShelfListView.values.add(0, utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
				Cookbook_ShelfListView.ids.add(0, uniqueid);
				Cookbook_ShelfListView.images.add(0, byteArray);
				bookAddDialog.dismiss();
				Cookbook_ShelfListView.adapter.notifyDataSetChanged();	
			}catch(SQLException e)
			{
				Toast.makeText(context, "Cookbook was not added", Toast.LENGTH_LONG).show();
			}

		}
	}


	/**
	 * Checks for any errors with user input
	 * @return boolean 	true if errors, false if no errors
	 */
	private boolean errorCheck()
	{
		boolean error = true;
		
		//Retrieve id for cookbook inserted in textbox - if its not  0 then it already exists
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
			error = false;
		}
		
		return error;
	}

	/**
	 * This method gets a default image for cookbooks and sets it to the byte array
	 */
	private void setDefaultImage()
	{
		Bitmap bitmap = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.image_default_cookbook)).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] array = stream.toByteArray();
		byteArray = array;
	}

	/**
	 * Creates an add a cookbook dialog
	 */
    void addCookbook()
	{
		//set up
		bookAddDialog = utils.createDialog(activity , R.layout.cookbook_adddialog);
		errorView = (TextView) bookAddDialog.findViewById(R.id.errorView);
	
		//sets up the dialog style
		setStyle();
		
		//fills the spinner
		fillSpinner();

		//When clicked starts intent to get a photo from the phone
		Button browseButton = utils.setButtonTextDialog(R.id.browseButton, 22, bookAddDialog);
		browseButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				//Intent to pick a picture from gallery, camera etc	
				Intent chooserIntent = utils.getImageIntent();
				activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
			}

		});

		//Dismisses dialog
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, bookAddDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				bookAddDialog.dismiss();

			}});

		//Clicks to add the data
		Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, bookAddDialog);
		addButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//once book is added then add the details to list and notify the change
				addBook();


			}});
		bookAddDialog.show();	
	}


	/**
	 * Fills the spinner in the dialog
	 */
	private void fillSpinner()
	{
		//Fill spinner
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("public");
		spinnerArray.add("private");
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);

		//makes spinner triangle white
		sItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);

		//Adapts list to spinner
		sItems.setAdapter(adapter);
	}

	/**
	 * Gets the results from the activity intent. This is specifically for handling images loaded by user
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{

		switch(requestCode) { 
		case SELECT_PHOTO:
			
			//If the result code says its ok then get an image
			if(resultCode == RESULT_OK){  
			
				//Gets the image
				Uri selectedImage = imageReturnedIntent.getData();
				try {
				
					//Gets image and file then rotates the image correctly
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());

					//Sets name of image in the edittext box in the dialog
					String imageName = f.getName();
					utils.setDialogTextString(R.id.cookbookImageEditText, bookAddDialog, imageName);

					//Compress image and set to a byte array - 100 means compress for max quality
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byteArray = stream.toByteArray(); 

				} catch (FileNotFoundException e) 
				{
					e.printStackTrace();
				}
			}
		}


	}

	/**
	 * Sets up the dialog style for the adding cookbook dialog
	 */
	private void setStyle()
	{
		utils.setDialogText(R.id.errorView,bookAddDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.addBookView,bookAddDialog,22);
		utils.setDialogText(R.id.bookNameView,bookAddDialog,22);
		utils.setDialogText(R.id.bookDescView,bookAddDialog,22);
		utils.setDialogText(R.id.privacyView,bookAddDialog,22);
		utils.setDialogText(R.id.cookbookImageView, bookAddDialog, 22);
	}


}


