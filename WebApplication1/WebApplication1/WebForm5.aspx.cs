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
		protected void Page_Load(object sender, EventArgs e)
		{
			string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					recipe = js.Deserialize<List<Recipe>>(jsonInput);
					for (int i = 0; i < recipe.Count(); i++)
					{
						updateRecipe(i);
						
					}
				}catch(Exception ex)
				{
					Response.Write("Error ");
					Response.Write(ex);
				}
			}
		}
		//Update recipe in database based on json
		public void updateRecipe(int i)
		{
			connection = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			SqlCommand updateRecipe = new SqlCommand("UPDATE Recipe SET name=@name, description=@description, serves=@serves, prepTime=@prepTime, cookingTime=@cookingTime, changeTime=@changeTime, progress=@progress WHERE uniqueid=@uniqueid", connection);
			updateRecipe.Parameters.AddWithValue("@name", recipe[i].name);
			updateRecipe.Parameters.AddWithValue("@description", recipe[i].description);
			updateRecipe.Parameters.AddWithValue("@serves", recipe[i].serves);
			updateRecipe.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
			updateRecipe.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
			updateRecipe.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateRecipe.Parameters.AddWithValue("@uniqueid", recipe[i].uniqueid);
			updateRecipe.Parameters.AddWithValue("@progress", recipe[i].progress);

			connection.Open();
			try
			{

				SqlDataReader recipeReader= updateRecipe.ExecuteReader();
				recipeReader.Close();
				updatePrep(i);
				updateIngred(i);		
				updateImage(i);
			}
			catch (Exception ex)
			{

				Response.Write("Error ");
				Response.Write(ex);
			}
			connection.Close();
		}
		
		//Updates recipe prep info
		public void updatePrep(int i)
		{
			for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
			{
				SqlCommand updatePreperation = new SqlCommand("UPDATE Preperation SET instruction=@prep, instructionNum=@prepNums, changeTime=@changeTime WHERE uniqueid=@uniqueid", connection);
				updatePreperation.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
				updatePreperation.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
				updatePreperation.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				updatePreperation.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
				
				SqlDataReader rdrPrep = updatePreperation.ExecuteReader();
				rdrPrep.Close();
			}			  
		}
		
		//Updates recipe ingred details
		public void updateIngred(int i)
		{
			for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
			{
				
				
				selectIngredId(i , a);
				
				insertIngred(i , a);
				
				updateIngredDetails(i , a );
				
			}
		}
		
		//Updates ingredient details
		public void updateIngredDetails(int i,int  a)
		{
			SqlCommand updateDets = new SqlCommand("UPDATE IngredientDetails SET amount=@amount, note=@note, value=@value, changeTime=@changeTime WHERE uniqueid=@uniqueid", connection);
			updateDets.Parameters.AddWithValue("@id", ingredId);
			updateDets.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
			updateDets.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
			updateDets.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);

			updateDets.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateDets.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
			try
			{

				SqlDataReader detsReader = updateDets.ExecuteReader();
				detsReader.Close();
			}
			catch(Exception ex)
			{
				Response.Write("Error ");
				Response.Write(ex);
			}
		}
		
		//Inserts ingredient if updated ingredient does not exist
		public void insertIngred(int i , int a)
		{
			SqlCommand insertIngredient = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connection);
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
			}
		}
		
		//Checks if ingredient exists by fetching id from database
		public void selectIngredId(int i, int a)
		{
			SqlCommand selectIngId = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connection);
			selectIngId.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
			try
			{

				SqlDataReader selectIngIdReader = selectIngId.ExecuteReader();
				if(selectIngIdReader.HasRows)
				{
					while (selectIngIdReader.Read())
					{
						// read a row, for example:
						ingredId= selectIngIdReader.GetInt32(0);
					}
				}
				selectIngIdReader.Close();

			}
			catch (Exception ex)
			{

				Response.Write("Error ");
				Response.Write(ex);
			}
		}
		
		//Updates recipe prep info
		public void updateImage(int i)
		{
			
			SqlCommand updateImage = new SqlCommand("UPDATE Images SET image=@image, changeTime=@changeTime WHERE uniqueid=@uniqueid", connection);
			byte[] image  = null;
			if(recipe[i].image != "")
			{
				image = Convert.FromBase64String(recipe[i].image);
			}
			
			updateImage.Parameters.AddWithValue("@image", image);
			updateImage.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			updateImage.Parameters.AddWithValue("@uniqueid", recipe[i].imageid);
			
			SqlDataReader rdrPrep = updateImage.ExecuteReader();
			rdrPrep.Close();
			
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
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
		}

		//Stores prep details for recipe
		public class Preperation
		{
			public  List<String> prep { get; set; }
			public List<String> prepNums { get; set; }
			public List<String> uniqueid { get; set;  }
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
		}
	}
}

