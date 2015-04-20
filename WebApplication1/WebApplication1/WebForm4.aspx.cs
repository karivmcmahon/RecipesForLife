using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;
using System.Configuration;
using System.Data;

namespace WebApplication1
{
	/**
	* Sends recipe JSON to app to insert into database based on date
	**/
	public partial class WebForm4 : System.Web.UI.Page
	{
		int recipeID = 0;
		string jsonInput = "";
		SqlConnection connection1 = null;
		string lastUpdated = "";
		JavaScriptSerializer js = new JavaScriptSerializer();
		
		
		protected void Page_Load(object sender, EventArgs e)
		{
			js.MaxJsonLength = Int32.MaxValue;

			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Gets last update
					var time = js.Deserialize<List<RecipeDate>>(jsonInput);
					lastUpdated = time[0].updateTime;
					
					//Sets up connection
					connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					
					selectRecipe(); //selects recipe to build JSON
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}

		}
		
		/**
		* Select recipe info from database
		**/
		public void selectRecipe()
		{
			SqlCommand selectrecipe = new SqlCommand(" SELECT * FROM Recipe WHERE updateTime > @lastUpdated", connection1);
			selectrecipe.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			connection1.Open();
			Recipes recipes = new Recipes();
			SqlDataReader recipeReader = selectrecipe.ExecuteReader();
			recipes.Recipe = new List<Recipe>();
			
			while (recipeReader.Read())
			{
				//Uses reader to build recipe object which will be used to create JSON
				Recipe recipe = new Recipe();			
				recipe.Preperation = new List<Preperation>();
				recipe.Ingredient = new List<Ingredient>();		
				recipe.id = (Int32)recipeReader["id"];
				recipeID = (Int32)recipeReader["id"];
				recipe.name = (string)recipeReader["name"];
				recipe.description = (string)recipeReader["description"];
				recipe.prepTime = (string)recipeReader["prepTime"];
				recipe.cookingTime = (string)recipeReader["cookingTime"];
				recipe.addedBy = (string)recipeReader["addedBy"];
				recipe.uniqueid = (string)recipeReader["uniqueid"];
				recipe.progress = (string)recipeReader["progress"];
				recipe.serves = (Int32)recipeReader["serves"];
				
				//Checks for nulls
				recipe = checkForNulls(recipe, recipeReader);
				
				recipe.Preperation = selectPrep(recipe.Preperation, recipe); //calls select prep to build preperation aspect of recipe for JSON
				recipe.Ingredient = selectIngredient(recipe.Ingredient, recipe); //calls select ingred to build ingred aspect of recipe for JSON
				recipe = selectBook(recipe);	 //Calls select book to build book aspect of recipe for JSON
				recipe = selectImage(recipe); //Calls select image to build image aspect of recipe for JSON
				recipes.Recipe.Add(recipe);  //Add these details to a list which will be serialized as a JSON
			}		
			connection1.Close();
			string json = js.Serialize(recipes); //serializes json and writes for app to read
			Response.Write(json);
		}
		
		/**
		* Checks for nulls in db values and sets them to empty quotes
		* recipe - stores recipe data
		* recipereader - stores info from query
		*
		* return - recipe - with updated recipe info
		**/
		public Recipe checkForNulls(Recipe recipe, SqlDataReader recipeReader)
		{
			if( recipeReader["difficulty"] == DBNull.Value)
			{
				recipe.difficulty = "";
			}
			else
			{
				recipe.difficulty = (string)recipeReader["difficulty"];
			}
			if( recipeReader["dietary"] == DBNull.Value)
			{
				recipe.dietary = "";
			}
			else
			{
				recipe.dietary = (string)recipeReader["dietary"];
			}
			if( recipeReader["tips"] == DBNull.Value)
			{
				recipe.tips = "";
			}
			else
			{
				recipe.tips = (string)recipeReader["tips"];
			}
			if( recipeReader["cusine"] == DBNull.Value)
			{
				recipe.cusine = "";
			}
			else
			{
				recipe.cusine = (string)recipeReader["cusine"];
			}
			return recipe;
		}
		
		/**
		* Select prep info from database
		* recipeprep - List of prep info for recipe
		* recipe - info about recipe
		*
		* return - recipeprep - Update list of prep info for recipe
		**/
		public List<Preperation> selectPrep(List<Preperation> recipeprep, Recipe recipe)
		{
			SqlCommand selectPreperation = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid, Preperation.progress FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = @recipe;", connection1);
			selectPreperation.Parameters.AddWithValue("@recipe", recipe.id);
			var selectPreperationReader = selectPreperation.ExecuteReader();
			while (selectPreperationReader.Read())
			{
				//Builds preperation details for JSON based on reader
				Preperation preps = new Preperation();
				preps.prep = new List<string>();
				preps.uniqueid = new List<string>();
				preps.prepNums = new List<Int32>();
				preps.prepprogress = new List<string>();
				preps.prep.Add((string)selectPreperationReader["instruction"]);
				preps.uniqueid.Add((string)selectPreperationReader["uniqueid"]);
				preps.prepNums.Add((Int32)selectPreperationReader["instructionNum"]);
				preps.prepprogress.Add((string)selectPreperationReader["progress"]);
				recipeprep.Add(preps);
			}
			return recipeprep;
		}
		
