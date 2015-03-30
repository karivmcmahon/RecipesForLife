package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import com.example.recipesforlife.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {
	Context context;
	ArrayList<String> items;
	Activity activity;
	 public CustomSpinnerAdapter(Context context, Activity activity, int resource, ArrayList<String> items) {
		super(context,R.layout.general_spinner_item2, items);
		this.context = context;
		this.activity = activity;
		this.items = items;
		// TODO Auto-generated constructor stub
	}

	 @Override
		/**
		 * Adapts list data 
		 */
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.general_spinner_item2, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		txtTitle.setText(items.get(position));
		Typeface typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/elsie.ttf");
		txtTitle.setTypeface(typeFace);
         return rowView;
 }


 public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
	 LayoutInflater inflater = activity.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.general_spinner_item2, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		txtTitle.setText(items.get(position));
		rowView.setBackgroundColor(Color.parseColor("#F3216C"));
		Typeface typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/elsie.ttf");
		txtTitle.setTypeface(typeFace);
      return rowView;
 }

}