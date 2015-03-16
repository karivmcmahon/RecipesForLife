package com.example.recipesforlife.views;

import java.util.ArrayList;

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
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.controllers.recipeBean;
import com.example.recipesforlife.models.util;

public class CustomCookbookSearchAdapter extends ArrayAdapter<cookbookBean> {

	private final Activity activity;
	Context context;
	util utils;
	ArrayList<cookbookBean> cb;
	ImageLoader2 imgload;
	
	public CustomCookbookSearchAdapter(Context context, Activity activity, ArrayList<cookbookBean> cookbookbean) {
		super(context, R.layout.cookbookindivdsearch, cookbookbean);
		this.activity = activity;
		this.context = context;
		cb = cookbookbean;
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
		
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		if(cb.get(position).getName().equals("empty"))
		{
			rowView= inflater.inflate(R.layout.noresultsearch, null, true);
			TextView txtTitle = (TextView) rowView.findViewById(R.id.noResults);
			utils.setRowText(R.id.noResults, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));
		}
		else
		{
		rowView= inflater.inflate(R.layout.cookbookindivdsearch, null, true);
		
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
		
		ImageView cookbookImage = (ImageView) rowView.findViewById(R.id.icon);
		imgload.DisplayImage(cookbookImage, cb.get(position).getImage(), Base64.encodeToString(cb.get(position).getImage(), Base64.DEFAULT) + cb.get(position).getUniqueid());
		}
		return rowView;
	}
}
