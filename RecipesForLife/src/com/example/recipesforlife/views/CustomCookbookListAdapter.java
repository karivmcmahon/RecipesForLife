package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

public class CustomCookbookListAdapter  extends ArrayAdapter<String> {
	private final Activity activity;
	public ArrayList<String> booknames;
	public  ArrayList<String> bookids;
	public  ArrayList<byte[]> bookimages;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Context context;
	util utils;
	CustomContribListAdapter adapter2;
	boolean isCreator = false;
	cookbookModel model;
	ImageLoader2 imgload;
	EditCookbookView edit;

	/** 
	 * Gets list data
	 * @param activity
	 * @param users
	 * @param context
	 * @param cookbookuid
	 */
	public CustomCookbookListAdapter(Activity activity , ArrayList<String> booknames, Context context, ArrayList<String> bookids, ArrayList<byte[]> bookimages)
	{
		super(context, R.layout.contriblistsingle, booknames);
		this.activity = activity;
		this.context = context;
		this.booknames = booknames;
		this.bookids = bookids;
		this.bookimages = bookimages;
		utils = new util(this.context, activity);
		model = new cookbookModel(context);
		imgload = new ImageLoader2(context);

	}


	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Fill list adapter with cookbook names
		String creator = model.creatorForCookbook(bookids.get(position));
		if(creator.equals(sharedpreferences.getString(emailk, "")))
		{
			isCreator = true;
		}
		else
		{
			isCreator = false;
		}
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		//Depending on if the bookname is empty or not display an empty row or a cookbook row
		if(booknames.get(position).toString().equals(""))
		{
			rowView= inflater.inflate(R.layout.emptyrow, null, true);
		}
		else
		{
			rowView= inflater.inflate(R.layout.cookbooklistsingle, null, true);

			TextView txtTitle = (TextView) rowView.findViewById(R.id.myImageViewText);
			txtTitle.setText(booknames.get(position));
			utils.setRowText(R.id.myImageViewText, rowView, 22);		

			//Show edit cookbook dialog
			ImageView editButton = (ImageView) rowView.findViewById(R.id.editView);
			if(isCreator == false)
			{
				editButton.setVisibility(View.INVISIBLE);
				Log.v("Invisible", "invisible");
			}
			else
			{
			editButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						edit = new EditCookbookView(context, activity, CustomCookbookListAdapter.this, position);
						edit.editBook();
					}
					return false;
				}});
			}

			//Show contributors dialog
			ImageView contribButton = (ImageView) rowView.findViewById(R.id.userView);
			contribButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						ContributerView contrib = new ContributerView(context, activity, CustomCookbookListAdapter.this, position);
						contrib.manageContribs();
					}
					return false;
				}});

			//Show a list of recipes based on the cookbook selected
			ImageView bookButton = (ImageView) rowView.findViewById(R.id.myImageView);
			imgload.DisplayImage(bookButton, bookimages.get(position), Base64.encodeToString(bookimages.get(position), Base64.DEFAULT) + bookids.get(position));
			bookButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						Intent i = new Intent(activity, RecipeListViewActivity.class);
						//intents used on getting the recipes		
						i.putExtra("uniqueid", bookids.get(position));
						i.putExtra("type", "view");
						i.putExtra("bookname", booknames.get(position));
						activity.startActivity(i);
					}
					return false;
				}});
			
			//Show edit cookbook dialog
			ImageView delButton = (ImageView) rowView.findViewById(R.id.delView);
			if(isCreator == false)
			{
				delButton.setVisibility(View.INVISIBLE);
			}
		}
		return rowView;
	}
	
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{
		edit.resultRecieved( requestCode,  resultCode, imageReturnedIntent);
	}
}
