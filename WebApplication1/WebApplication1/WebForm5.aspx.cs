using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.Web.UI.WebControls;
using System.Web.Script.Serialization;
using System.Data.SqlClient;
using System.IO;

namespace WebApplication1
{
	/**
	Class gets JSON to update recipes from app in the database
	**/
	public partial class WebForm5 : System.Web.UI.Page
	{
		SqlConnection connection = null;
		List<Recipe> recipe = new List<Recipe>();
		JavaScriptSerializer js = new JavaScriptSerializer();
		Int32 ingredId = 0;
		SqlTransaction transaction;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			//Set up connection
			connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			js.MaxJsonLength = Int32.MaxValue;
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Deserializes JSON into a list of recipe objects
					recipe = js.Deserialize<List<Recipe>>(jsonInput);
					for (int i = 0; i < recipe.Count(); i++)
					{
						connection.Open();
						transaction = connection.BeginTransaction();
						try
						{
							updateRecipe(i);
							transaction.Commit();
							connection.Close();
						}catch(Exception ex)
						{
							transaction.Rollback();
							Response.Write("Error ");
							Response.Write(ex);
							connection.Close();
						}
						
					}
				}catch(Exception ex)
				{
					Response.Write("Error ");
					Response.Write(ex);
				}
			}
		}
		
		/**
		* Update recipe in database based on json
		* int i - point in loop
		**/
		public void updateRecipe(int i)
		{
			
			//Create command to update recipe
			SqlCommand updateRecipe = new SqlCommand("UPDATE Recipe SET name=@name, description=@description, serves=@serves, prepTime=@prepTime, cookingTime=@cookingTime, changeTime=@changeTime, progress=@progress, difficulty=@difficulty, cusine=@cusine, dietary=@dietary, tips=@tips WHERE uniqueid=@uniqueid", connection, transaction);
			//Set up params for update recipe
			updateRecipe.Parameters.AddWithValue("@name", recipe[i].name);
			updateRecipe.Parameters.AddWithValue("@description", recipe[i].description);
			updateRecipe.Parameters.AddWithValue("@serves", recipe[i].serves);
			updateRecipe.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
			updateRecipe.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
			updateRecipe.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateRecipe.Parameters.AddWithValue("@uniqueid", recipe[i].uniqueid);
			updateRecipe.Parameters.AddWithValue("@progress", recipe[i].progress);
			updateRecipe.Parameters.AddWithValue("@difficulty", recipe[i].difficulty);
			updateRecipe.Parameters.AddWithValue("@dietary", recipe[i].dietary);
			updateRecipe.Parameters.AddWithValue("@tips", recipe[i].tips);
			updateRecipe.Parameters.AddWithValue("@cusine", recipe[i].cusine);
			
			//	connection.Open();
			try
			{
				//Execute update
				SqlDataReader recipeReader= updateRecipe.ExecuteReader();
				recipeReader.Close();
				
				updatePrep(i); // updates prep details that relate to recipe
				updateIngred(i); //update ingred details that relate to recipe		
				updateImage(i); //update image details that relate to recipe
			}
			catch (Exception ex)
			{

				Response.Write("Error At Recipe Update ");
				Response.Write(ex);
				throw ex;
			}
			//	connection.Close();
		}
		
		/**
		* Updates recipe prep info
		* int i - point in loop
		**/
		public void updatePrep(int i)
		{
			for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
			{
				SqlCommand updatePreperation = new SqlCommand("UPDATE Preperation SET instruction=@prep, instructionNum=@prepNums, changeTime=@changeTime, progress=@progress WHERE uniqueid=@uniqueid", connection, transaction);
				updatePreperation.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
				updatePreperation.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
				updatePreperation.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				updatePreperation.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
				updatePreperation.Parameters.AddWithValue("@progress", recipe[i].Preperation[3].prepprogress[y]);
				
				SqlDataReader rdrPrep = updatePreperation.ExecuteReader();
				rdrPrep.Close();
			}			  
		}
		
		/**
		* Updates recipe ingred details
		* int i -point in loop
		**/
		public void updateIngred(int i)
		{
			for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
			{
				
				
				selectIngredId(i , a);
				
				insertIngred(i , a);
				
				updateIngredDetails(i , a );
				
			}
		}
		
		/**
		* Updates ingredient details
		* int i - point in recipe loop
		* int a -  point in ingredient loop
		**/
		public void updateIngredDetails(int i,int  a)
		{
			SqlCommand updateDets = new SqlCommand("UPDATE IngredientDetails SET amount=@amount, note=@note, value=@value, changeTime=@changeTime, progress=@progress, ingredientId = @id WHERE uniqueid=@uniqueid", connection, transaction);
			updateDets.Parameters.AddWithValue("@id", ingredId);
			updateDets.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
			updateDets.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
			updateDets.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);
			updateDets.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateDets.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
			updateDets.Parameters.AddWithValue("@progress", recipe[i].Ingredient[5].ingredprogress[a]);
			try
			{

				SqlDataReader detsReader = updateDets.ExecuteReader();
				detsReader.Close();
			}
			catch(Exception ex)
			{
				Response.Write("Error Ingredient Details ");
				Response.Write(ex);
				throw ex;
			}
		}
		
		/**
		* Inserts ingredient if updated ingredient does not exist
		* int i - point in recipe loop
		* int a - point in ingredient loop
		**/
		public void insertIngred(int i , int a)
		{
			SqlCommand insertIngredient = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connection, transaction);
			insertIngredient.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
			insertIngredient.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertIngredient.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			try
			{
				if (ingredId == 0)
				{
					ingredId = (Int32)insertIngredient.ExecuteScalar();
				}
				
				
			}
			catch(Exception ex)
			{
				Response.Write("Error ");
				Response.Write(ex);
				throw ex;
			}
		}
		
		/**
		* Checks if ingredient exists by fetching id from database
		* int i - point in recipe loop
		* int a - point in ingredient loop
		**/
		public void selectIngredId(int i, int a)
		{
			SqlCommand selectIngId = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connection, transaction);
			selectIngId.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
			try
			{
				SqlDataReader selectIngIdReader = selectIngId.ExecuteReader();
				if(selectIngIdReader.HasRows)
				{
					while (selectIngIdReader.Read())
					{
						ingredId= selectIngIdReader.GetInt32(0);
					}
					
				}
				else
				{
					ingredId = 0;
				}
				selectIngIdReader.Close();

			}
			catch (Exception ex)
			{

				Response.Write("Error At Select Ingred ");
				Response.Write(ex);
				throw ex;
			}
		}
		
		/**
		* Updates recipe image information
		* int i - point in recipe loop
		*
		**/
		public void updateImage(int i)
		{
			
			SqlCommand updateImage = new SqlCommand("UPDATE Images SET image=@image, changeTime=@changeTime WHERE uniqueid=@uniqueid", connection, transaction);
			byte[] image  = null;
			if(recipe[i].image != "")
			{
				image = Convert.FromBase64String(recipe[i].image); //gets image as byte array
			}
			
			updateImage.Parameters.AddWithValue("@image", image);
			updateImage.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateImage.Parameters.AddWithValue("@uniqueid", recipe[i].imageid);
			
			SqlDataReader rdrImage = updateImage.ExecuteReader();
			rdrImage.Close();
			
		}
		
		//Stores JSON as recipe
		public class Recipe
		{
			public string name { get; set; }
			public string description { get; set; }
			public string serves { get; set; }
			public string prepTime { get; set; }
			public string cookingTime { get; set; }
			public string updateTime { get; set; }
			public string changeTime { get; set; }
			public string uniqueid { get; set; }
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

		//Stores prep details for recipe
		public class Preperation
		{
			public  List<String> prep { get; set; }
			public List<String> prepNums { get; set; }
			public List<String> uniqueid { get; set;  }
			public List<String> prepprogress { get; set; }
			//  public int prepNums { get; set; }
		}

		//Stores ingred details for recipe
		public class Ingredient
		{
			public List<String> Ingredients { get; set; }
			public List<String> Value { get; set; }
			public List<String> Amount { get; set; }
			public List<String> Notes { get; set; }
			public List<String> uniqueid {get;set;}
			public List<String> ingredprogress { get; set; }
		}
	}
}

