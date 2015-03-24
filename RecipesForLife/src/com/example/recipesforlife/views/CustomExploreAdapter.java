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
import android.widget.ListView;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

public class CustomExploreAdapter extends ArrayAdapter<String> {
	
	private final Activity activity;
	Context context;
	Util utils;
	ArrayList<String> categories = new ArrayList<String>();

	public CustomExploreAdapter(Context context, Activity activity, ArrayList<String> categories) {
		super(context, R.layout.explore_listitem, categories);
		this.activity = activity;
		this.context = context;
		this.categories = categories;
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
		rowView= inflater.inflate(R.layout.explore_listitem, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.category);
		txtTitle.setText(categories.get(position));
		utils.setRowText(R.id.category, rowView, 26);
		txtTitle.setTextColor(Color.parseColor("#F3216C"));
		return rowView;
	}
	
	

}
