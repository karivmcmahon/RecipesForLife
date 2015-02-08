using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.UI;
using System.IO;
using System.Web.UI.WebControls;
using System.Data.SqlClient;
using System.Configuration;
using System.Web.Script.Serialization;

namespace WebApplication1
{
    public partial class WebForm1 : System.Web.UI.Page
    {
        protected void Page_Load(object sender, EventArgs e)
        {
            string jsonInput = new System.IO.StreamReader(Context.Request.InputStream, System.Text.Encoding.UTF8).ReadToEnd();
            if (jsonInput != null)
            {
                //Serializing a json
                JavaScriptSerializer js = new JavaScriptSerializer();
                Employee emp = new Employee();
                emp.Name = "a";
                emp.age = 12;
                string json = js.Serialize(emp);
              

               var p2 = js.Deserialize<List<Account>>(jsonInput);
               for (int i = 0; i < p2.Count(); i++)
               {
                 /**  Response.Write("Name " + p2[i].name);
                   Response.Write("Country " + p2[i].country);
                   Response.Write("ID " + p2[i].id);
                   Response.Write("City " + p2[i].city);
                   Response.Write("Bio " + p2[i].bio);
                   Response.Write("Cooking interest " + p2[i].cookingInterest);
                   Response.Write("Update time " + p2[i].updateTime); **/

                   SqlConnection con = new SqlConnection(System.Configuration.ConfigurationManager.ConnectionStrings["SQLDbConnection"].ConnectionString);
                   SqlCommand insert = new SqlCommand(" INSERT INTO Users(name, cookingInterest, updateTime, bio, city, country) OUTPUT INSERTED.id VALUES (@name, @cookingInterest, @updateTime, @bio, @city, @country)", con);
                  // insert.Parameters.AddWithValue("@id", p2[i].id);
                   insert.Parameters.AddWithValue("@name", p2[i].name);
                   insert.Parameters.AddWithValue("@cookingInterest", p2[i].cookingInterest);
                   insert.Parameters.AddWithValue("@updateTime", p2[i].updateTime);
                   insert.Parameters.AddWithValue("@bio", p2[i].bio);
                   insert.Parameters.AddWithValue("@city", p2[i].city);
                   insert.Parameters.AddWithValue("@country", p2[i].country); 

                 

                   try
                   {
                      
                       con.Open();
                       Int32 newId = (Int32) insert.ExecuteScalar();
                       Response.Write(newId);
                      // Response.Write("Success");

                       SqlCommand insert2 = new SqlCommand(" INSERT INTO Account(id,email,password,updateTime) VALUES (@id,@email,@password,@updateTime)", con);
                       insert2.Parameters.AddWithValue("@id", newId);
                       insert2.Parameters.AddWithValue("@email", p2[i].email);
                       insert2.Parameters.AddWithValue("@password", p2[i].password);
                       insert2.Parameters.AddWithValue("@updateTime", p2[i].updateTime);
                       insert2.ExecuteNonQuery();
                       Response.Write("Success");

                   }
                   catch (Exception ex)
                   {

                       Response.Write("Error ");
                       Response.Write(ex);
                   }

                  
                  con.Close(); 
               }
			  
               

                
                
            }
            

            // Encode image - String base64String = Convert.ToBase64String(stream1.ToArray());
            //p.image = base64String
        }

     /**   protected void Button1_Click(object sender, EventArgs e)
        {
            string connectionString = ConfigurationManager.ConnectionStrings["SQLDbConnection"].ToString();
            SqlConnection connection = new SqlConnection(connectionString);
            connection.Open();
            Label1.Text = "Connected to Database Server !!";
            connection.Close();
        } **/
    }
}


public class Account
{
    public int  id { get; set; }
    public string name { get; set; }
    public string updateTime { get; set; }
    public string country { get; set; }
    public string city { get; set; }
    public string bio { get; set; }
    public string email { get; set; }
    public string password { get; set; }
    public string cookingInterest { get; set; }
}

public class Employee
{
    public string Name { get; set; }
    public int age { get; set; }
}


