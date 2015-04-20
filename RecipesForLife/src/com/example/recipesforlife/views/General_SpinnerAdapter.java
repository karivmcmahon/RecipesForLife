package com.example.recipesforlife.views;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.recipesforlife.R;


/**
 * Creates a view for a general spinner item
 * 
 * @author Kari
 *
 */
class General_SpinnerAdapter extends ArrayAdapter<String> {
	private Context context;
	private ArrayList<String> items;
	private Activity activity;
	
	General_SpinnerAdapter(Context context, Activity activity, int resource, ArrayList<String> items) {
		super(context,R.layout.general_spinner_item2, items);
		this.context = context;
		this.activity = activity;
		this.items = items;
	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("ViewHolder")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = activity.getLayoutInflater();
		
		//Set up style for row view and return it
		View rowView= inflater.inflate(R.layout.general_spinner_item2, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.item);
		txtTitle.setText(items.get(position));
		Typeface typeFace=Typeface.createFromAsset(context.getAssets(),"fonts/elsie.ttf");
		txtTitle.setTypeface(typeFace);
		return rowView;
	}


	/**
	 * Create a view for the drop down list
	 * 
	 * @return View that was adapted for drop down list
	 */
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
