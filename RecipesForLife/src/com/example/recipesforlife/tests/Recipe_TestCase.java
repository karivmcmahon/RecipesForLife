package com.example.recipesforlife.tests;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.StrictMode;
import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;
import android.test.ProviderTestCase2;
import android.test.RenamingDelegatingContext;

import com.example.recipesforlife.R;
import com.example.recipesforlife.controllers.ImageBean;
import com.example.recipesforlife.controllers.IngredientBean;
import com.example.recipesforlife.controllers.PreperationBean;
import com.example.recipesforlife.controllers.RecipeBean;
import com.example.recipesforlife.controllers.ReviewBean;
import com.example.recipesforlife.models.Database_BaseDataSource;
import com.example.recipesforlife.models.Database_DatabaseConnection;
import com.example.recipesforlife.models.ApplicationModel_RecipeModel;
import com.example.recipesforlife.models.ApplicationModel_ReviewModel;
import com.example.recipesforlife.views.Account_SignUpSignInView;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Test case for recipe related database code
 * @author Kari
 *
 */

public class Recipe_TestCase extends AndroidTestCase{
	ApplicationModel_RecipeModel recipemodel;
	ApplicationModel_ReviewModel reviewmodel;
	Account_SignUpSignInView activity;
	Database_DatabaseConnection dbConnection;
	 RenamingDelegatingContext context;
	

	protected void setUp() throws Exception {
		super.setUp();
	    context 
	        = new RenamingDelegatingContext(getContext(), "test_");

		recipemodel = new ApplicationModel_RecipeModel(context);
		reviewmodel = new ApplicationModel_ReviewModel(context);
		copyDataBase();
	
		
		
	}
	
	private void copyDataBase() throws IOException 
	{
	    //Open your local db as the input stream
	    AssetManager mg = context.getAssets();
	    InputStream myInput = mg.open("databases/mockdv.sqlite");
	
	    // Path to the just created empty db
	    String outFileName = recipemodel.dbHelper.getWritableDatabase().getPath().toString();
	
	    //Open the empty db as the output stream
	    OutputStream myOutput = new FileOutputStream(outFileName);
	
	    //Transfer bytes from the inputfile to the outputfile
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = myInput.read(buffer)) > 0) 
	    {
	        myOutput.write(buffer, 0, length);
	    }
	    //Close the streams
	    myOutput.flush();
	    myOutput.close();
	    myInput.close();
	}
	

	protected void tearDown() throws Exception {
		super.tearDown();
		//dbConnection.close();
		
	}
	
	
	
