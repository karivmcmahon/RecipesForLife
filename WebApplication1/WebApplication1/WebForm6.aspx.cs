using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;

namespace WebApplication1
{
	/**
	Class creates JSON to send to app with recipes needing updated based on date
	**/
	public partial class WebForm6 : System.Web.UI.Page
	{
		JavaScriptSerializer js = new JavaScriptSerializer();
		string lastUpdated = "";
		SqlConnection connection = null;
		protected void Page_Load(object sender, EventArgs e)
		{
			js.MaxJsonLength = Int32.MaxValue;
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{	
				try
				{
					var time = js.Deserialize<List<Date>>(jsonInput);
					lastUpdated = time[0].changeTime;
					connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
					selectRecipe();
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		
		//Selects recipe needing updated
		public void selectRecipe()
		{
			SqlCommand select = new SqlCommand(" SELECT * FROM Recipe WHERE changeTime > @lastUpdated", connection);
			select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
			connection.Open();
			var reader = select.ExecuteReader();
			Recipes recipes = new Recipes();
			recipes.Recipe = new List<Recipe>();
			
			while (reader.Read())
			{

				Recipe recipe = new Recipe();
				recipe.Preperation = new List<Preperation>();
				recipe.Ingredient = new List<Ingredient>();
				recipe.id = (Int32)reader["id"];
				recipe.name = (string)reader["name"];
				recipe.description = (string)reader["description"];
				recipe.prepTime = (string)reader["prepTime"];
				recipe.cookingTime = (string)reader["cookingTime"];
				recipe.addedBy = (string)reader["addedBy"];
				recipe.uniqueid = (string)reader["uniqueid"];
				recipe.serves = (Int32)reader["serves"];
				recipe.progress = (string)reader["progress"];
				if( reader["difficulty"] == DBNull.Value)
				{
					recipe.difficulty = "";
				}
				else
				{
					recipe.difficulty = (string)reader["difficulty"];
				}
				if( reader["dietary"] == DBNull.Value)
				{
					recipe.dietary = "";
				}
				else
				{
					recipe.dietary = (string)reader["dietary"];
				}
				if( reader["tips"] == DBNull.Value)
				{
					recipe.tips = "";
				}
				else
				{
					recipe.tips = (string)reader["tips"];
				}
				if( reader["cusine"] == DBNull.Value)
				{
					recipe.cusine = "";
				}
				else
				{
					recipe.cusine = (string)reader["cusine"];
				}
				recipes.Recipe.Add(recipe); 
				selectPrep(recipe, recipe.Preperation);
				selectIngred(recipe, recipe.Ingredient);
				
				recipe = selectImages(recipe);
			}
			connection.Close();
			string json = js.Serialize(recipes);
			Response.Write(json);
		}
		
		//Selects related preperation to recipe
		public void selectPrep(Recipe recipe, List<Preperation> preprecipe)
		{		
			SqlCommand selectprep = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = @recipe;", connection);
			selectprep.Parameters.AddWithValue("@recipe", recipe.id);
			var selectprepReader = selectprep.ExecuteReader();
			// int i = 0;
			while (selectprepReader.Read())
			{
				//Preperation preps = new Preperation();
				Preperation preps = new Preperation();
				preps.prep = new List<string>();
				preps.uniqueid = new List<string>();
				preps.prepNums = new List<Int32>();
				preps.prep.Add((string)selectprepReader["instruction"]);
				preps.uniqueid.Add((string)selectprepReader["uniqueid"]);
				preps.prepNums.Add((Int32)selectprepReader["instructionNum"]);
				preprecipe.Add(preps);			
			}
		}
		
		//selects related ingredients to recipe
		public void selectIngred(Recipe recipe, List<Ingredient> ingredrecipe)
		{
			SqlCommand selectingred = new SqlCommand("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = @recipe;", connection);
			selectingred.Parameters.AddWithValue("@recipe", recipe.id);
			var selectingredReader = selectingred.ExecuteReader();
			while (selectingredReader.Read())
			{
				Ingredient ingreds = new Ingredient();
				ingreds.Ingredients = new List<string>();
				ingreds.Value = new List<string>();
				ingreds.Amount = new List<Int32>();
				ingreds.Notes = new List<string>();
				ingreds.uniqueid = new List<string>();
				ingreds.Ingredients.Add((string)selectingredReader["name"]);
				ingreds.Amount.Add((Int32)selectingredReader["amount"]);
				ingreds.Value.Add((string)selectingredReader["value"]);
				ingreds.Notes.Add((string)selectingredReader["note"]);
				ingreds.uniqueid.Add((string)selectingredReader["uniqueid"]);
				ingredrecipe.Add(ingreds); 
			}
		}
		
		public Recipe selectImages(Recipe recipe)
		{
			SqlCommand selectimage = new SqlCommand("SELECT image, uniqueid FROM Images INNER JOIN RecipeImages ON RecipeImages.imageid=Images.imageid WHERE RecipeImages.Recipeid = @recipe;", connection);
			selectimage.Parameters.AddWithValue("@recipe", recipe.id);
			var selectimageReader = selectimage.ExecuteReader();
			while (selectimageReader.Read())
			{
				recipe.imageid = (string)selectimageReader["uniqueid"];
				byte[] image = (byte[])selectimageReader["image"];
				recipe.image = Convert.ToBase64String(image);
			}
			return recipe;
		}
		
		//Stores date sent to webpage
		public class Date
		{
			public string changeTime { get; set; }
		}

		//Class to create recipe json
		public class Recipe
		{
			public string name { get; set; }
			public string description { get; set; }
			public int serves { get; set; }
			public string prepTime { get; set; }
			public string cookingTime { get; set; }
			public string addedBy { get; set; }
			public string image { get; set; }
			public string imageid { get; set; }
			public Int32 id { get; set; }
			public string uniqueid { get; set; }
			public string progress { get; set; }
			public string difficulty { get; set; }
			public string dietary { get; set; }
			public string tips { get; set; }
			public string cusine { get; set; }
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
			//  public List<Ingredient> Ingredient { get; set; }
		}
		//Creates list of recipes - json array
		public class Recipes
		{
			public List<Recipe> Recipe { get; set;} 

		}
		//Class to store preperation dets
		public class Preperation
		{
			public List<String> prep { get; set; }
			public List<Int32> prepNums { get; set; }
			public List<String> uniqueid { get; set; }
			
		}
		//Class to store ingredient dets
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

