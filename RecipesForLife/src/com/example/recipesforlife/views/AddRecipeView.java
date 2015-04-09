package com.example.recipesforlife.views;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

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
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.CookbookModel;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.Util;

/**
 * Creates and gets the data for displaying the add recipe dialog
 * @author Kari
 *
 */
public class AddRecipeView extends RecipeListViewActivity {

	ActionBarActivity activity;
	Context context;
	Util utils;
	static ArrayList<IngredientBean> ingredBeanList;
	static ArrayList<PreperationBean> prepBeanList;
	static Dialog recipeAddDialog2;
	public static Dialog recipeIngredDialog, recipeAddStepDialog, addRecipeDialog3;
	protected Dialog recipeAddDialog;
	public static Button nextButton;
	public static Button nextButton2;
	public static Button addIngredButton;
	public static Button addRecipeButton;
	static String name, desc,recipeBook, serves, prep, cooking, cusine, difficulty, tips, dietary, uniqueid, imageName, bookname;
	public static final String MyPREFERENCES = "MyPrefs";
	public static final String emailk = "emailKey"; 
	public static final String pass = "passwordKey"; 
	public static final int SELECT_PHOTO = 100;
	static byte[] array;
	protected ArrayList<String> cookbookuids = new ArrayList<String>();
	AddRecipeView_Dialog1 dialog1;
	int prepnumcount = 1;

	
	public AddRecipeView(Context context, ActionBarActivity activity, String uniqueid, String bookname)
	{
		this.context = context;
		this.activity = activity;
		this.uniqueid = uniqueid;
		this.bookname = bookname;
		utils = new Util(context, activity);
	}

	public void addRecipe()
	{
		dialog1 = new  AddRecipeView_Dialog1(activity, context, uniqueid, bookname);
		//Set up dialog style
		dialog1.setUpInitialRecipeAddDialog();
		//Once next pressed
		nextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//Get data from the dialog
				dialog1.getInitialRecipeAddDialogData();
			}
		}); 	
	}



	/**
	 * Prepare the data to send to the model where the data will be inserted 
	 */
	public void sendDataToModel()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		RecipeBean recipe = new RecipeBean();
		recipe.setName(name);
		recipe.setDesc(desc);
		recipe.setCooking(cooking);
		recipe.setServes(serves);
		recipe.setPrep(prep);
		recipe.setRecipeBook(recipeBook);
		recipe.setDifficulty(difficulty);
		recipe.setDietary(dietary);
		recipe.setTips(tips);
		recipe.setCusine(cusine);
		recipe.setAddedBy(sharedpreferences.getString(emailk, ""));
		ImageBean imgBean = new ImageBean();
		imgBean.setImage(array);
		RecipeModel model = new RecipeModel(context);
		try
		{
			String uid = model.insertRecipe(recipe, false, ingredBeanList, prepBeanList, imgBean);
			//Updates recipe list once inserted
			RecipeListViewActivity.recipenames.add(0, name);
			RecipeListViewActivity.recipeids.add(0, uid);
			RecipeListViewActivity.recipeimages.add(0, imgBean.getImage());
			RecipeListViewActivity.adapter.notifyDataSetChanged(); 

		}catch(SQLException e)
		{
			Toast.makeText(context, "Recipe was not added", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * Retrieves result from intent
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				try {
					//Get image and file and rotate correctly
					Bitmap yourSelectedImage = utils.decodeUri(selectedImage);
					File f = new File(utils.getRealPathFromURI(selectedImage));
					yourSelectedImage = utils.rotateImage(yourSelectedImage, f.getPath());
					//Set image name in edit text
					imageName = f.getName();					
					utils.setDialogTextString(R.id.recipeImagesEditText, addRecipeDialog3, imageName);
					//compress image
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					yourSelectedImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
					//set image to byte array
					byte[] byteArray = stream.toByteArray(); 
					array = byteArray;
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}


	}



}



