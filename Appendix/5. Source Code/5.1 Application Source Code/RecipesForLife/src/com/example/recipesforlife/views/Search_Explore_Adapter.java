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
import com.example.recipesforlife.util.Util;

/**
 * Adapts the explore category list
 * @author Kari
 *
 */
class Search_Explore_Adapter extends ArrayAdapter<String> {
	
	private final Activity activity;
	private Context context;
	private Util utils;
	private ArrayList<String> categories = new ArrayList<String>();

	Search_Explore_Adapter(Context context, Activity activity, ArrayList<String> categories) {
		super(context, R.layout.explore_listitem, categories);
		this.activity = activity;
		this.context = context;
		this.categories = categories;
		utils = new Util(this.context, activity);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint({ "InflateParams", "ViewHolder" })
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{
		//Set up row view with data
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
