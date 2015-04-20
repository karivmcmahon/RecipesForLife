package com.example.recipesforlife.views;

import java.util.ArrayList;

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
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

/**
 * The listview adapter to display cookbooks that have been searched for
 * @author Kari
 *
 */
class Search_CookbookAdapter extends ArrayAdapter<CookbookBean> {

	private final Activity activity;
	private Context context;
	private Util utils;
	private ArrayList<CookbookBean> cb;
	private ImageLoader2 imgload;

	Search_CookbookAdapter(Context context, Activity activity, ArrayList<CookbookBean> cookbookbean) {
		super(context, R.layout.search_cookbookview, cookbookbean);
		this.activity = activity;
		this.context = context;
		cb = cookbookbean;
		imgload = new ImageLoader2(this.context);
		utils = new Util(this.context, activity);
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{
		//Sets up rowview with style and data
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
	
		//If no results
		if(cb.get(position).getName().equals("empty"))
		{
			//Display no results layout
			rowView= inflater.inflate(R.layout.search_noresultsview, null, true);
		
			//Set text style
			TextView txtTitle = (TextView) rowView.findViewById(R.id.noResults);
			utils.setRowText(R.id.noResults, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));
		}
		else
		{
			rowView= inflater.inflate(R.layout.search_cookbookview, null, true);

			//Set results from database to listview and set there text style
			TextView txtTitle = (TextView) rowView.findViewById(R.id.cookbookName);
			txtTitle.setText(cb.get(position).getName());
			utils.setRowText(R.id.cookbookName, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));

			TextView cookbookDesc = (TextView) rowView.findViewById(R.id.cookbookDescription);
			cookbookDesc.setText(cb.get(position).getDescription());
			utils.setRowText(R.id.cookbookDescription, rowView, 20);
			cookbookDesc.setTextColor(Color.parseColor("#000000"));

			TextView cookbookCreator = (TextView) rowView.findViewById(R.id.cookbookCreator);
			cookbookCreator.setText("By " + cb.get(position).getCreator());
			utils.setRowText(R.id.cookbookCreator, rowView, 20);
			cookbookCreator.setTextColor(Color.parseColor("#000000"));

			// Display image using image loader
			ImageView cookbookImage = (ImageView) rowView.findViewById(R.id.icon);
			imgload.DisplayImage(cookbookImage, cb.get(position).getImage(), Base64.encodeToString(cb.get(position).getImage(), Base64.DEFAULT) + cb.get(position).getUniqueid());
		}
		return rowView;
	}
}