public void testInsertRecipe()
	{
	
		ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		RecipeBean recipe = new RecipeBean();
		recipe.setName("Chicken Soup");
		recipe.setDesc("Soothes the cold");
		recipe.setServes("4");
		recipe.setPrep("1:00");
		recipe.setCooking("1:00");
		recipe.setAddedBy("addison");
		recipe.setRecipeBook("book1");
		recipe.setProgress("added");
		IngredientBean ingred = new IngredientBean();
		ingred.setName("stock");
		ingred.setAmount(1);
	    ingred.setValue("packet");
		ingred.setNote("");
	    PreperationBean prep = new PreperationBean();
	    prep.setPreperation("boil water");
	    prep.setPrepNum(1);
		prepList.add(prep);
		ingredList.add(ingred);
		
		
		Bitmap bitmap = ((BitmapDrawable) context.getResources().getDrawable(R.drawable.image_default_recipe)).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] byteArray = stream.toByteArray();
		ImageBean imgbean = new ImageBean();
		imgbean.setImage(byteArray);
		recipemodel.insertRecipe(recipe, false, ingredList, prepList, imgbean);
		
	   
		ArrayList<RecipeBean> exists = recipemodel.selectRecipesByUser("addison");
		Assert.assertEquals("Chicken Soup", exists.get(0).getName().toString());
		
		
	} 

	public void testEditRecipe()
	{
	
		ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		
		ImageBean imgbean = new ImageBean();
		
		RecipeBean recipe = new RecipeBean();
		recipe.setName("pizza");
		recipe.setDesc("good food");
		recipe.setUniqueid("uniqueidrecip1");
		recipe.setAddedBy("doe"); 
		recipe.setProgress("added");
		recipemodel.updateRecipe(recipe, prepList, ingredList, imgbean, false);	
		RecipeBean recipeSelect = new RecipeBean();
		recipeSelect = recipemodel.selectRecipe2(recipe.getUniqueid());
		Assert.assertEquals(recipeSelect.getDesc(), "good food");
		
		
	} 
	
	public void testDeleteRecipe()
	{
		ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		ImageBean imgbean = new ImageBean();
		
		RecipeBean recipe = new RecipeBean();
		recipe.setName("pizza");
		recipe.setDesc("good food");
		recipe.setUniqueid("uniqueidrecip1");
		recipe.setAddedBy("doe"); 
		recipe.setProgress("deleted"); 
		
		recipemodel.updateRecipe(recipe, prepList, ingredList, imgbean, false);	
		RecipeBean recipeSelect = new RecipeBean();
		recipeSelect = recipemodel.selectRecipe2(recipe.getUniqueid());
		Assert.assertEquals(recipeSelect.getName(), null);
	}
		

	
	public void testSelectRecipe()
	{
		ArrayList<RecipeBean> recipeSelect = new ArrayList<RecipeBean>();
		recipeSelect = recipemodel.selectRecipesByUser("doe");
		Assert.assertEquals(recipeSelect.get(0).getName(), "pizza");
		
	}
	
	public void testRecipeReviews()
	{
		ArrayList<ReviewBean> reviews = reviewmodel.selectReviews(1);
		Assert.assertEquals(reviews.get(0).getComment(), "good food");
	}
	
	public void testReviewInsert()
	{
		ReviewBean review = new ReviewBean();
		review.setComment("I like this recipe");
		review.setUser("doe");
		review.setRecipeid(1);
		reviewmodel.insertReview(review, false);
		ArrayList<ReviewBean> reviews = reviewmodel.selectReviews(1);
		Assert.assertEquals(reviews.get(1).getComment(), "I like this recipe");
				
	}
	
	public void testDoesUserHasAccess()
	{
		boolean access = recipemodel.doesUserHaveAccess("kimk@aol.co.uk", "cookbookuniqueid1");
		Assert.assertEquals(access, true);
	}
	
	public void testAllRecipesUserCanAccess()
	{
		ArrayList<RecipeBean> rblist = recipemodel.selectAllRecipesUserCanAccess("doe");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testIngredSelection()
	{
		recipemodel.open();
		int id = recipemodel.selectIngredient("cheese");
		Assert.assertEquals(id, 1);
		recipemodel.close();
	}
	
	public void testSelectIngredsByRecipeID()
	{
		ArrayList<IngredientBean> iblist = recipemodel.selectIngredients(1);
		Assert.assertEquals(iblist.get(0).getName(), "cheese");
	}
	
	public void testSelectRecipeByID()
	{
		String uniqueid = recipemodel.selectRecipeByID(1);
		Assert.assertEquals(uniqueid, "uniqueidrecip1");
	}
	
	public void testIngredExists()
	{
		recipemodel.open();
		boolean exists = recipemodel.ingredientExists("ingreddetsuniqueid1");
		Assert.assertEquals(exists, true);
		recipemodel.close();
	
	} 
	
	public void testSelectRecipeByUser()
	{
		ArrayList<RecipeBean> rblist = recipemodel.selectRecipesByUser("doe");
		Assert.assertEquals(rblist.get(0).getName(), "pizza");
	}
	
	public void testSelectPrepExists()
	{
		recipemodel.open();
		boolean exists = recipemodel.preperationExists("prepuniqueid1");
		Assert.assertEquals(exists, true);
		recipemodel.close();
	}
	
	public void testInsertPrepEdit()
	{
		RecipeBean recipe = new RecipeBean();
		recipe.setId(1);
		ArrayList<PreperationBean> prepList = new ArrayList<PreperationBean>();
		PreperationBean prep = new PreperationBean();
		prep.setPreperation("Add topping");
		prep.setPrepNum(2);
		prep.setProgress("added");
		prepList.add(prep);
		recipemodel.insertPrepFromEdit(false, prepList, recipe);
		
		ArrayList<PreperationBean> prepsList =  recipemodel.selectPreperation(1);
		Assert.assertEquals(prepsList.get(1).getPreperation(), "Add topping");
	}
	
	public void testInsertIngredEdit()
	{
		RecipeBean recipe = new RecipeBean();
		recipe.setId(1);
		ArrayList<IngredientBean> ingredList = new ArrayList<IngredientBean>();
		IngredientBean ingred = new IngredientBean();
		ingred.setName("passata");
		ingred.setAmount(500);
		ingred.setValue("ml");
		ingred.setProgress("added");
		ingredList.add(ingred);
		recipemodel.insertIngredFromEdit(false, ingredList, recipe);
		
		ArrayList<IngredientBean> ingredlist = recipemodel.selectIngredients(1);
		Assert.assertEquals(ingredlist.get(1).getName(), "passata");
	}

}
