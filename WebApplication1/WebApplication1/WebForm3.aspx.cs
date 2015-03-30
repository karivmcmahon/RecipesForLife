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
	Class handles recipe JSON from app
	**/
	public partial class WebForm3 : System.Web.UI.Page
	{
		
		string jsonInput = "";
		List<Recipe> recipe = new List<Recipe>();
		SqlConnection connn = null;
		SqlConnection connn2 = null;
		Int32 recipeId =0;
		
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
						
						insertRecipe(i);
						
					}
				}catch(Exception ex)
				{
					Response.Write("Error");
					Response.Write(ex);
				}
			}
		}
		
		
		/**
		Inserts recipe into database
		**/
		public void insertRecipe(int i)
		{
			connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			connn2 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			SqlCommand insertRecipe = new SqlCommand(" INSERT INTO Recipe(name, description, updateTime, serves, prepTime, cookingTime, addedBy, changeTime,uniqueid, progress, difficulty, dietary, tips, cusine) OUTPUT INSERTED.id VALUES (@name, @description, @updateTime, @serves, @prepTime, @cookingTime, @addedBy, @changeTime, @uniqueid, @progress, @difficulty, @dietary, @tips, @cusine)", connn);
			insertRecipe.Parameters.AddWithValue("@name", recipe[i].name);
			insertRecipe.Parameters.AddWithValue("@description", recipe[i].description);
			insertRecipe.Parameters.AddWithValue("@serves", recipe[i].serves);
			insertRecipe.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
			insertRecipe.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
			insertRecipe.Parameters.AddWithValue("@addedBy", recipe[i].addedBy);
			insertRecipe.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertRecipe.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			insertRecipe.Parameters.AddWithValue("@uniqueid", recipe[i].uniqueid);
			insertRecipe.Parameters.AddWithValue("@progress", recipe[i].progress);
			insertRecipe.Parameters.AddWithValue("@difficulty", recipe[i].difficulty);
			insertRecipe.Parameters.AddWithValue("@dietary", recipe[i].dietary);
			insertRecipe.Parameters.AddWithValue("@tips", recipe[i].tips);
			insertRecipe.Parameters.AddWithValue("@cusine", recipe[i].cusine);
			connn.Open();
			connn2.Open();
			try
			{ 
				recipeId = (Int32)insertRecipe.ExecuteScalar();
				insertPrep(i);	
				insertIngredient(i);
				insertCookbook(i);
				insertImage(i);
			}
			catch(Exception e)
			{
				Response.Write("Error Insert Recipe");
				Response.Write(e);
			}
			connn.Close();
			connn2.Close();		
		}
		
		/**
		inserts prep details into database
		**/
		public void insertPrep(int i)
		{
			for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
			{
				SqlCommand insertPrep = new SqlCommand(" INSERT INTO Preperation(instruction, instructionNum, updateTime, changeTime, uniqueid, progress) OUTPUT INSERTED.id VALUES (@prep, @prepNums, @updateTime, @changeTime, @uniqueid, @progress)", connn);
				insertPrep.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
				insertPrep.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
				insertPrep.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertPrep.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				insertPrep.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
				insertPrep.Parameters.AddWithValue("@progress", recipe[i].Preperation[3].prepprogress[y]);
				try
				{
					Int32 prepId = (Int32)insertPrep.ExecuteScalar();
					Response.Write(prepId);
					SqlCommand insertPrepLink = new SqlCommand(" INSERT INTO PrepRecipe(recipeId, Preperationid, updateTime, changeTime)  VALUES (@recipeId, @Preperationid, @updateTime, @changeTime)", connn);
					insertPrepLink.Parameters.AddWithValue("@recipeId", recipeId);
					insertPrepLink.Parameters.AddWithValue("@Preperationid", prepId);
					insertPrepLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
					insertPrepLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
					try
					{
						SqlDataReader rdr = insertPrepLink.ExecuteReader();
						rdr.Close();

					}
					catch (Exception ex)
					{
						Response.Write("Error ");
						Response.Write(ex);
					}
				}
				catch (Exception ex)
				{
					Response.Write("Error ");
					Response.Write(ex);
				}			
			}
		}
		
		/**
		Inserts ingredient details into database
		**/
		public void insertIngredient(int i)
		{
			for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
			{
				
				Int32 ingredId = 0;
				SqlCommand selectIngredId = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connn);
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
					else{
				ingredId = 0;
				}
					rdr.Close();

				}
				catch (Exception ex)
				{

					Response.Write("Error ");
					Response.Write(ex);
				}
				
				
				SqlCommand insertIngred = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connn);
				insertIngred.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
				insertIngred.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertIngred.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				try
				{
					if (ingredId == 0)
					{
						ingredId = (Int32)insertIngred.ExecuteScalar();
					}
					
					SqlCommand insertIngredDet = new SqlCommand(" INSERT INTO IngredientDetails(ingredientId, amount, note, value, updateTime, changeTime, uniqueid, progress)  OUTPUT INSERTED.id  VALUES (@id, @amount, @note, @value, @updateTime, @changeTime, @uniqueid, @progress)", connn);
					insertIngredDet.Parameters.AddWithValue("@id", ingredId);
					insertIngredDet.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
					insertIngredDet.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
					insertIngredDet.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);
					insertIngredDet.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
					insertIngredDet.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
					insertIngredDet.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
					insertIngredDet.Parameters.AddWithValue("@progress", recipe[i].Ingredient[5].ingredprogress[a]);
					try
					{

						Int32 ingredDetsId = (Int32)insertIngredDet.ExecuteScalar();
						SqlCommand insertIngredRecipeLink = new SqlCommand(" INSERT INTO RecipeIngredient(Recipeid, ingredientDetailsId, updateTime, changeTime)  VALUES (@recipeid,@detsid, @updateTime, @changeTime)", connn);
						insertIngredRecipeLink.Parameters.AddWithValue("@recipeid", recipeId);
						insertIngredRecipeLink.Parameters.AddWithValue("@detsid", ingredDetsId);
						insertIngredRecipeLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
						insertIngredRecipeLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
						try
						{

							SqlDataReader rdr = insertIngredRecipeLink.ExecuteReader();
							rdr.Close();
							SqlCommand insertIngredDetsLink = new SqlCommand(" INSERT INTO IngredToIngredDetails(Ingredientdetailsid, ingredientid, updateTime, changeTime)  VALUES (@detsid,@ingredid, @updateTime, @changeTime)", connn);
							insertIngredDetsLink.Parameters.AddWithValue("@ingredid", ingredId);
							insertIngredDetsLink.Parameters.AddWithValue("@detsid", ingredDetsId);
							insertIngredDetsLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
							insertIngredDetsLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
							try
							{

								SqlDataReader rdrs = insertIngredDetsLink.ExecuteReader();
								rdrs.Close();

							}
							catch (Exception ex)
							{

								Response.Write("Error ");
								Response.Write(ex);
							}

						}
						catch (Exception ex)
						{

							Response.Write("Error ");
							Response.Write(ex);
						}

					}
					catch (Exception ex)
					{

						Response.Write("Error ");
						Response.Write(ex);
					}

				}
				catch (Exception ex)
				{

					Response.Write("Error ");
					Response.Write(ex);
				}
			}
			
		}
		
		
		/**
		Inserts cookbook details for recipe into database
		**/
		public void insertCookbook(int i)
		{
			SqlCommand selectCookbookId = new SqlCommand(" SELECT id FROM Cookbook WHERE uniqueid=@uniqueid", connn);
			selectCookbookId.Parameters.AddWithValue("@uniqueid", recipe[i].cookbookid);
			var reader = selectCookbookId.ExecuteReader();
			while(reader.Read())
			{
				Int32 id = (Int32)reader["id"];
				Response.Write("ID  " + id);
				SqlCommand insertCookbook = new SqlCommand("INSERT INTO CookbookRecipe (Recipeid, Cookbookid, changeTime, updateTime) VALUES(@recipeid, @cookbookid, @changeTime, @updateTime)", connn);
				insertCookbook.Parameters.AddWithValue("@recipeid", recipeId);
				insertCookbook.Parameters.AddWithValue("@cookbookid", id);
				insertCookbook.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertCookbook.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				try
				{
					Response.Write("Execute  " + id);
					SqlDataReader rdrs = insertCookbook.ExecuteReader();
					rdrs.Close();

				}
				catch (Exception ex)
				{

					Response.Write("Error ");
					Response.Write(ex);
				}
			}
			
		}
		
		public void insertImage(int i)
		{
			SqlCommand insertimage = new SqlCommand(" INSERT INTO Images(image, updateTime, changeTime, uniqueid) OUTPUT INSERTED.imageid VALUES (@image, @updateTime, @changeTime, @uniqueid)", connn);
			byte[] image  = null;
			if(recipe[i].image != "")
			{
				image = Convert.FromBase64String(recipe[i].image);
			}
			insertimage.Parameters.AddWithValue("@image", image);
			insertimage.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertimage.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			insertimage.Parameters.AddWithValue("@uniqueid", recipe[i].imageid);

			try
			{
				Int32 imageid = (Int32)insertimage.ExecuteScalar();
				SqlCommand insertimagelink = new SqlCommand(" INSERT INTO RecipeImages(Recipeid, imageid, updateTime, changeTime)  VALUES (@recipeid,@imageid, @updateTime, @changeTime)", connn);
				insertimagelink.Parameters.AddWithValue("@recipeid", recipeId);
				insertimagelink.Parameters.AddWithValue("@imageid", imageid);
				insertimagelink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertimagelink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				
				try
				{
					SqlDataReader rdr = insertimagelink.ExecuteReader();
					rdr.Close();

				}
				catch (Exception ex)
				{
					Response.Write("Error ");
					Response.Write(ex);
				}
			}
			catch (Exception ex)
			{
				Response.Write("Error ");
				Response.Write(ex);
			}
			
		}
		
		
		/**
		Class stores recipe details to send to json
		**/
		public class Recipe
		{
			public string name { get; set; }
			public string description { get; set; }
			public string serves { get; set; }
			public string prepTime { get; set; }
			public string cookingTime { get; set; }
			public string addedBy { get; set; }
			public string updateTime { get; set;  }
			public string changeTime { get; set;  }
			public string uniqueid { get; set;  }
			public string cookbookid {get; set; }
			public string image { get; set; }
			public string imageid {get; set;}
			public string progress { get; set; }
			public string difficulty { get; set; }
			public string dietary { get; set; }
			public string tips { get; set; }
			public string cusine { get; set; }
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
		}
	}
	
	
}




