package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomRecipeListAdapter extends ArrayAdapter<String> {
	private final Activity activity;
	private ArrayList<String> recipenames;
	private ArrayList<String> recipeids;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Context context;
	util utils;
	
	public CustomRecipeListAdapter(Activity activity , ArrayList<String> recipenames, Context context, ArrayList<String> recipeids)
	{
		super(context, R.layout.recipelistsingle, recipenames);
		this.activity = activity;
		this.context = context;
		this.recipenames = recipenames;
		Log.v("size", " " + recipenames.size());
		this.recipeids = recipeids;
		utils = new util(this.context, activity);

	}
	
	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		Log.v("size", "size " + recipenames.size());
		if(recipenames.get(position).toString().equals(""))
		{
			rowView= inflater.inflate(R.layout.emptyrow, null, true);
		}
		else
		{

		 rowView= inflater.inflate(R.layout.recipelistsingle, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.myImageViewText);
		txtTitle.setText(recipenames.get(position));
		utils.setRowText(R.id.myImageViewText, rowView, 22);
		ImageView recipeImage = (ImageView) rowView.findViewById(R.id.myImageView);
		recipeImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{
				// TODO Auto-generated method stub
					Intent i = new Intent(activity, RecipeViewActivity.class);
					i.putExtra("uniqueidr", recipeids.get(position));
					i.putExtra("name", recipenames.get(position));
					activity.startActivity(i);
				}
				return false;
			}
			
		});
		ImageView editRecipeImage = (ImageView) rowView.findViewById(R.id.editView);
		editRecipeImage.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) 
				{
					Intent i = new Intent(activity, RecipeEditActivity.class);
					i.putExtra("uniqueidr", recipeids.get(position));
					i.putExtra("name", recipenames.get(position));
					activity.startActivity(i);
				}
				return false;
			}
			
		});
		}
		return rowView;		
	}


}
