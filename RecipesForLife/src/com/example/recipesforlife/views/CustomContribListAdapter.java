package com.example.recipesforlife.views;

import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class CustomContribListAdapter extends ArrayAdapter<String>{
	private final Activity activity;
	private final ArrayList<String> users;
	Context context;
	String cookbookuid;

	util utils;
	
	public CustomContribListAdapter(Activity activity , ArrayList<String> users, Context context, String cookbookuid)
	{
		super(context, R.layout.contriblistsingle, users);
		this.activity = activity;
		this.context = context;
		this.users = users;
		this.cookbookuid = cookbookuid;
		utils = new util(this.context, activity);
		
	}
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.contriblistsingle, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		utils.setRowText(R.id.txt, rowView, 22);
		ImageButton imageView = (ImageButton) rowView.findViewById(R.id.img);
		imageView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					Log.v("Usaa clicked ", "Usaaa clicked " + users.get(position));
					cookbookModel model = new cookbookModel(context);
					int id = model.selectCookbooksIDByUnique(cookbookuid);
					Log.v("Usaa clicked ", "Usaaa clicked " + id);
					model.deleteContributers(id, users.get(position));
					users.remove(position);
					notifyDataSetChanged();
				}
				return false;
			}
			
		});
		txtTitle.setText(users.get(position));
		return rowView;
	}
}