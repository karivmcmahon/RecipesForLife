package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.cookbookBean;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

public class CustomCookbookListAdapter  extends ArrayAdapter<String> {
	private final Activity activity;
	public ArrayList<String> booknames;
	public static ArrayList<String> bookids;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Context context;
	util utils;
	 CustomContribListAdapter adapter2;

	/** 
	 * Gets list data
	 * @param activity
	 * @param users
	 * @param context
	 * @param cookbookuid
	 */
	public CustomCookbookListAdapter(Activity activity , ArrayList<String> booknames, Context context, ArrayList<String> bookids)
	{
		super(context, R.layout.contriblistsingle, booknames);
		this.activity = activity;
		this.context = context;
		this.booknames = booknames;
		this.bookids = bookids;
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
		if(booknames.get(position).toString().equals(""))
		{
			rowView= inflater.inflate(R.layout.emptyrow, null, true);
		}
		else
		{
			rowView= inflater.inflate(R.layout.cookbooklistsingle, null, true);
		
		TextView txtTitle = (TextView) rowView.findViewById(R.id.myImageViewText);
		txtTitle.setText(booknames.get(position));
		utils.setRowText(R.id.myImageViewText, rowView, 22);		
		
		ImageView editButton = (ImageView) rowView.findViewById(R.id.editView);
		editButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					EditCookbookView edit = new EditCookbookView(context, activity, CustomCookbookListAdapter.this, position);
					edit.editBook();
				}
				return false;
			}});
		
		
		
		ImageView contribButton = (ImageView) rowView.findViewById(R.id.userView);
		contribButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					ContributerView contrib = new ContributerView(context, activity, CustomCookbookListAdapter.this, position);
					contrib.manageContribs();
				}
				return false;
			}});
		
		ImageView bookButton = (ImageView) rowView.findViewById(R.id.myImageView);
		bookButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					Intent i = new Intent(activity, RecipeListViewActivity.class);
					

					
					i.putExtra("uniqueid", bookids.get(position));
					i.putExtra("type", "view");
					i.putExtra("bookname", booknames.get(position));
					activity.startActivity(i);
				}
				return false;
			}});
		}
		return rowView;
	}
	
	

}
