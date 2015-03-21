package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.controllers.UserBean;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

public class CustomUserSearchAdapter extends ArrayAdapter<UserBean> {

	private final Activity activity;
	Context context;
	Util utils;
	ArrayList<UserBean> ub;
	ImageLoader2 imgload;

	public CustomUserSearchAdapter(Context context, Activity activity, ArrayList<UserBean> userbean) {
		super(context, R.layout.search_userview, userbean);
		this.activity = activity;
		this.context = context;
		ub = userbean;
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
		if(ub.get(position).getName().equals("empty"))
		{
			rowView= inflater.inflate(R.layout.search_noresultsview, null, true);
			TextView txtTitle = (TextView) rowView.findViewById(R.id.noResults);
			utils.setRowText(R.id.noResults, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));
		}
		else
		{
			rowView= inflater.inflate(R.layout.search_userview, null, true);

			TextView txtTitle = (TextView) rowView.findViewById(R.id.userinfo);
			txtTitle.setText(ub.get(position).getName() + " - " + ub.get(position).getCountry());
			utils.setRowText(R.id.userinfo, rowView, 26);
			txtTitle.setTextColor(Color.parseColor("#F3216C"));

			TextView bio = (TextView) rowView.findViewById(R.id.bio);
			bio.setText(ub.get(position).getBio());
			utils.setRowText(R.id.bio, rowView, 20);
			bio.setTextColor(Color.parseColor("#000000"));

			TextView account = (TextView) rowView.findViewById(R.id.accountid);
			account.setText("Account id:  " + ub.get(position).getEmail());
			utils.setRowText(R.id.accountid, rowView, 18);
			account.setTextColor(Color.parseColor("#000000"));
		}
		return rowView;
	}
}
