package com.example.recipesforlife.views;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.SQLException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.models.ApplicationModel_CookbookModel;
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.util.TypefaceSpan;
import com.example.recipesforlife.util.Util;

/**
 * Class to show a list of recipes that belong to a specific cookbook
 * @author Kari
 *
 */
public class Recipe_ShelfListView extends ActionBarActivity {
	private Util utils;	
	private ListView listView;
	private String uniqueid = "";
	private ArrayList<RecipeBean> recipeList;
	private ApplicationModel_CookbookModel model;
	private ApplicationModel_RecipeModel rmodel;
	static Recipe_ListAdapter adapter;
	private Navigation_DrawerCreation nav;
	private static final String MyPREFERENCES = "MyPrefs";
	private static final String emailk = "emailKey";  
	static ArrayList<String> recipeids,  recipenames;
	static ArrayList<byte[]> recipeimages;
	private Recipe_AddView add;
	private ArrayList<RecipeBean> rbList;
    private Dialog cloneDialog;
    private TextView errorView;



	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onCreate(android.os.Bundle)
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		setContentView(R.layout.recipe_listview);

		//Setup
		listView = (ListView) findViewById(R.id.list);
		utils = new Util(getApplicationContext(), this);
		model = new ApplicationModel_CookbookModel(getApplicationContext());
		rmodel = new ApplicationModel_RecipeModel(getApplicationContext());
		recipeList = new ArrayList<RecipeBean>();
		Intent intent = getIntent();
	
		//gets details from intent
		uniqueid = intent.getStringExtra("uniqueid");
		String bookname = intent.getStringExtra("bookname");

