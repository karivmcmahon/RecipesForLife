package com.example.recipesforlife.views;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.RecipeModel;
import com.example.recipesforlife.util.ImageLoader2;
import com.example.recipesforlife.util.Util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class contains a list of recipes relating to a cookbook
 * @author Kari
 *
 */
public class CustomRecipeListAdapter extends ArrayAdapter<String> {
	private final Activity activity;
	private ArrayList<String> recipenames;
	private ArrayList<String> recipeids;
	private ArrayList<byte[]> recipeimages;
	public static final String emailk = "emailKey";
	public static final String MyPREFERENCES = "MyPrefs";
	Context context;
	Util utils;
	ImageLoader2 imgload;

	public CustomRecipeListAdapter(Activity activity , ArrayList<String> recipenames, Context context, ArrayList<String> recipeids, ArrayList<byte[]> recipeimages)
	{
		super(context, R.layout.recipe_listitem, recipenames);
		this.activity = activity;
		this.context = context;
		this.recipenames = recipenames;
		this.recipeids = recipeids;
		this.recipeimages = recipeimages;


		imgload = new ImageLoader2(context);
		utils = new Util(this.context, activity);

	}

	@Override
	/**
	 * Adapts list data 
	 */
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
						// TODO Auto-generated method stub
						Intent i = new Intent(activity, RecipeViewActivity.class);
						i.putExtra("uniqueidr", recipeids.get(position));
						i.putExtra("name", recipenames.get(position));
						activity.startActivity(i);
					}
					return false;
				}

			});
			//If edit button selected, then take the user to edit recipe page
			ImageView editRecipeImage = (ImageView) rowView.findViewById(R.id.editView);
			editRecipeImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
					{
						Intent i = new Intent(activity, RecipeEditActivity.class);
						i.putExtra("uniqueidr", recipeids.get(position));
						i.putExtra("name", recipenames.get(position));
						activity.startActivity(i);
					}
					return false;
				}

			});

			//If delete button selected - delete recipe
			ImageView delRecipeImage = (ImageView) rowView.findViewById(R.id.delView);
			delRecipeImage.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) 
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
								RecipeModel model = new RecipeModel(context);
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
					return false;

				}

			});
		}
		return rowView;		
	}


}
