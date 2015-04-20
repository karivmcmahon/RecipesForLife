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
	* Sends additional recipe details JSON to app based on date
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
					select(); //select additional prep and ingred details
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}

		}
		
		/**
		* Select recipe additional info from database
		**/
		public void select()
		{
			
			connection1.Open();
			Recipes recipes = new Recipes();
			recipes.Recipe = new List<Recipe>();
			Recipe recipe = new Recipe();
			recipe.Preperation = new List<Preperation>();
			recipe.Ingredient = new List<Ingredient>();
			recipe.Preperation = selectPrep(recipe.Preperation, recipe);
			recipe.Ingredient =	selectIngredient(recipe.Ingredient, recipe);
			recipes.Recipe.Add(recipe); 		
			connection1.Close();
			string json = js.Serialize(recipes);
			Response.Write(json);
		}
		
		/**
		* Select recipes unique id based on id in database
		* int id - row in database
		* return - String - recipe uniqueid
		**/
		public String selectRecipe(int id)
		{
			String uniqueid = "";
			SqlCommand selectrecipe = new SqlCommand(" SELECT uniqueid FROM Recipe WHERE id=@id", connection1);
			selectrecipe.Parameters.AddWithValue("@id", id);
			var recipeReader = selectrecipe.ExecuteReader();
			while (recipeReader.Read())
			{
				uniqueid = (string)recipeReader["uniqueid"];
			}
			return uniqueid;
		}
		
		/**
		* Select prep info from database
		**/
		public List<Preperation> selectPrep(List<Preperation> recipeprep, Recipe recipe)
		{
			SqlCommand selectPreperation = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid, Preperation.progress, PrepRecipe.recipeId FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.updateTime > @lastUpdated", connection1);
			selectPreperation.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			var selectPreperationReader = selectPreperation.ExecuteReader();
			while (selectPreperationReader.Read())
			{
				Preperation preps = new Preperation();
				preps.prep = new List<string>();
				preps.uniqueid = new List<string>();
				preps.prepNums = new List<Int32>();
				preps.prepprogress = new List<string>();
				preps.preprecipe = new List<string>();
				preps.prep.Add((string)selectPreperationReader["instruction"]);
				preps.uniqueid.Add((string)selectPreperationReader["uniqueid"]);
				preps.prepNums.Add((Int32)selectPreperationReader["instructionNum"]);
				preps.prepprogress.Add((string)selectPreperationReader["progress"]);
				preps.preprecipe.Add(selectRecipe((Int32)selectPreperationReader["recipeId"]));
				recipeprep.Add(preps);
			}
			return recipeprep;
		}
		
		/**
		* Select ingredient info from database
		**/
		public List<Ingredient> selectIngredient(List<Ingredient> recipeingred, Recipe recipe)
		{
			SqlCommand selectIngred = new SqlCommand("SELECT IngredientDetails.uniqueid AS uid, * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.updateTime > @lastUpdated;", connection1);
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
				ingreds.ingredrecipe = new List<string>();
				ingreds.Ingredients.Add((string)selectIngredReader["name"]);
				ingreds.Amount.Add((Int32)selectIngredReader["amount"]);
				ingreds.Value.Add((string)selectIngredReader["value"]);
				ingreds.Notes.Add((string)selectIngredReader["note"]);
				ingreds.uniqueid.Add((string)selectIngredReader["uid"]);	
				ingreds.ingredprogress.Add((string)selectIngredReader["progress"]);			
				ingreds.ingredrecipe.Add(selectRecipe((Int32)selectIngredReader["Recipeid"]));
				recipeingred.Add(ingreds); 			
			}
			return recipeingred;
		}
		
		
		/**
		* Class stores date sent from JSON
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
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
			
		}

		/**
		* List of recipe info used to create  a json array
		**/
		public class Recipes
		{
			public List<Recipe> Recipe { get; set;} 

		}

		/**
		* Stores recipe prep details
		**/
		public class Preperation
		{
			public List<String> prep { get; set; }
			public List<Int32> prepNums { get; set; }
			public List<String> uniqueid { get; set; }
			public List<String> prepprogress { get; set; }
			public List<String> preprecipe { get; set; }
			
		}

		/**
		* Stores recipe ingred details
		**/
		public class Ingredient
		{
			public List<String> Ingredients { get; set; }
			public List<String> Value { get; set; }
			public List<Int32> Amount { get; set; }
			public List<String> Notes { get; set; }
			public List<String> uniqueid { get; set; }
			public List<String> ingredprogress { get; set; }
			public List<String> ingredrecipe { get; set; }
		}
	}
}