		//Sets up the navigation drawer
		nav = new Navigation_DrawerCreation(this, "My Recipes");
		nav.createDrawer();
		SpannableString s = new SpannableString("My Recipes in " + bookname);
		s.setSpan(new TypefaceSpan(this, "elsie.otf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		android.support.v7.app.ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(s);

		//Gets recipe list and set to adapter
		recipeList = model.selectRecipesByCookbook(intent.getStringExtra("uniqueid"));
		recipenames = new ArrayList<String>();
		recipeids = new ArrayList<String>();
		recipeimages = new ArrayList<byte[]>();

		//updates list
		for(int a = 0; a < recipeList.size(); a++)
		{
			ImageBean image = model.selectImage(recipeList.get(a).getId());
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
			recipeimages.add(image.getImage());
		}
		
		//if recipes list is less than 6 add extra recipe rows for layout
		if(recipeList.size() < 6)
		{
			int num = 6 - recipeList.size();
			for(int i = 0; i < num; i++)
			{
				byte[] arr = new byte[0];
				recipenames.add("");
				recipeids.add("");
				recipeimages.add(arr);
			}
		}
		//sets list details to adapter
		adapter = new Recipe_ListAdapter(this, recipenames, getApplicationContext(), recipeids, recipeimages, uniqueid);
		listView.setAdapter(adapter); 

	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		menu.clear();
		
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		Intent intent = getIntent();
		
		//Depending on access shows menu options
		boolean access = rmodel.doesUserHaveAccess(sharedpreferences.getString(emailk, ""), intent.getStringExtra("uniqueid"));
		if(access == true)
		{
			getMenuInflater().inflate(R.menu.menu_recipe_list, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.menu_plain, menu);
		}
		
		//set up search
		utils.setUpSearch(menu);
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) { 
		menu.clear();
		
		SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		Intent intent = getIntent();
		
		//depending on access show different views
		boolean access = rmodel.doesUserHaveAccess(sharedpreferences.getString(emailk, ""), intent.getStringExtra("uniqueid"));
		if(access == true)
		{
			getMenuInflater().inflate(R.menu.menu_recipe_list, menu);
		}
		else
		{
			getMenuInflater().inflate(R.menu.menu_plain, menu);
		}
		utils.setUpSearch(menu);
		return super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();
		
		//clears list
		recipenames.clear();
		recipeids.clear();
		recipeimages.clear();
		
		recipeList = model.selectRecipesByCookbook(uniqueid);
		
		//updates list
		for(int a = 0; a < recipeList.size(); a++)
		{
			ImageBean image = model.selectImage(recipeList.get(a).getId());
			recipenames.add(recipeList.get(a).getName());
			recipeids.add(recipeList.get(a).getUniqueid());
			recipeimages.add(image.getImage());

		}
		
		//if recipes list is less than 6 add extra recipe rows for layout
		if(recipeList.size() < 6)
		{
			int num = 6 - recipeList.size();
			for(int i = 0; i < num; i++)
			{
				byte[] arr = new byte[0];
				recipenames.add("");
				recipeids.add("");
				recipeimages.add(arr);
			}
		}
		
		//Updates the list view
		adapter.notifyDataSetChanged();

	}
	
	@Override
    public void onDestroy()
    {
        listView.setAdapter(null);
        super.onDestroy();
    }

	/* (non-Javadoc)
	 * @see android.app.Activity#onPostCreate(android.os.Bundle)
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		
		super.onPostCreate(savedInstanceState);
		// Sync the toggle 
		nav.syncState();
	}

	/* (non-Javadoc)
	 * @see android.support.v7.app.ActionBarActivity#onConfigurationChanged(android.content.res.Configuration)
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		super.onConfigurationChanged(newConfig);
		nav.config(newConfig);
	}


	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		//Checks for nav drawer selection
		nav.drawerToggle(item);
		
		//If add icons selected - displays dialog to add a new recipe
		if(item.getItemId() ==  R.id.action_bookadd)
		{
			Intent intent = getIntent();
			add = new Recipe_AddView(getApplicationContext(), Recipe_ShelfListView.this, uniqueid, intent.getStringExtra("bookname"));
			add.addRecipe();

		}
		else if(item.getItemId() ==  R.id.action_copy) //if copy icon selected displays clone dialog
		{
			createCloneDialog();
		} 
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		add.resultRecieved(requestCode, resultCode, imageReturnedIntent);

	}
	
	/**
	 * Creates the clone dialog - to clone recipes into cookbook
	 */
	private void createCloneDialog()
	{
		//creates clone style
		cloneDialog = utils.createDialog(Recipe_ShelfListView.this , R.layout.recipe_clone);
		setCloneDialogStyle();

		final ApplicationModel_RecipeModel model = new ApplicationModel_RecipeModel(getApplicationContext());
		final SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
		rbList = model.selectAllRecipesUserCanAccess(sharedpreferences.getString(emailk, ""));	
		
		final Spinner spinner = fillSpinner(); //fills spinner
		
		Button closeButton = utils.setButtonTextDialog(R.id.closeButton, 22, cloneDialog);
		closeButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				cloneDialog.dismiss();
				
			}});

		Button addButton = utils.setButtonTextDialog(R.id.addButton, 22, cloneDialog);
		addButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				
				//gets a position of select item
				int namepos = spinner.getSelectedItemPosition();
				
				//sets recipe thats being cloned to new info
				rbList.get(namepos).setName(utils.getTextFromDialog(R.id.recipenameEditText, cloneDialog));
				rbList.get(namepos).setAddedBy(sharedpreferences.getString(emailk, ""));
				rbList.get(namepos).setRecipeBook(uniqueid);
				
				//Retrieve preps, ingreds and images 
				ArrayList<PreperationBean> prepList = model.selectPreperation(rbList.get(namepos).getId());
				ArrayList<IngredientBean> ingredList = model.selectIngredients(rbList.get(namepos).getId());
				ImageBean imgBean = model.selectImages(rbList.get(namepos).getId());

				try
				{
					//check for errors
					if(model.selectRecipe(rbList.get(namepos).getName(), uniqueid))
					{
						errorView.setText("You already have a recipe with that name");
					}
					else if(rbList.get(namepos).getName().equals(""))
					{
						errorView.setText("Please enter a recipe name");
					}
					else
					{
						//clone recipe
						String uid = model.insertRecipe(rbList.get(namepos), false, ingredList, prepList, imgBean);
						
						//Add to list
						recipenames.add(0, rbList.get(namepos).getName());
						recipeids.add(0,  uid);
						recipeimages.add(0, imgBean.getImage());
						Recipe_ShelfListView.adapter.notifyDataSetChanged(); 
						cloneDialog.dismiss();
					}

				}catch(SQLException e)
				{
					Toast.makeText(getApplicationContext(), "Recipe was not cloned", Toast.LENGTH_LONG).show();
				}



			}});

		cloneDialog.show();
	}
	
	/**
	 * Fills spinner for cloning
	 * @return filled spinner
	 */
	private Spinner fillSpinner()
	{
		//Fill with a list of recipes the user owns
		List<String> spinnerArray =  new ArrayList<String>();
		for(int i = 0; i < rbList.size(); i++)
		{
			spinnerArray.add(rbList.get(i).getName());
			
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				Recipe_ShelfListView.this, R.layout.general_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
		final Spinner spinner = (Spinner) cloneDialog.findViewById(R.id.currentRecipesSpinner);
		spinner.setAdapter(adapter);
		spinner.getBackground().setColorFilter(getResources().getColor(R.color.white), android.graphics.PorterDuff.Mode.SRC_ATOP);
		return spinner;
	}
	
	/**
	 * Set up a clone dialog style
	 */
	private void setCloneDialogStyle()
	{
		utils.setDialogText(R.id.cloneTitle,cloneDialog,22);
		utils.setDialogText(R.id.currentRecipesView,cloneDialog,22);
		utils.setDialogText(R.id.recipeNameView,cloneDialog,22);
		errorView = (TextView) cloneDialog.findViewById(R.id.errorView);
		utils.setDialogText(R.id.errorView,cloneDialog,16);
		errorView.setTextColor(Color.parseColor("#F70521"));
	}
}



