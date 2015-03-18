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
					connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					selectRecipe();
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
			var recipeReader = selectrecipe.ExecuteReader();
			recipes.Recipe = new List<Recipe>();
			
			while (recipeReader.Read())
			{
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
				selectPrep(recipe.Preperation, recipe);
				selectIngredient(recipe.Ingredient, recipe);
				recipe = selectBook(recipe);	
				recipe = selectImage(recipe);
				recipes.Recipe.Add(recipe); 
			}		
			connection1.Close();
			string json = js.Serialize(recipes);
			Response.Write(json);
		}
		
		/**
		* Select prep info from database
		**/
		public void selectPrep(List<Preperation> recipeprep, Recipe recipe)
		{
			SqlCommand selectPreperation = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = @recipe;", connection1);
			selectPreperation.Parameters.AddWithValue("@recipe", recipe.id);
			var selectPreperationReader = selectPreperation.ExecuteReader();
			while (selectPreperationReader.Read())
			{
				Preperation preps = new Preperation();
				preps.prep = new List<string>();
				preps.uniqueid = new List<string>();
				preps.prepNums = new List<Int32>();
				preps.prep.Add((string)selectPreperationReader["instruction"]);
				preps.uniqueid.Add((string)selectPreperationReader["uniqueid"]);
				preps.prepNums.Add((Int32)selectPreperationReader["instructionNum"]);
				recipeprep.Add(preps);
			}
		}
		
		/**
		* Select ingredient info from database
		**/
		public void selectIngredient(List<Ingredient> recipeingred, Recipe recipe)
		{
			SqlCommand selectIngred = new SqlCommand("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = @recipe;", connection1);
			selectIngred.Parameters.AddWithValue("@recipe", recipe.id);
			var selectIngredReader = selectIngred.ExecuteReader();
			while (selectIngredReader.Read())
			{
				Ingredient ingreds = new Ingredient();
				ingreds.Ingredients = new List<string>();
				ingreds.Value = new List<string>();
				ingreds.Amount = new List<Int32>();
				ingreds.Notes = new List<string>();
				ingreds.uniqueid = new List<string>();
				ingreds.Ingredients.Add((string)selectIngredReader["name"]);
				ingreds.Amount.Add((Int32)selectIngredReader["amount"]);
				ingreds.Value.Add((string)selectIngredReader["value"]);
				ingreds.Notes.Add((string)selectIngredReader["note"]);
				ingreds.uniqueid.Add((string)selectIngredReader["uniqueid"]);			
				recipeingred.Add(ingreds); 			
			}
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
			
		}

		//Stores recipe ingred details
		public class Ingredient
		{
			public List<String> Ingredients { get; set; }
			public List<String> Value { get; set; }
			public List<Int32> Amount { get; set; }
			public List<String> Notes { get; set; }
			public List<String> uniqueid { get; set; }
		}
	}
}

