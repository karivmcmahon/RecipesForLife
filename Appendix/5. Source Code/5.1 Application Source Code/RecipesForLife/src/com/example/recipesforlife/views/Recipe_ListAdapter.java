package com.example.recipesforlife.views;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

/**
 * This class contains a list of recipes relating to a cookbook
 * @author Kari
 *
 */
class Recipe_ListAdapter extends ArrayAdapter<String> {
	private final Activity activity;
	private ArrayList<String> recipenames;
	private ArrayList<String> recipeids;
	private ArrayList<byte[]> recipeimages;
	String bookid;
	private static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	private Context context;
	private Util utils;
	private ImageLoader2 imgload;
	private ApplicationModel_RecipeModel model;
	

	Recipe_ListAdapter(Activity activity , ArrayList<String> recipenames, Context context, ArrayList<String> recipeids, ArrayList<byte[]> recipeimages, String bookid)
	{
		super(context, R.layout.recipe_listitem, recipenames);
		this.activity = activity;
		this.context = context;
		this.recipenames = recipenames;
		this.recipeids = recipeids;
		this.recipeimages = recipeimages;
		this.bookid = bookid;
		model = new ApplicationModel_RecipeModel(context);
		imgload = new ImageLoader2(context);
		utils = new Util(this.context, activity);

	}

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View view, ViewGroup parent) 
	{
		LayoutInflater inflater = activity.getLayoutInflater();
		View rowView = null;
		
		//Shows empty row for layout reasons or a recipe based on the string
		if(recipenames.get(position).toString().equals(""))
		{
			rowView= inflater.inflate(R.layout.general_listview_emptyrow, null, true);
		}
		else
		{
			//Show recipe if recipes exist
			rowView= inflater.inflate(R.layout.recipe_listitem, null, true);
			TextView txtTitle = (TextView) rowView.findViewById(R.id.myImageViewText);
			SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
			
			boolean access = model.doesUserHaveAccess(sharedpreferences.getString(emailk, ""), bookid);
	       
			//set text style
			txtTitle.setText(recipenames.get(position));
			utils.setRowText(R.id.myImageViewText, rowView, 22);
		
			//If recipe selected view the recipe
			ImageView recipeImage = (ImageView) rowView.findViewById(R.id.myImageView);
			imgload.DisplayImage(recipeImage, recipeimages.get(position), Base64.encodeToString(recipeimages.get(position), Base64.DEFAULT) + recipeids.get(position));
			recipeImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
					{
						Intent i = new Intent(activity, Recipe_View.class);
						i.putExtra("uniqueidr", recipeids.get(position));
						i.putExtra("name", recipenames.get(position));
						activity.startActivity(i);
					}
					return false;
				}

			});
			
			
			//If edit button selected, then take the user to edit recipe page
			ImageView editRecipeImage = (ImageView) rowView.findViewById(R.id.editView);
			if(access == false)
			{
				//if not creator then set edit button to invisible
				editRecipeImage.setVisibility(View.INVISIBLE);
			}
			else
			{
			editRecipeImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
					{
						//Start edit activity
						Intent i = new Intent(activity, Recipe_EditView.class);
						i.putExtra("uniqueidr", recipeids.get(position));
						i.putExtra("name", recipenames.get(position));
						activity.startActivity(i);
					}
					return false;
				}

			});
			}
			//If delete button selected - delete recipe
			ImageView delRecipeImage = (ImageView) rowView.findViewById(R.id.delView);
			if(access == false)
			{
				//if not creator then sets the edit button to invisible
				delRecipeImage.setVisibility(View.INVISIBLE);
			}
			else
			{
			delRecipeImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
					{
						getDeleteDialog(position);
					}
					return false;

				}

			});
			}
		}
		return rowView;		
	}
	
	/**
	 * Displays a delete dialog
	 * @param position - position in row
	 */
	public void getDeleteDialog(final int position)
	{
		final Dialog dialog = utils.createDialog(activity, R.layout.general_savedialog);
		utils.setDialogText(R.id.textView, dialog, 18);
		TextView tv = (TextView) dialog.findViewById(R.id.textView);
		tv.setText("Would you like to delete this recipe ?");
		
		// Show dialog
		dialog.show();

		//Deletes users and dismiss dialog
		Button yesButton = utils.setButtonTextDialog(R.id.yesButton, 22, dialog);
		yesButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
		
				//Gets the recipe details and updates the recipe to be deleted
				ApplicationModel_RecipeModel model = new ApplicationModel_RecipeModel(context);
				RecipeBean recipebean = model.selectRecipe2(recipeids.get(position));
			
				ArrayList<PreperationBean> prepList = model.selectPreperation(recipebean.getId());
				ArrayList<IngredientBean> ingredList = model.selectIngredients(recipebean.getId());
				
				ImageBean imgBean = model.selectImages(recipebean.getId());
				recipebean.setProgress("deleted");
				try
				{
					//updates recipe to be deleted and removes from list
					model.updateRecipe( recipebean, prepList, ingredList, imgBean, false);
					recipeids.remove(position);
					recipeimages.remove(position);
					recipenames.remove(position);
					if(recipeids.size() < 6)
					{
						int num = 6 - recipeids.size();
						for(int a = 0; a < num; a++)
						{
							byte[] emptyarr = new byte[0];
							recipenames.add("");
							recipeids.add("");
							recipeimages.add(emptyarr);
						}
					} 
					notifyDataSetChanged();
				}catch(SQLiteException e)
				{
					Toast.makeText(context, "Recipe was not deleted", Toast.LENGTH_LONG).show();
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
}


