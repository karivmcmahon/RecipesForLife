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
    public partial class WebForm4 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            int recipeID = 0;
            string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {
                JavaScriptSerializer js = new JavaScriptSerializer();
                var time = js.Deserialize<List<Date>>(jsonInput);
                string lastUpdated = time[0].updateTime;
                SqlConnection con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlConnection conn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                SqlCommand select = new SqlCommand(" SELECT * FROM Recipe WHERE updateTime > @lastUpdated", con);
                select.Parameters.AddWithValue("@lastUpdated", lastUpdated);
                con.Open();
                conn.Open();
                connn.Open();
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
                   
                    SqlCommand select2 = new SqlCommand("SELECT PrepRecipe.Preperationid, Preperation.instruction, Preperation.instructionNum, Preperation.uniqueid FROM PrepRecipe INNER JOIN Preperation ON PrepRecipe.PreperationId=Preperation.id WHERE PrepRecipe.recipeId = @recipe;", conn);
                    select2.Parameters.AddWithValue("@recipe", recipe.id);
                    var reader2 = select2.ExecuteReader();
                    // int i = 0;
                    while (reader2.Read())
                    {

                        //Preperation preps = new Preperation();
							Preperation preps = new Preperation();
                            preps.prep = new List<string>();
							 preps.uniqueid = new List<string>();
                            preps.prepNums = new List<Int32>();
                            preps.prep.Add((string)reader2["instruction"]);
							preps.uniqueid.Add((string)reader2["uniqueid"]);
                            preps.prepNums.Add((Int32)reader2["instructionNum"]);
                            recipe.Preperation.Add(preps);
					}
                        
                       SqlCommand select3 = new SqlCommand("SELECT * FROM IngredientDetails INNER JOIN RecipeIngredient ON IngredientDetails.id=RecipeIngredient.ingredientDetailsId INNER JOIN Ingredient ON Ingredient.id = IngredientDetails.ingredientId WHERE RecipeIngredient.RecipeId = @recipe;", connn);
                        select3.Parameters.AddWithValue("@recipe", recipe.id);
                        var reader3 = select3.ExecuteReader();
                        while (reader3.Read())
                        {
                          
                            

                           
                           

                            Ingredient ingreds = new Ingredient();
                            ingreds.Ingredients = new List<string>();
                            ingreds.Value = new List<string>();
                            ingreds.Amount = new List<Int32>();
                            ingreds.Notes = new List<string>();
							ingreds.uniqueid = new List<string>();
                            ingreds.Ingredients.Add((string)reader3["name"]);
                            ingreds.Amount.Add((Int32)reader3["amount"]);
                            ingreds.Value.Add((string)reader3["value"]);
                            ingreds.Notes.Add((string)reader3["note"]);
							 ingreds.uniqueid.Add((string)reader3["uniqueid"]);
							

                            
                            recipe.Ingredient.Add(ingreds); 

                           
                            
                        }
						
				    SqlCommand select4 = new SqlCommand("SELECT uniqueid FROM Cookbook INNER JOIN CookbookRecipe ON Cookbook.id=CookbookRecipe.Cookbookid WHERE CookbookRecipe.Recipeid=@id", conn);
                    select4.Parameters.AddWithValue("@id", recipe.id);
                    var reader4 = select4.ExecuteReader();
					while(reader4.Read())
					{
						recipe.cookingid = (string)reader4["uniqueid"];
					}
                      

                      
                      recipes.Recipe.Add(recipe); 
                    }
					

                
                con.Close();
                conn.Close();
                connn.Close();
                string json = js.Serialize(recipes);
                Response.Write(json);
				}

            }
        }
    }

    public class Date
    {
        public string updateTime { get; set; }
    }

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
        public List<Preperation> Preperation { get; set; }
        public List<Ingredient> Ingredient { get; set; }
        //  public List<Ingredient> Ingredient { get; set; }
    }

public class Recipes
{
    public List<Recipe> Recipe { get; set;} 

}

    public class Preperation
    {
        public List<String> prep { get; set; }
        public List<Int32> prepNums { get; set; }
		public List<String> uniqueid { get; set; }
        
    }

    public class Ingredient
    {
        public List<String> Ingredients { get; set; }
        public List<String> Value { get; set; }
        public List<Int32> Amount { get; set; }
        public List<String> Notes { get; set; }
		public List<String> uniqueid { get; set; }
    }
