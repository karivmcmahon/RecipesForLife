package com.example.recipesforlife.views;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteException;
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
import android.widget.Toast;
import com.example.recipesforlife.R;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.util.Util;

/**
 * List adapter for contributers and handles the ability to remove contributors
 * @author Kari
 *
 */
class Contributer_ListAdapter extends ArrayAdapter<String>{
	private final Activity activity;
	private final ArrayList<String> users;
	private Context context;
	private String cookbookuid;
	private boolean isCreator;
	private Util utils;

	/** 
	 * Gets list data
	 * @param activity 
	 * @param users			List of users
	 * @param context
	 * @param cookbookuid	Cookbook that contains the contributers
	 */
	Contributer_ListAdapter(Activity activity , ArrayList<String> users, Context context, String cookbookuid, boolean isCreator)
	{
		super(context, R.layout.contributers_listitem, users);
		this.activity = activity;
		this.context = context;
		this.users = users;
		this.cookbookuid = cookbookuid;
		this.isCreator = isCreator;
		utils = new Util(this.context, activity);

	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@SuppressLint("ViewHolder")
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView= inflater.inflate(R.layout.contributers_listitem, null, true);

		//Set title for name of contributer
		TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
		utils.setRowText(R.id.txt, rowView, 22);

		//If dustbin clicked - delete contributer from database
		ImageButton imageView = (ImageButton) rowView.findViewById(R.id.img);
		if(isCreator == false)
		{
			//If the person is not the creator then do not display the option to delete
			imageView.setVisibility(View.INVISIBLE);
		}
		else
		{
			imageView.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						deleteContribDialog(position);
					}
					return false;
				}});
		}


		//Sets the users name in the row
		txtTitle.setText(users.get(position));
		return rowView;
	}

	/**
	 * Displays a delete contributor dialog and handles user actions
	 * 
	 * @param position 	position in list
	 */
	private void deleteContribDialog(final int position)
	{
		//Create dialog
		final Dialog dialog = utils.createDialog(activity, R.layout.general_savedialog);
		utils.setDialogText(R.id.textView, dialog, 18);

		//Set text in dialog
		TextView tv = (TextView) dialog.findViewById(R.id.textView);
		tv.setText("Would you like to delete this user ?");

		//display dialog
		dialog.show();

		//Sets on click for yes button
		//If clicked deletes contrib and dismisses dialog
		Button yesButton = utils.setButtonTextDialog(R.id.yesButton, 22, dialog);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				ApplicationModel_CookbookModel model = new ApplicationModel_CookbookModel(context);

				//get cookbook id based on unique id
				int id = model.selectCookbooksIDByUnique(cookbookuid);

				try
				{
					//updates contributer to deleted and removes from list if update is successful
					model.updateContributers( users.get(position), id, "deleted", false);
					users.remove(position);
					notifyDataSetChanged();
				}
				catch(SQLiteException e)
				{
					Toast.makeText(context, "Contributer could not be added", Toast.LENGTH_LONG).show();
				}
				dialog.dismiss(); 
			}
		});

		//If user selects no - dismiss dialog
		Button noButton = utils.setButtonTextDialog(R.id.noButton, 22, dialog);
		noButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) 
			{
				dialog.dismiss();
			}
		});

	}
}