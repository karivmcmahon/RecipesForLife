package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.Util;

public class EditRecipeIngredient extends RecipeEditActivity{

	ActionBarActivity activity;
	Context context;
	Util utils;
	LinearLayout.LayoutParams params;
	LinearLayout ingredDialogLinearLayout;
	Dialog recipeIngredDialog;
	
	public EditRecipeIngredient(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
		
	}

	public void getIngredient()
	{
		setUp();
		//Create dialog with textviews and edit text from the database
		createList();
		
		Button okButton = createButton();
		final TextView errorView = createErrorView();
		//When ok button clicked get new ingredient list
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {	
				listCheck(errorView);
			}});

		ingredDialog.show();	

	}
	
	public void listCheck(TextView errorView)
	{
		
		modifiedIngredList = new ArrayList<IngredientBean>();
		boolean dismissed = false;
		int size = 0;
		for(int i = 0; i < ingredList.size(); i++)
		{
			IngredientBean ingred = new IngredientBean();
			if(ingredList.get(i).getProgress().equals("deleted"))
			{
				size += 1;
				modifiedIngredList.add(ingredList.get(i));	
				dismissed = sizeCheck(size);
			}
			else if(ingredList.get(i).getProgress().equals("added"))
			{
				
				if(utils.getTextFromDialog(ingredEditIds.get(i), ingredDialog).equals(""))
				{
					errorView.setText("Please input text into all the textboxes");
				}
				else if(utils.getTextFromDialog(amountEditIds.get(i), ingredDialog).equals(""))
				{
					errorView.setText("Please input text into all the textboxes");
				}
				else
				{
					size += 1;
					ingred.setName(utils.getTextFromDialog(ingredEditIds.get(i), ingredDialog));
					ingred.setAmount(Integer.parseInt(utils.getTextFromDialog(amountEditIds.get(i), ingredDialog)));
					ingred.setNote(utils.getTextFromDialog(noteEditIds.get(i), ingredDialog));
					ingred.setProgress(ingredList.get(i).getProgress());
					Spinner spinner = (Spinner) ingredDialog.findViewById(valueEditIds.get(i));
					String value = spinner.getSelectedItem().toString();
					ingred.setValue(value);			
					ingred.setUniqueid(ingredList.get(i).getUniqueid());
					modifiedIngredList.add(ingred);
					dismissed = sizeCheck(size);
				}
				
				
			}	
			
		}
		if(dismissed == true)
		{
			updateList();
		}
	}
	public void setUp()
	{
		ingredDialog = utils.createDialog(activity, R.layout.recipe_edit_dialog5);
		utils.setDialogText(R.id.recipeEditView, ingredDialog, 22);
		utils.setDialogText(R.id.amountTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.valueTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.ingredientTitleView, ingredDialog, 22);
		utils.setDialogText(R.id.noteTitleView, ingredDialog, 22);

		amountEditIds = new ArrayList<Integer>();
		noteEditIds = new ArrayList<Integer>();
		valueEditIds = new ArrayList<Integer>();
		ingredEditIds = new ArrayList<Integer>();
	    ingredDialogLinearLayout = (LinearLayout)ingredDialog.findViewById(R.id.editdialog);
		params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,      
				LayoutParams.WRAP_CONTENT
				);
	}
	
	public void createList()
	{
		for(int i = 0; i < ingredList.size(); i++)
		{
			final int point = i;
			if(ingredList.get(i).getProgress().equals("added"))
			{
				LinearLayout linearLayoutInDialog = new LinearLayout(activity);
				linearLayoutInDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				params.setMargins(5,5,5,5);

						
				int amountEditId = findId();
				amountEditIds.add(amountEditId);
				final EditText amountEdit = createEditText(80);
				amountEdit.setId(amountEditId);
				
			
				final Spinner sItems = createSpinner(true);
				int valueEditId = findId();
				valueEditIds.add(valueEditId);
				sItems.setId(valueEditId); 

				final EditText ingredEdit = createEditText(200);
				int ingredEditId = findId();
				ingredEditIds.add(ingredEditId);
				ingredEdit.setId(ingredEditId);
		
				final TextView view = new TextView(activity);
				int viewid = findId();
				view.setText(" - ");
				view.setId(viewid);

				final EditText noteEdit = createEditText(160);
				int noteEditId = findId();
				noteEditIds.add(noteEditId);
				noteEdit.setId(noteEditId);
				

				ImageButton img = createImageButton();
				addViews(linearLayoutInDialog,
						amountEdit,  sItems, ingredEdit, view, noteEdit, img);
						
				
				amountEdit.setText(Integer.toString(ingredList.get(i).getAmount()));
				ingredEdit.setText(ingredList.get(i).getName());
				noteEdit.setText(ingredList.get(i).getNote());
				sItems.setSelection(utils.getIndex(sItems, ingredList.get(i).getValue()));
				utils.setDialogText(viewid, ingredDialog, 22);
				ingredList.get(point).setProgress("added");
				
				img.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						setVisibility(point, amountEdit, ingredEdit, sItems, view, noteEdit);
						
					}});
			}
			else
			{
				ingredEditIds.add(i,0);
				amountEditIds.add(i,0);
				noteEditIds.add(i,0);
				valueEditIds.add(i,0); 
			}
		}
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
		ingredDialogLinearLayout.addView(okButton);
		okButton = utils.setButtonTextDialog(buttonId, 16, ingredDialog);
		return okButton;
	}

	public TextView createErrorView()
	{
		final TextView errorView = new TextView(activity);
		int errorId = findId();
		errorView.setId(errorId);
		params.gravity = Gravity.CENTER;
		ingredDialogLinearLayout.addView(errorView);
		utils.setDialogText(errorId,ingredDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
		return errorView;
	}
	
	public EditText createEditText(int width)
	{
		EditText edit = new EditText(activity);
		edit.setBackgroundColor(Color.parseColor("#FFFFFF"));
		edit.setLayoutParams(params);
		edit.setWidth(width);
		return edit;
	}
	
	public Spinner createSpinner(boolean create)
	{
		//Spinner set up with varying measurement amounts
		List<String> spinnerArray =  new ArrayList<String>();
		spinnerArray.add("teaspoon");
		spinnerArray.add("tablespoon");
		spinnerArray.add(" ");
		spinnerArray.add("cup");
		spinnerArray.add("kg");
		spinnerArray.add("g");
		spinnerArray.add("l");
		spinnerArray.add("ml");
		spinnerArray.add("oz");
		spinnerArray.add("pint");
		spinnerArray.add("quart");
		spinnerArray.add("gallon");
		spinnerArray.add("lb");
		spinnerArray.add("ounces");
		spinnerArray.add("pinch");
		spinnerArray.add("other");

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				activity, R.layout.general_spinner_item, spinnerArray);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		Spinner sItems = null;
		if(create == true)
		{
			sItems = new Spinner(activity);
		}
		else
		{
			sItems = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		}
		sItems.getBackground().setColorFilter(context.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		sItems.setAdapter(adapter);
		sItems.setLayoutParams(params);
		return sItems;
	}
	
	public ImageButton createImageButton()
	{
		ImageButton img = new ImageButton(activity);
		int imgid = findId();
		img.setId(imgid);
		img.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_delete));
		LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
		lparams2.setMargins(5,5,5,5);
		img.setLayoutParams(lparams2);
		return img;
	}
	
	public void setVisibility(int point, EditText amountEdit, EditText ingredEdit, Spinner sItems, TextView view, EditText noteEdit)
	{
		ingredList.get(point).setProgress("deleted");
		amountEdit.setVisibility(View.INVISIBLE);
		ingredEdit.setVisibility(View.INVISIBLE);
		sItems.setVisibility(View.INVISIBLE);
		view.setVisibility(View.INVISIBLE);
		noteEdit.setVisibility(View.INVISIBLE);
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
	
	public boolean sizeCheck(int size)
	{
		boolean dismissed = false;
		if(size == (ingredList.size()  ))
		{
			//set ingred list to new modified ingred list
			dismissed = true;
			ingredList = modifiedIngredList;
			ingredDialog.dismiss();
		}
		return dismissed;
	}
	
	public void updateList()
	{
		//Apply to edit page
		TextView ingredients = (TextView) activity.findViewById(R.id.ingredientList);
		ingredients.setText("");
		for(int i = 0; i < modifiedIngredList.size(); i++)
		{
			if(modifiedIngredList.get(i).getProgress().equals("added"))
			{
				ingredients.append("- " + modifiedIngredList.get(i).getAmount() + " "+  modifiedIngredList.get(i).getValue().replace("other", "") + " " + modifiedIngredList.get(i).getName().toString() + " - " + modifiedIngredList.get(i).getNote().toString() + "\n");

			}
		} 
	}
	public void addViews(LinearLayout linearLayoutInDialog,
			EditText amountEdit, Spinner sItems, EditText ingredEdit, TextView view, EditText noteEdit, ImageButton img)
	{
		linearLayoutInDialog.addView(amountEdit);
		linearLayoutInDialog.addView(sItems);
		linearLayoutInDialog.addView(ingredEdit);
		linearLayoutInDialog.addView(view);
		linearLayoutInDialog.addView(noteEdit);
		linearLayoutInDialog.addView(img);
		ingredDialogLinearLayout.addView(linearLayoutInDialog);
	}
	
	public void setUpIngredAddDialog()
	{
		recipeIngredDialog = utils.createDialog(activity,R.layout.recipe_add_dialog5);
		utils.setDialogText(R.id.addIngredientView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.ingredsView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.valueView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.amountView, recipeIngredDialog, 22);
		utils.setDialogText(R.id.noteView, recipeIngredDialog, 22);
		Button addButton = utils.setButtonTextDialog(R.id.addIngredButton, 22, recipeIngredDialog);
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, recipeIngredDialog);
		createSpinner(false);
		 
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recipeIngredDialog.dismiss();
				
			}});

		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getIngredient(recipeIngredDialog);

			}}); 
		recipeIngredDialog.show();
	}




	/**
	 * Get information from ingredient dialogs
	 */
	public void getIngredient(Dialog recipeIngredDialog)
	{
		//Getting text
		String ingredient = utils.getTextFromDialog(R.id.ingredEditText, recipeIngredDialog);
		String amount = utils.getTextFromDialog(R.id.amountEditText, recipeIngredDialog);
		String note = utils.getTextFromDialog(R.id.noteEditText, recipeIngredDialog);
		Spinner spinner = (Spinner) recipeIngredDialog.findViewById(R.id.valueSpinner);
		String value = spinner.getSelectedItem().toString();

		TextView errorView = (TextView) recipeIngredDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,recipeIngredDialog,16);
		errorView.setTextColor(Color.parseColor("#FFFFFF"));
		//Error catching before moving onto next stage
		if(ingredient.equals(""))
		{
			errorView.setText("Please enter an ingredient name");
		}
		else if(amount.equals(""))
		{
			errorView.setText("Please enter an amount");
		}
		else
		{
			recipeIngredDialog.dismiss();
			//Sets details to ingredient bean

			IngredientBean ingredBean = new IngredientBean();
			RecipeModel rm = new RecipeModel(context);
			ingredBean.setUniqueid(rm.generateuuid(recipe.getAddedBy(), "IngredientDetails"));
			ingredBean.setName(ingredient);
			ingredBean.setAmount(Integer.parseInt(amount));
			ingredBean.setNote(note);
			ingredBean.setValue(value);
			ingredBean.setProgress("added");
			ingredList.add(ingredBean);
			addIngredList.add(ingredBean);
			recipeIngredDialog.dismiss();
			//How ingredients are displayed in the edit text box
			TextView ingredsEdit = (TextView) activity.findViewById(R.id.ingredientList);
			if(note.equals(""))
			{
				ingredsEdit.append(  amount + " " + value + " " + ingredient + "\n");
			}
			else
			{
				ingredsEdit.append( amount + " " + value + " " + ingredient.replace("other", "") + " - " + note + "\n");
			} 
		}


	}

}
