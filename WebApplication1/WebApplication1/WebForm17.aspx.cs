using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;
using System.Configuration;

namespace WebApplication1
{
	/**
	* Sends recipe JSON to app based on date
	**/
	public partial class WebForm17 : System.Web.UI.Page
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
					connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					select();
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
		public void select()
		{
			
			connection1.Open();
			Recipes recipes = new Recipes();
			recipes.Recipe = new List<Recipe>();
			Recipe recipe = new Recipe();
			recipe.Preperation = new List<Preperation>();
			recipe.Ingredient - new List<Ingredient();
			recipe.Preperation = selectPrep(recipe.Preperation, recipe);
			recipe.Ingredient =	selectIngredient(recipe.Ingredient, recipe);
			recipes.Recipe.Add(recipe); 		
			connection1.Close();
			string json = js.Serialize(recipes);
			Response.Write(json);
		}
		
		/**
		* Select prep info from database
		**/
		public List<Preperation> selectPrep(List<Preperation> recipeprep, Recipe recipe)
		{
			SqlCommand selectPreperation = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid, Preperation.progress FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE updateTime > @lastUpdated", connection1);
			selectPreperation.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			var selectPreperationReader = selectPreperation.ExecuteReader();
			while (selectPreperationReader.Read())
			{
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
		**/
		public List<Ingredient> selectIngredient(List<Ingredient> recipeingred, Recipe recipe)
		{
			SqlCommand selectIngred = new SqlCommand("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE updateTime > @lastUpdated;", connection1);
			selectIngred.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			var selectIngredReader = selectIngred.ExecuteReader();
			while (selectIngredReader.Read())
			{
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
		**/
		public Recipe selectBook(Recipe recipe)
		{
			SqlCommand selectCookbook = new SqlCommand("SELECT uniqueid FROM Cookbook INNER JOIN CookbookRecipe ON Cookbook.id=CookbookRecipe.Cookbookid WHERE CookbookRecipe.Recipeid=@id", connection1);
			selectCookbook.Parameters.AddWithValue("@id", recipeID);
			var selectCookbookReader = selectCookbook.ExecuteReader();
			recipe.cookingid = "";
			while(selectCookbookReader.Read())
			{
				recipe.cookingid = (string)selectCookbookReader["uniqueid"];
			}
			return recipe;
		}
		
		public Recipe selectImage(Recipe recipe)
		{
			SqlCommand selectImage = new SqlCommand("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON Images.imageid=RecipeImages.imageid WHERE RecipeImages.Recipeid=@id", connection1);
			selectImage.Parameters.AddWithValue("@id", recipeID);
			var selectImageReader = selectImage.ExecuteReader();
			recipe.image = "";
			recipe.imageid = "";
			while(selectImageReader.Read())
			{
				byte[] image = (byte[])selectImageReader["image"];
				recipe.image = Convert.ToBase64String(image);
				recipe.imageid = (string)selectImageReader["uniqueid"];
			}
			return recipe;
		}
		
		public class RecipeDate
		{
			public string updateTime { get; set; }
		}

		/**
		* Class used to create JSON
		**/
		public class Recipe
		{
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
			
		}

		//List of recipes - creates a json array
		public class Recipes
		{
			public List<Recipe> Recipe { get; set;} 

		}

		//Stores recipe prep details
		public class Preperation
		{
			public List<String> prep { get; set; }
			public List<Int32> prepNums { get; set; }
			public List<String> uniqueid { get; set; }
			public List<String> prepprogress { get; set; }
			
		}

		//Stores recipe ingred details
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

