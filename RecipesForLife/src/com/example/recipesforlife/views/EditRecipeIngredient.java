package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
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
import com.example.recipesforlife.util.Util;

public class EditRecipeIngredient extends RecipeEditActivity{

	ActionBarActivity activity;
	Context context;
	Util utils;
	LinearLayout prepDialogLinearLayout;
	LinearLayout.LayoutParams params;


	public EditRecipeIngredient(Context context, ActionBarActivity activity)
	{
		this.context = context;
		this.activity = activity;
		utils = new Util(context, activity);
	}

	public void getIngredient()
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
		LinearLayout ingredDialogLinearLayout = (LinearLayout)ingredDialog.findViewById(R.id.editdialog);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT,      
				LayoutParams.WRAP_CONTENT
				);
		//Create dialog with textviews and edit text from the database
		for(int i = 0; i < ingredList.size(); i++)
		{
			final int point = i;
			if(ingredList.get(i).getProgress().equals("added"))
			{
				LinearLayout linearLayoutInDialog = new LinearLayout(activity);
				linearLayoutInDialog.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));


				params.setMargins(5,5,5,5);

				final EditText amountEdit = new EditText(activity);
				int amountEditId = findId();
				amountEditIds.add(amountEditId);
				amountEdit.setId(amountEditId);
				amountEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				amountEdit.setLayoutParams(params);
				amountEdit.setWidth(80);

				//Spinner set up with varying measurement amounts
				List<String> spinnerArray =  new ArrayList<String>();
				spinnerArray.add("teaspoon");
				spinnerArray.add("tablespoon");
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
				spinnerArray.add(" ");

				ArrayAdapter<String> adapter = new ArrayAdapter<String>(
						activity, R.layout.general_spinner_item, spinnerArray);

				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				final Spinner sItems = new Spinner(activity);
				sItems.getBackground().setColorFilter(context.getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
				sItems.setAdapter(adapter);
				sItems.setLayoutParams(params);

				int valueEditId = findId();
				valueEditIds.add(valueEditId);
				sItems.setId(valueEditId); 

				final EditText ingredEdit = new EditText(activity);
				int ingredEditId = findId();
				ingredEditIds.add(ingredEditId);
				ingredEdit.setId(ingredEditId);
				ingredEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				ingredEdit.setLayoutParams(params);
				ingredEdit.setWidth(200);

				final TextView view = new TextView(activity);
				int viewid = findId();
				view.setText(" - ");
				view.setId(viewid);

				final EditText noteEdit = new EditText(activity);
				int noteEditId = findId();
				noteEditIds.add(noteEditId);
				noteEdit.setId(noteEditId);
				noteEdit.setBackgroundColor(Color.parseColor("#FFFFFF"));
				noteEdit.setLayoutParams(params);
				noteEdit.setWidth(160);

				ImageButton img = new ImageButton(activity);
				int imgid = findId();
				img.setId(imgid);
				img.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.icon_delete));
				LinearLayout.LayoutParams lparams2 = new LinearLayout.LayoutParams(40, 40);
				lparams2.setMargins(5,5,5,5);
				img.setLayoutParams(lparams2);

				linearLayoutInDialog.addView(amountEdit);
				linearLayoutInDialog.addView(sItems);
				linearLayoutInDialog.addView(ingredEdit);
				linearLayoutInDialog.addView(view);
				linearLayoutInDialog.addView(noteEdit);
				linearLayoutInDialog.addView(img);

				ingredDialogLinearLayout.addView(linearLayoutInDialog);
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
						
						ingredList.get(point).setProgress("deleted");
						
						amountEdit.setVisibility(View.INVISIBLE);
						ingredEdit.setVisibility(View.INVISIBLE);
						sItems.setVisibility(View.INVISIBLE);
						view.setVisibility(View.INVISIBLE);
						noteEdit.setVisibility(View.INVISIBLE);


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
		Button okButton = new Button(activity);
		int buttonId = findId();
		okButton.setId(buttonId);
		okButton.setText("Ok");
		okButton.setBackgroundResource(R.drawable.drawable_button);
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		ingredDialogLinearLayout.addView(okButton);
		okButton = utils.setButtonTextDialog(buttonId, 16, ingredDialog);

		final TextView errorView = new TextView(activity);
		int errorId = findId();
		errorView.setId(errorId);
		params.gravity = Gravity.CENTER;
		okButton.setLayoutParams(params);
		ingredDialogLinearLayout.addView(errorView);
		utils.setDialogText(errorId,ingredDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));

		//When ok button clicked get new ingredient list
		okButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				modifiedIngredList = new ArrayList<IngredientBean>();
				boolean dismissed = false;
				int b = 0;
				for(int i = 0; i < ingredList.size(); i++)
				{
					IngredientBean ingred = new IngredientBean();
					
				
					
					if(ingredList.get(i).getProgress().equals("deleted"))
					{
						b += 1;
						modifiedIngredList.add(ingredList.get(i));
						
					}
					else if(ingredList.get(i).getProgress().equals("added"))
					{
						Log.v("i ", "i " + i );
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
							b += 1;

							ingred.setName(utils.getTextFromDialog(ingredEditIds.get(i), ingredDialog));
							ingred.setAmount(Integer.parseInt(utils.getTextFromDialog(amountEditIds.get(i), ingredDialog)));
							ingred.setNote(utils.getTextFromDialog(noteEditIds.get(i), ingredDialog));
							ingred.setProgress(ingredList.get(i).getProgress());
							Spinner spinner = (Spinner) ingredDialog.findViewById(valueEditIds.get(i));
							String value = spinner.getSelectedItem().toString();
							ingred.setValue(value);			
							ingred.setUniqueid(ingredList.get(i).getUniqueid());
							Log.v("mod ", "mod add " + i + ingred.getName());
							modifiedIngredList.add(ingred);
						}
						
						
					}
					
					if(b == (ingredList.size()  ))
					{
						//set ingred list to new modified ingred list
						dismissed = true;
						ingredList = modifiedIngredList;
						ingredDialog.dismiss();
					}
						
						
					
				}
				if(dismissed == true)
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

			}});

		ingredDialog.show();	

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

}
