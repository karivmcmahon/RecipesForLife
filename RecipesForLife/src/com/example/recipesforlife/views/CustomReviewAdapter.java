package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

public class CustomReviewAdapter extends ArrayAdapter<ReviewBean> {


	private final Activity activity;
	Context context;
	Util utils;
	ArrayList<ReviewBean> rb;
	ImageLoader2 imgload;

	public CustomReviewAdapter(Context context, Activity activity, ArrayList<ReviewBean> reviewbean) {
		super(context, R.layout.recipe_individreview, reviewbean);
		this.activity = activity;
		this.context = context;
		rb = reviewbean;
		imgload = new ImageLoader2(this.context);
		// TODO Auto-generated constructor stub
		utils = new Util(this.context, activity);
	}

	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{

		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		rowView= inflater.inflate(R.layout.recipe_individreview, null, true);


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
