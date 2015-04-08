package com.example.recipesforlife.views;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.Util;


public class EditRecipePreperation extends RecipeEditActivity{

	ActionBarActivity activity;
	Context context;
	Util utils;
	LinearLayout prepDialogLinearLayout;
	LinearLayout.LayoutParams params;


	public EditRecipePreperation(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}

	public void getPreperation()
	{
		setUp();
		createList();

		//Create a button and when on click change prep list to what was set in the dialog box
		Button okButton = createButton();
		final TextView errorView = createErrorView();
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				modifiedPrepList = new ArrayList<PreperationBean>();
				listCheck(errorView);
			}});
		prepDialog.show();
	}

	public void setUp()
	{
		prepDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog4);
		utils.setDialogText(R.id.recipeEditView, prepDialog, 22);
		prepNumEditIds = new ArrayList<Integer>();
		prepEditIds = new ArrayList<Integer>();

		prepDialogLinearLayout = (LinearLayout)prepDialog.findViewById(R.id.editdialog);
		params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,      
				LayoutParams.WRAP_CONTENT
				);
	}

	public void createList()
	{
		//Set the text view and edit text data for a linear layout
		for(int i = 0; i < prepList.size(); i++)
		{
			if(prepList.get(i).getProgress().equals("added"))
			{
				final int point = i;
				LinearLayout linearLayoutInDialog = new LinearLayout(activity);
				linearLayoutInDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

				int prepviewid = findId();
				final TextView prepView = createViewsForDialog("Preperation",prepviewid);		
				int prepNumViewId = findId();
				final TextView prepNumView =  createViewsForDialog("Preperation Num",prepNumViewId);

				int prepEditId = findId();
				prepEditIds.add(i, prepEditId);
				final EditText prepEdit = createEditTextForDialog(prepEditId);

				int ids = findId();
				prepNumEditIds.add(i, ids);
				final EditText prepNumEdit = createEditTextForDialog(ids);
				prepNumEdit.setLayoutParams(params);

				ImageButton img = createImageButton();		
				addViews(linearLayoutInDialog, prepView, prepNumView, prepNumEdit, prepEdit, img);

				//Create styles
				utils.setDialogText(prepviewid, prepDialog, 22);
				utils.setDialogText(prepNumViewId, prepDialog, 22);
				//SetText
				prepEdit.setText(prepList.get(i).getPreperation());
				prepEdit.setWidth(400);
				prepNumEdit.setText(Integer.toString(prepList.get(i).getPrepNum()));


				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						setVisibility(point, prepView, prepNumView, prepNumEdit, prepEdit);
					}});

			}
			else
			{
				prepEditIds.add(i, 0);
				prepNumEditIds.add(i,0);
			}


		}
	}

	public TextView createViewsForDialog(String text, int id)
	{
		params.setMargins(5,5,5,5);
		TextView tv = new TextView(activity);
		tv.setText(text);
		tv.setId(id);
		tv.setLayoutParams(params);
		return tv;
	}

	public EditText createEditTextForDialog(int id)
	{
		final EditText edittext = new EditText(activity);
		edittext.setId(id);
		edittext.setBackgroundColor(Color.parseColor("#FFFFFF"));
		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(200, 300);
		lparams.setMargins(5,5,5,5);
		edittext.setLayoutParams(lparams);
		return edittext;
	}
	public ImageButton createImageButton()
	{
		ImageButton img = new ImageButton(activity);
		int imgid = findId();
		img.setId(imgid);
		img.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_delete));
		LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
		img.setLayoutParams(lparams2);
		return img;
	}

	public void addViews(LinearLayout linearLayoutInDialog, TextView prepView, TextView prepNumView, EditText prepNumEdit, EditText prepEdit, ImageButton img)
	{
		linearLayoutInDialog.addView(prepNumView);
		linearLayoutInDialog.addView(prepNumEdit);
		linearLayoutInDialog.addView(prepView);
		linearLayoutInDialog.addView(prepEdit);
		linearLayoutInDialog.addView(img);
		prepDialogLinearLayout.addView(linearLayoutInDialog);
	}

	public void setVisibility(int point, TextView prepView, TextView prepNumView, EditText prepNumEdit, EditText prepEdit)
	{
		prepList.get(point).setProgress("deleted");
		prepNumEdit.setVisibility(View.INVISIBLE);
		prepEdit.setVisibility(View.INVISIBLE);
		prepNumView.setVisibility(View.INVISIBLE);
		prepView.setVisibility(View.INVISIBLE);
	}

	public Button createButton()
	{
		Button okButton = new Button(activity);
		int buttonId = findId();
		okButton.setId(buttonId);
		okButton.setText("Ok");
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		okButton.setBackgroundResource(R.drawable.drawable_button);
		prepDialogLinearLayout.addView(okButton);
		okButton = utils.setButtonTextDialog(buttonId, 16, prepDialog);
		return okButton;
	}

	public TextView createErrorView()
	{
		final TextView errorView = new TextView(activity);
		int errorId = findId();
		errorView.setId(errorId);
		params.gravity = Gravity.CENTER;
		prepDialogLinearLayout.addView(errorView);
		utils.setDialogText(errorId,prepDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		return errorView;
	}

	public void listCheck(TextView errorView)
	{
		boolean dismissed = false;
		int size = 0;
		
		for(int i = 0; i < prepList.size(); i++)
		{
			if(prepList.get(i).getProgress().equals("deleted"))
			{
				size += 1;
				modifiedPrepList.add(prepList.get(i));
				Log.v("prep ", "prep " + prepList.get(i).getProgress());
				dismissed = sizeCheck(size);
			}
			else if(prepList.get(i).getProgress().equals("added"))
			{
				Log.v("prep ", "prep " + prepList.get(i).getProgress());
				PreperationBean prep = new PreperationBean();
				if(utils.getTextFromDialog(prepEditIds.get(i), prepDialog).equals(""))
				{
					errorView.setText("Please input text into all the textboxes");
				}
				else if(utils.getTextFromDialog(prepNumEditIds.get(i), prepDialog).equals(""))
				{
					errorView.setText("Please input text into all the textboxes");
				}
				else
				{

					prep.setPreperation(utils.getTextFromDialog(prepEditIds.get(i), prepDialog));
					prep.setPrepNum(Integer.parseInt(utils.getTextFromDialog(prepNumEditIds.get(i), prepDialog)));
					prep.setUniqueid(prepList.get(i).getUniqueid());
					prep.setProgress(prepList.get(i).getProgress());
					modifiedPrepList.add(prep); 

					size += 1;
					dismissed = sizeCheck(size);

				}
			} 

			
		}

		//Set prep list to new modified list
		if(dismissed == true)
		{
			updateList(modifiedPrepList);
		}
	}

	public void updateList(ArrayList<PreperationBean> list)
	{
		TextView instructions = (TextView) activity.findViewById(R.id.methodList);
		instructions.setText("");
		//Order list
		Collections.sort(list, new Comparator<PreperationBean>() {
			@Override 
			public int compare(PreperationBean p1, PreperationBean p2) {
				return p1.getPrepNum() - p2.getPrepNum(); // Ascending
			}});
		//Set on edit page
		for(int i = 0; i < list.size(); i++)
		{
			if(list.get(i).getProgress().equals("added"))
			{
				instructions.append(Integer.toString(list.get(i).getPrepNum()) + ". " + list.get(i).getPreperation().toString() + "\n \n");

			} 
		}
	}

	public boolean sizeCheck(int size)
	{
		boolean dismiss = false;
		if(size == (prepList.size()))
		{
			prepList = modifiedPrepList;
			prepDialog.dismiss();
			dismiss = true;
		} 
		return dismiss;
	}

	@Override
	public int findId(){  
		View v = activity.findViewById(id);  
		while (v != null)
		{  
			v = activity.findViewById(++id);  
		}  
		return id++;  
	}
	
	public void setUpStepAddDialog()
	{
		final Dialog recipeAddStepDialog = utils.createDialog(activity, R.layout.recipe_add_dialog4);		
		utils.setDialogText(R.id.stepNumView,recipeAddStepDialog,22);
		utils.setDialogText(R.id.stepView, recipeAddStepDialog, 22);
		utils.setDialogText(R.id.addStepView, recipeAddStepDialog, 22);
		
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeAddStepDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recipeAddStepDialog.dismiss();
				
			}});
		final TextView errorView = (TextView) recipeAddStepDialog.findViewById(R.id.errorView);
		Button addButton = utils.setButtonTextDialog(R.id.addStepButton, 22, recipeAddStepDialog);
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getRecipeStep(recipeAddStepDialog, errorView);
			}});
		recipeAddStepDialog.show();
	}
	
	public void getRecipeStep(Dialog recipeAddStepDialog, TextView errorView)
	{
		//Getting  data from the text boxes
		
		utils.setDialogText(R.id.errorView,recipeAddStepDialog,16);
		errorView.setTextColor(Color.parseColor("#FFFFFF"));
		String stepNum = utils.getTextFromDialog(R.id.stepNumEditText, recipeAddStepDialog);
		String step = utils.getTextFromDialog(R.id.stepEditText, recipeAddStepDialog);
		errorView.setText("");
		//Error catching before moving to next dialog stage
		if(stepNum.equals(""))
		{
			errorView.setText("Please enter a step number");
		}
		else if(step.equals(""))
		{
			errorView.setText("Please enter a step");
		}
		else
		{
			recipeAddStepDialog.dismiss();
			//Sets the details to a prep bean
			PreperationBean prepBean = new PreperationBean();
			prepBean.setPreperation(step);
			prepBean.setPrepNum(Integer.parseInt(stepNum));
			prepBean.setProgress("added");
			RecipeModel rm = new RecipeModel(context);
			prepBean.setUniqueid(rm.generateuuid(recipe.getAddedBy(), "Preperation"));
			prepList.add(prepBean);
			addPrepList.add(prepBean);
			updateList(prepList);
		}
	}

}
