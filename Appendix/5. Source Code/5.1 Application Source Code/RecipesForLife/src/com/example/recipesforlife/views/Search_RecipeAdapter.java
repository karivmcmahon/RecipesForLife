package com.example.recipesforlife.views;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

/**
 * This class handles the layout adapting of recipes for search results
 * @author Kari
 *
 */
class Search_RecipeAdapter extends ArrayAdapter<RecipeBean> {

	private final Activity activity;
	private Context context;
	private Util utils;
	private ArrayList<RecipeBean> rb;
	private ImageLoader2 imgload;
	
	Search_RecipeAdapter(Context context, Activity activity, ArrayList<RecipeBean> recipebean) {
		super(context, R.layout.search_recipeview, recipebean);
		this.activity = activity;
		this.context = context;
		rb = recipebean;
		imgload = new ImageLoader2(this.context);
		utils = new Util(this.context, activity);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{

		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
	
		//If no results then display the no results layout
		if(rb.get(position).getName().equals("empty"))
		{
			rowView= inflater.inflate(R.layout.search_noresultsview, null, true);
			TextView txtTitle = (TextView) rowView.findViewById(R.id.noResults);
			utils.setRowText(R.id.noResults, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));
		}
		else
		{
			//display the recipe layout
			rowView= inflater.inflate(R.layout.search_recipeview, null, true);
			
			//set the styles for the layout with the data from the search
			rowView = setListStyle(rowView, position);
			

			//Calculate the total cooking time - prep + cooking to display in row
			SimpleDateFormat  format = new SimpleDateFormat("HH:mm");  
			int hours = 0;
			int minutes = 0;
			try {
				Date cookingdate = format.parse((rb.get(position).getCooking()));
				Date prepdate = format.parse((rb.get(position).getPrep()));
				hours = cookingdate.getHours() + prepdate.getHours();
				minutes = cookingdate.getMinutes() + prepdate.getMinutes();
				hours +=  minutes /60;
				minutes %= 60;

			} catch (ParseException e) {
				e.printStackTrace();
			}

			//Display time
			TextView time = (TextView) rowView.findViewById(R.id.cookTime);
			time.setText( hours + ":" + minutes );
			utils.setRowText(R.id.cookTime, rowView, 18);
			time.setTextColor(Color.parseColor("#000000")); 

			//Display recipe image
			ImageView recipeImage = (ImageView) rowView.findViewById(R.id.icon);
			imgload.DisplayImage(recipeImage, rb.get(position).getImage(), Base64.encodeToString(rb.get(position).getImage(), Base64.DEFAULT) + rb.get(position).getUniqueid());
		}
		return rowView;


	}
	
	/**
	 * Set the layout with correct styles and data thats being adapted
	 * 
	 * @param rowView	 View of row to be modified
	 * @param position   Position of list
	 * @return rowView   Updated rowView
	 */
	private View setListStyle(View rowView, int position)
	{
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
		return rowView;
	}



}
