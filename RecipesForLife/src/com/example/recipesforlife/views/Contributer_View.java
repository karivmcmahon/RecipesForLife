package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.ApplicationModel_AccountModel;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.util.Util;

/**
 * Displays the dialog showing a list of the cookbook contributers and the ability to add contributers
 * @author Kari
 *
 */
public class Contributer_View {

	Context context;
	Activity activity;
	Util utils;
	Cookbook_ListAdapter ccadapter;
	int position;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Dialog addContribDialog, contribDialog;
	TextView errorView;
	ApplicationModel_CookbookModel model;
	boolean isCreator = false;

	public Contributer_View(Context context, Activity activity, Cookbook_ListAdapter adapter, int position)
	{
		this.context = context;
		this.activity = activity;
		ccadapter = adapter;
		this.position = position;
		utils = new Util(context, activity);
		model = new ApplicationModel_CookbookModel(context);
	}

	/**
	 * Handles the contributer dialog style depending whether they are a cookbook owner or just a contributor
	 */
	public void manageContribs()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

		//Retrieves cookbook creator and checks if the person viewing the activity is the same as the creator
		String creator = model.creatorForCookbook(ccadapter.bookids.get(position));
		if(creator.equals(sharedpreferences.getString(emailk, "")))
		{
			isCreator = true;
		}

		//Sets up the dialog to show a list of contributers
		setContribView();

		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, contribDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				contribDialog.dismiss();

			}});

		ImageButton addButton = (ImageButton) contribDialog.findViewById(R.id.contributerAddButton);
		if(isCreator == false)
		{
			//If the user is not creator then do not enable them to add a contributor
			addButton.setVisibility(View.INVISIBLE);
		}
		else
		{

			//Display dialog to add a contributer if selected to add a contributer
			addButton.setOnTouchListener(new OnTouchListener(){

				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (arg1.getAction() == MotionEvent.ACTION_DOWN) 
					{
						//Set up the add contributer dialog
						addContribDialogViewCreate();

						Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, addContribDialog);
						closeButton.setOnClickListener(new OnClickListener(){

							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								addContribDialog.dismiss();

							}});

						//If they press add
						Button addContribButton = utils.setButtonTextDialog(R.id.addContribButton, 22, addContribDialog);
						addContribButton.setOnTouchListener(new OnTouchListener()
						{

							@Override
							public boolean onTouch(View v, MotionEvent event) {
								if (event.getAction() == MotionEvent.ACTION_DOWN) 
								{
									//Add a contributer to database
									addContributer();
								}
								return false;

							}

						});

						addContribDialog.show();
					}
					return false;
				}});
		}
		contribDialog.show();
	}

	/**
	 * Sets up the add a contrib dialog style
	 */
	private void addContribDialogViewCreate()
	{
		//Set styles
		addContribDialog = utils.createDialog(activity, R.layout.contributers_adddialog);
		errorView = (TextView) addContribDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,addContribDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.contributersView, addContribDialog, 22);
		utils.setDialogText(R.id.emailContributerView, addContribDialog, 22);
	}

	/**
	 * Adds a contributer to the database if no errors
	 */
	private void addContributer()
	{
		int id = 0;
		ApplicationModel_AccountModel am = new ApplicationModel_AccountModel(context);

		//checks if the email entered exists
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

			id = model.selectCookbooksIDByUnique(ccadapter.bookids.get(position));

			//If it exists either update or insert contributer - may be able to update as they might not have been deleted yet
			boolean contribExists = model.selectContributer(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id);
			if(contribExists == true)
			{
				model.updateContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id, "added", false);
			}
			else
			{
				model.insertContributers(utils.getTextFromDialog(R.id.emailEditText, addContribDialog), id, false);
			}

			//Updates contributer list
			ArrayList<String > contribslist = model.selectCookbookContributers(ccadapter.bookids.get(position), "added");
			ccadapter.adapter2.clear();
			ccadapter.adapter2.addAll(contribslist);
			ccadapter.adapter2.notifyDataSetChanged();
			addContribDialog.dismiss();
		}
	}

	/**
	 * Set up the initial contributer view dialog
	 */
	private void setContribView()
	{
		contribDialog = utils.createDialog(activity, R.layout.contributers_viewdialog);
		utils.setDialogText(R.id.contributerTitle, contribDialog, 22);
		TextView tvTitle = (TextView) contribDialog.findViewById(R.id.contributerTitle);

		//Depending on whether the user is the creator or not show a different title
		if(isCreator == false)
		{
			tvTitle.setText("View Contributors");
		}
		else
		{
			tvTitle.setText("Manage Contributors");
		}
		ArrayList<String> contribs = new ArrayList<String>();

		//Show list of contributers
		ListView listView2 = (ListView) contribDialog.findViewById(R.id.lists);

		//select the contributers and dispaly in a listview
		contribs = model.selectCookbookContributers(ccadapter.bookids.get(position), "added");
		ccadapter.adapter2 = new
				Contributer_ListAdapter(activity, contribs, context, ccadapter.bookids.get(position), isCreator);
		listView2.setAdapter(ccadapter.adapter2); 
	}

}
