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
	Class handles recipe JSON from app to insert into database
	**/
	public partial class WebForm3 : System.Web.UI.Page
	{
		
		string jsonInput = "";
		List<Recipe> recipe = new List<Recipe>();
		SqlConnection connn = null;
		Int32 recipeId =0;
		
		protected void Page_Load(object sender, EventArgs e)
		{
			jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
			if (jsonInput != null)
			{
				try
				{
					//Deserializes JSON into a list of recipe objects
					JavaScriptSerializer js = new JavaScriptSerializer();
					js.MaxJsonLength = Int32.MaxValue;
					recipe = js.Deserialize<List<Recipe>>(jsonInput);
					
					//Inserts recipes for the number in the list
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
		* Inserts recipe into database
		* int i - point in loop
		**/
		public void insertRecipe(int i)
		{
			//Sets up and opens connection
			connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
			connn.Open();
			
			//Sets up command to insert recipe
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
			
			
			try
			{ 
				recipeId = (Int32)insertRecipe.ExecuteScalar(); //inserts recipe into database and retrieves inserted id
				
				insertPrep(i);	//makes call to insert prep related to this recipe
				insertIngredient(i); //makes call to insert prep related to this recipe
				insertCookbook(i); //makes call to insert cookbook and recipe link for the recipe
				insertImage(i); //makes call to insert image for this recipe
			}
			catch(Exception e)
			{
				Response.Write("Error At Insert Recipe   ");
				
			}
			connn.Close();
		}
		
		/**
		* Inserts prep details into database
		*
		* Int i -  point in for loop
		*
		**/
		public void insertPrep(int i)
		{
			for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
			{
				//Creates command to insert into preperation table
				SqlCommand insertPrep = new SqlCommand(" INSERT INTO Preperation(instruction, instructionNum, updateTime, changeTime, uniqueid, progress) OUTPUT INSERTED.id VALUES (@prep, @prepNums, @updateTime, @changeTime, @uniqueid, @progress)", connn);
				
				//Sets up command
				insertPrep.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
				insertPrep.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
				insertPrep.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertPrep.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				insertPrep.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
				insertPrep.Parameters.AddWithValue("@progress", recipe[i].Preperation[3].prepprogress[y]);
				
				try
				{
					Int32 prepId = (Int32)insertPrep.ExecuteScalar(); //Inserts prep details into preperation
					insertPrepLink(prepId, i );
					
				}
				catch (Exception ex)
				{
					Response.Write("Error At Prep ");
					Response.Write(ex);
				}			
			}
		}
		
		/**
		* Inserts the link between recipe and prep
		* prepId - value for where prep is in database
		**/
		public void insertPrepLink(Int32 prepId, int i)
		{
			//Creates commnd to insert preperation id into a linking table
			SqlCommand insertPrepLink = new SqlCommand(" INSERT INTO PrepRecipe(recipeId, Preperationid, updateTime, changeTime)  VALUES (@recipeId, @Preperationid, @updateTime, @changeTime)", connn);
			insertPrepLink.Parameters.AddWithValue("@recipeId", recipeId);
			insertPrepLink.Parameters.AddWithValue("@Preperationid", prepId);
			insertPrepLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertPrepLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			try
			{
				SqlDataReader rdr = insertPrepLink.ExecuteReader(); // inserts prep/recipe link
				rdr.Close();

			}
			catch (Exception ex)
			{
				Response.Write("Error At Prep Link ");
				Response.Write(ex);
			}
		}
		
		/**
		* Selects ingredient from database where name matches name from json
		* i - point in recipe loop
		* a - point in ingred loop
		*
		* return - Int32 - ingredient id
		**/
		public Int32 selectIngredient(int i, int a)
		{
			Int32 ingredId = 0;
			//Selects ingredient where name is the name of ingredient in JSON
			SqlCommand selectIngredId = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connn);
			selectIngredId.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
			
			try
			{
				SqlDataReader rdr = selectIngredId.ExecuteReader(); //Select query
				if(rdr.HasRows)
				{
					while (rdr.Read())
					{
						//If exists set ingred id
						ingredId= rdr.GetInt32(0);
					}
				}
				else
				{
					ingredId = 0; //Set id to 0
				}
				rdr.Close();

			}
			catch (Exception ex)
			{

				Response.Write("Error At Ingred Select ");
				Response.Write(ex);
			}
			return ingredId;
		}
		
		/**
		* Insert ingredient into database if it doesn't exist
		* ingredId - ingred id, if 0 tells us doesnt exist
		* i - point in recipe loop
		* a  - point in ingred loop
		* 
		* return - Int32 - updated ingredid
		**/
		public Int32 insertIngred(Int32 ingredId, int i, int a)
		{
			//Command  to insert ingredient into database if it doesnt exist
			SqlCommand insertIngred = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connn);
			//Set up command
			insertIngred.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
			insertIngred.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertIngred.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			try
			{
				if (ingredId == 0) // If 0 insert ingredient
				{
					ingredId = (Int32)insertIngred.ExecuteScalar();
				}
			}
			catch (Exception ex)
			{

				Response.Write("Error Ingredient ");
				Response.Write(ex);
			}
			return ingredId;
		}
		
		/**
		* Inserts ingred details into db
		* i - point in recipe loop
		* a -point in ingred loop
		* ingredId - ingred id which is inserted into ingred dets table
		**/
		public void insertIngredDets(Int32 ingredId, int i, int a)
		{
			//Command to insert the ingredient details
			SqlCommand insertIngredDet = new SqlCommand(" INSERT INTO IngredientDetails(ingredientId, amount, note, value, updateTime, changeTime, uniqueid, progress)  OUTPUT INSERTED.id  VALUES (@id, @amount, @note, @value, @updateTime, @changeTime, @uniqueid, @progress)", connn);
			//Set up command
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

				Int32 ingredDetsId = (Int32)insertIngredDet.ExecuteScalar(); //Inserts details
				insertIngredRecipeLink(ingredDetsId, i , a);
				insertIngredDetsLink(ingredId, ingredDetsId, i , a);
				
			}
			catch (Exception ex)
			{

				Response.Write("Error Ingredient Details ");
				Response.Write(ex);
			}
		}
		
		/**
		* Inserts the link between ingred  dets and recipe  in db
		* i  - point in recipe loop
		* a - point in ingred loop
		* ingredDetsId - ingred id in db
		**/
		public void insertIngredRecipeLink(Int32 ingredDetsId, int i, int a)
		{
			//Command to insert link between recipe and ingredient 
			SqlCommand insertIngredRecipeLink = new SqlCommand(" INSERT INTO RecipeIngredient(Recipeid, ingredientDetailsId, updateTime, changeTime)  VALUES (@recipeid,@detsid, @updateTime, @changeTime)", connn);
			insertIngredRecipeLink.Parameters.AddWithValue("@recipeid", recipeId);
			insertIngredRecipeLink.Parameters.AddWithValue("@detsid", ingredDetsId);
			insertIngredRecipeLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertIngredRecipeLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			try
			{

				SqlDataReader rdr = insertIngredRecipeLink.ExecuteReader(); //inserts recipe ingredient link
				rdr.Close();
			}
			catch (Exception ex)
			{

				Response.Write("Error Ingred Dets/Recipe Link ");
				Response.Write(ex);
			}
		}
		
		/**
		* Inserts the link between ingred dets and ingred id
		* ingredId - ingredient  id in db
		* ingredDetsId - ingredient dets id in db
		* i - point in recipe loop
		* a - point in ingred loop
		**/
		public void insertIngredDetsLink(Int32 ingredId, Int32 ingredDetsId, int i , int a)
		{
			//Command to insert link between ingredient and ingredient details
			SqlCommand insertIngredDetsLink = new SqlCommand(" INSERT INTO IngredToIngredDetails(Ingredientdetailsid, ingredientid, updateTime, changeTime)  VALUES (@detsid,@ingredid, @updateTime, @changeTime)", connn);
			insertIngredDetsLink.Parameters.AddWithValue("@ingredid", ingredId);
			insertIngredDetsLink.Parameters.AddWithValue("@detsid", ingredDetsId);
			insertIngredDetsLink.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertIngredDetsLink.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			try
			{

				SqlDataReader rdrs = insertIngredDetsLink.ExecuteReader(); //inserts ingred to ingred dets link
				rdrs.Close();

			}
			catch (Exception ex)
			{

				Response.Write("Error Ingred/Ingred Details Link ");
				Response.Write(ex);
			}
		}
		
		/**
		* Inserts ingredient details into database
		* int i - point in loop
		**/
		public void insertIngredient(int i)
		{
			for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
			{
				Int32 ingredId = selectIngredient(i, a);
				ingredId = insertIngred(ingredId, i,  a);
				insertIngredDets(ingredId, i , a);
				
			}
			
		}
		
		
		/**
		* Inserts cookbook link for recipe into database
		* int i - point in loop
		**/
		public void insertCookbook(int i)
		{
			//Selects cookbook with unique id to retrieve row id in database
			SqlCommand selectCookbookId = new SqlCommand(" SELECT id FROM Cookbook WHERE uniqueid=@uniqueid", connn);
			selectCookbookId.Parameters.AddWithValue("@uniqueid", recipe[i].cookbookid);
			var reader = selectCookbookId.ExecuteReader();
			while(reader.Read())
			{
				Int32 id = (Int32)reader["id"];
				
				//Inserts cookbook/recipe link 
				SqlCommand insertCookbook = new SqlCommand("INSERT INTO CookbookRecipe (Recipeid, Cookbookid, changeTime, updateTime) VALUES(@recipeid, @cookbookid, @changeTime, @updateTime)", connn);
				insertCookbook.Parameters.AddWithValue("@recipeid", recipeId);
				insertCookbook.Parameters.AddWithValue("@cookbookid", id);
				insertCookbook.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
				insertCookbook.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
				try
				{
					SqlDataReader rdrs = insertCookbook.ExecuteReader();
					rdrs.Close();

				}
				catch (Exception ex)
				{

					Response.Write("Error Insert Recipe/Cookbook ");
					Response.Write(ex);
				}
			}
			
		}
		
		/**
		* Insert image into image table for recipe
		* int i - point in loop
		**/
		public void insertImage(int i)
		{
			//Command to insert image into image table
			SqlCommand insertimage = new SqlCommand(" INSERT INTO Images(image, updateTime, changeTime, uniqueid) OUTPUT INSERTED.imageid VALUES (@image, @updateTime, @changeTime, @uniqueid)", connn);
			byte[] image  = null;
			if(recipe[i].image != "")
			{
				image = Convert.FromBase64String(recipe[i].image); //get image to byte array
			}
			insertimage.Parameters.AddWithValue("@image", image);
			insertimage.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
			insertimage.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
			insertimage.Parameters.AddWithValue("@uniqueid", recipe[i].imageid);

			try
			{
				Int32 imageid = (Int32)insertimage.ExecuteScalar(); //Insert and retrieve image id
				
				//Insert image / recipe link into database based on retrieved id
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
					Response.Write("Error  At Image/Recipe ");
					Response.Write(ex);
				}
			}
			catch (Exception ex)
			{
				Response.Write("Error At Image/Recipe ");
				Response.Write(ex);
			}
			
		}
		
		
		/**
		Class stores recipe details to from json
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
		Class stores prep details to from json
		**/
		public class Preperation
		{
			public  List<String> prep { get; set; }
			public List<String> prepNums { get; set; }
			public List<String> uniqueid { get; set;  }
			public List<String> prepprogress { get; set; }
		}

		/**
		Class stores ingredient details to from json
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




