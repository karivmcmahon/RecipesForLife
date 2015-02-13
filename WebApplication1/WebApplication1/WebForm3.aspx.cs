﻿using System;
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
					 SqlConnection connn2 = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                     SqlCommand insert = new SqlCommand(" INSERT INTO Recipe(name, description, updateTime, serves, prepTime, cookingTime, addedBy, changeTime,uniqueid) OUTPUT INSERTED.id VALUES (@name, @description, @updateTime, @serves, @prepTime, @cookingTime, @addedBy, @changeTime, @uniqueid)", connn);
                     insert.Parameters.AddWithValue("@name", recipe[i].name);
                     insert.Parameters.AddWithValue("@description", recipe[i].description);
                     insert.Parameters.AddWithValue("@serves", recipe[i].serves);
                     insert.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
                     insert.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
                     insert.Parameters.AddWithValue("@addedBy", recipe[i].addedBy);
                     insert.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
					 insert.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
					 insert.Parameters.AddWithValue("@uniqueid", recipe[i].uniqueid);

                    connn.Open();
					connn2.Open();
                     try
                     {
                         
                         Int32 recipeId = (Int32)insert.ExecuteScalar();
                         Response.Write(recipeId);
                  
                        
                         for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
                         {
                                SqlCommand insert2 = new SqlCommand(" INSERT INTO Preperation(instruction, instructionNum, updateTime, changeTime, uniqueid) OUTPUT INSERTED.id VALUES (@prep, @prepNums, @updateTime, @changeTime, @uniqueid)", connn);
                                 insert2.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
                                 insert2.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
                                 insert2.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
								  insert2.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
								  insert2.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
                                 try
                                 {

                                     Int32 prepId = (Int32)insert2.ExecuteScalar();
                                     Response.Write(prepId);
                                     SqlCommand insert3 = new SqlCommand(" INSERT INTO PrepRecipe(recipeId, Preperationid, updateTime, changeTime)  VALUES (@recipeId, @Preperationid, @updateTime, @changeTime)", connn);
                                     insert3.Parameters.AddWithValue("@recipeId", recipeId);
                                     insert3.Parameters.AddWithValue("@Preperationid", prepId);
                                     insert3.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
									  insert3.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
                                     try
                                     {

                                          SqlDataReader rdr = insert3.ExecuteReader();
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
                         
                         for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
                         {
                             
                             Int32 ingredId = 0;
                             SqlCommand select = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connn);
                             select.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
                             try
                             {

                                 SqlDataReader rdr = select.ExecuteReader();
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
                             }
                             
                            
                             SqlCommand insert4 = new SqlCommand(" INSERT INTO Ingredient(name, updateTime, changeTime)  OUTPUT INSERTED.id  VALUES (@name,  @updateTime, @changeTime)", connn);
                             insert4.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
                             insert4.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
							  insert4.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
                             try
                             {
                                 if (ingredId == 0)
                                 {
                                     ingredId = (Int32)insert4.ExecuteScalar();
                                 }
                              
                                 SqlCommand insert5 = new SqlCommand(" INSERT INTO IngredientDetails(ingredientId, amount, note, value, updateTime, changeTime, uniqueid)  OUTPUT INSERTED.id  VALUES (@id, @amount, @note, @value, @updateTime, @changeTime, @uniqueid)", connn);
                                 insert5.Parameters.AddWithValue("@id", ingredId);
                                 insert5.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
                                 insert5.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
                                 insert5.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);
                                 insert5.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
								 insert5.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
								 insert5.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
                                 try
                                 {

                                     Int32 ingredDetsId = (Int32)insert5.ExecuteScalar();
                                     SqlCommand insert6 = new SqlCommand(" INSERT INTO RecipeIngredient(Recipeid, ingredientDetailsId, updateTime, changeTime)  VALUES (@recipeid,@detsid, @updateTime, @changeTime)", connn);
                                     insert6.Parameters.AddWithValue("@recipeid", recipeId);
                                     insert6.Parameters.AddWithValue("@detsid", ingredDetsId);
                                     insert6.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
									  insert6.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
                                     try
                                     {

                                         SqlDataReader rdr = insert6.ExecuteReader();
                                         rdr.Close();
                                         SqlCommand insert7 = new SqlCommand(" INSERT INTO IngredToIngredDetails(Ingredientdetailsid, ingredientid, updateTime, changeTime)  VALUES (@detsid,@ingredid, @updateTime, @changeTime)", connn);
                                         insert7.Parameters.AddWithValue("@ingredid", ingredId);
                                         insert7.Parameters.AddWithValue("@detsid", ingredDetsId);
                                         insert7.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
										 insert7.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
                                         try
                                         {

                                             SqlDataReader rdrs = insert7.ExecuteReader();
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
							 
							 Response.Write("HI");
							 SqlCommand select2 = new SqlCommand(" SELECT id FROM Cookbook WHERE uniqueid=@uniqueid", connn);
							select2.Parameters.AddWithValue("@uniqueid", recipe[i].cookbookid);
							var reader = select2.ExecuteReader();
							 Response.Write("HI");
							while(reader.Read())
							{
							 Response.Write("HI");
							  Int32 id = (Int32)reader["id"];
							   Response.Write("ID  " + id);
							  SqlCommand insert8 = new SqlCommand("INSERT INTO CookbookRecipe (Recipeid, Cookbookid, changeTime, updateTime) VALUES(@recipeid, @cookbookid, @changeTime, @updateTime)", connn);
							  insert8.Parameters.AddWithValue("@recipeid", recipeId);
							  insert8.Parameters.AddWithValue("@cookbookid", id);
							  insert8.Parameters.AddWithValue("@updateTime", recipe[i].updateTime);
							 insert8.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
							  try
                                         {
											 Response.Write("Execute  " + id);
                                             SqlDataReader rdrs = insert8.ExecuteReader();
                                             rdrs.Close();

                                         }
                                         catch (Exception ex)
                                         {

                                             Response.Write("Error ");
                                             Response.Write(ex);
                                         }
							}
							
                             
                         

                        
                         
                        
                     }
                     catch (Exception ex)
                     {

                         Response.Write("Error ");
                         Response.Write(ex);
                     }
					 
					 
                     connn.Close();
					 connn2.Close();

                       
                     
                         

                       
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
	public string changeTime { get; set;  }
    public string uniqueid { get; set;  }
	public string cookbookid {get; set; }
    public List<Preperation> Preperation { get; set; }
    public List<Ingredient> Ingredient { get; set; }
}

public class Preperation
{
    public  List<String> prep { get; set; }
    public List<String> prepNums { get; set; }
    public List<String> uniqueid { get; set;  }
  //  public int prepNums { get; set; }
}

public class Ingredient
{
    public List<String> Ingredients { get; set; }
    public List<String> Value { get; set; }
    public List<String> Amount { get; set; }
    public List<String> Notes { get; set; }
	public List<String> uniqueid {get;set;}
}