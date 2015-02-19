package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

public class CustomContribListAdapter extends ArrayAdapter<String>{
	private final Activity activity;
	private final ArrayList<String> users;
	Context context;
	String cookbookuid;
	util utils;

	/** 
	 * Gets list data
	 * @param activity
	 * @param users
	 * @param context
	 * @param cookbookuid
	 */
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
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.contriblistsingle, null, true);
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		utils.setRowText(R.id.txt, rowView, 22);

		//If dustbin clicked - delete contributer from database
		ImageButton imageView = (ImageButton) rowView.findViewById(R.id.img);
		imageView.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					final Dialog dialog = utils.createDialog(activity, R.layout.savedialog);
					utils.setDialogText(R.id.textView, dialog, 18);
					TextView tv = (TextView) dialog.findViewById(R.id.textView);
					tv.setText("Would you like to delete this user ?");
					// Show dialog
					dialog.show();
					Button button = utils.setButtonTextDialog(R.id.yesButton, 22, dialog);
					button.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							cookbookModel model = new cookbookModel(context);
							int id = model.selectCookbooksIDByUnique(cookbookuid);
							model.deleteContributers(id, users.get(position));
							users.remove(position);
							notifyDataSetChanged();
							dialog.dismiss();
						}
					});
					Button button2 = utils.setButtonTextDialog(R.id.noButton, 22, dialog);
					button2.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							dialog.dismiss();
						}
					});
				}
				return false;
			}

		});
		txtTitle.setText(users.get(position));
		return rowView;
	}
}