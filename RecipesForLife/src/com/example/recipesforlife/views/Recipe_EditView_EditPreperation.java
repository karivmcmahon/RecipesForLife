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
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.util.Util;

/**
 * Handles the recipe preperation edit details
 * @author Kari
 *
 */
class Recipe_EditView_EditPreperation extends Recipe_EditView{

	private ActionBarActivity activity;
	private Context context;
	private Util utils;
	private LinearLayout prepDialogLinearLayout;
	private LinearLayout.LayoutParams params;


	Recipe_EditView_EditPreperation(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}

	/**
	 * Sets up the preperation list to be edited
	 */
	void getPreperation()
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

	/**
	 * Set up information for the dialog
	 */
	private void setUp()
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

	/**
	 * Creates a list of preperation details to be edited
	 */
	private void createList()
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

				//If img click - set visibility to invisible and set info to deleted progress
				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
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

	/**
	 * Create textview for prep list
	 * 
	 * @param text			Text displayed in the text view
	 * @param id			TextViews id
	 * @return TextView		Updated textview
	 */
	private TextView createViewsForDialog(String text, int id)
	{
		params.setMargins(5,5,5,5);
		TextView tv = new TextView(activity);
		tv.setText(text);
		tv.setId(id);
		tv.setLayoutParams(params);
		return tv;
	}

	/**
	 * Create EditText for prep list
	 * 
	 * @param id			EditText id
	 * @return EditText		Updated edit text
	 */
	private EditText createEditTextForDialog(int id)
	{
		final EditText edittext = new EditText(activity);
		edittext.setId(id);
		edittext.setBackgroundColor(Color.parseColor("#FFFFFF"));
		LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(200, 300);
		lparams.setMargins(5,5,5,5);
		edittext.setLayoutParams(lparams);
		return edittext;
	}
	
	/**
	 * Create image button for delete next to each list item
	 * 
	 * @return ImageButton
	 */
	@SuppressWarnings("deprecation")
	private ImageButton createImageButton()
	{
		ImageButton img = new ImageButton(activity);
		int imgid = findId();
		img.setId(imgid);
		img.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_delete));
		LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
		img.setLayoutParams(lparams2);
		return img;
	}
	
	
	/**
	 * Set up views for the list
	 * 
	 * @param linearLayoutInDialog
	 * @param prepView
	 * @param prepNumView
	 * @param prepNumEdit
	 * @param prepEdit
	 * @param img
	 */
	private void addViews(LinearLayout linearLayoutInDialog, TextView prepView, TextView prepNumView, EditText prepNumEdit, EditText prepEdit, ImageButton img)
	{
		linearLayoutInDialog.addView(prepNumView);
		linearLayoutInDialog.addView(prepNumEdit);
		linearLayoutInDialog.addView(prepView);
		linearLayoutInDialog.addView(prepEdit);
		linearLayoutInDialog.addView(img);
		prepDialogLinearLayout.addView(linearLayoutInDialog);
	}

	/**
	 * Set visibility of list items to visible and set the progress info to deleted
	 * 
	 * @param point				point in loop
	 * @param prepView			TextView
	 * @param prepNumView		TextView
	 * @param prepNumEdit		EditText
	 * @param prepEdit			EditText
	 */
	private void setVisibility(int point, TextView prepView, TextView prepNumView, EditText prepNumEdit, EditText prepEdit)
	{
		prepList.get(point).setProgress("deleted");
		prepNumEdit.setVisibility(View.INVISIBLE);
		prepEdit.setVisibility(View.INVISIBLE);
		prepNumView.setVisibility(View.INVISIBLE);
		prepView.setVisibility(View.INVISIBLE);
	}

	/**
	 * Create button to be shown at the end of the list with correct styles and fonts
	 * 
	 * @return Button
	 */
	private Button createButton()
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

	/**
	 * Create error view with the correct styles
	 * 
	 * @return TextView 	Updated TextView
	 */
	private TextView createErrorView()
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

	/**
	 * Checks the list for errors before making changes
	 * 
	 * @param errorView
	 */
	private void listCheck(TextView errorView)
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

	/**
	 * Updates the list on screen once change has been made
	 * 
	 * @param list	Preperation List
	 */
	private void updateList(ArrayList<PreperationBean> list)
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

	/**
	 * Check list size against number - If equals dialog should dismiss
	 * @param size		Value from a counter
	 * @return boolean  Stating whether dialog should be dismissed
	 */
	private boolean sizeCheck(int size)
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
	/**
	 * Checks for ids which can be used
	 */
	public int findId(){  
		View v = activity.findViewById(id);  
		while (v != null)
		{  
			v = activity.findViewById(++id);  
		}  
		return id++;  
	}
	
	/**
	 * Set the dialog for adding a new step in the edit page
	 */
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
	
	/**
	 * Get the details from the recipe add step dialog
	 * 
	 * @param recipeAddStepDialog	Dialog to retrieve data from
	 * @param errorView
	 */
	private void getRecipeStep(Dialog recipeAddStepDialog, TextView errorView)
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
			ApplicationModel_RecipeModel rm = new ApplicationModel_RecipeModel(context);
			prepBean.setUniqueid(rm.generateuuid(recipe.getAddedBy(), "Preperation"));
			prepList.add(prepBean);
			addPrepList.add(prepBean);
			updateList(prepList);
		}
	}

}
