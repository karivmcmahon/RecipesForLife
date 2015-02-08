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
    public partial class WebForm5 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {
                    JavaScriptSerializer js = new JavaScriptSerializer();
                 var recipe = js.Deserialize<List<Recipe>>(jsonInput);
                 for (int i = 0; i < recipe.Count(); i++)
                 {
                     SqlConnection connn = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                     SqlCommand insert = new SqlCommand("UPDATE Recipe SET name=@name, description=@description, serves=@serves, prepTime=@prepTime, cookingTime=@cookingTime, changeTime=@changeTime WHERE uniqueid=@uniqueid", connn);
                     insert.Parameters.AddWithValue("@name", recipe[i].name);
                     insert.Parameters.AddWithValue("@description", recipe[i].description);
                     insert.Parameters.AddWithValue("@serves", recipe[i].serves);
                     insert.Parameters.AddWithValue("@prepTime", recipe[i].prepTime);
                     insert.Parameters.AddWithValue("@cookingTime", recipe[i].cookingTime);
                     insert.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
                     insert.Parameters.AddWithValue("@uniqueid", recipe[i].uniqueid);

                     connn.Open();
                     try
                     {

                        SqlDataReader rdr= insert.ExecuteReader();
                                          rdr.Close();
                         for (int y = 0; y < recipe[i].Preperation[0].prep.Count(); y++)
                         {
                                SqlCommand insert2 = new SqlCommand("UPDATE Preperation SET instruction=@prep, instructionNum=@prepNums, changeTime=@changeTime WHERE uniqueid=@uniqueid", connn);
                                 insert2.Parameters.AddWithValue("@prep", recipe[i].Preperation[0].prep[y]);
                                 insert2.Parameters.AddWithValue("@prepNums", recipe[i].Preperation[1].prepNums[y]);
								  insert2.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
								  insert2.Parameters.AddWithValue("@uniqueid", recipe[i].Preperation[2].uniqueid[y]);
						
								 SqlDataReader rdr2 = insert2.ExecuteReader();
                                          rdr2.Close();
						}			  
						
						for (int a = 0; a < recipe[i].Ingredient[0].Ingredients.Count(); a++)
                         {
                             
                             Int32 ingredId = 0;
                             SqlCommand select = new SqlCommand(" SELECT id FROM Ingredient WHERE name=@name", connn);
                             select.Parameters.AddWithValue("@name", recipe[i].Ingredient[0].Ingredients[a]);
                             try
                             {

                                 SqlDataReader rdr3 = select.ExecuteReader();
                                 if(rdr3.HasRows)
                                 {
                                     while (rdr3.Read())
                                     {
                                         // read a row, for example:
                                         ingredId= rdr3.GetInt32(0);
                                     }
                                 }
                                 rdr3.Close();

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
                              
                                 SqlCommand insert5 = new SqlCommand("UPDATE IngredientDetails SET amount=@amount, note=@note, value=@value, changeTime=@changeTime WHERE uniqueid=@uniqueid", connn);
                                 insert5.Parameters.AddWithValue("@id", ingredId);
                                 insert5.Parameters.AddWithValue("@amount", recipe[i].Ingredient[2].Amount[a]);
                                 insert5.Parameters.AddWithValue("@note", recipe[i].Ingredient[3].Notes[a]);
                                 insert5.Parameters.AddWithValue("@value", recipe[i].Ingredient[1].Value[a]);
   
								 insert5.Parameters.AddWithValue("@changeTime", recipe[i].changeTime);
								 insert5.Parameters.AddWithValue("@uniqueid", recipe[i].Ingredient[4].uniqueid[a]);
                                 try
                                 {

                                     SqlDataReader rdr4 = insert5.ExecuteReader();
									 rdr4.Close();
								 }
								 catch(Exception ex)
								 {
								     Response.Write("Error ");
                                             Response.Write(ex);
                                         }
							}
								 catch(Exception ex)
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
	public string updateTime { get; set; }
    public string changeTime { get; set; }
    public string uniqueid { get; set; }
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