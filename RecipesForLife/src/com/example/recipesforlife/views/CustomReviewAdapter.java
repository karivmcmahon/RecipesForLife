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
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.controllers.reviewBean;
import com.example.recipesforlife.models.util;

public class CustomReviewAdapter extends ArrayAdapter<reviewBean> {


	private final Activity activity;
	Context context;
	util utils;
	ArrayList<reviewBean> rb;
	ImageLoader2 imgload;
	
	public CustomReviewAdapter(Context context, Activity activity, ArrayList<reviewBean> reviewbean) {
		super(context, R.layout.individreview, reviewbean);
		this.activity = activity;
		this.context = context;
		rb = reviewbean;
		imgload = new ImageLoader2(this.context);
		// TODO Auto-generated constructor stub
		utils = new util(this.context, activity);
		Log.v("RBS ", "RBS " + rb.size());
	}
	
	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		rowView= inflater.inflate(R.layout.individreview, null, true);
		

		TextView txtTitle = (TextView) rowView.findViewById(R.id.comment);
		txtTitle.setText(rb.get(position).getComment());
		utils.setRowText(R.id.comment, rowView, 26);
		txtTitle.setTextColor(Color.parseColor("#F3216C"));

		TextView recipeDesc = (TextView) rowView.findViewById(R.id.by);
		recipeDesc.setText("By " + rb.get(position).getUser());
		utils.setRowText(R.id.by, rowView, 20);
		recipeDesc.setTextColor(Color.parseColor("#000000"));
		Log.v("row","row");
		return rowView;
	}
}
