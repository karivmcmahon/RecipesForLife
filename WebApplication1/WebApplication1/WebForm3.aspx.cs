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
    public partial class WebForm3 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
             string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
             if (jsonInput != null)
             {
                 JavaScriptSerializer js = new JavaScriptSerializer();
                 var recipe = js.Deserialize<List<Recipe>>(jsonInput);
                 for(int i = 0; i < recipe.Count(); i++)
                 {
                     SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                     SqlCommand insert = new SqlCommand(" INSERT INTO Recipe(name, description, updateTime, serves, prepTime, cookingTime, addedBy) OUTPUT INSERTED.id VALUES (@name, @description, @updateTime, @serves, @prepTime, @cookingTime, @addedBy)", connn);
                     insert.Parameters.AddWithValue("@name", recipe[i].name);
                     insert.Parameters.AddWithValue("@description", recipe[i].description);
                     insert.Parameters.AddWithValue("@serves", recipe[i].serves);
                     insert.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
                     insert.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
                     insert.Parameters.AddWithValue("@addedBy", recipe[i].addedBy);
                     insert.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);

                    connn.Open();
                     try
                     {
                         
                         Int32 recipeId = (Int32)insert.ExecuteScalar();
                         Response.Write(recipeId);
                  
                        
                         for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
                         {
                                SqlCommand insert2 = new SqlCommand(" INSERT INTO Preperation(instruction, instructionNum, updateTime) OUTPUT INSERTED.id VALUES (@prep, @prepNums, @updateTime)", connn);
                                 insert2.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
                                 insert2.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
                                 insert2.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
                                 try
                                 {

                                     Int32 prepId = (Int32)insert2.ExecuteScalar();
                                     Response.Write(prepId);
                                     SqlCommand insert3 = new SqlCommand(" INSERT INTO PrepRecipe(recipeId, Preperationid, updateTime)  VALUES (@recipeId, @Preperationid, @updateTime)", connn);
                                     insert3.Parameters.AddWithValue("@recipeId", recipeId);
                                     insert3.Parameters.AddWithValue("@Preperationid", prepId);
                                     insert3.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
                                     try
                                     {

                                         insert3.ExecuteReader();
               
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
                                 
                             
                             //  Response.Write(recipe[i].Preperation[1].prepNums[z] + "\n");
                             
                             //  Response.Write(recipe[i].Preperation[0].prep[y] + "\n");
                         }

                        
                         
                        
                     }
                     catch (Exception ex)
                     {

                         Response.Write("Error ");
                         Response.Write(ex);
                     }
                     connn.Close();

                       
                     
                         

                        for(int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
                        {
                          //  Response.Write(recipe[i].Ingredient[0].Ingredients[a]);
                        }

                        for(int b = 0; b < recipe[i].Ingredient[1].Value.Count(); b++)
                        {
                          //  Response.Write(recipe[i].Ingredient[1].Value[b]);
                        }

                        for(int c = 0; c < recipe[i].Ingredient[2].Amount.Count(); c++)
                        {
                         //   Response.Write(recipe[i].Ingredient[2].Amount[c]);
                        }

                        for(int d = 0; d < recipe[i].Ingredient[3].Notes.Count(); d++)
                         {
                            // Response.Write(recipe[i].Ingredient[3].Notes[d]);
                         }

                     
                    // Response.Write(recipe[i].Preperation.prepNums[0]);
                 }
             }
        }
    }
}

public class Recipe
{
    public string name { get; set; }
    public string description { get; set; }
    public string serves { get; set; }
    public string prepTime { get; set; }
    public string cookingTime { get; set; }
    public string addedBy { get; set; }
    public string updateTime { get; set;  }
    public List<Preperation> Preperation { get; set; }
    public List<Ingredient> Ingredient { get; set; }
}

public class Preperation
{
    public  List<String> prep { get; set; }
    public List<String> prepNums { get; set; }
  //  public int prepNums { get; set; }
}

public class Ingredient
{
    public List<String> Ingredients { get; set; }
    public List<String> Value { get; set; }
    public List<String> Amount { get; set; }
    public List<String> Notes { get; set; }
}