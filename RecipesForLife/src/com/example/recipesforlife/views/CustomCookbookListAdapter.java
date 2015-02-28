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
					manageDialog(position);
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
	
	public void manageDialog(final int position)
	{
		Dialog contribDialog = utils.createDialog(activity, R.layout.contributersdialog);
		utils.setDialogText(R.id.contributerTitle, contribDialog, 22);
		ImageButton addButton = (ImageButton) contribDialog.findViewById(R.id.contributerAddButton);
		final cookbookModel model = new cookbookModel(context);
		ArrayList<String> contribs = new ArrayList<String>();
		//Show list of contributers
		ListView listView2 = (ListView) contribDialog.findViewById(R.id.lists);
	    contribs = model.selectCookbookContributers(bookids.get(position), "added");
		 adapter2 = new
				CustomContribListAdapter(activity, contribs, context, bookids.get(position));
		listView2.setAdapter(adapter2); 

		//Display dialog to add a contributer
		addButton.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
				{
					final Dialog addContribDialog = utils.createDialog(activity, R.layout.contributeradddialog);
					//Getting data
					final TextView errorView = (TextView) addContribDialog.findViewById(R.id.errorView);
					utils.setDialogText(R.id.errorView,addContribDialog,16);
					errorView.setTextColor(Color.parseColor("#F70521"));
					utils.setDialogText(R.id.contributersView, addContribDialog, 22);
					utils.setDialogText(R.id.emailContributerView, addContribDialog, 22);
					//When user has choosen to add someone
					Button addContribButton = utils.setButtonTextDialog(R.id.addContribButton, 22, addContribDialog);
					addContribButton.setOnTouchListener(new OnTouchListener()
					{

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								int id = 0;
								accountModel am = new accountModel(context);
								boolean exists = am.checkEmail( utils.getTextFromDialog(R.id.emailEditText, addContribDialog));
								//Check for any errors
								if (exists == false)
								{
									errorView.setText("The user entered does not exist");
								}
								else if( utils.getTextFromDialog(R.id.emailEditText, addContribDialog).equals(""))
								{
									errorView.setText("Please enter a user");
								}
								else
								{
									
									id = model.selectCookbooksIDByUnique(bookids.get(position));
									//If it exists either update or insert contributer
									boolean contribExists = model.selectContributer(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
									if(contribExists == true)
									{
										model.updateContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id, "added");
									}
									else
									{
										model.insertContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
									}
									//Updates contributer list
									ArrayList<String > contribslist = model.selectCookbookContributers(bookids.get(position), "added");
									adapter2.clear();
									adapter2.addAll(contribslist);
									adapter2.notifyDataSetChanged();
									addContribDialog.dismiss();
								}
							}
							return false;

						}

					});
					addContribDialog.show();



				}
				return false;
			}});
		contribDialog.show();
	}

}
