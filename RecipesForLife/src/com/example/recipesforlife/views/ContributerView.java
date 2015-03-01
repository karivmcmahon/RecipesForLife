package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.models.accountModel;
import com.example.recipesforlife.models.cookbookModel;
import com.example.recipesforlife.models.util;

/**
 * Displays the dialog showing a list of the cookbook contributers and the ability to add contributers
 * @author Kari
 *
 */
public class ContributerView {

	Context context;
	Activity activity;
	util utils;
	CustomCookbookListAdapter ccadapter;
	int position;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Dialog addContribDialog;
	TextView errorView;
	cookbookModel model;
	Dialog contribDialog;
	boolean isCreator = false;

	public ContributerView(Context context, Activity activity, CustomCookbookListAdapter adapter, int position)
	{
		this.context = context;
		this.activity = activity;
		ccadapter = adapter;
		this.position = position;
		utils = new util(context, activity);
		model = new cookbookModel(context);
	}

	/**
	 * Handles the contributer dialog
	 */
	public void manageContribs()
	{
		SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		//Fill list adapter with cookbook names
		String creator = model.creatorForCookbook(ccadapter.bookids.get(position));
		if(creator.equals(sharedpreferences.getString(emailk, "")))
		{
			isCreator = true;
		}
		//Sets up the dialog to show a list of contributers
		setContribView();
		ImageButton addButton = (ImageButton) contribDialog.findViewById(R.id.contributerAddButton);
		if(isCreator == false)
		{
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
	 * Sets up the add a contrib dialog
	 */
	public void addContribDialogViewCreate()
	{
		addContribDialog = utils.createDialog(activity, R.layout.contributeradddialog);
		//Getting data
		errorView = (TextView) addContribDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,addContribDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		utils.setDialogText(R.id.contributersView, addContribDialog, 22);
		utils.setDialogText(R.id.emailContributerView, addContribDialog, 22);
	}

	/**
	 * Adds a contributer to the database if no errors
	 */
	public void addContributer()
	{
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

			id = model.selectCookbooksIDByUnique(ccadapter.bookids.get(position));
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
	public void setContribView()
	{
		contribDialog = utils.createDialog(activity, R.layout.contributersdialog);
		utils.setDialogText(R.id.contributerTitle, contribDialog, 22);
		TextView tvTitle = (TextView) contribDialog.findViewById(R.id.contributerTitle);
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
		contribs = model.selectCookbookContributers(ccadapter.bookids.get(position), "added");
		ccadapter.adapter2 = new
				CustomContribListAdapter(activity, contribs, context, ccadapter.bookids.get(position), isCreator);
		listView2.setAdapter(ccadapter.adapter2); 
	}

}
