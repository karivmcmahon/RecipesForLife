package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomRecipeSearchAdapter extends ArrayAdapter<recipeBean> {

	private final Activity activity;
	Context context;
	util utils;
	ArrayList<recipeBean> rb;
	ImageLoader2 imgload;
	public CustomRecipeSearchAdapter(Context context, Activity activity, ArrayList<recipeBean> recipebean) {
		super(context, R.layout.recipeindividsearch, recipebean);
		this.activity = activity;
		this.context = context;
		rb = recipebean;
		imgload = new ImageLoader2(this.context);
		// TODO Auto-generated constructor stub
		utils = new util(this.context, activity);
	}
	
	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		Log.v("pos" , "Pos " + position);
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		rowView= inflater.inflate(R.layout.recipeindividsearch, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.recipeName);
		txtTitle.setText(rb.get(position).getName());
		utils.setRowText(R.id.recipeName, rowView, 26);
		txtTitle.setTextColor(Color.parseColor("#F3216C"));
		
		TextView recipeDesc = (TextView) rowView.findViewById(R.id.recipeDescription);
		recipeDesc.setText(rb.get(position).getDesc());
		utils.setRowText(R.id.recipeDescription, rowView, 20);
		recipeDesc.setTextColor(Color.parseColor("#000000"));
		
		TextView recipeBook = (TextView) rowView.findViewById(R.id.cookbook);
		recipeBook.setText("From " + rb.get(position).getRecipeBook());
		utils.setRowText(R.id.cookbook, rowView, 18);
		recipeBook.setTextColor(Color.parseColor("#000000"));
		
		TextView diffTitle = (TextView) rowView.findViewById(R.id.difficultyTitle);
		diffTitle.setText("Difficulty: ");
		utils.setRowText(R.id.difficultyTitle, rowView, 20);
		diffTitle.setTextColor(Color.parseColor("#F3216C"));
		
		TextView diff = (TextView) rowView.findViewById(R.id.difficulty);
		diff.setText(rb.get(position).getDifficulty());
		utils.setRowText(R.id.difficulty, rowView, 20);
		diff.setTextColor(Color.parseColor("#000000"));
		
		TextView cusineTitle = (TextView) rowView.findViewById(R.id.cusineTitle);
		cusineTitle.setText("Cusine: ");
		utils.setRowText(R.id.cusineTitle, rowView, 20);
		cusineTitle.setTextColor(Color.parseColor("#F3216C"));
		
		TextView cusine = (TextView) rowView.findViewById(R.id.cusine);
		cusine.setText(rb.get(position).getCusine());
		utils.setRowText(R.id.cusine, rowView, 20);
		cusine.setTextColor(Color.parseColor("#000000")); 
		
		TextView timeTitle = (TextView) rowView.findViewById(R.id.cookTimeTitle);
		timeTitle.setText("Time: ");
		utils.setRowText(R.id.cookTimeTitle, rowView, 20);
		timeTitle.setTextColor(Color.parseColor("#F3216C"));
		
		/**TextView time = (TextView) rowView.findViewById(R.id.cookTime);
		time.setText(rb.get(position).getPrep() + rb.get(position).getCooking());
		utils.setRowText(R.id.cookTime, rowView, 18);
		time.setTextColor(Color.parseColor("#000000"));**/ 
		
		ImageView recipeImage = (ImageView) rowView.findViewById(R.id.icon);
		imgload.DisplayImage(recipeImage, rb.get(position).getImage(), Base64.encodeToString(rb.get(position).getImage(), Base64.DEFAULT) + rb.get(position).getUniqueid());
		
		return rowView;
		
	}

}