		/**
		* Select ingredient info from database
		* recipeingred - List of ingred info for recipe
		* recipe - recipe ingred info
		*
		* return - List<Ingredient> - List of ingred info for recipe
		**/
		public List<Ingredient> selectIngredient(List<Ingredient> recipeingred, Recipe recipe)
		{
			SqlCommand selectIngred = new SqlCommand("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = @recipe;", connection1);
			selectIngred.Parameters.AddWithValue("@recipe", recipe.id);
			var selectIngredReader = selectIngred.ExecuteReader();
			while (selectIngredReader.Read())
			{
				//Builds ingredient details for JSON based on reader
				Ingredient ingreds = new Ingredient();
				ingreds.Ingredients = new List<string>();
				ingreds.Value = new List<string>();
				ingreds.Amount = new List<Int32>();
				ingreds.Notes = new List<string>();
				ingreds.uniqueid = new List<string>();
				ingreds.ingredprogress = new List<string>();
				ingreds.Ingredients.Add((string)selectIngredReader["name"]);
				ingreds.Amount.Add((Int32)selectIngredReader["amount"]);
				ingreds.Value.Add((string)selectIngredReader["value"]);
				ingreds.Notes.Add((string)selectIngredReader["note"]);
				ingreds.uniqueid.Add((string)selectIngredReader["uniqueid"]);	
				ingreds.ingredprogress.Add((string)selectIngredReader["progress"]);				
				recipeingred.Add(ingreds); 			
			}
			return recipeingred;
		}
		
		/**
		* Select cookbook recipe info from database
		*
		* recipe - stores recipe info 
		* return - recipe - updated recipe info
		**/
		public Recipe selectBook(Recipe recipe)
		{
			SqlCommand selectCookbook = new SqlCommand("SELECT uniqueid FROM Cookbook INNER JOIN CookbookRecipe ON Cookbook.id=CookbookRecipe.Cookbookid WHERE CookbookRecipe.Recipeid=@id", connection1);
			selectCookbook.Parameters.AddWithValue("@id", recipeID);
			var selectCookbookReader = selectCookbook.ExecuteReader();
			recipe.cookingid = "";
			while(selectCookbookReader.Read())
			{
				//Gets the unique id of the cookbook the recipe is contained in
				recipe.cookingid = (string)selectCookbookReader["uniqueid"];
			}
			return recipe;
		}
		
		/**
		* Selects image related to recipe
		* recipe - stores recipe info
		* return - recipe - updated recipe info
		**/
		public Recipe selectImage(Recipe recipe)
		{
			SqlCommand selectImage = new SqlCommand("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON Images.imageid=RecipeImages.imageid WHERE RecipeImages.Recipeid=@id", connection1);
			selectImage.Parameters.AddWithValue("@id", recipeID);
			var selectImageReader = selectImage.ExecuteReader();
			recipe.image = "";
			recipe.imageid = "";
			while(selectImageReader.Read())
			{
				//Gets the image in a base 64 string and its uniqueid  so when know how to insert the image on the application side
				byte[] image = (byte[])selectImageReader["image"];
				recipe.image = Convert.ToBase64String(image);
				recipe.imageid = (string)selectImageReader["uniqueid"];
			}
			return recipe;
		}
		
		/**
		* Class retrieves JSON from app which stores the time it was last updated
		*
		**/
		public class RecipeDate
		{
			public string updateTime { get; set; }
		}

		/**
		* Class used to create JSON
		**/
		public class Recipe
		{
			public string name { get; set; }
			public string description { get; set; }
			public int serves { get; set; }
			public string prepTime { get; set; }
			public string cookingTime { get; set; }
			public string addedBy { get; set; }
			public Int32 id { get; set; }
			public string uniqueid { get; set; }
			public string cookingid { get; set; }
			public string image { get; set; }
			public string imageid { get; set; }
			public string progress { get; set; }
			public string difficulty { get; set; }
			public string dietary { get; set; }
			public string tips { get; set; }
			public string cusine { get; set; }
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
			
		}

		/**
		* List of recipes - creates a json array
		*
		**/
		public class Recipes
		{
			public List<Recipe> Recipe { get; set;} 

		}

		/**
		* Stores recipe prep details
		*
		**/
		public class Preperation
		{
			public List<String> prep { get; set; }
			public List<Int32> prepNums { get; set; }
			public List<String> uniqueid { get; set; }
			public List<String> prepprogress { get; set; }
			
		}

		/**
		* Stores recipe ingred details
		*
		**/
		public class Ingredient
		{
			public List<String> Ingredients { get; set; }
			public List<String> Value { get; set; }
			public List<Int32> Amount { get; set; }
			public List<String> Notes { get; set; }
			public List<String> uniqueid { get; set; }
			public List<String> ingredprogress { get; set; }
		}
	}
}

