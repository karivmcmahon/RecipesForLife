package com.example.recipesforlife.views;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.util.Util;

/**
 * Adapts a list of review to the layout
 * @author Kari
 *
 */
class Recipe_Review_Adapter extends ArrayAdapter<ReviewBean> {


	private final Activity activity;
	private Context context;
	private Util utils;
	private ArrayList<ReviewBean> rb;
	

	Recipe_Review_Adapter(Context context, Activity activity, ArrayList<ReviewBean> reviewbean) {
		super(context, R.layout.recipe_individreview, reviewbean);
		this.activity = activity;
		this.context = context;
		rb = reviewbean;
		utils = new Util(this.context, activity);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{

		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		rowView= inflater.inflate(R.layout.recipe_individreview, null, true);
		
		//sets style for each row in list
		rowView = setListStyle(rowView, position);

		return rowView;
	}
	
	/**
	 * Sets the style of row in list and adapts the data for that row
	 * 
	 * @param rowView		rowView
	 * @param position		position in list
	 * @return rowView 		updated row
	 */
	private View setListStyle(View rowView, int position)
	{
		//Sets data to text views and sets the style
		TextView txtTitle = (TextView) rowView.findViewById(R.id.comment);
		txtTitle.setText(rb.get(position).getComment());
		utils.setRowText(R.id.comment, rowView, 26);
		txtTitle.setTextColor(Color.parseColor("#F3216C"));

		TextView recipeDesc = (TextView) rowView.findViewById(R.id.by);
		recipeDesc.setText("By " + rb.get(position).getUser());
		utils.setRowText(R.id.by, rowView, 20);
		recipeDesc.setTextColor(Color.parseColor("#000000"));
		
		return rowView;
	}
}
