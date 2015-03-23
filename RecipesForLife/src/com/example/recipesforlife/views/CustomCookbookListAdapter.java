package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
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
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.CookbookBean;
import com.example.recipesforlife.models.AccountModel;
import com.example.recipesforlife.models.CookbookModel;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

/**
 * List adapter for displaying the cookbooks
 * @author Kari
 *
 */
public class CustomCookbookListAdapter  extends ArrayAdapter<String> {
	private final Activity activity;
	public ArrayList<String> booknames, bookids;
	public  ArrayList<byte[]> bookimages;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Context context;
	Util utils;
	CustomContribListAdapter adapter2;
	boolean isCreator = false;
	CookbookModel model;
	ImageLoader2 imgload;
	EditCookbookView edit;

	/** 
	 * Gets list data
	 * @param activity
	 * @param users
	 * @param context
	 * @param cookbookuid
	 */
	public CustomCookbookListAdapter(Activity activity , ArrayList<String> booknames, Context context, ArrayList<String> bookids, ArrayList<byte[]> bookimages)
	{
		super(context, R.layout.contributers_listitem, booknames);
		this.activity = activity;
		this.context = context;
		this.booknames = booknames;
		this.bookids = bookids;
		this.bookimages = bookimages;
		utils = new Util(this.context, activity);
		model = new CookbookModel(context);
		imgload = new ImageLoader2(context);

	}


	@Override
	/**
	 * Adapts list data 
	 */
	public View getView(final int position, View view, ViewGroup parent) 
	{

		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Gets cookbook creator and checks if their creator - determines what images are shown
		String creator = model.creatorForCookbook(bookids.get(position));
		if(creator.equals(sharedpreferences.getString(emailk, "")))
		{
			isCreator = true;
		}
		else
		{
			isCreator = false;
		}
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		//Depending on if the bookname is empty or not display an empty row or a cookbook row
		if(booknames.get(position).toString().equals(""))
		{
			rowView= inflater.inflate(R.layout.general_listview_emptyrow, null, true);
		}
		else
		{
			rowView= inflater.inflate(R.layout.cookbook_display, null, true);

			//Sets up text style
			TextView txtTitle = (TextView) rowView.findViewById(R.id.myImageViewText);
			txtTitle.setText(booknames.get(position));
			utils.setRowText(R.id.myImageViewText, rowView, 22);		

			//Show edit cookbook dialog
			ImageView editButton = (ImageView) rowView.findViewById(R.id.editView);
			if(isCreator == false)
			{
				//if not creator then set edit button to invisible
				editButton.setVisibility(View.INVISIBLE);
			}
			else
			{
				editButton.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View arg0, MotionEvent arg1) {
						// TODO Auto-generated method stub
						if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
						{
							//Show edit dialog
							edit = new EditCookbookView(context, activity, CustomCookbookListAdapter.this, position);
							edit.editBook();
						}
						return false;
					}});
			}

			//Show contributors dialog
			ImageView contribButton = (ImageView) rowView.findViewById(R.id.userView);
			contribButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						//Show contrib dialog
						ContributerView contrib = new ContributerView(context, activity, CustomCookbookListAdapter.this, position);
						contrib.manageContribs();
					}
					return false;
				}});

			//Loads image for the listview
			ImageView bookButton = (ImageView) rowView.findViewById(R.id.myImageView);
			imgload.DisplayImage(bookButton, bookimages.get(position), Base64.encodeToString(bookimages.get(position), Base64.DEFAULT) + bookids.get(position));
			
			//if cookbook clicked move on to recipe view to show a list of recipes 
			bookButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					// TODO Auto-generated method stub
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						Intent i = new Intent(activity, RecipeListViewActivity.class);
						//intents used on getting the recipes		
						i.putExtra("uniqueid", bookids.get(position));
						i.putExtra("type", "view");
						i.putExtra("bookname", booknames.get(position));
						imgload.clearCache();
						notifyDataSetChanged();
						activity.startActivity(i);
					}
					return false;
				}});

			//Show delete cookbook dialog
			ImageView delButton = (ImageView) rowView.findViewById(R.id.delView);
			if(isCreator == false)
			{
				delButton.setVisibility(View.INVISIBLE);
			}
			delButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
					{
						final Dialog dialog = utils.createDialog(activity, R.layout.general_savedialog);
						utils.setDialogText(R.id.textView, dialog, 18);
						TextView tv = (TextView) dialog.findViewById(R.id.textView);
						//confirms with user
						tv.setText("Would you like to delete this cookbook ?");
						// Show dialog
						dialog.show();

						//Deletes cookbook and dismiss dialog
						Button yesButton = utils.setButtonTextDialog(R.id.yesButton, 22, dialog);
						yesButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								CookbookModel model = new CookbookModel(context);
								ArrayList<CookbookBean> cbBean = model.selectCookbook(bookids.get(position));
								cbBean.get(0).setProgress("deleted");
								try
								{
									//updates book to deleted and if successful removes from list
									model.updateBook( cbBean.get(0), false);
									booknames.remove(position);
									bookids.remove(position);
									bookimages.remove(position);
									notifyDataSetChanged();
								}
								catch(SQLiteException e)
								{
									Toast.makeText(context, "Cookbook was not deleted", Toast.LENGTH_LONG).show();
								}
								dialog.dismiss();
							}
						});

						//If user selects no - dismiss dialog
						Button noButton = utils.setButtonTextDialog(R.id.noButton, 22, dialog);
						noButton.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								dialog.dismiss();
							}
						});
					}
					return false;
				}});
		}
		return rowView;
	}

	/**
	 * Handles result recievedd for edit dialog
	 * @param requestCode
	 * @param resultCode
	 * @param imageReturnedIntent
	 */
	public void resultRecieved(int requestCode, int resultCode, Intent imageReturnedIntent)
	{
		edit.resultRecieved( requestCode,  resultCode, imageReturnedIntent);
	}
}
