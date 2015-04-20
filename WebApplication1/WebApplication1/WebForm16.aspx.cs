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
	Class handles recipe details JSON from app
	**/
	public partial class WebForm16 : System.Web.UI.Page
	{
		
		string jsonInput = "";
		List<Recipe> recipe = new List<Recipe>();
		SqlConnection connn = null;
		SqlConnection connn2 = null;
		Int32 recipeId =0;
		SqlTransaction transaction;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Deserializes JSON into recipe
					JavaScriptSerializer js = new JavaScriptSerializer();
					js.MaxJsonLength = Int32.MaxValue;
					recipe = js.Deserialize<List<Recipe>>(jsonInput);
					
					for(int i = 0; i < recipe.Count(); i++)
					{
						
						insertAdditionalDets(i); //inserts additional recipe details into database
						
					}
				}catch(Exception ex)
				{
					Response.Write("Error Additional Details");
					Response.Write(ex);
				}
			}
		}
		
		
		/**
		* Inserts additional prep and recipe details which are added after the recipe was inserted 
		* int i - point in loop
		**/
		public void insertAdditionalDets(int i)
		{
			connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			connn2 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			connn.Open();
			connn2.Open();
			transaction = connn.BeginTransaction(); // Start transaction
			try
			{ 
				
				insertPrep(i);	
				insertIngredient(i);
				transaction.Commit(); //commit transaction if successful
			}
			catch(Exception e)
			{
				transaction.Rollback(); //rollback transaction if unsuccessful
				Response.Write("Error Insert Recipe Details");
				Response.Write(e);
			}
			connn.Close();
			connn2.Close();		
		}
		
		/**
		* Inserts prep details into database
		* i - point in loop
		**/
		public void insertPrep(int i)
		{
			for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
			{
			if(selectPrepUID(recipe[i].Preperation[2].uniqueid[y]) == false)
			{

				SqlCommand insertPrep = new SqlCommand(" INSERT INTO Preperation(instruction, instructionNum, updateTime, changeTime, uniqueid, progress) OUTPUT INSERTED.id VALUES (@prep, @prepNums, @updateTime, @changeTime, @uniqueid, @progress)", connn, transaction);
				insertPrep.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
				insertPrep.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
				insertPrep.Parameters.AddWithValue("@updateTime", recipe[i].Preperation[5].updateTime[y]);
				insertPrep.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				insertPrep.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
				insertPrep.Parameters.AddWithValue("@progress", recipe[i].Preperation[3].prepprogress[y]);
				try
				{
					Int32 prepId = (Int32)insertPrep.ExecuteScalar();
					SqlCommand insertPrepLink = new SqlCommand(" INSERT INTO PrepRecipe(recipeId, Preperationid, updateTime, changeTime)  VALUES (@recipeId, @Preperationid, @updateTime, @changeTime)", connn, transaction);
					insertPrepLink.Parameters.AddWithValue("@recipeId", selectRecipe(recipe[i].Preperation[4].preprecipeid[y]));
					insertPrepLink.Parameters.AddWithValue("@Preperationid", prepId);
					insertPrepLink.Parameters.AddWithValue("@updateTime", recipe[i].Preperation[5].updateTime[y]);
					insertPrepLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
					try
					{
						SqlDataReader rdr = insertPrepLink.ExecuteReader();
						rdr.Close();

					}
					catch (Exception ex)
					{
						Response.Write("Error Prep Details");
						Response.Write(ex);
						throw ex;
					}
				}
				catch (Exception ex)
				{
					Response.Write("Error Prep Details ");
					Response.Write(ex);
					throw ex;
				}	
				}
			}
		}
		
		/**
		* Inserts ingredient details into database if they dont already exist
		* i - point in loop
		**/
		public void insertIngredient(int i)
		{
			for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
			{
				
				Int32 ingredId = 0;
				SqlCommand selectIngredId = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connn, transaction);
				selectIngredId.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
				try
				{

					SqlDataReader rdr = selectIngredId.ExecuteReader();
					if(rdr.HasRows)
					{
						while (rdr.Read())
						{
							// read a row, for example:
							ingredId= rdr.GetInt32(0);
						}
					}
					rdr.Close();

				}
				catch (Exception ex)
				{

					Response.Write("Error ");
					Response.Write(ex);
					throw ex;
				}
				
				if(selectIngredUID(recipe[i].Ingredient[4].uniqueid[a]) == false)
				{
				
				SqlCommand insertIngred = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connn, transaction);
				insertIngred.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
				insertIngred.Parameters.AddWithValue("@updateTime", recipe[i].Ingredient[7].updateTime[a]);
				insertIngred.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				try
				{
					if (ingredId == 0)
					{
						ingredId = (Int32)insertIngred.ExecuteScalar();
					}
					
					SqlCommand insertIngredDet = new SqlCommand(" INSERT INTO IngredientDetails(ingredientId, amount, note, value, updateTime, changeTime, uniqueid, progress)  OUTPUT INSERTED.id  VALUES (@id, @amount, @note, @value, @updateTime, @changeTime, @uniqueid, @progress)", connn, transaction);
					insertIngredDet.Parameters.AddWithValue("@id", ingredId);
					insertIngredDet.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
					insertIngredDet.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
					insertIngredDet.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);
					insertIngredDet.Parameters.AddWithValue("@updateTime", recipe[i].Ingredient[7].updateTime[a]);
					insertIngredDet.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
					insertIngredDet.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
					insertIngredDet.Parameters.AddWithValue("@progress", recipe[i].Ingredient[5].ingredprogress[a]);
					try
					{

						Int32 ingredDetsId = (Int32)insertIngredDet.ExecuteScalar();
						SqlCommand insertIngredRecipeLink = new SqlCommand(" INSERT INTO RecipeIngredient(Recipeid, ingredientDetailsId, updateTime, changeTime)  VALUES (@recipeid,@detsid, @updateTime, @changeTime)", connn, transaction);
						insertIngredRecipeLink.Parameters.AddWithValue("@recipeid", selectRecipe(recipe[i].Ingredient[6].ingredrecipeid[a]));
						insertIngredRecipeLink.Parameters.AddWithValue("@detsid", ingredDetsId);
						insertIngredRecipeLink.Parameters.AddWithValue("@updateTime", recipe[i].Ingredient[7].updateTime[a]);
						insertIngredRecipeLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
						try
						{

							SqlDataReader rdr = insertIngredRecipeLink.ExecuteReader();
							rdr.Close();
							SqlCommand insertIngredDetsLink = new SqlCommand(" INSERT INTO IngredToIngredDetails(Ingredientdetailsid, ingredientid, updateTime, changeTime)  VALUES (@detsid,@ingredid, @updateTime, @changeTime)", connn, transaction);
							insertIngredDetsLink.Parameters.AddWithValue("@ingredid", ingredId);
							insertIngredDetsLink.Parameters.AddWithValue("@detsid", ingredDetsId);
							insertIngredDetsLink.Parameters.AddWithValue("@updateTime", recipe[i].Ingredient[7].updateTime[a]);
							insertIngredDetsLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
							try
							{

								SqlDataReader rdrs = insertIngredDetsLink.ExecuteReader();
								rdrs.Close();

							}
							catch (Exception ex)
							{

								Response.Write("Error Ingred Additional Dets ");
								Response.Write(ex);
								throw ex;
							}

						}
						catch (Exception ex)
						{

							Response.Write("Error Ingred Additional Dets ");
							Response.Write(ex);
							throw ex;
						}

					}
					catch (Exception ex)
					{

						Response.Write("Error Ingred Additional Dets ");
						Response.Write(ex);
						throw ex;
					}

				}
				catch (Exception ex)
				{

					Response.Write("Error Ingred Additional Dets ");
					Response.Write(ex);
					throw ex;
				}
			}
			}
			
		}
		
		/**
		* Selects recipe id from database based on recipe unique id
		* uniqueid - recipe uniqueid
		* I
		* return - Int32 - recipe id
		**/
		public Int32 selectRecipe(String uniqueid)
		{
			SqlConnection connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			SqlCommand selectrecipe = new SqlCommand(" SELECT id FROM Recipe WHERE uniqueid = @uniqueid", connection1);
			selectrecipe.Parameters.AddWithValue("@uniqueid", uniqueid);
			connection1.Open();
			Int32 id = 0;
			var recipeReader = selectrecipe.ExecuteReader();
			
			
			while (recipeReader.Read())
			{
				
				 id = (Int32)recipeReader["id"];
				
			}		
			connection1.Close();
			return id;
		}
		
		/**
		* Checks if the ingredient unique id already exists
		* uniqueid - ingred unique 
		* return - bool - states whether the ingred already exists or not
		**/
		public bool selectIngredUID(String uniqueid)
		{
			SqlConnection connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			SqlCommand selectingred = new SqlCommand(" SELECT id FROM IngredientDetails WHERE uniqueid = @uniqueid", connection1);
			selectingred.Parameters.AddWithValue("@uniqueid", uniqueid);
			connection1.Open();
			bool exists = false;
			var ingredReader = selectingred.ExecuteReader();
			
			
			while (ingredReader.Read())
			{
				
				 exists = true;
				
			}		
			connection1.Close();
			return exists;
		}
		
		/**
		* Check if prep unique id already exists in db
		* uniqueid - prep unique id
		* return - bool - states whether the ingred already exists or not
		**/
		public bool selectPrepUID(String uniqueid)
		{
			SqlConnection connection1 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			SqlCommand selectprep = new SqlCommand(" SELECT id FROM Preperation WHERE uniqueid = @uniqueid", connection1);
			selectprep.Parameters.AddWithValue("@uniqueid", uniqueid);
			connection1.Open();
			bool exists = false;
			var prepReader = selectprep.ExecuteReader();
			
			
			while (prepReader.Read())
			{
				
				 exists = true;
				
			}		
			connection1.Close();
			return exists;
		}
		
	
		
		
		
		
		/**
		Class stores recipe details to send to json
		**/
		public class Recipe
		{
			public String changeTime { get; set; }
			public List<Preperation> Preperation { get; set; }
			public List<Ingredient> Ingredient { get; set; }
		}

		/**
		Class stores prep details to send to json
		**/
		public class Preperation
		{
			public  List<String> prep { get; set; }
			public List<String> prepNums { get; set; }
			public List<String> uniqueid { get; set;  }
			public List<String> prepprogress { get; set; }
			public List<String> preprecipeid { get; set; }
			public List<String> updateTime {get; set; }
			
		}

		/**
		Class stores ingredient details to send to json
		**/
		public class Ingredient
		{
			public List<String> Ingredients { get; set; }
			public List<String> Value { get; set; }
			public List<String> Amount { get; set; }
			public List<String> Notes { get; set; }
			public List<String> uniqueid {get;set;}
			public List<String> ingredprogress {get;set;}
			public List<String> ingredrecipeid { get; set; }
			public List<String> updateTime {get; set; }
			
		}
	}
	
	
}