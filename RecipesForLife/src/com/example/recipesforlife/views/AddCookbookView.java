package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.models.CookbookModel;
import com.example.recipesforlife.util.Util;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Displays dialog to create/add a cookbook
 * @author Kari
 *
 */
public class AddCookbookView extends CookbookListActivity {

	ActionBarActivity activity;
	Context context;
	Util utils;
	Dialog bookAddDialog;
	TextView errorView;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String pass = "passwordKey"; 
	public static final String emailk = "emailKey";
	ArrayList<String> bookDetails;
	int finished = 0;
	private static final int SELECT_PHOTO = 100;
	ImageBean imgBean;
	byte[] byteArray;

	public AddCookbookView(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}

	/**
	 * Creates an add a cookbook dialog
	 */
	public void addCookbook()
	{

		bookAddDialog = utils.createDialog(activity , R.layout.cookbook_adddialog);
		errorView = (TextView) bookAddDialog.findViewById(R.id.errorView);
		bookDetails = new ArrayList<String>();
		//sets up the dialog style
		setStyle();
		//fills the spinner
		fillSpinner();

		Button browseButton = utils.setButtonTextDialog(R.id.browseButton, 22, bookAddDialog);
		browseButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				//Intent to pick a picture
				
				Intent chooserIntent = utils.getImageIntent();
				activity.startActivityForResult(chooserIntent, SELECT_PHOTO);
			}

		});

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
		utils.setDialogText(R.id.cookbookImageView, bookAddDialog, 22);
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
				activity, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = (Spinner) bookAddDialog.findViewById(R.id.privacySpinner);
		//makes spinner triangle white
		sItems.getBackground().setColorFilter(activity.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
	}

	/**
	 * Checks for any errors and if none adds the cookbook to database
	 */
	public void addBook()
	{
		// Retrieves data and inserts into database
		SharedPreferences sharedpreferences =  context.getSharedPreferences(SignUpSignInActivity.MyPREFERENCES, Context.MODE_PRIVATE);
		CookbookBean book = new CookbookBean();
		CookbookModel model = new CookbookModel(context);
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
			//if an image is not set - then set a default
			if(utils.getTextFromDialog(R.id.cookbookImageEditText, bookAddDialog).equals(""))
			{
				Bitmap bitmap = ((BitmapDrawable) activity.getResources().getDrawable(R.drawable.image_default_cookbook)).getBitmap();
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] array = stream.toByteArray();
				byteArray = array;
			}  
			book.setImage(byteArray);

			CookbookModel cbmodel = new CookbookModel(context);
			try
			{
				//insert book - once inserted add to list
				String uniqueid = cbmodel.insertBook(book, false);
				CookbookListActivity.values.add(0, utils.getTextFromDialog(R.id.bookNameEditText, bookAddDialog));
				CookbookListActivity.ids.add(0, uniqueid);
				CookbookListActivity.images.add(0, byteArray);
				bookAddDialog.dismiss();
				CookbookListActivity.adapter.notifyDataSetChanged();	
			}catch(SQLException e)
			{
				Toast.makeText(context, "Cookbook was not added", Toast.LENGTH_LONG).show();
			}

		}
	}

	/**
	 * Gets the result from the activity intent
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				//Gets the image
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					//Gets image and file then rotates the image correctly
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());
					//Sets name of image in edittext
					String imageName = f.getName();
					utils.setDialogTextString(R.id.cookbookImageEditText, bookAddDialog, imageName);
					//Compress image and set to a byte array - 100 means compress for max quality
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


