package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomNavArrayAdapter extends ArrayAdapter<String> {
	
	private final Activity activity;
	private ArrayList<String> names;
	private ArrayList<Integer> ids;
	private Context context;
	util utils;
	public CustomNavArrayAdapter(Context context, int resource,
			ArrayList<String> objects, Activity activity, ArrayList<Integer> ids) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.context = context;
		names = objects;
		utils = new util(context, activity);
		this.ids =ids;
		
	}
	
	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = inflater.inflate(R.layout.drawer_list_item, null, true);
		TextView title = (TextView) rowView.findViewById(R.id.textView);
		title.setText(names.get(position));
		ImageView imageView =(ImageView) rowView.findViewById(R.id.myImageView);
		imageView.setImageResource(ids.get(position));
		utils.setRowText(R.id.textView, rowView, 28);
		return rowView;
	}
	

	
	
	

}
